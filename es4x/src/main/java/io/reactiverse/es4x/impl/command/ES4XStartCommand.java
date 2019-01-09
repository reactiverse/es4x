package io.reactiverse.es4x.impl.command;

import io.reactiverse.es4x.ES4X;
import io.vertx.core.cli.annotations.Description;
import io.vertx.core.cli.annotations.Name;
import io.vertx.core.cli.annotations.Summary;
import io.vertx.core.impl.launcher.commands.StartCommand;

@Name("start")
@Summary("Start a vert.x application in background")
@Description("Start a vert.x application as a background service. The application is identified with an id that can be set using the `vertx-id` option. If not set a random UUID is generated. The application can be stopped with the `stop` command.")
public class ES4XStartCommand extends StartCommand {

  public ES4XStartCommand() {
    super.setLauncherClass(ES4X.class.getName());
  }
}
