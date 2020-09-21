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

import static io.reactiverse.es4x.cli.Helper.fatal;

@Name("dockerfile")
@Summary("Creates a generic Dockerfile for building and deploying the current project.")
public class DockerfileCommand extends DefaultCommand {

  @Override
  public void run() throws CLIException {
    File dockerfile = new File(getCwd(), "Dockerfile");

    if (dockerfile.exists()) {
      fatal("Dockerfile already exists.");
    }

    // Load the file from the class path
    try (InputStream in = DockerfileCommand.class.getClassLoader().getResourceAsStream("META-INF/es4x-commands/Dockerfile")) {
      if (in == null) {
        fatal("Cannot load Dockerfile template.");
      } else {
        Files.copy(in, dockerfile.toPath());
      }
    } catch (IOException e) {
      fatal(e.getMessage());
    }

    File dockerignore = new File(getCwd(), ".dockerignore");

    if (!dockerignore.exists()) {
      // Load the file from the class path
      try (InputStream in = DockerfileCommand.class.getClassLoader().getResourceAsStream("META-INF/es4x-commands/.dockerignore")) {
        if (in == null) {
          fatal("Cannot load .dockerignore template.");
        } else {
          Files.copy(in, dockerignore.toPath());
        }
      } catch (IOException e) {
        fatal(e.getMessage());
      }
    }
  }
}
