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
import io.vertx.core.json.JsonObject;

public interface Runtime<T> {

  static Runtime getCurrent(Vertx vertx) {
    String rtName = System.getProperty("es4x.engine");
    // rt name takes precedence in the choice
    if (rtName == null) {
      String vmName = System.getProperty("java.vm.name");
      if (vmName != null && vmName.startsWith("GraalVM")) {
        // we're running on a GraalVM JDK
        rtName = "graaljs";
      } else {
        // it is not GraalVM JDK but if it is JDK11 and the graal components
        // are available graaljs can be available too

        try {
          double spec = Double.parseDouble(System.getProperty("java.specification.version"));
          if (spec < 11) {
            rtName = "nashorn";
          }
        } catch (NumberFormatException nfe) {
          rtName = "nashorn";
        }
      }
    } else {
      rtName = rtName.toLowerCase();
    }

    if (rtName == null || "graaljs".equals(rtName)) {
      try {
        System.setProperty("es4x.engine", "graaljs");
        return new GraalRuntime(vertx);
      } catch (NoClassDefFoundError | IllegalStateException e) {
        // in the case classes are missing, the graal bits are missing
        // so fallback to Nashorn

        // we could also have an illegal state when the graal is missing
        // the language bits, in that case also try to fallback to nashorn
        rtName = "nashorn";
      }
    }

    if ("nashorn".equals(rtName)) {
      System.setProperty("es4x.engine", rtName);
      return new NashornRuntime(vertx);
    }

    // no nashorn or graal available on the system!
    throw new RuntimeException("Unsupported runtime: " + rtName);
  }

  /**
   * return the runtime name
   *
   * @return runtime name.
   */
  String name();

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
   * @param literal literals are non listed on debug sessions
   * @return returns the evaluation result.
   * @throws Exception on error
   */
  T eval(String script, boolean literal) throws Exception;

  /**
   * Evals a given sript string.
   *
   * @param script string containing code.
   * @return returns the evaluation result.
   * @throws Exception on error
   */
  default T eval(String script) throws Exception {
    return eval(script, false);
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
