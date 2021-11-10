package io.reactiverse.es4x.test;

import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class VerticleGlobalsTest {

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Test(timeout = 30000)
  public void testThatGlobalObjectsArePresentMJS(TestContext ctx) {
    final Async async = ctx.async();
    rule.vertx().deployVerticle("mjs:globals-present.js", deploy -> {
      if (deploy.failed()) {
        ctx.fail(deploy.cause());
      } else {
        ctx.assertTrue(deploy.succeeded());
        rule.vertx().setTimer(1000L, t -> rule.vertx().undeploy(deploy.result(), undeploy -> {
          ctx.assertTrue(undeploy.succeeded());
          async.complete();
        }));
      }
    });
  }

  @Test(timeout = 30000)
  public void testThatGlobalObjectsArePresentJS(TestContext ctx) {
    final Async async = ctx.async();
    rule.vertx().deployVerticle("js:globals-present.js", deploy -> {
      if (deploy.failed()) {
        ctx.fail(deploy.cause());
      } else {
        ctx.assertTrue(deploy.succeeded());
        rule.vertx().setTimer(1000L, t -> rule.vertx().undeploy(deploy.result(), undeploy -> {
          ctx.assertTrue(undeploy.succeeded());
          async.complete();
        }));
      }
    });
  }
}
