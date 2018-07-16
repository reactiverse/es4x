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

public class VerticleFactory implements io.vertx.core.spi.VerticleFactory {

  private Vertx vertx;

  @Override
  public void init(Vertx vertx) {
    this.vertx = vertx;
  }

  @Override
  public String prefix() {
    return "js";
  }

  @Override
  public Verticle createVerticle(String verticleName, ClassLoader classLoader) throws Exception {

    final Loader engine;

    synchronized (this) {
      // create a new CommonJS loader
      engine = Loader.create(vertx);
    }

    return new Verticle() {

      private Vertx vertx;
      private Context context;

      private Object self;

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
        final boolean worker;

        // extract prefix if present
        if (verticleName.startsWith(prefix() + ":")) {
          fsVerticleName = verticleName.substring(prefix().length() + 1);
        } else {
          fsVerticleName = verticleName;
        }

        if (context != null) {
          address = context.deploymentID();
          worker = context.isWorkerContext() || context.isMultiThreadedWorkerContext();
          // expose config
          if (context.config() != null) {
            engine.config(context.config());
          }
        } else {
          worker = false;
          address = null;
        }

        if (worker) {
          // in the case of being a worker we need to define an extra function in the global scope `postMessage`
          // define the postMessage function
          engine.eval(
            "(function (global) {\n" +
              "  global.postMessage = function (aMessage) {\n" +
              "    vertx.eventBus().send('" + address + ".in', JSON.stringify(aMessage));\n" +
              "  };\n" +
              "})(global || this);");
        }

        // this can take some time to load so it might block the event loop
        // this is usually not a issue as it is a one time operation
        self = engine.main(fsVerticleName);

        if (self != null) {
          if (worker) {
            // if it is a worker and there is a onmessage handler we need to bind it to the eventbus
            if (engine.hasMember(self, "onmessage")) {
              try {
                // if the worker has specified a onmessage function we need to bind it to the eventbus
                final Object JSON = engine.eval("JSON");

                vertx.eventBus().consumer(address + ".out", msg -> {
                  // parse the json back to the engine runtime type
                  Object json = engine.invokeMethod(JSON, "parse", msg.body());
                  // deliver it to the handler
                  engine.invokeMethod(self, "onmessage", json);
                });
              } catch (RuntimeException e) {
                startFuture.fail(e);
                return;
              }
            }
          } else {
            // if the main module exports 2 function we bind those to the verticle lifecycle
            if (engine.hasMember(self, "start")) {
              try {
                engine.enter();
                engine.invokeMethod(self, "start");
              } catch (RuntimeException e) {
                startFuture.fail(e);
                return;
              } finally {
                engine.leave();
              }
            }
          }
        }

        // worker initialization is complete
        startFuture.complete();
      }

      @Override
      public void stop(Future<Void> stopFuture) throws Exception {
        if (self != null) {
          if (engine.hasMember(self, "stop")) {
            try {
              engine.enter();
              engine.invokeMethod(self, "stop");
            } catch (RuntimeException e) {
              stopFuture.fail(e);
              return;
            } finally {
              // done!
              engine.leave();
            }
          }
        }
        // close the loader
        engine.close();
        stopFuture.complete();
      }
    };
  }
}
