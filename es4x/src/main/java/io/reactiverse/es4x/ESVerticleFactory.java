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

import io.reactiverse.es4x.impl.REPLVerticle;
import io.reactiverse.es4x.impl.StructuredClone;
import io.reactiverse.es4x.impl.VertxFileSystem;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.spi.VerticleFactory;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.io.FileSystem;
import org.graalvm.polyglot.proxy.ProxyExecutable;

import java.io.IOException;
import java.util.concurrent.Callable;

import static io.reactiverse.es4x.impl.Utils.getManifestAttribute;
import static io.reactiverse.es4x.impl.Utils.toNixPath;

/**
 * An abstract verticle factory for EcmaScript verticles. All factories can extend
 * this class and only need to implement the 2 abstract methods:
 *
 * <ul>
 *  <li>{@link #createRuntime(ECMAEngine)} to create the runtime where scripts will run</li>
 *  <li>{@link #createVerticle(Runtime, String)} to create the verticle that wraps the script</li>
 * </ul>
 */
public abstract class ESVerticleFactory implements VerticleFactory {

  protected ECMAEngine engine;
  private FileSystem fileSystem;

  @Override
  public synchronized void init(final Vertx vertx) {
    if (engine == null) {
      try {
        this.fileSystem = new VertxFileSystem(vertx, getManifestAttribute("Import-Map"), defaultExtensions());
      } catch (IOException e) {
        throw new IllegalStateException("Failed to initialize the file system", e);
      }
      this.engine = new ECMAEngine(vertx);
    } else {
      throw new IllegalStateException("Engine already initialized");
    }
  }

  @Override
  public int order() {
    return -1;
  }

  /**
   * Create a runtime. Use the method {@link ECMAEngine#newContext(FileSystem, Source...)} to create the runtime.
   * <p>
   * This method allows the customization of the runtime (which initial scripts will be run, and add/remove
   * objects to the global scope).
   *
   * @param engine the graaljs engine.
   * @return the configured runtime.
   */
  protected Runtime createRuntime(ECMAEngine engine) {
    return engine.newContext(
      fileSystem,
      Source.newBuilder("js", ESVerticleFactory.class.getResource("polyfill/global.js")).buildLiteral(),
      Source.newBuilder("js", ESVerticleFactory.class.getResource("polyfill/date.js")).buildLiteral(),
      Source.newBuilder("js", ESVerticleFactory.class.getResource("polyfill/console.js")).buildLiteral(),
      Source.newBuilder("js", ESVerticleFactory.class.getResource("polyfill/worker.js")).buildLiteral(),
      Source.newBuilder("js", ESVerticleFactory.class.getResource("polyfill/arraybuffer.js")).buildLiteral(),
      Source.newBuilder("js", ESVerticleFactory.class.getResource("polyfill/async-error.js")).buildLiteral()
    );
  }

  /**
   * Create a vertx verticle that wraps the script, it will use the runtime configured in the
   * {@link #createRuntime(ECMAEngine)} method.
   *
   * @param fsVerticleName the name as provided during the application initialization.
   * @param runtime        the runtime created before.
   * @return the configured verticle.
   */
  protected abstract Verticle createVerticle(Runtime runtime, String fsVerticleName);

  /**
   * Declares the default extensions this verticle factory will use when looking up for modules without extension on the
   * filesystem.
   *
   * @return array of string in the format {@code .EXTENSION}
   */
  protected abstract String[] defaultExtensions();

  @Override
  public final void createVerticle(String verticleName, ClassLoader classLoader, Promise<Callable<Verticle>> promise) {

    final String strippedName = toNixPath(VerticleFactory.removePrefix(verticleName));

    if (">".equals(strippedName)) {
      promise.complete(() -> {
        // Runtime needs to be initialized here to ensure
        // we are on the right context thread for the graaljs engine
        final Runtime runtime = createRuntime(engine);
        return new REPLVerticle(runtime);
      });
    } else {
      promise.complete(() -> {
        // Runtime needs to be initialized here to ensure
        // we are on the right context thread for the graaljs engine
        final Runtime runtime = createRuntime(engine);
        return createVerticle(runtime, mainScript(strippedName));
      });
    }
  }

  protected void setupVerticleMessaging(Runtime runtime, Vertx vertx, String address) {
    final Value undefined = runtime.eval("[undefined]").getArrayElement(0);

    // workers will follow the browser semantics, they will have an extra global "postMessage"
    runtime.put("postMessage", (ProxyExecutable) arguments -> {
      // a shallow copy of the first argument is to be sent over the eventbus,
      // JS specific types are to be converted to Java types for better
      // polyglot support
      vertx.eventBus()
        .send(
          address + ".in",
          StructuredClone.cloneObject(arguments[0]));

      return undefined;
    });

    // if it is a worker and there is a onmessage handler we need to bind it to the eventbus
    vertx.eventBus().consumer(address + ".out", msg -> {
      final Value onmessage = runtime.get("onmessage");
      if (onmessage != null && onmessage.canExecute()) {
        // deliver it
        onmessage.executeVoid(msg.body());
      }
    });
  }

  /**
   * Utility to extract the main script name from the command line arguments.
   *
   * @param fsVerticleName the verticle name.
   * @return the normalized name.
   */
  private String mainScript(String fsVerticleName) {
    String main = fsVerticleName;

    if (fsVerticleName.equals(".") || fsVerticleName.equals("..")) {
      // invoke the main script
      main = fsVerticleName + "/";
    } else {
      // patch the main path to be a relative path
      if (!fsVerticleName.startsWith("./") && !fsVerticleName.startsWith("/")) {
        main = "./" + fsVerticleName;
      }
    }

    return main;
  }

  protected final Future<Void> waitFor(Runtime runtime, String callback) {
    final Promise<Void> wrapper = Promise.promise();
    try {
      if (runtime.emit(callback, wrapper) == 0) {
        wrapper.complete();
      }
    } catch (RuntimeException e) {
      wrapper.fail(e);
    }

    return wrapper.future();
  }

  /**
   * Close the factory. The implementation must release all resources.
   */
  @Override
  public void close() {
    if (engine != null) {
      engine.close();
    }
  }
}
