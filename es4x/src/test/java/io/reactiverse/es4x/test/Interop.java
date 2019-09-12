package io.reactiverse.es4x.test;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyArray;
import org.graalvm.polyglot.proxy.ProxyObject;

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

  public ProxyObject getMap() {
    return new MyObjectProxy(
      new JsonObject().put("k1", 1)
        .put("k2", "two")
        .put("k3", true)
        .put("k4", new JsonArray().addNull().add(1).add("two")));
  }

  public ProxyArray getList() {
    return new MyArrayProxy(
      new JsonArray()
        .add(1)
        .add(new JsonObject().put("k1", 1)
          .put("k2", "two")
          .put("k3", true)
          .put("k4", new JsonArray().addNull().add(1).add("two"))));
  }
}

class MyObjectProxy implements ProxyObject {

  final JsonObject json;

  MyObjectProxy(JsonObject json) {
    this.json = json;
  }

  @Override
  public Object getMember(String key) {
    Object val = json.getValue(key);
    if (val instanceof JsonArray) {
      return ((JsonArray) val).getList();
    }
    return val;
  }

  @Override
  public Object getMemberKeys() {
    return json.fieldNames().toArray();
  }

  @Override
  public boolean hasMember(String key) {
    return json.containsKey(key);
  }

  @Override
  public void putMember(String key, Value value) {
    json.put(key, value.as(Object.class));
  }

  @Override
  public boolean removeMember(String key) {
    return json.remove(key) != null;
  }
}

class MyArrayProxy implements ProxyArray {

  final JsonArray json;

  MyArrayProxy(JsonArray json) {
    this.json = json;
  }

  @Override
  public Object get(long index) {
    return json.getValue((int) index);
  }

  @Override
  public void set(long index, Value value) {
    System.out.println("HERE!!! " + index);
  }

  @Override
  public boolean remove(long index) {
    return json.remove((int) index) != null;
  }

  @Override
  public long getSize() {
    return json.size();
  }
}
