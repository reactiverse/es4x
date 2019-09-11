package io.reactiverse.es4x.test;

import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.time.Instant;
import java.util.List;
import java.util.Map;

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

  public Map getMap() {
    return new JsonObject().put("k1", 1)
      .put("k2", "two")
      .put("k3", true)
      .put("k4", new JsonArray().addNull().add(1).add("two"))
      .getMap();
  }

  public List getList() {
    return new JsonArray()
      .add(1)
      .add(new JsonObject().put("k1", 1)
        .put("k2", "two")
        .put("k3", true)
        .put("k4", new JsonArray().addNull().add(1).add("two")))
      .getList();
  }
}
