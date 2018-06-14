package io.reactiverse.es4x.dynalink;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import jdk.dynalink.linker.*;
import jdk.nashorn.api.scripting.ScriptUtils;

import java.lang.invoke.*;
import java.util.*;
import java.util.function.Supplier;

public class ES4XJSONLinker implements GuardingDynamicLinker, GuardingTypeConverterFactory {

  private static final MethodHandle TO_JSONOBJECT;
  private static final MethodHandle TO_JSONARRAY;

  private final Map<Class, DataObjectLinker> linkers = new IdentityHashMap<>();

  static {
    final MethodHandles.Lookup lookup = MethodHandles.lookup();

    try {
      TO_JSONOBJECT = lookup.findStatic(
        ES4XJSONLinker.class,
        "toJsonObject",
        MethodType.methodType(JsonObject.class, Object.class));

      TO_JSONARRAY = lookup.findStatic(
        ES4XJSONLinker.class,
        "toJsonArray",
        MethodType.methodType(JsonArray.class, Object.class));
    } catch (NoSuchMethodException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public GuardedInvocation getGuardedInvocation(LinkRequest linkRequest, LinkerServices linkerServices) {
    // this linker does not provide enhanced functionality to a type
    // but instead provides a converter from nashorn JSON to Vert.x JSON types.
    return null;
  }

  @Override
  public GuardedInvocation convertToType(Class<?> sourceType, Class<?> targetType, Supplier<MethodHandles.Lookup> lookupSupplier) {
    if (targetType.isAssignableFrom(JsonObject.class)) {
      return new GuardedInvocation(TO_JSONOBJECT);
    }
    if (targetType.isAssignableFrom(JsonArray.class)) {
      return new GuardedInvocation(TO_JSONARRAY);
    }
    // will attempt to cache the linker if not available yet
    DataObjectLinker linker;

    if (linkers.containsKey(targetType)) {
      linker = linkers.get(targetType);
      if (linker != null) {
        return new GuardedInvocation(linker.getHandler());
      }
    } else {
      // first time check
      try {
        linker = new DataObjectLinker(targetType);
        linkers.put(targetType, linker);
        return new GuardedInvocation(linker.getHandler());
      } catch (NoSuchMethodException | IllegalAccessException e) {
        linkers.put(targetType, null);
      }
    }

    // we can't convert
    return null;
  }

  @SuppressWarnings("unchecked")
  private static JsonObject toJsonObject(final Object obj) {
    // nulls will be nulls
    if (obj == null) {
      return null;
    }
    // if we're already working with the correct type return identity
    if (obj instanceof JsonObject) {
      return (JsonObject) obj;
    }
    // rely on nashorn to do the intermediate representation for us
    return new JsonObject((Map<String, Object>) ScriptUtils.convert(obj, Map.class));
  }

  @SuppressWarnings("unchecked")
  private static JsonArray toJsonArray(final Object obj) {
    // nulls will be nulls
    if (obj == null) {
      return null;
    }
    // if we're already working with the correct type return identity
    if (obj instanceof JsonArray) {
      return (JsonArray) obj;
    }
    // rely on nashorn to do the intermediate representation for us
    return new JsonArray((List<Object>) ScriptUtils.convert(obj, List.class));
  }
}
