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
package io.reactiverse.es4x.graal;

import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import org.graalvm.polyglot.Value;

public class VerticleFactory implements io.vertx.core.spi.VerticleFactory {

  private Vertx vertx;

  @Override
  public void init(Vertx vertx) {
    this.vertx = vertx;
  }

  @Override
  public String prefix() {
    return "graal.js";
  }

  @Override
  public Verticle createVerticle(String verticleName, ClassLoader classLoader) throws Exception {

    final Loader loader;

    synchronized (this) {
      // create a new CommonJS loader
      loader = new Loader(vertx);
    }

    return new Verticle() {

      private Vertx vertx;
      private Context context;

      private Value self;
      private Value stop;

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
      public void start(Future<Void> startFuture) {
        // expose config
        if (context != null && context.config() != null) {
          loader.config(context.config());
        }

        final String fsVerticleName;

        // extract prefix if present
        if (verticleName.startsWith(prefix() + ":")) {
          fsVerticleName = verticleName.substring(prefix().length() + 1);
        } else {
          fsVerticleName = verticleName;
        }

        // the initial script is a relative file, not necessarily a module
        self = loader.main(fsVerticleName);

        // if the main module exports 2 function we bind those to the verticle lifecycle
        if (self != null) {
          if (self.hasMember("start")) {
            try {
              self.getMember("start").execute();
            } catch (RuntimeException e) {
              startFuture.fail(e);
              return;
            }
          }

          if (self.hasMember("stop")) {
            this.stop = self.getMember("stop");
          }
        }
        // ready!
        startFuture.complete();
      }

      @Override
      public void stop(Future<Void> stopFuture) {
        if (stop != null) {
          try {
            stop.execute();
          } catch (RuntimeException e) {
            stopFuture.fail(e);
          }
        }
        // done!
        stopFuture.complete();
      }
    };
  }
}
