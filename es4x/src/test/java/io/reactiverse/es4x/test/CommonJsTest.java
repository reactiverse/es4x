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
public class CommonJsTest {

  private Runtime runtime;

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    runtime = commonjs(rule.vertx());
  }

  @Test
  public void shouldThrowAnErrorIfFileCantBeFound() {
    try {
      require(runtime, "./not_found.js");
      fail();
    } catch (RuntimeException e) {
      assertTrue("Error: Module \"./not_found.js\" was not found".equals(e.getMessage()) || "ModuleError: Module \"./not_found.js\" was not found".equals(e.getMessage()));
    }
  }

  @Test
  public void shouldNotWrapErrorsEncounteredWhenLoadingModule() {
    try {
      require(runtime, "./lib/throws");
      fail();
    } catch (RuntimeException e) {
      assertTrue("ReferenceError: \"bar\" is not defined".equals(e.getMessage()) || "ReferenceError: bar is not defined".equals(e.getMessage()));
    }
  }

  @Test
  public void shouldSupportNestedRequires() {
    Value outer = require(runtime, "./lib/outer");
    Value quadruple = getMember(outer, "quadruple");
    Number n = callAs(outer, quadruple, Number.class, 2);
    assertEquals(8, n.intValue());
  }

  @Test
  public void shouldSupportAnIdWithAnExtension() {
    Value outer = require(runtime, "./lib/outer.js");
    Value quadruple = getMember(outer, "quadruple");
    Number n = callAs(outer, quadruple, Number.class, 2);
    assertEquals(8, n.intValue());
  }

  @Test
  public void shouldReturnDotJsonFileAsJsonObject() {
    Value json = require(runtime, "./lib/some_data.json");
    assertEquals("This is a JSON file", getMember(json, "description", String.class));
  }

  @Test
  public void shouldCacheModulesInRequireCache() {
    Value outer = require(runtime, "./lib/outer.js");
    Value require = runtime.eval("require");

    Value cache = getMember(require, "cache");
    String filename = getMember(outer, "filename", String.class);

    // JS has no concept ot equals() so we cast to String to compare
    assertEquals(getMember(cache, filename).toString(), outer.toString());
    Object outer2 = require(runtime, "./lib/outer.js");
    // JS has no concept ot equals() so we cast to String to compare
    assertEquals(outer.toString(), outer2.toString());
  }

  @Test
  public void shoudlHandleCyclicDependencies() {
    Value main = require(runtime, "./lib/cyclic");
    assertEquals("Hello from A", (getMember(getMember(main, "a"), "fromA", String.class)));
    assertEquals("Hello from B", (getMember(getMember(main, "b"), "fromB", String.class)));
  }
}
