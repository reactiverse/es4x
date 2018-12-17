package io.reactiverse.es4x.test;

import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunnerWithParametersFactory;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(VertxUnitRunnerWithParametersFactory.class)
public class FactoryTest {

  @Parameterized.Parameters
  public static List<String> engines() {
    return Arrays.asList("Nashorn", "GraalJS");
  }

  public FactoryTest(String engine) {
    System.setProperty("es4x.engine", engine.toLowerCase());
  }

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
