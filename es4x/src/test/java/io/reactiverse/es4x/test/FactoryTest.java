package io.reactiverse.es4x.test;

import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class FactoryTest {

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Test(timeout = 10000)
  public void shouldDeployVerticle(TestContext ctx) {
    final Async async = ctx.async();
    rule.vertx().deployVerticle("js:./verticle.js", deploy -> {
      ctx.assertTrue(deploy.succeeded());
      async.complete();
    });
  }

  @Test(timeout = 10000)
  public void shouldDeployVerticleWithOnStop(TestContext ctx) {
    final Async async = ctx.async();
    rule.vertx().deployVerticle("js:./verticle2.js", deploy -> {
      ctx.assertTrue(deploy.succeeded());
      rule.vertx().setTimer(1000L, t -> rule.vertx().undeploy(deploy.result(), undeploy -> {
        ctx.assertTrue(undeploy.succeeded());
        async.complete();
      }));
    });
  }

  @Test(timeout = 10000)
  public void shouldDeployVerticleWithOnStopAsync(TestContext ctx) {
    final Async async = ctx.async();
    rule.vertx().deployVerticle("js:./verticle4.js", deploy -> {
      ctx.assertTrue(deploy.succeeded());
      rule.vertx().setTimer(1000L, t -> rule.vertx().undeploy(deploy.result(), undeploy -> {
        ctx.assertTrue(undeploy.succeeded());
        async.complete();
      }));
    });
  }

  @Test(timeout = 10000)
  public void shouldDeployVerticleWithoutOnStop(TestContext ctx) {
    final Async async = ctx.async();
    rule.vertx().deployVerticle("js:./verticle.js", deploy -> {
      ctx.assertTrue(deploy.succeeded());
      rule.vertx().undeploy(deploy.result(), undeploy -> {
        ctx.assertTrue(undeploy.succeeded());
        async.complete();
      });
    });
  }
}
