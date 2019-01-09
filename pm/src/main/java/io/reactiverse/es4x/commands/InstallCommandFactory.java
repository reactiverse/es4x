package io.reactiverse.es4x.commands;

import io.vertx.core.spi.launcher.DefaultCommandFactory;

public class InstallCommandFactory extends DefaultCommandFactory<InstallCommand> {

  public InstallCommandFactory() {
    super(InstallCommand.class);
  }
}
