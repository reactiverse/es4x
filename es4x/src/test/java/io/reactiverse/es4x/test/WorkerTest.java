package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
import io.reactiverse.es4x.impl.graal.GraalEngine;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class WorkerTest {

  private Runtime runtime;

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    runtime = new GraalEngine(rule.vertx()).newContext();
  }

  @Test(timeout = 30000)
  public void testWorkerLoading(TestContext ctx) throws Exception {
    final Async async = ctx.async();

    runtime.put("ctx", ctx);
    runtime.put("async", async);

    // @language=JavaScript
    String script =
      "Worker.create('workers/worker.js', function (create) {" +
//        "console.log(create.cause());\n" +
        "var worker = create.result();\n" +
        "worker.onmessage = function (msg) {\n" +
        "  console.log('onmessage: ' + msg)\n" +
        "  async.complete();\n" +
        "};\n" +
        "console.log('posting...');\n" +
        "worker.postMessage({data: [2, 3]});\n" +
      "});\n";

    runtime.eval(script);
  }
}
