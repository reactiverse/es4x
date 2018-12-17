package io.reactiverse.es4x.test;

import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunnerWithParametersFactory;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.swing.text.html.parser.Parser;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(VertxUnitRunnerWithParametersFactory.class)
public class FilterHostClassTest {

  @Parameterized.Parameters
  public static List<String> engines() {
    return Arrays.asList("Nashorn", "GraalJS");
  }

  public FilterHostClassTest(String engine) {
    System.setProperty("es4x.engine", engine.toLowerCase());
    //System.setProperty("es4x.host.class.filter", "io.vertx.**,java.util.**,java.time.**,java.lang.**,java.net.**,io.reactiverse.es4x.**,!java.nio.file.FileAlreadyExistsException");
    System.setProperty("es4x.host.class.filter", "!java.nio.file.FileAlreadyExistsException");
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
