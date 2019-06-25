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

public final class JSVerticleFactory extends ESVerticleFactory {

  @Override
  public String prefix() {
    return "js";
  }

  @Override
  Verticle createVerticle(Runtime runtime, String fsVerticleName) {
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
        final boolean worker;
        final Object self;

        if (context != null) {
          address = context.deploymentID();
          worker = context.isWorkerContext();
          // expose config
          if (context.config() != null) {
            runtime.config(context.config());
          }
        } else {
          worker = false;
          address = null;
        }

        // this can take some time to load so it might block the event loop
        // this is usually not a issue as it is a one time operation
        try {
          runtime.enter();
          if (worker) {
            self = runtime.worker(fsVerticleName, address);
          } else {
            self = runtime.main(fsVerticleName);
          }
        } catch (RuntimeException e) {
          startFuture.fail(e);
          return;
        } finally {
          runtime.leave();
        }

        if (self != null) {
          if (worker) {
            // if it is a worker and there is a onmessage handler we need to bind it to the eventbus
            if (runtime.hasMember(self, "onmessage")) {
              try {
                // if the worker has specified a onmessage function we need to bind it to the eventbus
                final Object JSON = runtime.eval("JSON");

                vertx.eventBus().consumer(address + ".out", msg -> {
                  // parse the json back to the engine runtime type
                  Object json = runtime.invokeMethod(JSON, "parse", msg.body());
                  // deliver it to the handler
                  runtime.invokeMethod(self, "onmessage", json);
                });
              } catch (RuntimeException e) {
                startFuture.fail(e);
                return;
              }
            }
          }
        }

        startFuture.complete();
      }

      @Override
      public void stop(Future<Void> stopFuture) {
        try {
          runtime.enter();
          runtime.emit("undeploy");
          stopFuture.complete();
        } catch (RuntimeException e) {
          stopFuture.fail(e);
        } finally {
          runtime.leave();
          runtime.close();
        }
      }
    };
  }
}
