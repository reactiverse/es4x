package io.reactiverse.es4x.graal;

import io.vertx.core.Vertx;
import org.graalvm.polyglot.Value;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class CommonJsNodeModulesTest {

  private static final Vertx vertx = Vertx.vertx();
  private static Loader loader;

  @BeforeClass
  public static void beforeClass() {
    loader = new Loader(vertx);
  }

  @Test
  public void shouldLoadFileModulesFromTheNode_modulesFolderInCwd() {
    Value top = loader.require("./lib/a_package");
    assertEquals("Hello from a file module", top.getMember("file_module").asString());
  }

  @Test
  public void shouldLoadPackageModulesFromNode_modulesFolder() {
    Value top = loader.require("./lib/a_package");
    assertEquals("Hello from a package module", top.getMember("pkg_module").getMember("pkg").asString());
  }

  @Test
  public void shouldFindNode_modulePackagesInTheParentPath() {
    Value top = loader.require("./lib/a_package");
    assertEquals("Hello from a file module", top.getMember("pkg_module").getMember("file").asString());
  }

  @Test
  public void shouldFindNode_modulePackagesFromSiblingPath() {
    Value top = loader.require("./lib/a_package");
    assertFalse(top.getMember("parent_test").getMember("parentChanged").asBoolean());
  }

  @Test
  public void shouldFindNode_modulePackagesAllTheWayUpAboveCwd() {
    Value m = loader.require("root_module");
    assertEquals("You are at the root", m.getMember("message").asString());
  }
}
