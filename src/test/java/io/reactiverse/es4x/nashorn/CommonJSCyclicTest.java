package io.reactiverse.es4x.nashorn;

import io.vertx.core.Vertx;
import jdk.nashorn.api.scripting.JSObject;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import static org.junit.Assert.assertTrue;

public class CommonJSCyclicTest {

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
  public void shouldHaveTheSameSenseOfAnObjectInAllPlaces() {
    JSObject stream = require("./lib/cyclic2/stream.js");
    assertTrue(stream.isFunction());
    JSObject readable = (JSObject) stream.getMember("Readable");
    assertTrue(readable.isFunction());
    JSObject stream2 = (JSObject) readable.getMember("Stream");
    assertTrue(stream2.isFunction());
  }
}
