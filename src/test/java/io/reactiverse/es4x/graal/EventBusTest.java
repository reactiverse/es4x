package io.reactiverse.es4x.graal;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class EventBusTest {

  private static final Vertx vertx = Vertx.vertx();
  private static Loader loader;

  @BeforeClass
  public static void beforeClass() {
    loader = new Loader(vertx);
    loader.eval("(function (k, v) { global[k] = v; })").execute("eb", vertx.eventBus());
  }

  @Test(timeout = 10000)
  public void testNativeJSObjectOverEB(TestContext ctx) {
    final Async async = ctx.async();

    vertx.eventBus().consumer("test.address.object", msg -> {
      ctx.assertNotNull(msg);
      ctx.assertNotNull(msg.body());
      Object res = msg.body();
      ctx.assertNotNull(res);
      ctx.assertTrue(res instanceof JsonObject);
      async.complete();
    });

    loader.eval("eb.send('test.address.object', {foo: 'bar'})");
    async.await();
  }

  @Test(timeout = 10000)
  public void testNativeJSArrayOverEB(TestContext ctx) {
    final Async async = ctx.async();

    vertx.eventBus().consumer("test.address.array", msg -> {
      ctx.assertNotNull(msg);
      ctx.assertNotNull(msg.body());
      Object res = msg.body();
      ctx.assertNotNull(res);
      ctx.assertTrue(res instanceof JsonArray);
      async.complete();
    });

    loader.eval("eb.send('test.address.array', ['foo', 'bar'])");
    async.await();
  }
}
