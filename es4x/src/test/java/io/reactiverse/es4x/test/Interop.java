package io.reactiverse.es4x.test;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.time.Instant;

public class Interop {

  public void printObject(JsonObject json) {
    System.out.println(json.encodePrettily());
  }

  public void printArray(JsonArray json) {
    System.out.println(json.encodePrettily());
  }

  public void printInstant(Instant json) {
    System.out.println(json);
  }
}
