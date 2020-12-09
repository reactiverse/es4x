package io.reactiverse.es4x.commands;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.Assert.*;

public class InstallCommandTest {

  private final InstallCommand command = new InstallCommand();

  @Test
  public void shouldDownloadDependencies() throws IOException {
    command.setCwd(IOUtils.mkTempDir());

    File packageJson = new File(command.getCwd(), "package.json");
    packageJson.deleteOnExit();

    assertFalse(packageJson.exists());

    JSONObject json = new JSONObject();
    json.put("name", "empty");
    JSONObject dependencies = new JSONObject();
    dependencies.put("@vertx/core", "3.6.3");
    json.put("dependencies", dependencies);
    JSONObject devDependencies = new JSONObject();
    devDependencies.put("@vertx/unit", "3.6.3");
    json.put("devDependencies", devDependencies);
    JSONArray mvnDependencies = new JSONArray();
    mvnDependencies.put("io.reactiverse:es4x:0.7.2");
      mvnDependencies.put("io.vertx:vertx-core:3.6.3");
      mvnDependencies.put("io.vertx:vertx-unit:3.6.3");
    json.put("mvnDependencies", mvnDependencies);

    JSON.encodeObject(packageJson, json);

    System.out.println(new String(Files.readAllBytes(packageJson.toPath()), StandardCharsets.UTF_8));

    command.run();
  }
}
