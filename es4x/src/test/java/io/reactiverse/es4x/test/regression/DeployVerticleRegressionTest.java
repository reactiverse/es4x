package io.reactiverse.es4x.test.regression;

import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class DeployVerticleRegressionTest {

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Test(timeout = 10000)
  public void shouldDeployVerticle(TestContext ctx) {
    final Async async = ctx.async();
    rule.vertx().deployVerticle("js:./regressions/deployVerticle.js", deploy -> {
      ctx.assertTrue(deploy.succeeded());
      async.complete();
    });
  }
}
