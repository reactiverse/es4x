package io.reactiverse.es4x.nashorn;

import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

public class LoggerTest {

  Logger log = LoggerFactory.getLogger(LoggerTest.class);

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
      throw new RuntimeException();
    } catch (RuntimeException e) {
      log.error("Oops!", e);
    }
  }

  @Test
  public void shouldPrettyPrintException() throws NoSuchMethodException {
    JSObject module = require("./exp.js");
    try {
      ((NashornScriptEngine) engine).invokeMethod(module, "a");
    } catch (ScriptException e) {
      log.error("Error from Script", e);
    }
  }
}
