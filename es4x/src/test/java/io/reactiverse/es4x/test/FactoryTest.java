package io.reactiverse.es4x.test;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class FactoryTest {

  private static Vertx vertx;

  @BeforeClass
  public static void beforeClass() {
    System.setProperty("es4x.engine", "Nashorn");
    vertx = Vertx.vertx();
  }

  @AfterClass
  public static void afterClass() {
    vertx.close();
  }

  @Test(timeout = 10000)
  public void shouldDeployVerticle(TestContext ctx) {
    final Async async = ctx.async();
    vertx.deployVerticle("js:./verticle.js", deploy -> {
      ctx.assertTrue(deploy.succeeded());
      async.complete();
    });
  }

  @Test(timeout = 10000)
  public void shouldDeployVerticleWithOnStop(TestContext ctx) {
    final Async async = ctx.async();
    vertx.deployVerticle("js:./verticle2.js", deploy -> {
      ctx.assertTrue(deploy.succeeded());
      vertx.setTimer(1000L, t -> vertx.undeploy(deploy.result(), undeploy -> {
        ctx.assertTrue(undeploy.succeeded());
        async.complete();
      }));
    });
  }

  @Test(timeout = 10000)
  public void shouldDeployVerticleWithoutOnStop(TestContext ctx) {
    final Async async = ctx.async();
    vertx.deployVerticle("js:./verticle.js", deploy -> {
      ctx.assertTrue(deploy.succeeded());
      vertx.undeploy(deploy.result(), undeploy -> {
        ctx.assertTrue(undeploy.succeeded());
        async.complete();
      });
    });
  }
}
