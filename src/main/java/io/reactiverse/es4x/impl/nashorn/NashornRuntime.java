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
package io.reactiverse.es4x.impl.nashorn;

import io.reactiverse.es4x.Loader;
import io.reactiverse.es4x.Runtime;
import io.vertx.core.Vertx;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

public class NashornRuntime implements Runtime<Object> {

  @Override
  public String name() {
    return "Nashorn";
  }

  @Override
  public Runtime<Object> registerCodec(Vertx vertx) {
    // register a default codec to allow JSON messages directly from GraalVM to the JVM world
    vertx.eventBus()
      .unregisterDefaultCodec(ScriptObjectMirror.class)
      .registerDefaultCodec(ScriptObjectMirror.class, new JSObjectMessageCodec());

    return this;
  }

  /**
   * Returns a module loader for the given runtime.
   *
   * @param vertx
   * @return loader
   */
  @Override
  public Loader<Object> loader(Vertx vertx) {
    return new NashornLoader(vertx);
  }
}
