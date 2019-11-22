package io.reactiverse.es4x.test.manual;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

public class Receiver {

  public static void main(String[] args) {
    Vertx.clusteredVertx(new VertxOptions(), clusteredVertx -> {
      if (clusteredVertx.failed()) {
        clusteredVertx.cause().printStackTrace();
        System.exit(1);
      }

      final Vertx vertx = clusteredVertx.result();

      vertx.deployVerticle("js:cluster/receiver.js", deployReceiver -> {
        if (deployReceiver.failed()) {
          deployReceiver.cause().printStackTrace();
          System.exit(1);
        }
      });
    });
  }
}
