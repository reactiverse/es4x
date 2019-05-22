package io.reactiverse.es4x.test;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.unit.junit.VertxUnitRunnerWithParametersFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(VertxUnitRunnerWithParametersFactory.class)
public class CodecTest {

  @Parameterized.Parameters
  public static List<String> engines() {
    return Arrays.asList("Nashorn", "GraalJS");
  }

  public CodecTest(String engine) {
    System.setProperty("es4x.engine", engine.toLowerCase());
  }

  @Test(timeout = 10000)
  public void testAlias(TestContext should) throws Exception {
    final Async test = should.async();
    Vertx.clusteredVertx(new VertxOptions(), clusteredVertx -> {
      should.assertTrue(clusteredVertx.succeeded());
      final Vertx vertx = clusteredVertx.result();

      vertx.deployVerticle("js:cluster/receiver.js", deployReceiver -> {
        should.assertTrue(deployReceiver.succeeded());

        vertx.deployVerticle("js:cluster/sender.js", deploySender -> {
          should.assertTrue(deploySender.succeeded());
          vertx.close();
          test.complete();
        });
      });
    });
  }
}
