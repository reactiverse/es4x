package io.reactiverse.es4x.commands;

import io.vertx.core.cli.CLIException;
import io.vertx.core.cli.annotations.Name;
import io.vertx.core.cli.annotations.Summary;
import io.vertx.core.spi.launcher.DefaultCommand;

import java.io.*;
import java.nio.file.Files;

@Name("dockerfile")
@Summary("Creates a generic Dockerfile for building and deploying the current project.")
public class DockerfileCommand extends DefaultCommand {

  @Override
  public void run() throws CLIException {
    File cwd = new File(System.getProperty("user.dir"));

    File dockerfile = new File(cwd, "Dockerfile");

    if (dockerfile.exists()) {
      throw new IllegalStateException("Dockerfile already exists.");
    }

    // Load the file from the class path
    try (InputStream in = DockerfileCommand.class.getClassLoader().getResourceAsStream("META-INF/es4x-commands/Dockerfile")) {
      if (in == null) {
        throw new IllegalStateException("Cannot load Dockerfile template.");
      }
      Files.copy(in, dockerfile.toPath());
    } catch (IOException e) {
      throw new CLIException(e.getMessage(), e);
    }
  }
}
