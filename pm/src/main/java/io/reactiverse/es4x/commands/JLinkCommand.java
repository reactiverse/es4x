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
package io.reactiverse.es4x.commands;

import io.vertx.core.cli.CLIException;
import io.vertx.core.cli.annotations.Name;
import io.vertx.core.cli.annotations.Summary;
import io.vertx.core.spi.launcher.DefaultCommand;

import java.io.IOException;

import static io.reactiverse.es4x.commands.Helper.*;

@Name("jlink")
@Summary("Creates a slim runtime (requires java >= 11).")
public class JLinkCommand extends DefaultCommand {

  @Override
  public void run() throws CLIException {

    try {
      final double version = Double.parseDouble(System.getProperty("java.specification.version"));
      if (version >= 11) {
        // TODO: read the main attribute from package json to know the launcher name

        // Collect the jmods used in the application
        String mods = exec(javaHomePrefix() + "jdeps", "--module-path", "node_modules/.libs", "--print-module-deps", "node_modules/.bin/launcher.jar");

        // remove nashorn is jvmci is supported
        String modules = exec(javaHomePrefix() + "java", "--list-modules");
        if (modules.contains("jdk.internal.vm.ci")) {
          // remove nashorn
          mods = mods.replace("jdk.scripting.nashorn", "");
          mods = mods.replace("jdk.dynalink", "");
          mods = mods.replaceAll(",,", ",");
          mods = mods + ",jdk.internal.vm.ci";
        }

        // jlink
        exec(javaHomePrefix() + "jlink", "--no-header-files", "--no-man-pages", "--compress=2", "-strip-debug", "--add-modules", mods, "--output", "/jre");
      } else {
        err("Your JDK version does not support jlink (< 11)");
      }
    } catch (IOException | InterruptedException e) {
      err(e.getMessage());
    }
  }
}
