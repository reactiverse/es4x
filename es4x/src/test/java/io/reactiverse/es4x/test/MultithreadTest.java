package io.reactiverse.es4x.test;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunnerWithParametersFactory;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(VertxUnitRunnerWithParametersFactory.class)
public class MultithreadTest {

  @Parameterized.Parameters
  public static List<String> engines() {
    // Graal is disabled on purpose as it will not allow MT under JS
    // the workaround is to use the Worker API which deploys everything in the worker pool
    return Arrays.asList("Nashorn" /*, "GraalJS" */);
  }

  public MultithreadTest(String engine) {
    System.setProperty("es4x.engine", engine.toLowerCase());
  }

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Test(timeout = 10000)
  public void shouldNotPoluteMTState(TestContext ctx) {
    final Vertx vertx = rule.vertx();
    final Async async = ctx.async();
    // install global exception handler
    vertx.exceptionHandler(ctx::fail);
    // deploy 8 instances
    vertx.deployVerticle("js:./mt-verticle.js", new DeploymentOptions().setInstances(8), deploy -> {
      if (deploy.failed()) {
        // log the error (trying to find out why it fails on j9 travis)
        deploy.cause().printStackTrace();
      }
      ctx.assertTrue(deploy.succeeded());

      // will wait 1 second to let things start...
      vertx.setTimer(1000L, v -> {
        final AtomicInteger cnt = new AtomicInteger(50);

        // run 50 runs
        for(int i = 0; i < 50; i++) {
          vertx.eventBus().<JsonObject>send("es4x.mt.test", new JsonObject(), ar -> {
            if (ar.failed()) {
              ctx.fail(ar.cause());
              return;
            }

            int result = ar.result().body().getInteger("result");
            if(result != 2) {
              ctx.fail("Incorrect result from js, expected 1 + 1 = 2, but got: " + result);
              return;
            }

            if (cnt.decrementAndGet() == 0) {
              async.complete();
            }
          });
        }
      });
    });
  }
}
