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
import static org.junit.Assert.*;

@RunWith(VertxUnitRunner.class)
public class CommonJsUserModulesTest {

  private Runtime runtime;

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    runtime = runtime(rule.vertx());
  }

  @Test
  public void shouldFindPackageJsonInModuleFolder() {
    Value packageJson = require(runtime, "./lib/other_package");
    assertEquals("cool ranch", getMember(packageJson, "flavor", String.class));
    assertTrue(getMember(packageJson, "subdir", String.class).endsWith("/lib/other_package/lib/subdir"));
  }

  @Test
  public void shouldLoadPackageJsonMainPropertyEvenIfItIsDirectory() {
    Value cheese = require(runtime,  "./lib/cheese");
    assertEquals("nacho", getMember(cheese, "flavor", String.class));
  }

  @Test
  public void shouldFindIndexJsInDirectoryIfNoPackageJsonExists() {
    Value packageJson = require(runtime, "./lib/my_package");
    assertEquals("Hello!", getMember(packageJson, "data", String.class));
  }
}
