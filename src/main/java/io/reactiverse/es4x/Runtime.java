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

import io.reactiverse.es4x.impl.graal.GraalRuntime;
import io.reactiverse.es4x.impl.nashorn.NashornRuntime;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public interface Runtime<R> {

  static Runtime getCurrent() {
    String rtName = System.getProperty("es4x.engine");
    // rt name takes precedence in the choice
    if (rtName == null) {
      String vmName = System.getProperty("java.vm.name");
      if (vmName != null && vmName.startsWith("GraalVM")) {
        rtName = "GraalJS";
      } else {
        rtName = "Nashorn";
      }
    }

    if (rtName.equalsIgnoreCase("GraalJS")) {
      System.setProperty("es4x.engine", "GraalJS");
      return new GraalRuntime();
    }

    if (rtName.equalsIgnoreCase("Nashorn")) {
      System.setProperty("es4x.engine", "Nashorn");
      return new NashornRuntime();
    }

    System.clearProperty("es4x.engine");
    throw new RuntimeException("Unsupported runtime: " + rtName);
  }

  /**
   * return the runtime name
   * @return runtime name.
   */
  String name();

  /**
   * Bootstraps a Vert.x instance
   * @param arguments
   * @return
   */
  default Vertx vertx(Map<String, Object> arguments) {

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
        registerCodec(vertx);
        return vertx;
      }
    } else {
      final Vertx vertx = Vertx.vertx();
      registerCodec(vertx);
      return vertx;
    }
  }

  Runtime<R> registerCodec(Vertx vertx);

  /**
   * Returns a module loader for the given runtime.
   *
   * @return loader
   */
  Loader<R> loader(Vertx vertx);
}
