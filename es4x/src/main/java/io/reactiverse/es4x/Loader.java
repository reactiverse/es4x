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

public interface Loader<T> {

  String name();

  void config(final JsonObject config);

  T require(String main);

  T main(String main);

  T eval(String script) throws Exception;

  boolean hasMember(T thiz, String key);

  T invokeMethod(T thiz, String method, Object... args);

  T invokeFunction(String function, Object... args);

  void put(String name, Object value);

  default void enter() {
  }

  default void leave() {
  }

  void close();
}
