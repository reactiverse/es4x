package io.reactiverse.es4x.commands;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

public class IOUtils {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  static {
    MAPPER.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
    MAPPER.configure(SerializationFeature.INDENT_OUTPUT, true);
  }

  public static Map read(File file) {
    try {
      return MAPPER.readValue(file, Map.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void write(File file, Map json) {
    try {
      MAPPER.writeValue(file, json);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static File mkTempDir() {
    try {
      final File dir = Files.createTempDirectory("es4x-pm-test").toFile();
      dir.deleteOnExit();
      return dir;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static String cwd() {
    return System.getProperty("user.dir");
  }
}
