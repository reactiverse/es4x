package io.reactiverse.es4x.runtime.commands;

import io.vertx.core.spi.launcher.DefaultCommandFactory;

/**
 * Factory to create the {@code pom} command.
 *
 * @author Paulo Lopes
 */
public class PomCommandFactory extends DefaultCommandFactory<PomCommand> {

  /**
   * Creates a new instance of {@link PomCommand}.
   */
  public PomCommandFactory() {
    super(PomCommand.class);
  }
}
