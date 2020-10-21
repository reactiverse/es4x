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
import io.vertx.core.cli.annotations.*;
import io.vertx.core.spi.launcher.DefaultCommand;

@Name(Install.NAME)
@Summary(Install.SUMMARY)
public class InstallCommand extends DefaultCommand {

  private final Install command = new Install();

  @Option(longName = "vendor", shortName = "v")
  @Description("Comma separated list of vendor jars.")
  public void setVendor(String vendor) {
    command.setVendor(vendor);
  }

  @Option(longName = "link", shortName = "l", flag = true)
  @Description("Symlink jars instead of copy.")
  public void setLink(boolean link) {
    command.setLink(link);
  }

  @Option(longName = "only", shortName = "o", choices = {"prod", "dev", "all"})
  @Description("Only install 'prod/dev/all' (default: all).")
  public void setOnly(String only) {
    command.setOnly(only);
  }

  @Override
  public void run() throws CLIException {
    command
      .setCwd(getCwd())
      .run();
  }
}
