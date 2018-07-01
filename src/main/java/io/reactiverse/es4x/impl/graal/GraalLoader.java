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
package io.reactiverse.es4x.impl.graal;

import io.reactiverse.es4x.Loader;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class GraalLoader implements Loader<Value> {

  private final Context context;

  private static final String INSTALL_GLOBAL = "(function(k, v) global[k] = v)";
  private static final String UNINSTALL_GLOBAL = "(function(k) delete global[k])";

  // **INFO**: This is disabled until graal native images support passing object from Java to JS
  // get the type from the system properties, if missing attempt to get it from
  // the engine at runtime (however this doesn't work for native images)
  private static final String EVENTBUS_JSOBJECT_AOT_CLASS = System.getProperty("es4x.eventbus.jsobject.aot.class");

  public GraalLoader(final Vertx vertx) {
    this(
      vertx,
      // create the default context
      Context
        .newBuilder("js")
        .allowHostAccess(true)
        .allowCreateThread(true)
        .build()
    );
  }

  public GraalLoader(final Vertx vertx, Context context) {
    this.context = context;

    // remove the exit and quit functions
    context.eval("js", UNINSTALL_GLOBAL).execute("exit");
    context.eval("js", UNINSTALL_GLOBAL).execute("quit");
    // add vertx as a global
    context.eval("js", INSTALL_GLOBAL).execute("vertx", vertx);

    final AtomicReference<Class<?>> holder = new AtomicReference<>();

    //   **INFO**: This is disabled until graal native images support passing object from Java to JS
    if (EVENTBUS_JSOBJECT_AOT_CLASS == null) {
      final Consumer callback = value -> holder.set(value.getClass());
      context.eval("js", "(function (fn) { fn({}); })").execute(callback);
    } else {
      try {
        holder.set(Class.forName(EVENTBUS_JSOBJECT_AOT_CLASS));
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
    }

    // register a default codec to allow JSON messages directly from GraalVM to the JVM world
    vertx.eventBus()
      .unregisterDefaultCodec(holder.get())
      .registerDefaultCodec(holder.get(), new JSObjectMessageCodec<>(context.eval("js", "JSON")));

    final Value load = context.eval("js", "load");
    // add polyfills
    load.execute("classpath:io/reactiverse/es4x/polyfill/json.js");
    load.execute("classpath:io/reactiverse/es4x/polyfill/global.js");
    load.execute("classpath:io/reactiverse/es4x/polyfill/console.js");
    load.execute("classpath:io/reactiverse/es4x/polyfill/promise.js");
    // install the commonjs loader
    load.execute("classpath:io/reactiverse/es4x/jvm-npm.js");
  }

  @Override
  public String name() {
    return "GraalVM";
  }

  @Override
  public void config(final JsonObject config) {
    if (config != null) {
      // add vertx as a global
      context.eval("js", INSTALL_GLOBAL).execute("config", config.getMap());
    }
  }

  @Override
  public Value require(String main) {
    return context.eval("js", "require").execute(main);
  }

  @Override
  public Value main(String main) {
    // patch the main path to be a relative path
    if (!main.startsWith("./") && !main.startsWith("/")) {
      main = "./" + main;
    }
    // invoke the main script
    return require(main);
  }

  @Override
  public Value eval(String script) {
    return context.eval("js", script);
  }

  @Override
  public Value invokeMethod(Object thiz, String method, Object... args) {
    if (thiz instanceof Value) {
      Value fn = ((Value) thiz).getMember(method);
      if (fn != null && !fn.isNull()) {
        return fn.execute(args);
      }
    }
    return null;
  }

  @Override
  public Value invokeFunction(String function, Object... args) {
    return context.eval("js", function).execute(args);
  }

  @Override
  public void put(String name, Object value) {
    context.getBindings("js").putMember(name, value);
  }

  @Override
  public void close() {
    context.close();
  }

  @Override
  public void enter() {
    context.enter();
  }

  @Override
  public void leave() {
    context.leave();
  }

  public Engine getEngine() {
    return context.getEngine();
  }

  public Value eval(Source source) {
    return context.eval(source);
  }
}
