/*
 * Copyright 2018 Red Hat, Inc.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  The Apache License v2.0 is available at
 *  http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */
package io.reactiverse.es4x;

import io.netty.buffer.Unpooled;
import io.reactiverse.es4x.impl.JSObjectMessageCodec;
import io.reactiverse.es4x.impl.VertxFileSystem;
import io.reactiverse.es4x.jul.ES4XFormatter;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import org.graalvm.polyglot.*;
import org.graalvm.polyglot.io.FileSystem;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.regex.Pattern;

public final class ECMAEngine {

  private static final Logger LOG = LoggerFactory.getLogger(ECMAEngine.class);
  private static final Source CLASS_LOOKUP = Source.newBuilder(
    "js",
    "(function (fn) { fn({}, []); })",
    "<class-lookup>"
  ).internal(true).buildLiteral();

  private static Pattern[] allowedHostClassFilters() {
    String hostClassFilter = System.getProperty("es4x.host.class.filter", System.getenv("ES4XHOSTCLASSFILTER"));
    if (hostClassFilter == null || hostClassFilter.length() == 0) {
      return null;
    }

    String[] glob = hostClassFilter.split(",");
    Pattern[] patterns = new Pattern[glob.length];
    for (int i = 0; i < patterns.length; i++) {
      boolean negate = false;
      String regex = glob[i];

      if (glob[i].charAt(0) == '!') {
        negate = true;
        regex = glob[i].substring(1);
      }

      patterns[i] = Pattern.compile(
        (negate ? "^(?!" : "") +
          regex
            // replace dots
            .replace(".", "\\.")
            // replace stars
            .replace("*", "[^\\.]+")
            // replace double stars
            .replace("[^\\.]+[^\\.]+", ".*")
            // replace ?
            .replace("?", "\\w") +
          (negate ? "$).*$" : "")
      );
    }

    return patterns;
  }


  private final Vertx vertx;
  private final Engine engine;
  private final HostAccess hostAccess;
  private final FileSystem fileSystem;
  private final PolyglotAccess polyglotAccess;

  // lazy install the codec
  private static boolean nag = true;

  public ECMAEngine(Vertx vertx) {
    this.vertx = vertx;
    final Handler logHandler = new ConsoleHandler();
    // customize the formatter
    logHandler.setFormatter(new ES4XFormatter());
    // build it
    this.engine = Engine.newBuilder()
      .logHandler(logHandler)
      .build();

    if (!engine.getLanguages().containsKey("js")) {
      throw new IllegalStateException("A language with id 'js' is not installed");
    }

    if (nag) {
      nag = false;
      if ("Interpreted".equalsIgnoreCase(engine.getImplementationName())) {
        LOG.warn("ES4X is using graaljs in interpreted mode! Add the JVMCI compiler module in order to run in optimal mode!");
      }
    }

    // enable or disable the polyglot access
    polyglotAccess = Boolean.getBoolean("es4x.polyglot") ? PolyglotAccess.ALL : PolyglotAccess.NONE;

    hostAccess = HostAccess.newBuilder(HostAccess.ALL)
      // map native JSON Object to Vert.x JSONObject
      .targetTypeMapping(
        Map.class,
        JsonObject.class,
        null,
        JsonObject::new)
      // map native JSON Array to Vert.x JSONArray
      .targetTypeMapping(
        List.class,
        JsonArray.class,
        null,
        JsonArray::new)
      // map native buffer to Vert.x Buffer
      .targetTypeMapping(
        Value.class,
        Buffer.class,
        Value::hasMembers,
        v -> {
          if (v.hasMember("nioByteBuffer")) {
            return Buffer.buffer(Unpooled.wrappedBuffer(v.getMember("nioByteBuffer").as(ByteBuffer.class)));
          } else {
            // this is a raw ArrayBuffer
            throw new ClassCastException("Cannot cast ArrayBuffer(without j.n.ByteBuffer)");
          }
        })
      // Ensure Arrays are exposed as List when the Java API is accepting Object
      .targetTypeMapping(List.class, Object.class, null, v -> v)
      .build();

    fileSystem = new VertxFileSystem(vertx);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private synchronized void registerCodec(Class className) {
    vertx.eventBus()
      .unregisterDefaultCodec(className)
      .registerDefaultCodec(className, new JSObjectMessageCodec(className.getName()));
  }

  public FileSystem fileSystem() {
    return fileSystem;
  }

  /**
   * return a new context for this engine.
   *
   * @param scripts scripts that shall be run at the creation of the runtime,
   *                usually for polyfills, initialization, customization.
   * @return new context.
   */
  public synchronized Runtime newContext(Source... scripts) {

    final Pattern[] allowedHostAccessClassFilters = allowedHostClassFilters();

    final Context.Builder builder = Context.newBuilder("js")
      .engine(engine)
      .fileSystem(fileSystem)
      // IO is allowed because it delegates to the
      // vertx filesystem implementation
      .allowIO(true)
      // do not allow creation of threads as it breaks the JS model
      // multi threading is allowed using workers
      .allowCreateThread(false)
      // host access is required to function properly however
      // users might declare filters
      .allowHostClassLookup(fqcn -> {
        if (allowedHostAccessClassFilters == null) {
          return true;
        } else {
          for (Pattern filter : allowedHostAccessClassFilters) {
            if (filter.matcher(fqcn).matches()) {
              return true;
            }
          }
          return false;
        }
      })
      .allowHostAccess(hostAccess)
      .allowPolyglotAccess(polyglotAccess);

    // allow specifying the custom ecma version
    builder.option("js.ecmascript-version", System.getProperty("js.ecmascript-version", "2019"));

    final Context context = builder.build();
    // register a default codec to allow JSON messages directly from GraalJS to the JVM world
    final BiConsumer<Object, Object> callback = (obj, arr) -> {
      registerCodec(obj.getClass());
      registerCodec(arr.getClass());
    };
    context.eval(CLASS_LOOKUP).execute(callback);
    // setup complete

    return new Runtime(vertx, context, scripts);
  }
}
