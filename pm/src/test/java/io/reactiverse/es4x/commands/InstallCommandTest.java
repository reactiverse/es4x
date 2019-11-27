package io.reactiverse.es4x.commands;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class InstallCommandTest {

  private final InstallCommand command = new InstallCommand();

  @Test
  @SuppressWarnings("unchecked")
  public void shouldDownloadDependencies() throws IOException {
    command.setCwd(IOUtils.mkTempDir());

    File packageJson = new File(command.getCwd(), "package.json");
    packageJson.deleteOnExit();

    assertFalse(packageJson.exists());

    Map json = new HashMap();
    json.put("name", "empty");
    Map dependencies = new HashMap();
    dependencies.put("@vertx/core", "3.6.3");
    json.put("dependencies", dependencies);
    Map devDependencies = new HashMap();
    devDependencies.put("@vertx/unit", "3.6.3");
    json.put("devDependencies", devDependencies);
    List mvnDependencies = new ArrayList();
    mvnDependencies.add("io.reactiverse:es4x:0.7.2");
      mvnDependencies.add("io.vertx:vertx-core:3.6.3");
      mvnDependencies.add("io.vertx:vertx-unit:3.6.3");
    json.put("mvnDependencies", mvnDependencies);

    JSON.encode(packageJson, json);

    command.run();
  }
}
