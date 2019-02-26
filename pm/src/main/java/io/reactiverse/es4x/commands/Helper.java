package io.reactiverse.es4x.commands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

final class Helper {

  private static String OS = System.getProperty("os.name").toLowerCase();

  static boolean isWindows() {
    return OS.contains("win");
  }

  static boolean isUnix() {
    return
      OS.contains("nix") ||
        OS.contains("nux") ||
        OS.contains("aix") ||
        OS.contains("mac") ||
        OS.contains("sunos");
  }

  static String javaHomePrefix() {
    String prefix = System.getenv("JAVA_HOME");

    if (prefix == null) {
      prefix = "";
    } else if (prefix.length() > 0) {
      if (!prefix.endsWith(File.separator)) {
        prefix += File.separator;
      }
      prefix += "bin" + File.separator;
    }

    return prefix;
  }

  static String exec(String... command) throws IOException, InterruptedException {
    ProcessBuilder jdeps = new ProcessBuilder(command);
    jdeps.redirectError(ProcessBuilder.Redirect.INHERIT);
    File tmp = File.createTempFile(command[0], "out");
    tmp.deleteOnExit();

    jdeps.redirectOutput(ProcessBuilder.Redirect.appendTo(tmp));
    Process p = jdeps.start();
    int exit = p.waitFor();
    if (exit == 0) {
      return new String(Files.readAllBytes(tmp.toPath()));
    }

    throw new IOException(command[0] + " exit with status: " + exit);
  }

  static void err(String message) {
    System.err.println("\u001B[1m\u001B[33m" + message + "\u001B[0m");
    System.exit(1);
  }
}
