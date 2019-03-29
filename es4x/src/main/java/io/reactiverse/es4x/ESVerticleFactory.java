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

  private ECMAEngine engine;

  @Override
  public void init(Vertx vertx) {
    this.engine = ECMAEngine.newEngine(vertx);
  }

  @Override
  public int order() {
    return -1;
  }

  abstract Verticle createVerticle(Runtime runtime, String fsVerticleName);

  @Override
  public Verticle createVerticle(String verticleName, ClassLoader classLoader) {

    final Runtime runtime;
    final String fsVerticleName = VerticleFactory.removePrefix(verticleName);

    synchronized (this) {
      runtime = engine.newContext();
    }

    if (">".equals(fsVerticleName)) {
      return new REPLVerticle(runtime);
    }

    return createVerticle(runtime, fsVerticleName);
  }
}
