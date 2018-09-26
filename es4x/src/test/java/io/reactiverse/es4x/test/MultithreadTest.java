package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
import io.reactiverse.es4x.Loader;
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

import static org.junit.Assume.assumeTrue;

@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(VertxUnitRunnerWithParametersFactory.class)
public class MultithreadTest {

  @Parameterized.Parameters
  public static List<String> engines() {
    // Graal is disabled on purpose as it will not allow MT under JS
    // the workaround is to use the Worker API which deploys everything in the worker pool
    return Arrays.asList("Nashorn" /*, "GraalJS" */);
  }

  private final String engineName;

  public MultithreadTest(String engine) {
    System.setProperty("es4x.engine", engine);
    engineName = engine;
  }

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    Loader loader = Runtime.getCurrent().loader(rule.vertx());
    assumeTrue(loader.name().equalsIgnoreCase(engineName));
    loader.close();
  }

  @Test(timeout = 10000)
  public void shouldNotPoluteMTState(TestContext ctx) {
    final Vertx vertx = rule.vertx();
    final Async async = ctx.async();
    // install global exception handler
    vertx.exceptionHandler(ctx::fail);
    // deploy 8 instances
    vertx.deployVerticle("js:./mt-verticle.js", new DeploymentOptions().setInstances(8), deploy -> {
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
