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

import io.reactiverse.es4x.impl.graal.GraalLoader;
import io.reactiverse.es4x.impl.nashorn.NashornLoader;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public interface Loader<T> {

  static Loader create(Vertx vertx) {
    String rtName = System.getProperty("es4x.engine");
    // rt name takes precedence in the choice
    if (rtName == null) {
      String vmName = System.getProperty("java.vm.name");
      if (vmName != null && vmName.startsWith("GraalVM")) {
        rtName = "GraalVM";
      }
    }

    if (rtName != null && rtName.equalsIgnoreCase("GraalVM")) {
      // attempt to load graal loader
      try {
        System.out.println("WARNING: Trying to use GraalVM...");
        return new GraalLoader(vertx);
      } catch (RuntimeException e) {
        // Ignore...
        System.out.println("ERROR: Failed start GraalVM");
      }
    }
    // fallback (nashorn)
    return new NashornLoader(vertx);
  }

  String name();

  void config(final JsonObject config);

  T require(String main);

  T main(String main);

  T eval(String script) throws Exception;

  T invokeMethod(Object thiz, String method, Object... args);

  T invokeFunction(String function, Object... args);

  void put(String name, Object value);

  default void enter() {
  }

  default void leave() {
  }

  void close();
}
