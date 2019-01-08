package io.reactiverse.es4x;

import io.vertx.core.Launcher;

import java.io.File;

public class ES4X {

  private static String[] processArgs(String[] args) {
    boolean hasLauncherClass = false;
    boolean hasRedeploy = false;

    for (String arg : args) {
      if (arg != null) {
        if (arg.equals("--inspect")) {
          System.setProperty("polyglot.inspect", "9229");
          System.setProperty("vertx.options.blockedThreadCheckInterval", "100000");
          continue;
        }
        if (arg.startsWith("--inspect=")) {
          System.setProperty("polyglot.inspect", arg.substring(10));
          System.setProperty("vertx.options.blockedThreadCheckInterval", "100000");
          continue;
        }
        if (arg.equals("--inspect-brk")) {
          System.setProperty("polyglot.inspect", "9229");
          System.setProperty("polyglot.inspect.Suspend", "true");
          System.setProperty("vertx.options.blockedThreadCheckInterval", "100000");
          continue;
        }
        if (arg.startsWith("--inspect-brk=")) {
          System.setProperty("polyglot.inspect", arg.substring(10));
          System.setProperty("polyglot.inspect.Suspend", "true");
          System.setProperty("vertx.options.blockedThreadCheckInterval", "100000");
          continue;
        }
        if (arg.startsWith("--launcher-class=")) {
          hasLauncherClass = true;
          continue;
        }
        if (arg.startsWith("--redeploy=")) {
          hasRedeploy = true;
          continue;
        }
      }
    }

    if (hasRedeploy && !hasLauncherClass) {
      // patch the arguments to use this launcher
      String[] arguments = new String[args.length + 1];
      System.arraycopy(args, 0, arguments, 0, args.length);
      arguments[args.length] = "--launcher-class=io.reactiverse.es4x.ES4X";
      return arguments;
    }

    return args;
  }

  /**
   * Main entry point.
   *
   * @param args the user command line arguments.
   */
  public static void main(String[] args) {
    // apply system properties if needed
    args = processArgs(args);

    // small behavior change
    if (args.length > 0) {
      File script = new File(args[0]);
      // script can be either a file or directory
      if (script.exists()) {
        // we will assume a js command
        Launcher.executeCommand("js", args);
        return;
      }
    }

    // default behavior
    new Launcher().dispatch(args);
  }
}
