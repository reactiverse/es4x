package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunnerWithParametersFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assume.assumeTrue;

@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(VertxUnitRunnerWithParametersFactory.class)
public class EventBusTest {

  @Parameterized.Parameters
  public static List<String> engines() {
    return Arrays.asList("Nashorn", "GraalJS");
  }

  private final String engineName;
  private Runtime runtime;

  public EventBusTest(String engine) {
    System.setProperty("es4x.engine", engine);
    engineName = engine;
  }

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    try {
      runtime = Runtime.getCurrent(rule.vertx());

      runtime.put("eb", rule.vertx().eventBus());
    } catch (IllegalStateException e) {
      assumeTrue(engineName + " is not available", false);
    }
  }

  @Test(timeout = 10000)
  public void testNativeJSObjectOverEB(TestContext ctx) throws Exception {
    final Async async = ctx.async();

    rule.vertx().eventBus().consumer("test.address.object", msg -> {
      ctx.assertNotNull(msg);
      ctx.assertNotNull(msg.body());
      Object res = msg.body();
      ctx.assertNotNull(res);
      ctx.assertTrue(res instanceof JsonObject);
      ctx.assertEquals("bar", ((JsonObject) res).getString("foo"));
      async.complete();
    });

    runtime.eval("eb.send('test.address.object', {foo: 'bar'})");
  }

  @Test(timeout = 10000)
  public void testNativeJSArrayOverEB(TestContext ctx) throws Exception {
    final Async async = ctx.async();

    rule.vertx().eventBus().consumer("test.address.array", msg -> {
      ctx.assertNotNull(msg);
      ctx.assertNotNull(msg.body());
      Object res = msg.body();
      ctx.assertNotNull(res);
      ctx.assertTrue(res instanceof JsonArray);
      ctx.assertTrue(res instanceof JsonArray);
      ctx.assertEquals("foo", ((JsonArray) res).getString(0));
      ctx.assertEquals("bar", ((JsonArray) res).getString(1));
      async.complete();
    });

    runtime.eval("eb.send('test.address.array', ['foo', 'bar'])");
  }
}
