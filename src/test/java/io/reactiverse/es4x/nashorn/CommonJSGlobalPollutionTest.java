package io.reactiverse.es4x.nashorn;

import io.vertx.core.Vertx;
import jdk.nashorn.api.scripting.JSObject;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import static org.junit.Assert.*;

public class CommonJSGlobalPollutionTest {

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
  public void shouldHaveSideEffects() {
    try {
      // this test verifies that the pollution of the global context behaves like on node
      require("./pollution/a.js");
      fail("should throw");
    } catch (RuntimeException e) {
      assertEquals("Error: engine is tainted: b", e.getMessage());
    }
  }
}
