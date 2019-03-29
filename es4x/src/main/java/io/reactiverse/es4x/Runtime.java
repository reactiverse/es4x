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

import io.vertx.core.json.JsonObject;

public interface Runtime<T> {

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
  default T eval(String script, boolean literal) throws Exception {
    return eval(script, "<eval>", literal);
  }

  /**
   * Evals a given sript string.
   *
   * @param script string containing code.
   * @param name string containing name of the script (e.g.: the filename).
   * @param literal literals are non listed on debug sessions
   * @return returns the evaluation result.
   * @throws Exception on error
   */
  T eval(String script, String name, String contentType, boolean literal) throws Exception;

  /**
   * Evals a given sript string.
   *
   * @param script string containing code.
   * @param name string containing name of the script (e.g.: the filename).
   * @param literal literals are non listed on debug sessions
   * @return returns the evaluation result.
   * @throws Exception on error
   */
  default T eval(String script, String name, boolean literal) throws Exception {
    if (name.endsWith(".mjs")) {
      return eval(script, name, "application/javascript+module", false);
    } else {
      return eval(script, name, "application/javascript", false);
    }
  }

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
