package io.reactiverse.es4x.test;

import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class FilterHostClassTest {

  @BeforeClass
  public static void before() {
    //System.setProperty("es4x.host.class.filter", "io.vertx.**,java.util.**,java.time.**,java.lang.**,java.net.**,io.reactiverse.es4x.**,!java.nio.file.FileAlreadyExistsException");
    System.setProperty("es4x.host.class.filter", "!java.nio.file.FileAlreadyExistsException");
  }

  @AfterClass
  public static void after() {
    System.getProperties().remove("es4x.host.class.filter");
  }

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Test(timeout = 10000)
  public void shouldDeployVerticle(TestContext ctx) {
    final Async async = ctx.async();
    rule.vertx().deployVerticle("js:./verticle3.js", deploy -> {
      ctx.assertFalse(deploy.succeeded());
      System.out.println(deploy.cause());
      // fails because we excluded the class "java.nio.file.FileAlreadyExistsException"
      async.complete();
    });
  }
}
