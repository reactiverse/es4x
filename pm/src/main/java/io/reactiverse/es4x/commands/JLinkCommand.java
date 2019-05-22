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
import io.vertx.core.cli.annotations.Description;
import io.vertx.core.cli.annotations.Name;
import io.vertx.core.cli.annotations.Option;
import io.vertx.core.cli.annotations.Summary;
import io.vertx.core.spi.launcher.DefaultCommand;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static io.reactiverse.es4x.commands.Helper.*;

@Name("jlink")
@Summary("Creates a slim runtime (requires java >= 11).")
public class JLinkCommand extends DefaultCommand {

  private String launcher;

  @Option(longName = "launcher", shortName = "l")
  @Description("Will always install a basic runtime in the current working dir.")
  public void setLauncher(String launcher) {
    this.launcher = launcher;
  }

  @Override
  public void run() throws CLIException {

    try {
      final double version = Double.parseDouble(System.getProperty("java.specification.version"));
      if (version >= 11) {

        final File json = new File(getCwd(), "package.json");
        if (!json.exists()) {
          fatal("No 'package.json' in the current working directory.");
        }

        if (launcher == null) {
          Map npm = read(json);
          if (npm.containsKey("name")) {
            launcher = (String) npm.get("name");
          } else {
            fatal("'package.json' doesn't contain a 'name' property.");
          }
        }

        // Collect the jmods used in the application
        String mods = exec(javaHomePrefix() + "jdeps", "--module-path", "node_modules/.lib", "--print-module-deps", "node_modules/.bin/" + launcher + ".jar");
        // trim any new line
        mods = mods.replaceAll("\r?\n", "");
        // enable jvmci if supported
        String modules = exec(javaHomePrefix() + "java", "--list-modules");
        if (modules.contains("jdk.internal.vm.ci")) {
          if (!mods.contains("jdk.internal.vm.ci")) {
            // add the jvmci module
            mods = mods + ",jdk.internal.vm.ci";
          }
          // clean up
          mods = mods.replaceAll(",,", ",");
        }
        // jlink
        exec(javaHomePrefix() + "jlink", "--no-header-files", "--no-man-pages", "--compress=2", "-strip-debug", "--add-modules", mods, "--output", "jre");
      } else {
        fatal("Your JDK version does not support jlink (< 11)");
      }
    } catch (IOException | InterruptedException e) {
      fatal(e.getMessage());
    }
  }
}
