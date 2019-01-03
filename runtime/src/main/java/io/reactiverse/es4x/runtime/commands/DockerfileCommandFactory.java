package io.reactiverse.es4x.runtime.commands;

import io.vertx.core.spi.launcher.DefaultCommandFactory;

/**
 * Factory to create the {@code pom} command.
 *
 * @author Paulo Lopes
 */
public class DockerfileCommandFactory extends DefaultCommandFactory<DockerfileCommand> {

  /**
   * Creates a new instance of {@link DockerfileCommand}.
   */
  public DockerfileCommandFactory() {
    super(DockerfileCommand.class);
  }
}
