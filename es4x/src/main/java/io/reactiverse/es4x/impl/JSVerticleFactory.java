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
package io.reactiverse.es4x.impl;

import io.reactiverse.es4x.ESVerticleFactory;
import io.reactiverse.es4x.Runtime;
import io.vertx.core.*;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

public final class JSVerticleFactory extends ESVerticleFactory {

  @Override
  public String prefix() {
    return "js";
  }

  @Override
  protected Verticle createVerticle(Runtime runtime, String fsVerticleName) {

    final Value module;
    // we need to setup the script loader
    module = runtime.eval(
      Source.newBuilder("js", JSVerticleFactory.class.getResource("/io/reactiverse/es4x/jvm-npm.js")).buildLiteral()
    );

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
      public void start(Promise<Void> startFuture) {
        final String address;
        final boolean worker;
        final Value self;

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
          if (worker) {
            self = module.invokeMember("runWorker", mainScript(fsVerticleName), address);
          } else {
            self = module.invokeMember("runMain", mainScript(fsVerticleName));
          }
        } catch (RuntimeException e) {
          startFuture.fail(e);
          return;
        }

        if (self != null) {
          if (worker) {
            // if it is a worker and there is a onmessage handler we need to bind it to the eventbus
            if (self.hasMember("onmessage")) {
              try {
                vertx.eventBus().consumer(address + ".out", msg -> {
                  // deliver it to the handler
                  self.invokeMember("onmessage", msg.body());
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
      public void stop(Promise<Void> stopFuture) {
        final Promise<Void> wrapper = Promise.promise();

        try {
          int arity = runtime.emit("undeploy", wrapper);
          final Future<Void> future = wrapper.future();

          future.onComplete(undeploy -> {
            stopFuture.handle(undeploy);
            runtime.close();
          });

          if (arity == 0) {
            wrapper.complete();
          }
        } catch (RuntimeException e) {
          wrapper.fail(e);
        }
      }
    };
  }
}
