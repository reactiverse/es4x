package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static io.reactiverse.es4x.test.JS.commonjs;
import static io.reactiverse.es4x.test.JS.require;

@RunWith(VertxUnitRunner.class)
public class FutureTest {

  private Runtime runtime;

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    runtime = commonjs(rule.vertx());
  }

  @Test(timeout = 10000)
  public void testJavaThenable(TestContext should) {
    final Async test = should.async();

    // make it available to the script
    runtime.put("should", should);
    runtime.put("test", test);
    // run a small script
    require(runtime, "./future/future.js");
  }

  @Test(timeout = 10000)
  public void testCatchFutureAsPromise(TestContext should) {
    final Async test = should.async();

    // make it available to the script
    runtime.put("should", should);
    runtime.put("test", test);
    // run a small script
    require(runtime, "./future/future2.js");
  }

  @Test(timeout = 10000)
  public void testIssue184(TestContext should) {
    final Async test = should.async();

    // make it available to the script
    runtime.put("should", should);
    runtime.put("test", test);
    // run a small script
    require(runtime, "./future/issue184.js");
  }

  @Test(timeout = 10000)
  public void testJavaFutureAsThenable(TestContext should) {
    final Async test = should.async();

    // make it available to the script
    runtime.put("should", should);
    runtime.put("test", test);
    // run a small script
    require(runtime, "./future/future3.js");
  }

  public static class FutureTest2 {

    final TestContext should;
    final Vertx vertx;
    final Async test;

    final AtomicInteger ok = new AtomicInteger(3);
    final AtomicInteger fail = new AtomicInteger(3);

    FutureTest2(Vertx vertx, TestContext should, Async test) {
      this.should = should;
      this.vertx = vertx;
      this.test = test;
    }

    public void respond(Function<Object, Future<?>> fn) {
      should.assertNotNull(fn);
      String expected = "OK";
      fn.apply(expected)
        .onSuccess(res -> {
          should.assertEquals(expected, res);
          if (ok.decrementAndGet() == 0) {
            test.complete();
          }
        })
        .onFailure(should::fail);
    }

    public void fail(Function<Object, Future<?>> fn) {
      should.assertNotNull(fn);
      String expected = "OK";
      fn.apply(expected)
        .onSuccess(val -> should.fail("Should not pass"))
        .onFailure(err -> {
          err.printStackTrace();
          if (fail.decrementAndGet() == 0) {
            test.complete();
          }
        });
    }
  }

  @Test(timeout = 10000)
  public void testFunctionWithFuture(TestContext should) {
    final Async test = should.async();

    final FutureTest2 mock = new FutureTest2(rule.vertx(), should, test);

    mock.respond(Future::succeededFuture);

    // make it available to the script
    runtime.put("should", should);
    runtime.put("test", test);
    runtime.put("mock", mock);
    // run a small script
    require(runtime, "./future/response.js");
  }
}
