package io.reactiverse.es4x.nashorn;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

@RunWith(VertxUnitRunner.class)
public class GlobalsTest {

  private static ScriptEngine engine;

  @BeforeClass
  public static void beforeClass() throws ScriptException, NoSuchMethodException {
    Loader loader = new Loader(Vertx.vertx());
    engine = loader.getEngine();
  }

  @Test(timeout = 10000)
  public void testSetTimeout(TestContext ctx) throws ScriptException {
    final Async async = ctx.async();

    engine.put("ctx", ctx);
    engine.put("async", async);

    /// @language=JavaScript
    String script =
      "setTimeout(function () {" +
      "  async.complete();" +
      "}, 1);";


    engine.eval(script);
    async.await();
  }

  @Test(timeout = 10000)
  public void testSetTimeout0(TestContext ctx) throws ScriptException {
    final Async async = ctx.async();

    engine.put("ctx", ctx);
    engine.put("async", async);

    /// @language=JavaScript
    String script =
      "setTimeout(function () {" +
        "  async.complete();" +
        "}, 0);";


    engine.eval(script);
    async.await();
  }

  @Test(timeout = 10000)
  public void testSetTimeoutWithParams(TestContext ctx) throws ScriptException {
    final Async async = ctx.async();

    engine.put("ctx", ctx);
    engine.put("async", async);

    /// @language=JavaScript
    String script =
      "setTimeout(function (msg) {" +
        "  ctx.assertEquals('durp!', msg);" +
        "  async.complete();" +
        "}, 1, 'durp!');";


    engine.eval(script);
    async.await();
  }
}
