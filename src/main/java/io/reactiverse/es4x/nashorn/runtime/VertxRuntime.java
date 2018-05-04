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
package io.reactiverse.es4x.nashorn.runtime;

import io.vertx.core.Vertx;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.util.Map;

public final class VertxRuntime {

  private VertxRuntime() {
    throw new RuntimeException("Should not be instantiated");
  }

  /**
   * This is the main installer for the Vert.x Runtime extensions to the default Nashorn
   * script engine.
   *
   * The argument is a map for the sole purpose that it can allow the usage from within
   * the engine itself (via a running script) or during the engine bootstrap with the
   * usage of the engine bindings object.
   *
   * @param bindings the global engine bindings. The global object **must** contain a
   *                 property named <pre>global</pre> which is a JSObject. And a property
   *                 named <pre>vertx</pre>.
   */
  public static synchronized void install(Map<String, Object> bindings) {
    // get a reference to vertx instance
    final Vertx vertx = (Vertx) bindings.get("vertx");
    assert vertx != null;

    // get a reference to the global object
    final JSObject global = (JSObject) bindings.get("global");
    assert global != null;

    // get a reference to the "JSON" object
    final JSObject json = (JSObject) global.getMember("JSON");
    assert json != null;

    // get a reference to the "JSON" object
    final JSObject java = (JSObject) global.getMember("Java");
    assert java != null;

    // register a default codec to allow JSON messages directly from nashorn to the JVM world
    vertx.eventBus().unregisterDefaultCodec(ScriptObjectMirror.class);
    vertx.eventBus().registerDefaultCodec(ScriptObjectMirror.class, new JSObjectMessageCodec(json, java));
  }
}
