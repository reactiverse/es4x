package io.reactiverse.es4x.impl.command;

import io.vertx.core.cli.annotations.*;
import io.vertx.core.impl.launcher.commands.RunCommand;

@Name("run")
@Summary("Runs a JS script called <main-verticle> in its own instance of vert.x.")
public class ES4XRunCommand extends RunCommand {

  /**
   * Sets the main verticle that is deployed.
   *
   * @param verticle the verticle
   */
  @Argument(index = 0, argName = "main-verticle", required = false)
  @DefaultValue("js:index.js")
  @Description("The main verticle to deploy, it can be a script file or a package directory.")
  public void setMainVerticle(String verticle) {
    if (!verticle.startsWith("js:")) {
      super.setMainVerticle("js:" + verticle);
    } else {
      super.setMainVerticle(verticle);
    }
  }

  @Option(longName = "inspect", argName = "inspector-port")
  @Description("Specifies the node inspector port to listen on (GraalJS required).")
  public void setInspect(int inspect) {
    System.out.println("HERE: " + inspect);
    System.setProperty("polyglot.inspect", Integer.toString(inspect));
    System.setProperty("vertx.options.blockedThreadCheckInterval", "100000");
  }

  @Option(longName = "inspect-brk", argName = "inspector-port")
  @Description("Breaks on start the node inspector listening on given port (GraalJS required).")
  public void setInspectBrk(int inspect) {
    System.out.println("HERE: " + inspect);
    System.setProperty("polyglot.inspect", Integer.toString(inspect));
    System.setProperty("polyglot.inspect.Suspend", "true");
    System.setProperty("vertx.options.blockedThreadCheckInterval", "100000");
  }
}
