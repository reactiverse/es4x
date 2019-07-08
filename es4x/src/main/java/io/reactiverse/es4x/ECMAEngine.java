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

import io.reactiverse.es4x.impl.graal.GraalEngine;
import io.vertx.core.Vertx;
import org.graalvm.polyglot.io.FileSystem;

import java.util.regex.Pattern;

public interface ECMAEngine {

  static ECMAEngine newEngine(Vertx vertx) {
    return new GraalEngine(vertx);
  }

  default Pattern[] allowedHostClassFilters() {
    String hostClassFilter = System.getProperty("es4x.host.class.filter", System.getenv("ES4XHOSTCLASSFILTER"));
    if (hostClassFilter == null || hostClassFilter.length() == 0) {
      return null;
    }

    String[] glob = hostClassFilter.split(",");
    Pattern[] patterns = new Pattern[glob.length];
    for (int i = 0; i < patterns.length; i++) {
      boolean negate = false;
      String regex = glob[i];

      if (glob[i].charAt(0) == '!') {
        negate = true;
        regex = glob[i].substring(1);
      }

      patterns[i] = Pattern.compile(
        (negate ? "^(?!" : "") +
          regex
            // replace dots
            .replace(".", "\\.")
            // replace stars
            .replace("*", "[^\\.]+")
            // replace double stars
            .replace("[^\\.]+[^\\.]+", ".*")
            // replace ?
            .replace("?", "\\w") +
          (negate ? "$).*$" : "")
      );
    }

    return patterns;
  }

  /**
   * return a new context for this engine.
   *
   * @return new context.
   */
  Runtime newContext();

  FileSystem fileSystem();
}
