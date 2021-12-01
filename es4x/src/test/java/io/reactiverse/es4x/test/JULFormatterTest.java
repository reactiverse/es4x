package io.reactiverse.es4x.test;

import io.reactiverse.es4x.jul.ANSIFormatter;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

@RunWith(VertxUnitRunner.class)
public class JULFormatterTest {

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
      ANSIFormatter formatter = new ANSIFormatter();
      LogRecord item = new LogRecord(Level.ALL, "message");
      item.setThrown(deploy.cause());
      String out = formatter.format(item);
      System.out.println(out);

      try (StringWriter sw = new StringWriter()) {
        PrintWriter pw = new PrintWriter(sw);
        ANSIFormatter.printStackTrace(deploy.cause(), pw);
        System.out.println(sw);
      } catch (IOException e) {
        // ignore
      }
      async.complete();
    });
  }
}
