package io.reactiverse.es4x.nashorn;

import jdk.nashorn.api.scripting.JSObject;
import org.graalvm.polyglot.Value;

public final class JS {

  private JS () {
    throw new RuntimeException("Do not instantiate!");
  }

  public static boolean isFunction(Object obj) {
    if (obj instanceof JSObject) {
      return ((JSObject) obj).isFunction();
    }
    if (obj instanceof Value) {
      return ((Value) obj).canExecute();
    }
    return false;
  }

  public static Object getMember(Object obj, String member) {
    if (obj instanceof JSObject) {
      return ((JSObject) obj).getMember(member);
    }
    if (obj instanceof Value) {
      return ((Value) obj).getMember(member);
    }

    throw new RuntimeException("Cannot get member from: " + obj);
  }

  public static <T> T getMember(Object obj, String member, Class<T> asClass) {
    if (obj instanceof JSObject) {
      return (T) ((JSObject) obj).getMember(member);
    }
    if (obj instanceof Value) {
      return ((Value) obj).getMember(member).as(asClass);
    }

    throw new RuntimeException("Cannot get member from: " + obj);
  }

  public static Object call(Object thiz, Object fn, Object... args) {
    if (fn instanceof JSObject) {
      return ((JSObject) fn).call(thiz, args);
    }
    if (fn instanceof Value) {
      return ((Value) fn).execute(args);
    }

    throw new RuntimeException("Cannot call: " + fn);
  }

  public static <T> T callAs(Object thiz, Object fn, Class<T> asType, Object... args) {
    if (fn instanceof JSObject) {
      return (T) ((JSObject) fn).call(thiz, args);
    }

    if (fn instanceof Value) {
      return ((Value) fn).execute(args).as(asType);
    }

    throw new RuntimeException("Cannot call: " + fn);
  }
}
