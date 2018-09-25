package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
import io.reactiverse.es4x.Loader;
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
  private Loader loader;

  public EventBusTest(String engine) {
    System.setProperty("es4x.engine", engine);
    engineName = engine;
  }

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    loader = Runtime.getCurrent()
      // install the codec
      .registerCodec(rule.vertx())
      // create the loader
      .loader(rule.vertx());

    loader.put("eb", rule.vertx().eventBus());
    assumeTrue(loader.name().equalsIgnoreCase(engineName));
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

    loader.eval("eb.send('test.address.object', {foo: 'bar'})");
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

    loader.eval("eb.send('test.address.array', ['foo', 'bar'])");
  }
}
