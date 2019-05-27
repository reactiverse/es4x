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

import java.io.*;
import java.nio.file.Files;
import java.util.*;

import static io.reactiverse.es4x.commands.Helper.*;

@Name("init")
@Summary("Initializes the 'package.json' to work with ES4X.")
public class InitCommand extends DefaultCommand {

  @Override
  public void run() throws CLIException {
    try {

      final File file = new File(getCwd(), "package.json");

      if (!file.exists()) {
        // Load the file from the class path
        try (InputStream in = InitCommand.class.getClassLoader().getResourceAsStream("META-INF/es4x-commands/package.json")) {
          if (in == null) {
            fatal("Cannot load package.json template.");
          } else {
            Files.copy(in, file.toPath());
          }
        } catch (IOException e) {
          fatal(e.getMessage());
        }
      }

      Map npm = read(file);
      Map scripts = (Map) npm.get("scripts");

      if (scripts == null) {
        scripts = new LinkedHashMap();
        npm.put("scripts", scripts);
      }

      String main = (String) npm.get("main");
      if (main == null) {
        main = "index.js";
      }

      String test = main;
      if (test.endsWith(".js")) {
        test = test.substring(0, test.length() - 3) + ".test.js";
      }

      scripts.put("postinstall", "es4x install");
      scripts.put("start", "es4x-launcher");
      scripts.put("test", "es4x-launcher" + " test js:" + test);

      write(file, npm);

    } catch (IOException e) {
      fatal(e.getMessage());
    }
  }
}
