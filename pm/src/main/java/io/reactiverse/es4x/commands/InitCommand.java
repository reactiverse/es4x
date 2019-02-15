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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.vertx.core.cli.CLIException;
import io.vertx.core.cli.annotations.Name;
import io.vertx.core.cli.annotations.Summary;
import io.vertx.core.spi.launcher.DefaultCommand;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

@Name("init")
@Summary("Initializes the 'package.json' to work with ES4X.")
public class InitCommand extends DefaultCommand {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  static {
    MAPPER.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
    MAPPER.configure(SerializationFeature.INDENT_OUTPUT, true);
  }

  @Override
  public void run() throws CLIException {
    try {

      final File file = new File("package.json");

      if (!file.exists()) {
        // Load the file from the class path
        try (InputStream in = InitCommand.class.getClassLoader().getResourceAsStream("META-INF/es4x-commands/package.json")) {
          if (in == null) {
            throw new IllegalStateException("Cannot load package.json template.");
          }
          Files.copy(in, file.toPath());
        } catch (IOException e) {
          throw new CLIException(e.getMessage(), e);
        }
      }

      Map npm = MAPPER.readValue(file, Map.class);
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

      String name = (String) npm.get("name");

      scripts.put("postinstall", "es4x install -f");
      scripts.put("start", name);
      scripts.put("test", name + " test js:" + test);

      MAPPER.writeValue(file, npm);

    } catch (IOException e) {
      throw new CLIException(e.getMessage(), e);
    }
  }
}
