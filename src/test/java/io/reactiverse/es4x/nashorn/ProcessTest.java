package io.reactiverse.es4x.nashorn;

import io.vertx.core.Vertx;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.Map;

public class ProcessTest {

  private static ScriptEngine engine;

  @BeforeClass
  public static void beforeClass() throws ScriptException, NoSuchMethodException {
    Loader loader = new Loader(Vertx.vertx());
    engine = loader.getEngine();
  }

  @Test(timeout = 10000)
  public void testProcessEnv() throws ScriptException {
    Map<String, String> env = (Map<String, String>) engine.eval("process.env");

    for (Map.Entry<String, String> kv : env.entrySet()) {
      System.out.println(kv.getKey() + ": " + kv.getValue());
    }
  }
}
