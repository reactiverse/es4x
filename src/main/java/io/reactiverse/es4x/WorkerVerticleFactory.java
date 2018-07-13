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

import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;

public class WorkerVerticleFactory implements io.vertx.core.spi.VerticleFactory {

  private Vertx vertx;

  @Override
  public void init(Vertx vertx) {
    this.vertx = vertx;
  }

  @Override
  public String prefix() {
    return "worker-js";
  }

  @Override
  public boolean blockingCreate() {
    return true;
  }

  @Override
  public Verticle createVerticle(String verticleName, ClassLoader classLoader) throws Exception {

    final Loader loader;

    synchronized (this) {
      // create a new CommonJS loader
      loader = Loader.create(vertx);
    }

    return new Verticle() {

      private Vertx vertx;
      private Context context;

      @Override
      public Vertx getVertx() {
        return vertx;
      }

      @Override
      public void init(Vertx vertx, Context context) {
        this.vertx = vertx;
        this.context = context;
      }

      @Override
      public void start(Future<Void> startFuture) throws Exception {
        final String address;
        final String fsVerticleName;

        // extract prefix if present
        if (verticleName.startsWith(prefix() + ":")) {
          fsVerticleName = verticleName.substring(prefix().length() + 1);
        } else {
          fsVerticleName = verticleName;
        }

        // expose config
        if (context != null && context.config() != null) {
          loader.config(context.config());
          address = context.config().getString("address");
        } else {
          address = fsVerticleName;
        }

        // define the postMessage function
        loader.eval(
          "(function (global) {\n" +
          "  global.postMessage = function (aMessage) {\n" +
          "    vertx.eventBus().send('in." + address + "', aMessage);\n" +
          "  };\n" +
          "})(global || this);");

        // this can take some time to load so it might block the event loop
        // this is usually not a issue as it is a one time operation
        final Object worker = loader.main(fsVerticleName);

        if (loader.hasMember(worker, "onmessage")) {
          // if the worker has specified a onmessage function we need to bind it to the eventbus
          vertx.eventBus().consumer("out." + address, msg -> {
            loader.invokeMethod(worker, "onmessage", msg.body());
          });
        }

        startFuture.complete();
      }

      @Override
      public void stop(Future<Void> stopFuture) throws Exception {
        loader.close();
      }
    };
  }
}
