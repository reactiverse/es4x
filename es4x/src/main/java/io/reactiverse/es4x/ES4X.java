package io.reactiverse.es4x;

import io.reactiverse.es4x.impl.command.ES4XRunCommand;
import io.reactiverse.es4x.impl.command.ES4XStartCommand;
import io.vertx.core.Launcher;

import java.io.File;

public class ES4X {

  /**
   * Main entry point.
   *
   * @param args the user command line arguments.
   */
  public static void main(String[] args) {

    final Launcher launcher = new Launcher();
    // remove the default run command
    launcher.unregister("run");
    // apply the custom run command
    launcher.register(ES4XRunCommand.class);
    // remove the default start command
    launcher.unregister("start");
    // apply the custom start command
    launcher.register(ES4XStartCommand.class);

    // small behavior change
    if (args.length == 0) {
      // we will assume a js shell is required
      launcher.execute("run", "js:>");
      return;
    }

    File script = new File(args[0]);
    // script can be either a file or directory
    if (script.exists()) {
      // we will assume a js command
      launcher.execute("run", args);
      return;
    }

    // default behavior
    launcher.dispatch(args);
  }
}
