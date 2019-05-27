package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
import io.reactiverse.es4x.impl.graal.GraalEngine;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.reactiverse.es4x.test.JS.getMember;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(VertxUnitRunner.class)
public class CommonJsNodeModulesTest {

  private Runtime runtime;

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    runtime = new GraalEngine(rule.vertx()).newContext();
  }

  @Test
  public void shouldLoadFileModulesFromTheNode_modulesFolderInCwd() {
    Object top = runtime.require("./lib/a_package");
    assertEquals("Hello from a file module", getMember(top, "file_module", String.class));
  }

  @Test
  public void shouldLoadPackageModulesFromNode_modulesFolder() {
    Object top = runtime.require("./lib/a_package");
    assertEquals("Hello from a package module", getMember(getMember(top, "pkg_module"), "pkg", String.class));
  }

  @Test
  public void shouldFindNode_modulePackagesInTheParentPath() {
    Object top = runtime.require("./lib/a_package");
    assertEquals("Hello from a file module", getMember(getMember(top, "pkg_module"), "file", String.class));
  }

  @Test
  public void shouldFindNode_modulePackagesFromSiblingPath() {
    Object top = runtime.require("./lib/a_package");
    assertFalse(getMember(getMember(top, "parent_test"), "parentChanged", Boolean.class));
  }

  @Test
  public void shouldFindNode_modulePackagesAllTheWayUpAboveCwd() {
    Object m = runtime.require("root_module");
    assertEquals("You are at the root", getMember(m, "message", String.class));
  }
}
