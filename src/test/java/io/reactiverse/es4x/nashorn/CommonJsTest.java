package io.reactiverse.es4x.nashorn;

import io.vertx.core.Vertx;
import jdk.nashorn.api.scripting.JSObject;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CommonJsTest {

  private static ScriptEngine engine;
  private static JSObject require;

  @BeforeClass
  public static void beforeClass() throws ScriptException, NoSuchMethodException {
    Loader loader = new Loader(Vertx.vertx());
    engine = loader.getEngine();
    require = (JSObject) engine.get("require");
  }

  @SuppressWarnings("unchecked")
  private static <T> T require(String module) {
    return (T) require.call(null, module);
  }

  @Test
  public void shouldThrowAnErrorIfFileCantBeFound() {
    try {
      require("./not_found.js");
      fail();
    } catch (Exception e) {
      assertEquals("Error: Cannot find module ./not_found.js", e.getMessage());
    }
  }

  @Test
  public void shouldNotWrapErrorsEncounteredWhenLoadingModule() {
    try {
      require("./lib/throws");
      fail();
    } catch (Exception e) {
      assertEquals("ReferenceError: \"bar\" is not defined", e.getMessage());
    }
  }

  @Test
  public void shouldSupportNestedRequires() {
    JSObject outer = require("./lib/outer");
    JSObject quadruple = (JSObject) outer.getMember("quadruple");
    Number n = (Number) quadruple.call(outer, 2);
    assertEquals(8, n.intValue());
  }

  @Test
  public void shouldSupportAnIdWithAnExtension() {
    JSObject outer = require("./lib/outer.js");
    JSObject quadruple = (JSObject) outer.getMember("quadruple");
    Number n = (Number) quadruple.call(outer, 2);
    assertEquals(8, n.intValue());
  }

  @Test
  public void shouldReturnDotJsonFileAsJsonObject() {
    JSObject json = require("./lib/some_data.json");
    assertEquals("This is a JSON file", json.getMember("description"));
  }

  @Test
  public void shouldCacheModulesInRequireCache() {
    JSObject outer = require("./lib/outer.js");
    JSObject require = (JSObject) engine.get("require");
    assertEquals(((JSObject) require.getMember("cache")).getMember((String) outer.getMember("filename")), outer);
    JSObject outer2 = require("./lib/outer.js");
    assertEquals(outer, outer2);
  }

  @Test
  public void shoudlHandleCyclicDependencies() {
    JSObject main = require("./lib/cyclic");
    assertEquals("Hello from A", ((JSObject) main.getMember("a")).getMember
      ("fromA"));
    assertEquals("Hello from B", ((JSObject) main.getMember("b")).getMember
      ("fromB"));
  }
}
