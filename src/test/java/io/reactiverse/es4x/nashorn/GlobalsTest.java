package io.reactiverse.es4x.nashorn;

import io.reactiverse.es4x.Loader;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunnerWithParametersFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assume.assumeTrue;

@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(VertxUnitRunnerWithParametersFactory.class)
public class GlobalsTest {

  @Parameterized.Parameters
  public static List<String> engines() {
    return Arrays.asList("Nashorn", "GraalVM");
  }

  final String engineName;
  final Loader loader;

  public GlobalsTest(String engine) {
    System.setProperty("es4x.engine", engine);
    engineName = engine;
    loader = Loader.create(Vertx.vertx());
  }

  @Before
  public void initialize() {
    assumeTrue(loader.name().equalsIgnoreCase(engineName));
  }

  @Test(timeout = 10000)
  public void testSetTimeout(TestContext ctx) throws Exception {
    final Async async = ctx.async();

    loader.put("ctx", ctx);
    loader.put("async", async);

    /// @language=JavaScript
    String script =
      "setTimeout(function () {" +
      "  async.complete();" +
      "}, 1);";


    loader.eval(script);
    async.await();
  }

  @Test(timeout = 10000)
  public void testSetTimeout0(TestContext ctx) throws Exception {
    final Async async = ctx.async();

    loader.put("ctx", ctx);
    loader.put("async", async);

    /// @language=JavaScript
    String script =
      "setTimeout(function () {" +
        "  async.complete();" +
        "}, 0);";


    loader.eval(script);
    async.await();
  }

  @Test(timeout = 10000)
  public void testSetTimeoutWithParams(TestContext ctx) throws Exception {
    final Async async = ctx.async();

    loader.put("ctx", ctx);
    loader.put("async", async);

    /// @language=JavaScript
    String script =
      "setTimeout(function (msg) {" +
        "  ctx.assertEquals('durp!', msg);" +
        "  async.complete();" +
        "}, 1, 'durp!');";


    loader.eval(script);
    async.await();
  }
}
