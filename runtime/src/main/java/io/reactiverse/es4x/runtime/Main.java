package io.reactiverse.es4x.runtime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

  private static final String GRAALVM_VERSION = "1.0.0-rc10";

  public static void main(String[] args) throws Exception {
    if (args == null || args.length == 0) {
      printHelp();
      System.exit(-1);
      return;
    }
    switch (args[0]) {
      case "install":
        final double version = Double.parseDouble(System.getProperty("java.specification.version"));
        final String vm = System.getProperty("java.vm.name");
        if (!vm.toLowerCase().contains("graalvm")) {
          if (version >= 11) {
            System.err.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            System.err.println("@ Installing JVMCI to run GraalJS on stock JDK! @");
            System.err.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            InstallCommand.main(
              Arrays.asList(
                "org.graalvm.js:js:" + GRAALVM_VERSION,
                "org.graalvm.tools:profiler:" + GRAALVM_VERSION,
                "org.graalvm.tools:chromeinspector:" + GRAALVM_VERSION),
              getArg(args, "o"));
            InstallCommand.main(
              Arrays.asList(
                "org.graalvm.compiler:compiler:" + GRAALVM_VERSION),
              "@jvmci", true);
          } else {
            System.err.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            System.err.println("@ Current JDK only supports Nashorn! @");
            System.err.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
          }
        }
        InstallCommand.main(getArgs(args), getArg(args, "o"));
        return;
      case "install-jvmci":
        InstallCommand.main(
          Arrays.asList(
            "org.graalvm.js:js:" + GRAALVM_VERSION,
            "org.graalvm.tools:profiler:" + GRAALVM_VERSION,
            "org.graalvm.tools:chromeinspector:" + GRAALVM_VERSION),
          getArg(args, "o"));
        InstallCommand.main(
          Arrays.asList(
            "org.graalvm.compiler:compiler:" + GRAALVM_VERSION),
          "@jvmci", true);
        InstallCommand.main(getArgs(args), getArg(args, "o"));
        return;
      case "init":
        InitCommand.main();
        return;
      case "dockerfile":
        DockerfileCommand.main();
        return;
      case "help":
        printHelp();
        return;
      default:
        printHelp();
        System.exit(-1);
    }
  }

  private static void printHelp() {
    System.err.println("Usage: es4x-runtime <cmd> args...");
    System.err.println("Commands:");
    System.err.println("  init");
    System.err.println("    Inits the scripts section of a package.json.");
    System.err.println("");
    System.err.println("  install [arg...] [-o dir]");
    System.err.println("    Installs the required jars defined in the current node_modules.");
    System.err.println("    When invoked with arguments, these will be installed instead of");
    System.err.println("    crawling the 'node_modules'.");
    System.err.println("    -o output dir (default: @lib)");
    System.err.println("");
    System.err.println("  install-jvmci [-o dir]");
    System.err.println("    Installs the required JMVCI jars in order to run on stock");
    System.err.println("    [Open|Oracle]JDK >= 11.");
    System.err.println("");
    System.err.println("  dockerfile");
    System.err.println("    Creates a Dockerfile.");
  }

  private static List<String> getArgs(String[] args) {
    final List<String> arguments = new ArrayList<>();
    for (int i = 1; i < args.length; i++) {
      if (args[i].length() > 0 && args[i].charAt(0) == '-') {
        i++;
        continue;
      }
      arguments.add(args[i]);
    }

    return arguments;
  }

  private static String getArg(String[] args, String arg) {
    arg = "-" + arg;
    for (int i = 1; i < args.length; i++) {
      if (arg.equals(args[i])) {
        if (i + 1 < args.length) {
          return args[i + 1];
        }
      }
    }

    return null;
  }
}
