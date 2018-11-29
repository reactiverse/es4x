package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
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
public class GlobalsTest {

  @Parameterized.Parameters
  public static List<String> engines() {
    return Arrays.asList("Nashorn", "GraalJS");
  }

  private final String engineName;
  private Runtime runtime;

  public GlobalsTest(String engine) {
    System.setProperty("es4x.engine", engine);
    engineName = engine;
  }

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    try {
      runtime = Runtime.getCurrent(rule.vertx());
    } catch (IllegalStateException e) {
      assumeTrue(engineName + " is not available", false);
    }
  }

  @Test(timeout = 10000)
  public void testSetTimeout(TestContext ctx) throws Exception {
    final Async async = ctx.async();

    runtime.put("ctx", ctx);
    runtime.put("async", async);

    /// @language=JavaScript
    String script =
      "setTimeout(function () {" +
      "  async.complete();" +
      "}, 1);";


    runtime.eval(script);
  }

  @Test(timeout = 10000)
  public void testSetTimeout0(TestContext ctx) throws Exception {
    final Async async = ctx.async();

    runtime.put("ctx", ctx);
    runtime.put("async", async);

    /// @language=JavaScript
    String script =
      "setTimeout(function () {" +
        "  async.complete();" +
        "}, 0);";


    runtime.eval(script);
  }

  @Test(timeout = 10000)
  public void testSetTimeoutWithParams(TestContext ctx) throws Exception {
    final Async async = ctx.async();

    runtime.put("ctx", ctx);
    runtime.put("async", async);

    /// @language=JavaScript
    String script =
      "setTimeout(function (msg) {" +
        "  ctx.assertEquals('durp!', msg);" +
        "  async.complete();" +
        "}, 1, 'durp!');";


    runtime.eval(script);
  }

  @Test(timeout = 10000)
  public void testDateToInstant(TestContext ctx) throws Exception {
    runtime.eval("console.log(new Date().toInstant());");
  }
}
