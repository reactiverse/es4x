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

import io.reactiverse.es4x.impl.ESModuleIO;
import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import org.graalvm.polyglot.io.FileSystem;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;

public final class MJSVerticleFactory extends ESVerticleFactory {

  @Override
  public String prefix() {
    return "mjs";
  }

  @Override
  Verticle createVerticle(Runtime runtime, String fsVerticleName) {
    return new Verticle() {

      private Vertx vertx;

      @Override
      public Vertx getVertx() {
        return vertx;
      }

      @Override
      public void init(Vertx vertx, Context context) {
        this.vertx = vertx;
      }

      @Override
      public void start(Promise<Void> startFuture) throws Exception {
        final FileSystem fs = engine.fileSystem();

        try {
          final Path scriptPath = fs.parsePath(mainScript(fsVerticleName));
          // the main script buffer
          final Buffer buffer = vertx.fileSystem().readFileBlocking(scriptPath.toString());
          runtime.eval(
            // strip the shebang if present
            ESModuleIO.stripShebang(buffer.toString()),
            scriptPath.toString(),
            "application/javascript+module",
            false);
          startFuture.complete();
        } catch (InvalidPathException e) {
          startFuture.fail("File Not Found: " + fsVerticleName);
        } catch (RuntimeException e) {
          startFuture.fail(e);
        }
      }

      @Override
      public void stop(Promise<Void> stopFuture) {
        try {
          runtime.enter();
          runtime.emit("undeploy");
          runtime.leave();
          runtime.close();
          stopFuture.complete();
        } catch (RuntimeException e) {
          // done!
          runtime.leave();
          runtime.close();
          stopFuture.fail(e);
        }
      }
    };
  }
}
