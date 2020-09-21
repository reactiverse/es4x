package io.reactiverse.es4x.cli;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public final class Helper {

  private static final String OS = System.getProperty("os.name").toLowerCase();

  public static boolean isWindows() {
    return OS.contains("win");
  }

  public static boolean isUnix() {
    return
      OS.contains("nix") ||
        OS.contains("nux") ||
        OS.contains("aix") ||
        OS.contains("mac") ||
        OS.contains("sunos");
  }

  public static String javaHomePrefix() {
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

  public static String exec(String... command) throws IOException, InterruptedException {
    ProcessBuilder jdeps = new ProcessBuilder(command);
    jdeps.redirectError(ProcessBuilder.Redirect.INHERIT);
    File tmp = File.createTempFile(command[0], "out");
    tmp.deleteOnExit();

    jdeps.redirectOutput(ProcessBuilder.Redirect.appendTo(tmp));
    Process p = jdeps.start();
    int exit = p.waitFor();

    final String result = new String(Files.readAllBytes(tmp.toPath()));

    if (exit == 0) {
      return result;
    } else {
      // warn what was captured from stdout
      warn(result);
      // throw
      throw new IOException(command[0] + " exit with status: " + exit);
    }
  }

  public static void fatal(String message) {
    System.err.println("\u001B[1m\u001B[31m" + message + "\u001B[0m");
    System.exit(1);
  }

  public static String pad(String text, int padding) {
    StringBuilder sb = new StringBuilder();

    if (text.length() >= padding) {
      sb.append(text);
      sb.append(System.lineSeparator());
      for (int i = 0; i < padding; i++) {
        sb.append(' ');
      }
    } else {
      sb.append(text);
      for (int i = text.length(); i < padding; i++) {
        sb.append(' ');
      }
    }

    return sb.toString();
  }

  public static void err(String message) {
    System.err.println("\u001B[1m\u001B[31m" + message + "\u001B[0m");
  }

  public static void warn(String message) {
    System.err.println("\u001B[1m\u001B[33m" + message + "\u001B[0m");
  }
}
