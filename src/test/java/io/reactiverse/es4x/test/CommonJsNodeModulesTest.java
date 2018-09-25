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
import static org.junit.Assert.assertFalse;
import static org.junit.Assume.assumeTrue;

import static io.reactiverse.es4x.test.JS.*;

@RunWith(Parameterized.class)
public class CommonJsNodeModulesTest {

  @Parameterized.Parameters
  public static List<String> engines() {
    return Arrays.asList("Nashorn", "GraalJS");
  }

  private final String engineName;
  private Loader loader;

  public CommonJsNodeModulesTest(String engine) {
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
  public void shouldLoadFileModulesFromTheNode_modulesFolderInCwd() {
    Object top = loader.require("./lib/a_package");
    assertEquals("Hello from a file module", getMember(top, "file_module", String.class));
  }

  @Test
  public void shouldLoadPackageModulesFromNode_modulesFolder() {
    Object top = loader.require("./lib/a_package");
    assertEquals("Hello from a package module", getMember(getMember(top, "pkg_module"), "pkg", String.class));
  }

  @Test
  public void shouldFindNode_modulePackagesInTheParentPath() {
    Object top = loader.require("./lib/a_package");
    assertEquals("Hello from a file module", getMember(getMember(top, "pkg_module"), "file", String.class));
  }

  @Test
  public void shouldFindNode_modulePackagesFromSiblingPath() {
    Object top = loader.require("./lib/a_package");
    assertFalse(getMember(getMember(top, "parent_test"), "parentChanged", Boolean.class));
  }

  @Test
  public void shouldFindNode_modulePackagesAllTheWayUpAboveCwd() {
    Object m = loader.require("root_module");
    assertEquals("You are at the root", getMember(m, "message", String.class));
  }
}
