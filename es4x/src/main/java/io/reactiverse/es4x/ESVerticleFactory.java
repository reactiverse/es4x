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
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.spi.VerticleFactory;
import org.graalvm.polyglot.Source;

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

  @Override
  public void init(final Vertx vertx) {
    synchronized (this) {
      if (engine == null) {
        this.engine = new ECMAEngine(vertx);
      } else {
        throw new IllegalStateException("Engine already initialized");
      }
    }
  }

  @Override
  public int order() {
    return -1;
  }

  /**
   * Create a runtime. Use the method {@link ECMAEngine#newContext(Source...)} to create the runtime.
   * <p>
   * This method allows the customization of the runtime (which initial scripts will be run, and add/remove
   * objects to the global scope).
   *
   * @param engine the graaljs engine.
   * @return the configured runtime.
   */
  protected Runtime createRuntime(ECMAEngine engine) {
    return engine.newContext(
      Source.newBuilder("js", ESVerticleFactory.class.getResource("polyfill/json.js")).buildLiteral(),
      Source.newBuilder("js", ESVerticleFactory.class.getResource("polyfill/global.js")).buildLiteral(),
      Source.newBuilder("js", ESVerticleFactory.class.getResource("polyfill/date.js")).buildLiteral(),
      Source.newBuilder("js", ESVerticleFactory.class.getResource("polyfill/console.js")).buildLiteral(),
      Source.newBuilder("js", ESVerticleFactory.class.getResource("polyfill/promise.js")).buildLiteral(),
      Source.newBuilder("js", ESVerticleFactory.class.getResource("polyfill/worker.js")).buildLiteral(),
      Source.newBuilder("js", ESVerticleFactory.class.getResource("polyfill/arraybuffer.js")).buildLiteral()
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

  @Override
  public final Verticle createVerticle(String verticleName, ClassLoader classLoader) {

    final String fsVerticleName = VerticleFactory.removePrefix(verticleName);
    final Runtime runtime = createRuntime(engine);

    if (">".equals(fsVerticleName)) {
      return new REPLVerticle(runtime);
    }

    return createVerticle(runtime, fsVerticleName);
  }

  /**
   * Utility to extract the main script name from the command line arguments.
   *
   * @param fsVerticleName the verticle name.
   * @return the normalized name.
   */
  protected final String mainScript(String fsVerticleName) {
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
}
