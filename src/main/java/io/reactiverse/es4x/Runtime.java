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

import io.reactiverse.es4x.impl.graal.GraalRuntime;
import io.reactiverse.es4x.impl.nashorn.NashornRuntime;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public interface Runtime<T> {

  static Runtime getCurrent(Vertx vertx) {
    String rtName = System.getProperty("es4x.engine");
    // rt name takes precedence in the choice
    if (rtName == null) {
      String vmName = System.getProperty("java.vm.name");
      if (vmName != null && vmName.startsWith("GraalVM")) {
        rtName = "GraalJS";
      } else {
        rtName = "Nashorn";
      }
    }

    if (rtName.equalsIgnoreCase("GraalJS")) {
      System.setProperty("es4x.engine", "GraalJS");
      return new GraalRuntime(vertx);
    }

    if (rtName.equalsIgnoreCase("Nashorn")) {
      System.setProperty("es4x.engine", "Nashorn");
      return new NashornRuntime(vertx);
    }

    System.clearProperty("es4x.engine");
    throw new RuntimeException("Unsupported runtime: " + rtName);
  }

  /**
   * return the runtime name
   *
   * @return runtime name.
   */
  String name();

  /**
   * Bootstraps a Vert.x instance
   *
   * @param arguments arguments
   * @return a vertx instance
   */
  static Vertx vertx(Map<String, Object> arguments) {

    final VertxOptions options = arguments == null ? new VertxOptions() : new VertxOptions(new JsonObject(arguments));

    if (options.isClustered()) {
      final CountDownLatch latch = new CountDownLatch(1);

      final AtomicReference<Throwable> err = new AtomicReference<>();
      final AtomicReference<Vertx> holder = new AtomicReference<>();


      Vertx.clusteredVertx(options, ar -> {
        if (ar.failed()) {
          err.set(ar.cause());
          latch.countDown();
        } else {
          holder.set(ar.result());
          latch.countDown();
        }
      });

      try {
        latch.await();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }

      if (err.get() != null) {
        throw new RuntimeException(err.get());
      } else {
        return holder.get();
      }
    } else {
      return Vertx.vertx(options);
    }
  }

  /**
   * passes the given configuration to the runtime.
   *
   * @param config given configuration.
   */
  void config(final JsonObject config);

  /**
   * Require a module following the commonjs spec
   *
   * @param module a module
   * @return return the module
   */
  T require(String module);

  /**
   * Requires the main module as a commonjs module, the
   * module returned will be flagged as a main module.
   *
   * @param main the main module
   * @return the module
   */
  T main(String main);

  /**
   * Loads a JS Worker, meaning it will become a Vert.x Worker.
   *
   * @param main    the main entry script
   * @param address the eventbus address
   * @return the module
   */
  T worker(String main, String address);

  /**
   * Evals a given sript string.
   *
   * @param script string containing code.
   * @return returns the evaluation result.
   * @throws Exception on error
   */
  T eval(String script) throws Exception;

  /**
   * Evals a script literal. Script literals are hidden from the
   * chrome inspector loaded scripts.
   *
   * @param script string containing code.
   * @return returns the evaluation result.
   * @throws Exception on error
   */
  default T evalLiteral(CharSequence script) throws Exception {
    return eval(script.toString());
  }

  /**
   * Performs property lookups on a given evaluated object.
   *
   * @param thiz the evaluated object
   * @param key  the key to lookup
   * @return the value associated with the key
   */
  boolean hasMember(T thiz, String key);

  /**
   * Invokes a method on an evaluated object.
   *
   * @param thiz the evaluated object
   * @param method the method name to invoke
   * @param args the vararg arguments list
   * @return the call result
   */
  T invokeMethod(T thiz, String method, Object... args);

  /**
   * Invokes function on the global scope.
   *
   * @param function the function name to invoke
   * @param args the vararg arguments list
   * @return the call result
   */
  T invokeFunction(String function, Object... args);

  /**
   * Puts a value to the global scope.
   *
   * @param name the key to identify the value in the global scope
   * @param value the value to store.
   */
  void put(String name, Object value);

  /**
   * explicitly enter the script engine scope.
   */
  default void enter() {
  }

  /**
   * explicitly leave the script engine scope.
   */
  default void leave() {
  }

  /**
   * close the current runtime and shutdown all the engine related resources.
   */
  void close();
}
