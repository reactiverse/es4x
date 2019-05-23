package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
import io.reactiverse.es4x.impl.graal.GraalEngine;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.reactiverse.es4x.test.JS.callAs;
import static io.reactiverse.es4x.test.JS.getMember;
import static org.junit.Assert.*;

@RunWith(VertxUnitRunner.class)
public class CommonJsTest {

  private Runtime runtime;

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    runtime = new GraalEngine(rule.vertx()).newContext();
  }

  @Test
  public void shouldThrowAnErrorIfFileCantBeFound() {
    try {
      runtime.require("./not_found.js");
      fail();
    } catch (RuntimeException e) {
      assertTrue("Error: Module \"./not_found.js\" was not found".equals(e.getMessage()) || "ModuleError: Module \"./not_found.js\" was not found".equals(e.getMessage()));
    }
  }

  @Test
  public void shouldNotWrapErrorsEncounteredWhenLoadingModule() {
    try {
      runtime.require("./lib/throws");
      fail();
    } catch (RuntimeException e) {
      assertTrue("ReferenceError: \"bar\" is not defined".equals(e.getMessage()) || "ReferenceError: bar is not defined".equals(e.getMessage()));
    }
  }

  @Test
  public void shouldSupportNestedRequires() {
    Object outer = runtime.require("./lib/outer");
    Object quadruple = getMember(outer, "quadruple");
    Number n = callAs(outer, quadruple, Number.class, 2);
    assertEquals(8, n.intValue());
  }

  @Test
  public void shouldSupportAnIdWithAnExtension() {
    Object outer = runtime.require("./lib/outer.js");
    Object quadruple = getMember(outer, "quadruple");
    Number n = callAs(outer, quadruple, Number.class, 2);
    assertEquals(8, n.intValue());
  }

  @Test
  public void shouldReturnDotJsonFileAsJsonObject() {
    Object json = runtime.require("./lib/some_data.json");
    assertEquals("This is a JSON file", getMember(json, "description", String.class));
  }

  @Test
  public void shouldCacheModulesInRequireCache() throws Exception {
    Object outer = runtime.require("./lib/outer.js");
    Object require = runtime.eval("require");

    Object cache = getMember(require, "cache");
    String filename = getMember(outer, "filename", String.class);

    // JS has no concept ot equals() so we cast to String to compare
    assertEquals(getMember(cache, filename).toString(), outer.toString());
    Object outer2 = runtime.require("./lib/outer.js");
    // JS has no concept ot equals() so we cast to String to compare
    assertEquals(outer.toString(), outer2.toString());
  }

  @Test
  public void shoudlHandleCyclicDependencies() {
    Object main = runtime.require("./lib/cyclic");
    assertEquals("Hello from A", (getMember(getMember(main, "a"), "fromA", String.class)));
    assertEquals("Hello from B", (getMember(getMember(main, "b"), "fromB", String.class)));
  }
}
