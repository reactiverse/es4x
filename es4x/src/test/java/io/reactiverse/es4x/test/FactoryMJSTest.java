package io.reactiverse.es4x.test;

import io.vertx.core.DeploymentOptions;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class FactoryMJSTest {

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Test(timeout = 10000)
  public void shouldDeployVerticle(TestContext ctx) {
    final Async async = ctx.async();
    rule.vertx().deployVerticle("mjs:./verticle.mjs", deploy -> {
      ctx.assertTrue(deploy.succeeded());
      async.complete();
    });
  }

  @Test(timeout = 10000)
  public void shouldDeployVerticleWithOnStop(TestContext ctx) {
    final Async async = ctx.async();
    rule.vertx().deployVerticle("mjs:./verticle2.mjs", new DeploymentOptions().setInstances(8), deploy -> {
      ctx.assertTrue(deploy.succeeded());
      rule.vertx().setTimer(1000L, t -> rule.vertx().undeploy(deploy.result(), undeploy -> {
        ctx.assertTrue(undeploy.succeeded());
        async.complete();
      }));
    });
  }

  @Test(timeout = 30000)
  public void shouldDeployVerticleWithMod(TestContext ctx) {
    final Async async = ctx.async();
    rule.vertx().deployVerticle("mjs:./online.mjs", deploy -> {
      if (deploy.failed()) {
        deploy.cause().printStackTrace();
      }

      ctx.assertTrue(deploy.succeeded());
      rule.vertx().setTimer(1000L, t -> rule.vertx().undeploy(deploy.result(), undeploy -> {
        ctx.assertTrue(undeploy.succeeded());
        async.complete();
      }));
    });
  }

  @Test(timeout = 30000)
  public void deployUnderSubdirectoryAndPathsStillBeCorrect(TestContext ctx) {
    final Async async = ctx.async();
    rule.vertx().deployVerticle("mjs:./lib/main.spec.mjs", deploy -> {
      if (deploy.failed()) {
        deploy.cause().printStackTrace();
      }

      ctx.assertTrue(deploy.succeeded());
      rule.vertx().setTimer(1000L, t -> rule.vertx().undeploy(deploy.result(), undeploy -> {
        ctx.assertTrue(undeploy.succeeded());
        async.complete();
      }));
    });
  }
}
