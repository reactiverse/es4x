package io.reactiverse.es4x.dynalink;

import io.vertx.core.json.JsonObject;
import jdk.nashorn.api.scripting.ScriptUtils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Map;

public class DataObjectLinker {

  private static final MethodHandles.Lookup lookup = MethodHandles.publicLookup();

  private final Class type;
  private final MethodHandle constructor;
  private final MethodHandle handler;

  DataObjectLinker(Class target) throws NoSuchMethodException, IllegalAccessException {
    this.type = target;
    constructor = lookup.findConstructor(target, MethodType.methodType(void.class, JsonObject.class));

    // create a method handler for this type
    MethodHandle mh = lookup.findVirtual(
      DataObjectLinker.class,
      "convert",
      MethodType.methodType(Object.class, Object.class));

    // adapt as if it was a static method and force the correct signature
    handler = mh.bindTo(this).asType(MethodType.methodType(target, Object.class));
  }

  public MethodHandle getHandler() {
    return handler;
  }

  public Object convert(Object obj) throws Throwable {
    // nulls will be nulls
    if (obj == null) {
      return null;
    }

    // the given object is already an instance of this type
    if (type.isInstance(obj)) {
      return obj;
    }

    // if the object is not a json object, attempt to convert
    if (!(obj instanceof JsonObject)) {
      // rely on nashorn to do the intermediate representation for us
      obj = new JsonObject((Map<String, Object>) ScriptUtils.convert(obj, Map.class));
    }

    // rely on the constructor above to do the adaptation
    return constructor.invoke(obj);
  }
}
