package io.reactiverse.es4x.test;

import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Set;
import java.util.function.Function;

public class Mapping {

  public void map(JsonObject object) {
    System.out.println("JsonObject");
    System.out.println(object);
  }

  public void mapJsonObject(JsonObject object) {
    System.out.println("JsonObject");
    System.out.println(object);
  }

  public void map(JsonArray object) {
    System.out.println("JsonArray");
    System.out.println(object);
  }

  public void mapJsonArray(JsonArray object) {
    System.out.println("JsonArray");
    System.out.println(object);
  }

  public void map(Throwable object) {
    System.out.println("Throwable");
    System.out.println(object);
  }

  public void mapThrowable(Throwable object) {
    System.out.println("Throwable");
    System.out.println(object);
  }

  public void map(Byte object) {
    System.out.println("Byte");
    System.out.println(object);
  }

  public void mapByte(Byte object) {
    System.out.println("Byte");
    System.out.println(object);
  }

  public void map(Set object) {
    System.out.println("Set");
    System.out.println(object);
  }

  public void mapSet(Set object) {
    System.out.println("Set");
    System.out.println(object);
  }

  public void map(Buffer object) {
    System.out.println("Buffer");
    System.out.println(object);
  }

  public void mapBuffer(Buffer object) {
    System.out.println("Buffer");
    System.out.println(object);
  }

  public void map(Function<Object, Future<String>> object) {
    System.out.println("Function");
    System.out.println(object);
    object.apply("OK")
      .onFailure(Throwable::printStackTrace)
      .onSuccess(System.out::println);
  }

  public void mapFunction(Function<Object, Future<String>> object) {
    System.out.println("Function");
    System.out.println(object);
    object.apply("OK")
      .onFailure(Throwable::printStackTrace)
      .onSuccess(System.out::println);
  }

  public void mapErrFunction(Function<Object, Future<String>> object) {
    System.out.println("Function");
    System.out.println(object);
    object.apply("OK")
      .onSuccess(res -> {
        throw new RuntimeException("should not happen");
      })
      .onFailure(Throwable::printStackTrace);
  }

  public void mapObject(Object object) {
    System.out.println("Object");
    System.out.println(object);
  }
}
