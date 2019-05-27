package io.reactiverse.es4x.commands;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

final class Helper {

  private static String OS = System.getProperty("os.name").toLowerCase();

  private static final ObjectMapper MAPPER = new ObjectMapper();

  static {
    MAPPER.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
    MAPPER.configure(SerializationFeature.INDENT_OUTPUT, true);
  }

  static Map read(File file) throws IOException {
    return MAPPER.readValue(file, Map.class);
  }

  static void write(File file, Map json) throws IOException {
      MAPPER.writeValue(file, json);
  }

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

  static void fatal(String message) {
    System.err.println("\u001B[1m\u001B[31m" + message + "\u001B[0m");
    System.exit(1);
  }

  static void err(String message) {
    System.err.println("\u001B[1m\u001B[31m" + message + "\u001B[0m");
  }

  static void warn(String message) {
    System.err.println("\u001B[1m\u001B[33m" + message + "\u001B[0m");
  }
}
