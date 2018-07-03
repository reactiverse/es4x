package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Loader;
import io.vertx.core.Vertx;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;
import static io.reactiverse.es4x.test.JS.*;

@RunWith(Parameterized.class)
public class CommonJsUserModulesTest {

  @Parameterized.Parameters
  public static List<String> engines() {
    return Arrays.asList("Nashorn", "GraalVM");
  }

  final String engineName;
  final Loader loader;

  public CommonJsUserModulesTest(String engine) {
    System.setProperty("es4x.engine", engine);
    engineName = engine;
    loader = Loader.create(Vertx.vertx());
  }

  @Before
  public void initialize() {
    assumeTrue(loader.name().equalsIgnoreCase(engineName));
  }

  @Test
  public void shouldFindPackageJsonInModuleFolder() {
    Object packageJson = loader.require("./lib/other_package");
    assertEquals("cool ranch", getMember(packageJson, "flavor", String.class));
    assertEquals("jar:/lib/other_package/lib/subdir", getMember(packageJson, "subdir", String.class));
  }

  @Test
  public void shouldLoadPackageJsonMainPropertyEvenIfItIsDirectory() {
    Object cheese = loader.require( "./lib/cheese");
    assertEquals("nacho", getMember(cheese, "flavor", String.class));
  }

  @Test
  public void shouldFindIndexJsInDirectoryIfNoPackageJsonExists() {
    Object packageJson = loader.require("./lib/my_package");
    assertEquals("Hello!", getMember(packageJson, "data", String.class));
  }
}
