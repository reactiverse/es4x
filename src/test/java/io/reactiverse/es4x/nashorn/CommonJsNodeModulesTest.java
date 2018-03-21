package io.reactiverse.es4x.nashorn;

import io.vertx.core.Vertx;
import jdk.nashorn.api.scripting.JSObject;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class CommonJsNodeModulesTest {

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
  public void shouldLoadFileModulesFromTheNode_modulesFolderInCwd() {
    JSObject top = require("./lib/a_package");
    assertEquals("Hello from a file module", top.getMember("file_module"));
  }

  @Test
  public void shouldLoadPackageModulesFromNode_modulesFolder() {
    JSObject top = require("./lib/a_package");
    assertEquals("Hello from a package module", ((JSObject) top.getMember
      ("pkg_module")).getMember("pkg"));
  }

  @Test
  public void shouldFindNode_modulePackagesInTheParentPath() {
    JSObject top = require("./lib/a_package");
    assertEquals("Hello from a file module", ((JSObject) top.getMember
      ("pkg_module")).getMember("file"));
  }

  @Test
  public void shouldFindNode_modulePackagesFromSiblingPath() {
    JSObject top = require("./lib/a_package");
    assertFalse((Boolean) ((JSObject) top.getMember("parent_test")).getMember("parentChanged"));
  }

  @Test
  public void shouldFindNode_modulePackagesAllTheWayUpAboveCwd() {
    JSObject m = require("root_module");
    assertEquals("You are at the root", m.getMember("message"));
  }
}
