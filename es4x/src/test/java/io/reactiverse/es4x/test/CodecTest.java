package io.reactiverse.es4x.test;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class CodecTest {

  @Test(timeout = 10000)
  public void testCodec(TestContext should) {
    final Async test = should.async();
    Vertx.clusteredVertx(new VertxOptions(), clusteredVertx -> {
      should.assertTrue(clusteredVertx.succeeded());
      final Vertx vertx = clusteredVertx.result();

      vertx.eventBus().consumer("test-complete", msg -> {
        should.assertEquals("OK", msg.body());
        vertx.close();
        test.complete();
      });

      vertx.deployVerticle("js:cluster/receiver.js", deployReceiver -> {
        should.assertTrue(deployReceiver.succeeded());

        vertx.deployVerticle("js:cluster/sender.js", deploySender -> {
          should.assertTrue(deploySender.succeeded());
        });
      });
    });
  }
}
