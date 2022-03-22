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
import io.vertx.core.buffer.Buffer;
import io.vertx.core.impl.ContextInternal;
import org.graalvm.polyglot.Source;

import java.io.File;
import java.nio.file.InvalidPathException;

public final class MJSVerticleFactory extends ESVerticleFactory {

  @Override
  public String prefix() {
    return "mjs";
  }

  @Override
  protected Verticle createVerticle(Runtime runtime, String fsVerticleName) {
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
            // the main script buffer
            final Buffer buffer = vertx.fileSystem().readFileBlocking(fsVerticleName);
            final Source source = Source
              .newBuilder("js", new File(fsVerticleName))
              // strip the shebang if present
              .content(ESModuleIO.stripShebang(buffer.toString()))
              .cached(true)
              .interactive(false)
              .mimeType("application/javascript+module")
              .buildLiteral();

            runtime.eval(source);
            waitFor(runtime, (ContextInternal) context,"deploy")
              .onComplete(startPromise);
          } catch (InvalidPathException e) {
            startPromise.fail("File Not Found: " + fsVerticleName);
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
        waitFor(runtime, (ContextInternal) context, "undeploy")
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
      ".mjs",
      ".js"
    };
  }
}
