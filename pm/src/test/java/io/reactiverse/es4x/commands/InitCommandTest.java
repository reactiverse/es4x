package io.reactiverse.es4x.commands;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.*;

public class InitCommandTest {

  private final InitCommand command = new InitCommand();

  @Test
  public void shouldCreateAnEmptyProject() throws IOException {
    command.setCwd(IOUtils.mkTempDir());
    // the project name will be derived from the CWD
    String projectName = command.getCwd().getName();

    File packageJson = new File(command.getCwd(), "package.json");
    packageJson.deleteOnExit();

    assertFalse(packageJson.exists());
    command.run();
    assertTrue(packageJson.exists());

    Map json = JSON.parse(packageJson, Map.class);
    assertEquals(projectName, json.get("name"));
  }
}
