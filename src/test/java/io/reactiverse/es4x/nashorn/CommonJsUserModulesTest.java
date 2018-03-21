package io.reactiverse.es4x.nashorn;

import io.vertx.core.Vertx;
import jdk.nashorn.api.scripting.JSObject;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import static org.junit.Assert.assertEquals;

public class CommonJsUserModulesTest {

  private static JSObject require;

  @SuppressWarnings("unchecked")
  private static <T> T require(String module) {
    return (T) require.call(null, module);
  }

  @BeforeClass
  public static void beforeClass() throws ScriptException, NoSuchMethodException {
    Loader loader = new Loader(Vertx.vertx());
    ScriptEngine engine = loader.getEngine();
    require = (JSObject) engine.get("require");
  }

  @Test
  public void shouldFindPackageJsonInModuleFolder() {
    JSObject packageJson = require("./lib/other_package");
    assertEquals("cool ranch", packageJson.getMember("flavor"));
    assertEquals("jar:/lib/other_package/lib/subdir", packageJson.getMember("subdir"));
  }

  @Test
  public void shouldLoadPackageJsonMainPropertyEvenIfItIsDirectory() {
    JSObject cheese = require( "./lib/cheese");
    assertEquals("nacho", cheese.getMember("flavor"));
  }

  @Test
  public void shouldFindIndexJsInDirectoryIfNoPackageJsonExists() {
    JSObject packageJson = require("./lib/my_package");
    assertEquals("Hello!", packageJson.getMember("data"));
  }
}
