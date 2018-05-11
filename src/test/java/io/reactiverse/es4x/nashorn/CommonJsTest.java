package io.reactiverse.es4x.nashorn;

import io.reactiverse.es4x.Loader;
import io.vertx.core.Vertx;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import static io.reactiverse.es4x.nashorn.JS.*;
import static org.junit.Assume.assumeTrue;

@RunWith(Parameterized.class)
public class CommonJsTest {

  @Parameterized.Parameters
  public static List<String> engines() {
    return Arrays.asList("Nashorn", "GraalVM");
  }

  final String engineName;
  final Loader loader;

  public CommonJsTest(String engine) {
    System.setProperty("es4x.engine", engine);
    engineName = engine;
    loader = Loader.create(Vertx.vertx());
  }

  @Before
  public void initialize() {
    assumeTrue(loader.name().equalsIgnoreCase(engineName));
  }

  @Test
  public void shouldThrowAnErrorIfFileCantBeFound() {
    try {
      loader.require("./not_found.js");
      fail();
    } catch (RuntimeException e) {
      assertTrue("Error: Module \"./not_found.js\" was not found".equals(e.getMessage()) || "ModuleError: Module \"./not_found.js\" was not found".equals(e.getMessage()));
    }
  }

  @Test
  public void shouldNotWrapErrorsEncounteredWhenLoadingModule() {
    try {
      loader.require("./lib/throws");
      fail();
    } catch (RuntimeException e) {
      assertTrue("ReferenceError: \"bar\" is not defined".equals(e.getMessage()) || "ReferenceError: bar is not defined".equals(e.getMessage()));
    }
  }

  @Test
  public void shouldSupportNestedRequires() {
    Object outer = loader.require("./lib/outer");
    Object quadruple = getMember(outer, "quadruple");
    Number n = callAs(outer, quadruple, Number.class, 2);
    assertEquals(8, n.intValue());
  }

  @Test
  public void shouldSupportAnIdWithAnExtension() {
    Object outer = loader.require("./lib/outer.js");
    Object quadruple = getMember(outer, "quadruple");
    Number n = callAs(outer, quadruple, Number.class, 2);
    assertEquals(8, n.intValue());
  }

  @Test
  public void shouldReturnDotJsonFileAsJsonObject() {
    Object json = loader.require("./lib/some_data.json");
    assertEquals("This is a JSON file", getMember(json, "description", String.class));
  }

  @Test
  public void shouldCacheModulesInRequireCache() throws Exception {
    Object outer = loader.require("./lib/outer.js");
    Object require = loader.eval("require");

    Object cache = getMember(require, "cache");
    String filename = getMember(outer, "filename", String.class);

    // JS has no concept ot equals() so we cast to String to compare
    assertEquals(getMember(cache, filename).toString(), outer.toString());
    Object outer2 = loader.require("./lib/outer.js");
    // JS has no concept ot equals() so we cast to String to compare
    assertEquals(outer.toString(), outer2.toString());
  }

  @Test
  public void shoudlHandleCyclicDependencies() {
    Object main = loader.require("./lib/cyclic");
    assertEquals("Hello from A", (getMember(getMember(main, "a"), "fromA", String.class)));
    assertEquals("Hello from B", (getMember(getMember(main, "b"), "fromB", String.class)));
  }
}
