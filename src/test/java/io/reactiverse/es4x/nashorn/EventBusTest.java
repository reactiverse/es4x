package io.reactiverse.es4x.nashorn;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

@RunWith(VertxUnitRunner.class)
public class EventBusTest {

  private static ScriptEngine engine;
  private static Vertx vertx;

  @BeforeClass
  public static void beforeClass() throws ScriptException, NoSuchMethodException {
    vertx = Vertx.vertx();
    final Loader loader = new Loader(vertx);
    engine = loader.getEngine();
    engine.put("eb", vertx.eventBus());
  }

  @Test(timeout = 10000)
  public void testNativeJSObjectOverEB(TestContext ctx) throws ScriptException {
    final Async async = ctx.async();

    vertx.eventBus().consumer("test.address.object", msg -> {
      ctx.assertNotNull(msg);
      ctx.assertNotNull(msg.body());
      Object res = msg.body();
      ctx.assertNotNull(res);
      ctx.assertTrue(res instanceof JsonObject);
      async.complete();
    });

    engine.eval("eb.send('test.address.object', {foo: 'bar'})");
    async.await();
  }

  @Test(timeout = 10000)
  public void testNativeJSArrayOverEB(TestContext ctx) throws ScriptException {
    final Async async = ctx.async();

    vertx.eventBus().consumer("test.address.array", msg -> {
      ctx.assertNotNull(msg);
      ctx.assertNotNull(msg.body());
      Object res = msg.body();
      ctx.assertNotNull(res);
      ctx.assertTrue(res instanceof JsonArray);
      async.complete();
    });

    engine.eval("eb.send('test.address.array', ['foo', 'bar'])");
    async.await();
  }
}
