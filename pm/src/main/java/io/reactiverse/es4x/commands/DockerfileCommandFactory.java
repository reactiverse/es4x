package io.reactiverse.es4x.commands;

import io.vertx.core.spi.launcher.DefaultCommandFactory;

public class DockerfileCommandFactory extends DefaultCommandFactory<DockerfileCommand> {

  public DockerfileCommandFactory() {
    super(DockerfileCommand.class);
  }
}
