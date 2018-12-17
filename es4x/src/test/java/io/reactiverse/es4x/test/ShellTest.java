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
public class ShellTest {

  @Parameterized.Parameters
  public static List<String> engines() {
    return Arrays.asList("Nashorn", "GraalJS");
  }

  public ShellTest(String engine) {
    System.setProperty("es4x.engine", engine.toLowerCase());
  }

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Test(timeout = 10000)
  public void shouldRunAScript(TestContext should) throws Exception {
    final Async test = should.async();
    System.setProperty("script", "process.properties['result'] = 'OK';");
    rule.vertx().deployVerticle("js:>", deploy -> {
      should.assertTrue(deploy.succeeded());
      rule.vertx().setTimer(300, t -> {
        should.assertEquals("OK", System.getProperty("result"));
        test.complete();
      });
    });
  }
}
