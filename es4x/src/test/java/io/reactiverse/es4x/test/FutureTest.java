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
public class FutureTest {

  private Runtime runtime;

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    runtime = new GraalEngine(rule.vertx()).newContext();
  }

  @Test(timeout = 10000)
  public void testJavaThenable(TestContext should) {
    final Async test = should.async();

    // make it available to the script
    runtime.put("should", should);
    runtime.put("test", test);
    // run a small script
    runtime.require("./future/future.js");
  }

  @Test(timeout = 10000)
  public void testCatchFutureAsPromise(TestContext should) {
    final Async test = should.async();

    // make it available to the script
    runtime.put("should", should);
    runtime.put("test", test);
    // run a small script
    runtime.require("./future/future2.js");
  }
}
