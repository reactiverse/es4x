package io.reactiverse.es4x.commands;

import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class DockerfileCommandTest {

  private final DockerfileCommand command = new DockerfileCommand();

  @Test
  public void shouldCreateADockerFile() {
    command.setCwd(IOUtils.mkTempDir());

    File file = new File(command.getCwd(), "Dockerfile");
    file.deleteOnExit();

    assertFalse(file.exists());
    command.run();
    assertTrue(file.exists());
  }
}
