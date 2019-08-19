/*
 * Copyright 2019 Red Hat, Inc.
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

import io.vertx.core.Launcher;

import java.io.File;

public final class ES4X extends Launcher {

  /**
   * Main entry point.
   *
   * @param args the user command line arguments.
   */
  public static void main(String... args) {

    final ES4X launcher = new ES4X();

    // small behavior change
    if (args.length == 1) {
      File script = new File(args[0]);
      // script can be either a file or directory
      if (script.exists()) {
        // we will assume a js command
        launcher.execute("run", args);
        return;
      }
    }

    // default behavior
    launcher.dispatch(args);
  }
}
