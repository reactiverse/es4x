package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
import io.reactiverse.es4x.Loader;
import io.vertx.ext.unit.junit.RunTestOnContext;
import org.junit.Before;
import org.junit.Rule;
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
    return Arrays.asList("Nashorn", "GraalJS");
  }

  private final String engineName;
  private Loader loader;

  public CommonJsUserModulesTest(String engine) {
    System.setProperty("es4x.engine", engine);
    engineName = engine;
  }

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    loader = Runtime.getCurrent().loader(rule.vertx());
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
