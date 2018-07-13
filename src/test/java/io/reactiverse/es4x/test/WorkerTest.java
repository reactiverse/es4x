package io.reactiverse.es4x.test;

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
public class WorkerTest {

  @Parameterized.Parameters
  public static List<String> engines() {
    return Arrays.asList("Nashorn", "GraalVM");
  }

  final String engineName;
  private Loader loader;

  public WorkerTest(String engine) {
    System.setProperty("es4x.engine", engine);
    engineName = engine;
  }

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    loader = Loader.create(rule.vertx());
    assumeTrue(loader.name().equalsIgnoreCase(engineName));
  }

  @Test(timeout = 30000)
  public void testWorkerLoading(TestContext ctx) throws Exception {
    final Async async = ctx.async();

    loader.put("ctx", ctx);
    loader.put("async", async);

    // @Language=JavaScript
    String script =
      "var worker = new Worker('workers/worker.js');\n" +
      "console.log(worker);\n" +
      "worker.onmessage = function (msg) {\n" +
      "  async.complete();\n" +
      "};\n" +
      "console.log('posting...');\n" +
      "worker.postMessage([2, 3]);";

    loader.eval(script);
  }
}
