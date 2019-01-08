package io.reactiverse.es4x.impl.command;

import io.vertx.core.spi.launcher.DefaultCommandFactory;

/**
 * Factory to create the default run command.
 *
 * @author Paulo Lopes
 */
public class JSCommandFactory extends DefaultCommandFactory<JSCommand> {

  public JSCommandFactory() {
    super(JSCommand.class);
  }
}
