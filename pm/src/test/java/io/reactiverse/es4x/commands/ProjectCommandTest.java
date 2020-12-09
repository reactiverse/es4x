package io.reactiverse.es4x.commands;

import org.json.JSONObject;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class ProjectCommandTest {

  private final ProjectCommand command = new ProjectCommand();

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

    JSONObject json = JSON.parseObject(packageJson);
    assertEquals(projectName, json.get("name"));
  }
}
