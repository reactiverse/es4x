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
import io.vertx.core.VertxOptions;

import java.io.File;
import java.util.function.Consumer;

public final class ES4X extends Launcher {

  @Override
  public void beforeStartingVertx(VertxOptions options) {
    processProperty("inspect", inspect -> {
      System.setProperty("polyglot.inspect", inspect);
      System.setProperty("polyglot.inspect.Suspend", "false");
      options.setBlockedThreadCheckInterval(1000000);
    });

    processProperty("inspect-brk", inspect -> {
      System.setProperty("polyglot.inspect", inspect);
      System.setProperty("polyglot.inspect.Suspend", "true");
      options.setBlockedThreadCheckInterval(1000000);
    });
  }

  /**
   * Main entry point.
   *
   * @param args the user command line arguments.
   */
  public static void main(String... args) {

    final ES4X launcher = new ES4X();

    if (args != null && args.length > 0 && "--compgen".equals(args[0])) {
      String prefix = args.length == 2 ? args[1] : null;
      for (String name : launcher.commandByName.keySet()) {
        if (prefix == null || name.startsWith(prefix)) {
          System.out.println(name);
        }
      }
      System.exit(3);
      return;
    }

    // small behavior change
    if (args != null && args.length == 1) {
      // arg[0] is a file (or directory)
      boolean isFileOrDir;

      try {
        // script can be either a file or directory
        isFileOrDir = new File(args[0]).exists();
      } catch (SecurityException e) {
        // a security policy is preventing this file
        // from being read by the virtual machine,
        // fallback to the default behavior
        isFileOrDir = false;
      }

      if (isFileOrDir) {
        // we will assume a js command
        try {
          launcher.execute("run", args);
        } catch (NoClassDefFoundError e) {
          System.err.println("'node_modules' jars missing and/or wrong versions. Removing 'node_modules' may solve the problem.");
          System.exit(1);
        }
        return;
      }
    }

    // default behavior
    try {
      launcher.dispatch(args);
    } catch (NoClassDefFoundError e) {
      System.err.println("'node_modules' jars missing and/or wrong versions. Removing 'node_modules' may solve the problem.");
      System.exit(1);
    }
  }

  private static void processProperty(String name, Consumer<String> consumer) {

    if (System.getProperties().containsKey(name)) {
      try {
        String addr = System.getProperty(name);
        if ("".equals(addr)) {
          addr = "9229";
        }
        consumer.accept(addr);
      } catch (RuntimeException e) {
        System.err.println(e.getMessage());
        System.exit(1);
      }
    }
  }
}
