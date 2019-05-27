package io.reactiverse.es4x.commands;

import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class InitCommandTest {

  private final InitCommand command = new InitCommand();

  @Test
  public void shouldCreateAnEmptyProject() {
    command.setCwd(IOUtils.mkTempDir());

    File packageJson = new File(command.getCwd(), "package.json");
    packageJson.deleteOnExit();

    assertFalse(packageJson.exists());
    command.run();
    assertTrue(packageJson.exists());

    Map json = IOUtils.read(packageJson);
    assertEquals("empty", json.get("name"));
    assertEquals("index.js", json.get("main"));

    Map scripts = (Map) json.get("scripts");
    assertNotNull(scripts);
    assertEquals("es4x-launcher", scripts.get("start"));
    assertEquals("es4x-launcher test js:index.test.js", scripts.get("test"));
    assertEquals("es4x install", scripts.get("postinstall"));
  }

  @Test
  public void shouldUpdateAnExisting() {
    command.setCwd(IOUtils.mkTempDir());

    File packageJson = new File(command.getCwd(), "package.json");
    packageJson.deleteOnExit();
    IOUtils.write(packageJson, new HashMap() {{
      put("name", "my-project");
    }});

    command.run();
    assertTrue(packageJson.exists());

    Map json = IOUtils.read(packageJson);
    assertEquals("my-project", json.get("name"));

    Map scripts = (Map) json.get("scripts");
    assertNotNull(scripts);
    assertEquals("es4x-launcher", scripts.get("start"));
    assertEquals("es4x-launcher test js:index.test.js", scripts.get("test"));
    assertEquals("es4x install", scripts.get("postinstall"));
  }
}
