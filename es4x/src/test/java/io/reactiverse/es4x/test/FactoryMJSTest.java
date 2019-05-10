package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
import io.vertx.core.DeploymentOptions;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunnerWithParametersFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

import static io.reactiverse.es4x.test.Helper.getRuntime;

@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(VertxUnitRunnerWithParametersFactory.class)
public class FactoryMJSTest {

  @Parameterized.Parameters
  public static List<String> engines() {
    return Arrays.asList(/*"Nashorn", */"GraalJS");
  }

  private final String engineName;
  private Runtime runtime;

  public FactoryMJSTest(String engine) {
    System.setProperty("es4x.engine", engine.toLowerCase());
    engineName = engine;
  }

  @Before
  public void initialize() {
    runtime = getRuntime(rule.vertx(), engineName);
  }

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
}
