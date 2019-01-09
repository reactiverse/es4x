package io.reactiverse.es4x.commands;

import io.vertx.core.spi.launcher.DefaultCommandFactory;

public class InitCommandFactory extends DefaultCommandFactory<InitCommand> {

  public InitCommandFactory() {
    super(InitCommand.class);
  }
}
