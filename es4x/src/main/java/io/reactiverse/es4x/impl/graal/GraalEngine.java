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
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.graalvm.polyglot.*;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.regex.Pattern;

public class GraalEngine implements ECMAEngine {

  private final Vertx vertx;
  private final Engine engine;
  private final HostAccess hostAccess;

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
        System.err.println("\u001B[1m\u001B[33mES4X is using graaljs in interpreted mode! Add the JVMCI compiler module in order to run in optimal mode!\u001B[0m");
      }
    }

    hostAccess = HostAccess.newBuilder()
      .allowPublicAccess(true)
      .allowArrayAccess(true)
      .allowListAccess(true)
      .allowAllImplementations(true)
      // map native JSON Object to Vert.x JSONObject
      .targetTypeMapping(
        Value.class,
        JsonObject.class,
        Value::hasMembers,
        v -> {
          if (v.isNull()) {
            return null;
          }
          return new JsonObject(v.as(Map.class));
        })
      // map native JSON Array to Vert.x JSONObject
      .targetTypeMapping(
        Value.class,
        JsonArray.class,
        Value::hasArrayElements,
        v -> {
          if (v.isNull()) {
            return null;
          }
          return new JsonArray(v.as(List.class));
        })
      // map native Date to Instant
      .targetTypeMapping(
        Value.class,
        Instant.class,
        Value::hasMembers,
        v -> {
          if (v.isNull()) {
            return null;
          }
          return Instant.ofEpochMilli(v.invokeMember("getTime").asLong());
        })
      // map native Date to java.util.Date
      .targetTypeMapping(
        Value.class,
        Date.class,
        Value::hasMembers,
        v -> {
          if (v.isNull()) {
            return null;
          }
          return new Date(v.invokeMember("getTime").asLong());
        })
      .build();
  }

  @Override
  public String name() {
    return "graaljs";
  }

  @Override
  @SuppressWarnings("unchecked")
  public Runtime<Value> newContext() {

    final Pattern[] allowedHostAccessClassFilters = allowedHostClassFilters();

    final Context.Builder builder = Context.newBuilder("js")
      .engine(engine)
      .fileSystem(new VertxFileSystem(vertx))
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
      .allowHostAccess(hostAccess);

    // allow specifying the custom ecma version
    if (System.getProperties().containsKey("js.ecmascript-version")) {
      builder.option("js.ecmascript-version", System.getProperty("js.ecmascript-version"));
    }

    // the instance
    final Context context = builder.build();

    // install the codec if needed
    if (codecInstalled.compareAndSet(false, true)) {
      // register a default codec to allow JSON messages directly from GraalJS to the JVM world
      final Consumer callback = value -> vertx.eventBus()
        .unregisterDefaultCodec(value.getClass())
        .registerDefaultCodec(value.getClass(), new JSObjectMessageCodec());

      context.eval(
        Source.newBuilder("js", "(function (fn) { fn({}); })", "<class-lookup>").cached(false).internal(true).buildLiteral()
      ).execute(callback);
    }

    // setup complete
    return new GraalRuntime(vertx, context);
  }
}
