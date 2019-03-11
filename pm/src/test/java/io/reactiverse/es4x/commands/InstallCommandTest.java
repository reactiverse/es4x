package io.reactiverse.es4x.commands;

import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class InstallCommandTest {

  private final InstallCommand command = new InstallCommand();

  @Test
  public void shouldDownloadDependencies() {
    command.setCwd(IOUtils.mkTempDir());

    File packageJson = new File(command.getCwd(), "package.json");
    packageJson.deleteOnExit();

    assertFalse(packageJson.exists());

    IOUtils.write(packageJson, new HashMap() {{
      put("name", "empty");
      put("dependencies", new HashMap() {{
        put("@vertx/core", "3.6.3");
      }});
      put("devDependencies", new HashMap() {{
        put("@vertx/unit", "3.6.3");
      }});
      put("mvnDependencies", new ArrayList() {{
        add("io.reactiverse:es4x:0.7.2");
        add("io.vertx:vertx-core:3.6.3");
        add("io.vertx:vertx-unit:3.6.3");
      }});
    }});

    command.run();
  }
}
