package io.reactiverse.es4x.nashorn;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import jdk.nashorn.api.scripting.JSObject;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DinalynkTest {

  private static ScriptEngine engine;

  @BeforeClass
  public static void beforeClass() throws ScriptException, NoSuchMethodException {
    Loader loader = new Loader(Vertx.vertx());
    engine = loader.getEngine();
  }

  public static String testMe(JsonObject o) {
    return o.encodePrettily();
  }

  @Test
  public void testCasting() throws ScriptException {
    assertEquals("{\n  \"foo\" : \"bar\"\n}", engine.eval(
      "var DynalinkTest = Java.type('io.reactiverse.es4x.nashorn.DinalynkTest');\n" +
      "DynalinkTest.testMe({foo: 'bar'});\n"));
  }
}
