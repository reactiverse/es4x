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

import java.util.Map;

public interface Runtime<T> {

  static Runtime create() {
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
        return new GraalRuntime();
      } catch (RuntimeException e) {
        // Ignore...
      }
    }
    // fallback (nashorn)
    return new NashornRuntime();
  }

  String name();

  Vertx vertx(Object object, T JSON, Map<String, Object> arguments);
}
