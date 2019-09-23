package io.reactiverse.es4x.commands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class IOUtils {


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
