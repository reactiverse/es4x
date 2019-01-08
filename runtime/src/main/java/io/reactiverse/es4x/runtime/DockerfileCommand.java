package io.reactiverse.es4x.runtime;

import java.io.*;
import java.nio.file.Files;

class DockerfileCommand {

  public static void main() throws IOException {

    File cwd = new File(System.getProperty("user.dir"));

    File dockerfile = new File(cwd, "Dockerfile");

    if (dockerfile.exists()) {
      System.err.println("Dockerfile already exists.");
      return;
    }

    // Load the file from the class path
    try (InputStream in = DockerfileCommand.class.getClassLoader().getResourceAsStream("META-INF/es4x-pm/Dockerfile")) {
      Files.copy(in, dockerfile.toPath());
    }
  }
}
