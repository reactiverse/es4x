package io.reactiverse.es4x.graal;

import io.vertx.core.Vertx;
import org.graalvm.polyglot.Value;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CommonJsUserModulesTest {

  private static final Vertx vertx = Vertx.vertx();
  private static Loader loader;

  @BeforeClass
  public static void beforeClass() {
    loader = new Loader(vertx);
  }

  @Test
  public void shouldFindPackageJsonInModuleFolder() {
    Value packageJson = loader.require("./lib/other_package");
    assertEquals("cool ranch", packageJson.getMember("flavor").asString());
    assertEquals("jar:/lib/other_package/lib/subdir", packageJson.getMember("subdir").asString());
  }

  @Test
  public void shouldLoadPackageJsonMainPropertyEvenIfItIsDirectory() {
    Value cheese = loader.require( "./lib/cheese");
    assertEquals("nacho", cheese.getMember("flavor").asString());
  }

  @Test
  public void shouldFindIndexJsInDirectoryIfNoPackageJsonExists() {
    Value packageJson = loader.require("./lib/my_package");
    assertEquals("Hello!", packageJson.getMember("data").asString());
  }
}
