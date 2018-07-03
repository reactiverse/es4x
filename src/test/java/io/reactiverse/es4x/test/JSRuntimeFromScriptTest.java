package io.reactiverse.es4x.test;

import io.vertx.core.Vertx;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import org.junit.Test;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class JSRuntimeFromScriptTest {

  @Test
  public void testRuntimeCreationFromScript() throws ScriptException, NoSuchMethodException {
    // enable ES6 features
    System.setProperty("nashorn.args", "--language=es6");
    System.setProperty("es4x.engine", "Nashorn");

    // create a engine instance
    NashornScriptEngine engine = (NashornScriptEngine) new ScriptEngineManager().getEngineByName("nashorn");

    engine.invokeFunction("load", "classpath:vertx.js");
    assertNotNull(engine.get("vertx"));
    assertTrue(engine.get("vertx") instanceof Vertx);
    Vertx vertx = (Vertx) engine.get("vertx");
    vertx.close();
  }
}
