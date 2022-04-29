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
import io.vertx.core.impl.ContextInternal;
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
      public void start(Promise<Void> startPromise) {
        final String address;
        final boolean worker;

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

        try {
          if (worker) {
            runtime.put("self", runtime.eval(Source.create("js", "this")));
            setupVerticleMessaging(runtime, vertx, address);
          } else {
            runtime.put("global", runtime.eval(Source.create("js", "this")));
          }

          try {
            module.invokeMember("runMain", fsVerticleName);
            waitFor(runtime, (ContextInternal) context, "deploy")
              .onComplete(startPromise);
          } catch (RuntimeException e) {
            startPromise.fail(e);
          }
        } catch (RuntimeException e) {
          startPromise.fail(e);
        }
      }

      @Override
      public void stop(Promise<Void> stopPromise) {
        // call the undeploy if available
        waitFor(runtime, (ContextInternal) context,  "undeploy")
          .onComplete(stopPromise)
          .onSuccess(v -> {
            try {
              runtime.close();
            } catch (RuntimeException e) {
              e.printStackTrace();
            }
          });
      }
    };
  }

  @Override
  protected String[] defaultExtensions() {
    return new String[] {
      ".js"
    };
  }
}
