package io.reactiverse.es4x.test;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyArray;
import org.graalvm.polyglot.proxy.ProxyObject;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

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

  public void printSet(Set<String> json) {
    System.out.println(json.toArray());
  }

  public void printThrowable(Throwable throwable) {
    throwable.printStackTrace();
  }

  public void shouldBaAList(Object obj) {
    if (!(obj instanceof List)) {
      throw new RuntimeException("obj not List");
    }
  }

  public void shouldBaAMap(Object obj) {
    if (!(obj instanceof Map)) {
      throw new RuntimeException("obj not Map");
    }
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

  public void passBytes(byte[] data) {
    assertEquals(4, data.length);
    assertEquals((byte) 0xca, data[0]);
    assertEquals((byte) 0xfe, data[1]);
    assertEquals((byte) 0xba, data[2]);
    assertEquals((byte) 0xbe, data[3]);
  }

  public void passByte(byte data) {
    assertEquals((byte) 0xca, data);
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
