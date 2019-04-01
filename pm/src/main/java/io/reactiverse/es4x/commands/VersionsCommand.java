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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static io.reactiverse.es4x.commands.Helper.fatal;

@Name("versions")
@Summary("Displays the versions.")
public class VersionsCommand extends io.vertx.core.impl.launcher.commands.VersionCommand {

  private static final Properties VERSIONS = new Properties();

  static {
    try (InputStream is = InstallCommand.class.getClassLoader().getResourceAsStream("META-INF/es4x-commands/VERSIONS.properties")) {
      if (is == null) {
        fatal("Cannot find 'META-INF/es4x-commands/VERSIONS.properties' on classpath");
      } else {
        VERSIONS.load(is);
      }
    } catch (IOException e) {
      fatal(e.getMessage());
    }
  }

  @Override
  public void run() throws CLIException {
    System.out.println("Vert.x:  " + getVersion());
    System.out.println("ES4X:    " + VERSIONS.getProperty("es4x"));
    System.out.println("graaljs: " + VERSIONS.getProperty("graalvm"));
  }
}
