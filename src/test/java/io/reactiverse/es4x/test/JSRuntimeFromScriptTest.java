package io.reactiverse.es4x.test;

import io.vertx.core.Vertx;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import org.graalvm.polyglot.Context;
import org.junit.Test;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

public class JSRuntimeFromScriptTest {

  @Test
  public void testRuntimeCreationFromNashornScript() throws ScriptException, NoSuchMethodException {
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

  @Test
  public void testRuntimeCreationFromGraalJSScript() {
    System.setProperty("es4x.engine", "GraalJS");

    Context context;

    try {
      // create a context instance
      context = Context
        .newBuilder("js")
        .allowHostAccess(true)
        .allowCreateThread(true)
        .allowAllAccess(false)
        .allowHostClassLoading(false)
        .allowIO(false)
        .allowNativeAccess(false)
        .build();
    } catch (IllegalStateException e) {
      assumeTrue("GraalJS is not available", false);
      return;
    }

    context.eval("js", "load('classpath:vertx.js')");

    assertFalse(context.getBindings("js").getMember("vertx").isNull());
    assertTrue(context.getBindings("js").getMember("vertx").asHostObject() instanceof Vertx);
    Vertx vertx = context.getBindings("js").getMember("vertx").asHostObject();
    vertx.close();
    context.close();
  }
}
