package io.reactiverse.es4x.test;

import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class ShellTest {

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Test(timeout = 10000)
  public void shouldRunAScript(TestContext should) {
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
