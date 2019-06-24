package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
import io.reactiverse.es4x.impl.graal.GraalEngine;
import io.vertx.core.Future;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class FutureTest {

  private Runtime runtime;

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    runtime = new GraalEngine(rule.vertx()).newContext();
  }

  @Test(timeout = 10000)
  public void testWrapFutureAsPromise(TestContext should) throws Exception {
    final Async test = should.async();

    Future future = Future.future();
    // make it available to the script
    runtime.put("future", future);
    runtime.put("test", test);
    // run a small script
    runtime.eval(
      "new Promise(future).then((successMessage) => {\n" +
      "  print(\"Yay! \" + successMessage);\n" +
      "  test.complete();\n" +
      "});");

    rule.vertx().setTimer(250L, t -> future.complete("Success!"));
  }

  @Test(timeout = 10000)
  public void testThenFutureAsPromise(TestContext should) throws Exception {
    final Async test = should.async();

    Future future = Future.future().setHandler(ar -> {
      should.assertTrue(ar.succeeded());
      should.assertEquals("yay", ar.result());
      test.complete();
    });

    // make it available to the script
    runtime.put("future", future);
    // run a small script
    runtime.eval("Promise.resolve('yay').then(future)");
  }
}
