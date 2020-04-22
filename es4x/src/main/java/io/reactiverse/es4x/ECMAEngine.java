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
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.graalvm.polyglot.*;
import org.graalvm.polyglot.io.FileSystem;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.regex.Pattern;

import static io.reactiverse.es4x.impl.AsyncError.parseStrackTraceElement;

public final class ECMAEngine {

  private static final Logger LOG = LoggerFactory.getLogger(ECMAEngine.class);

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
  private final AtomicBoolean codecInstalled = new AtomicBoolean(false);
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
        // ensure that the type really matches
        v -> {
          final Value meta = v.getMetaObject();
          if (meta != null) {
            // 20.0.0
            if (meta.hasMember("className")) {
              return "ArrayBuffer".equals(meta.getMember("className").toString());
            }
            // 20.1.0
            if (meta.hasMember("name")) {
              return "EArrayBuffer".equals(meta.getMember("name").toString());
            }
          }
          return false;
        },
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
      // map native Error Object to Throwable
      .targetTypeMapping(
        Value.class,
        Throwable.class,
        // an error has 2 fields: name + message
        it -> isScriptObject(it) && it.hasMember("name") && it.hasMember("message"),
        v -> {
          final String nameField = v.getMember("name").asString();
          final String messageField = v.getMember("message").asString();
          // empty message fields usually it JS prints the name field
          final Throwable t = new Throwable("".equals(messageField) ? nameField : messageField);
          // the stacktrace for JS is a single string and we need to parse it back to a Java friendly way
          if (v.hasMember("stack")) {
            String stack = v.getMember("stack").asString();
            String[] sel = stack.split("\n");
            StackTraceElement[] elements = new StackTraceElement[sel.length - 1];
            for (int i = 1; i < sel.length; i++) {
              elements[i-1] = parseStrackTraceElement(sel[i]);
            }
            t.setStackTrace(elements);
          }

          return t;
        })
      .build();

    fileSystem = new VertxFileSystem(vertx);
  }

  /**
   * Is script like object.
   *
   * @param v the value to consider
   * @return true for non null, unknown shape and not Proxy
   */
  private static boolean isScriptObject(Value v) {
    return
      // nullability
      !v.isNull() &&
      // primitives
      !v.isBoolean() &&
      !v.isDate() &&
      !v.isDuration() &&
      !v.isInstant() &&
      !v.isNumber() &&
      !v.isString() &&
      !v.isTime() &&
      !v.isTimeZone() &&
      // exceptions
      !v.isException() &&
      // rest
      !v.isNativePointer() &&
      !v.isHostObject();
  }

  private void registerCodec(Class className) {
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

    final Pattern[] allowedHostAccessClassFilters = ECMAEngine.allowedHostClassFilters();

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
    if (System.getProperty("js.ecmascript-version") != null) {
      builder.option("js.ecmascript-version", System.getProperty("js.ecmascript-version"));
    }

    // the instance
    final Context context = builder.build();

    // install the codec if needed
    if (codecInstalled.compareAndSet(false, true)) {
      // register a default codec to allow JSON messages directly from GraalJS to the JVM world
      final Consumer<?> callback = value -> registerCodec(value.getClass());

      context.eval(
        Source.newBuilder("js", "(function (fn) { fn({}); })", "<class-lookup>").cached(false).internal(true).buildLiteral()
      ).execute(callback);

      context.eval(
        Source.newBuilder("js", "(function (fn) { fn([]); })", "<class-lookup>").cached(false).internal(true).buildLiteral()
      ).execute(callback);
    }

    // setup complete
    return new Runtime(vertx, context, scripts);
  }

  public void close() {
    engine.close();
  }
}
