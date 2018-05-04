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
package io.reactiverse.es4x.graal.runtime;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import java.util.AbstractMap;
import java.util.concurrent.atomic.AtomicReference;

public final class VertxRuntime {

  public static void callMe(Object o ) {
    System.out.println(o.getClass());
  }

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
  public static synchronized void install(Context bindings) {
    // get a reference to the global object
    final Value global = bindings.eval("js", "(function () { return global; })").execute();
    assert global != null;

    // get a reference to vertx instance
    final Vertx vertx = global.getMember("vertx").as(Vertx.class);
    assert vertx != null;

//    // install JavaScript global functions
//    Globals.install(bindings);
//    // install the process object
//    Process.install(bindings);
//    // patch the JSON object to handle Vert.x JSON types
//    JSON.install(bindings);
//    // install the console object
//    Console.install(bindings);
//    // install Object.assign (required by react.js for example)
//    Polyfill.install(bindings);

    // get a reference to the "JSON" object
    final Value json = global.getMember("JSON");
    assert json != null;

    // get a reference to the "JSON" object
    final Value java = global.getMember("Java");
    assert java != null;

    // register a default codec to allow JSON messages directly from nashorn to the JVM world
    final AtomicReference holder = new AtomicReference();
    bindings.eval("js", "(function (fn) { fn({}); })").execute((Handler) holder::set);

    vertx.eventBus().unregisterDefaultCodec(holder.get().getClass());
    vertx.eventBus().registerDefaultCodec(holder.get().getClass(), new JSObjectMessageCodec<>(json, bindings));
  }
}
