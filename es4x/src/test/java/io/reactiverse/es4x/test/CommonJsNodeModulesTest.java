package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.graalvm.polyglot.Value;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.reactiverse.es4x.test.JS.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(VertxUnitRunner.class)
public class CommonJsNodeModulesTest {

  private Runtime runtime;

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    runtime = runtime(rule.vertx());
  }

  @Test
  public void shouldLoadFileModulesFromTheNode_modulesFolderInCwd() {
    Value top = require(runtime, "./lib/a_package");
    assertEquals("Hello from a file module", getMember(top, "file_module", String.class));
  }

  @Test
  public void shouldLoadPackageModulesFromNode_modulesFolder() {
    Value top = require(runtime, "./lib/a_package");
    assertEquals("Hello from a package module", getMember(getMember(top, "pkg_module"), "pkg", String.class));
  }

  @Test
  public void shouldFindNode_modulePackagesInTheParentPath() {
    Value top = require(runtime, "./lib/a_package");
    assertEquals("Hello from a file module", getMember(getMember(top, "pkg_module"), "file", String.class));
  }

  @Test
  public void shouldFindNode_modulePackagesFromSiblingPath() {
    Value top = require(runtime, "./lib/a_package");
    assertFalse(getMember(getMember(top, "parent_test"), "parentChanged", Boolean.class));
  }

  @Test
  public void shouldFindNode_modulePackagesAllTheWayUpAboveCwd() {
    Value m = require(runtime, "root_module");
    assertEquals("You are at the root", getMember(m, "message", String.class));
  }
}
