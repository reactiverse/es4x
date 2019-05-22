package io.reactiverse.es4x.test;

import org.graalvm.polyglot.Value;

public final class JS {

  private JS () {
    throw new RuntimeException("Do not instantiate!");
  }

  static boolean isFunction(Object obj) {
    if (obj instanceof Value) {
      return ((Value) obj).canExecute();
    }
    return false;
  }

  static Object getMember(Object obj, String member) {
    if (obj instanceof Value) {
      return ((Value) obj).getMember(member);
    }

    throw new RuntimeException("Cannot get member from: " + obj);
  }

  static <T> T getMember(Object obj, String member, Class<T> asClass) {
    if (obj instanceof Value) {
      return ((Value) obj).getMember(member).as(asClass);
    }

    throw new RuntimeException("Cannot get member from: " + obj);
  }

  static Object call(Object thiz, Object fn, Object... args) {
    if (fn instanceof Value) {
      return ((Value) fn).execute(args);
    }

    throw new RuntimeException("Cannot call: " + fn);
  }

  static <T> T callAs(Object thiz, Object fn, Class<T> asType, Object... args) {
    if (fn instanceof Value) {
      return ((Value) fn).execute(args).as(asType);
    }

    throw new RuntimeException("Cannot call: " + fn);
  }
}
