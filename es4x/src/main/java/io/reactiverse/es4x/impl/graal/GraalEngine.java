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
package io.reactiverse.es4x.impl.graal;

import io.reactiverse.es4x.ECMAEngine;
import io.reactiverse.es4x.Runtime;
import io.reactiverse.es4x.jul.ES4XFormatter;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.graalvm.polyglot.*;
import org.graalvm.polyglot.io.FileSystem;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.regex.Pattern;

public class GraalEngine implements ECMAEngine {

  private static final Logger LOG = LoggerFactory.getLogger(GraalEngine.class);

  private final Vertx vertx;
  private final Engine engine;
  private final HostAccess hostAccess;
  private final FileSystem fileSystem;
  private final PolyglotAccess polyglotAccess;

  // lazy install the codec
  private final AtomicBoolean codecInstalled = new AtomicBoolean(false);
  private static boolean nag = true;

  public GraalEngine(Vertx vertx) {
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
      // map Promise to io.vertx.core.Promise
      .targetTypeMapping(
        Value.class,
        Promise.class,
        v -> v.hasMembers() && v.hasMember("then"),
        v -> {
          if (v.isNull()) {
            return null;
          }
          return new VertxPromise(v);
        })
      // Ensure Arrays are exposed as List when the Java API is accepting Object
      .targetTypeMapping(List.class, Object.class, null, v -> v)
      .build();

    fileSystem = new VertxFileSystem(vertx);
  }

  private void registerCodec(Class className) {
    vertx.eventBus()
      .unregisterDefaultCodec(className)
      .registerDefaultCodec(className, new JSObjectMessageCodec(className.getName()));
  }

  @Override
  public FileSystem fileSystem() {
    return fileSystem;
  }

  @Override
  public Runtime newContext() {

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
    builder.option("js.ecmascript-version", System.getProperty("js.ecmascript-version", "2019"));

    // the instance
    final Context context = builder.build();

    // install the codec if needed
    if (codecInstalled.compareAndSet(false, true)) {
      // register a default codec to allow JSON messages directly from GraalJS to the JVM world
      final Consumer callback = value -> registerCodec(value.getClass());

      context.eval(
        Source.newBuilder("js", "(function (fn) { fn({}); })", "<class-lookup>").cached(false).internal(true).buildLiteral()
      ).execute(callback);

      context.eval(
        Source.newBuilder("js", "(function (fn) { fn([]); })", "<class-lookup>").cached(false).internal(true).buildLiteral()
      ).execute(callback);
    }

    // setup complete
    return new GraalRuntime(vertx, context);
  }
}
