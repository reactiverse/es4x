package io.reactiverse.es4x.impl.command;

import io.vertx.core.cli.annotations.*;
import io.vertx.core.impl.launcher.commands.RunCommand;

@Name("js")
@Summary("Runs a JS verticle.")
@Description("Runs a JS verticle from the current directory or classpath.")
public class JSCommand extends RunCommand {

  /**
   * Sets the main verticle that is deployed.
   *
   * @param verticle the verticle
   */
  @Argument(index = 0, argName = "main-verticle")
  @Description("The main verticle to deploy, it can be a fully qualified class name or a file.")
  public void setMainVerticle(String verticle) {
    if (!verticle.startsWith("js:")) {
      this.mainVerticle = "js:" + verticle;
    } else {
      this.mainVerticle = verticle;
    }
  }

//  @Option(longName = "shell", acceptValue = false, flag = true)
//  @Description("If specified the verticle will be deployed with an attached REPL shell.")
//  public void setShell(boolean shell) {
//  }
}
