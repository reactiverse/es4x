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

import io.reactiverse.es4x.Runtime;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import org.graalvm.polyglot.Value;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class GraalRuntime implements Runtime<Value> {

  @Override
  public String name() {
    return "GraalVM";
  }

  @Override
  public Vertx vertx(Object object, Value json, Map<String, Object> arguments) {
    final VertxOptions options = new VertxOptions(new JsonObject(arguments));

    if (options.isClustered()) {
      final CountDownLatch latch = new CountDownLatch(1);

      final AtomicReference<Throwable> err = new AtomicReference<>();
      final AtomicReference<Vertx> holder = new AtomicReference<>();

      Vertx.clusteredVertx(new VertxOptions(), ar -> {
        if (ar.failed()) {
          err.set(ar.cause());
          latch.countDown();
        } else {
          holder.set(ar.result());
          latch.countDown();
        }
      });

      try {
        latch.await();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }

      if (err.get() != null) {
        throw new RuntimeException(err.get());
      } else {
        final Vertx vertx = holder.get();
        registerCodec(vertx, object, json);
        return vertx;
      }
    } else {
      final Vertx vertx = Vertx.vertx();
      registerCodec(vertx, object, json);
      return vertx;
    }
  }

  private static void registerCodec(Vertx vertx, Object object, Value json) {
    if (object != null && json != null) {
      // register a default codec to allow JSON messages directly from GraalVM to the JVM world
      vertx.eventBus().unregisterDefaultCodec(object.getClass());
      vertx.eventBus().registerDefaultCodec(object.getClass(), new JSObjectMessageCodec<>(json));
    }
  }
}
