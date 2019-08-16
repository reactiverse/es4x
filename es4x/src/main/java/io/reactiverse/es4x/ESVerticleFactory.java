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

public abstract class ESVerticleFactory implements VerticleFactory {

  protected ECMAEngine engine;

  @Override
  public void init(final Vertx vertx) {
    synchronized (vertx) {
      this.engine = ECMAEngine.newEngine(vertx);
    }
  }

  @Override
  public int order() {
    return -1;
  }

  abstract Verticle createVerticle(Runtime runtime, String fsVerticleName);

  @Override
  public Verticle createVerticle(String verticleName, ClassLoader classLoader) {

    final String fsVerticleName = VerticleFactory.removePrefix(verticleName);
    final Runtime runtime = engine.newContext();

    if (">".equals(fsVerticleName)) {
      return new REPLVerticle(runtime);
    }

    return createVerticle(runtime, fsVerticleName);
  }

  public String mainScript(String fsVerticleName) {
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
