/*
 * Copyright 2018 Red Hat, Inc.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  The Apache License v2.0 is available at
 *  http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */
package io.reactiverse.es4x.dynalink;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import jdk.dynalink.linker.*;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.api.scripting.ScriptUtils;

import java.lang.invoke.*;
import java.time.Instant;
import java.util.*;
import java.util.function.Supplier;

public class ES4XJSONLinker implements GuardingDynamicLinker, GuardingTypeConverterFactory {

  private static final MethodHandle TO_JSONOBJECT;
  private static final MethodHandle TO_JSONARRAY;

  private static final MethodHandle TO_DATE;
  private static final MethodHandle TO_INSTANT;

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

      TO_DATE = lookup.findStatic(
        ES4XJSONLinker.class,
        "toDate",
        MethodType.methodType(Date.class, Object.class));

      TO_INSTANT = lookup.findStatic(
        ES4XJSONLinker.class,
        "toInstant",
        MethodType.methodType(Instant.class, Object.class));

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
    if (targetType.isAssignableFrom(Date.class)) {
      return new GuardedInvocation(TO_DATE);
    }
    if (targetType.isAssignableFrom(Instant.class)) {
      return new GuardedInvocation(TO_INSTANT);
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

  @SuppressWarnings("unchecked")
  private static Date toDate(final Object obj) {
    // nulls will be nulls
    if (obj == null) {
      return null;
    }
    // if we're already working with the correct type return identity
    if (obj instanceof Date) {
      return (Date) obj;
    }
    // rely on nashorn to do the intermediate representation for us
    final ScriptObjectMirror jsDate = ScriptUtils.wrap(obj);

    if (!"Date".equals(jsDate.getClassName())) {
      throw new ClassCastException("Object is not a Date");
    }

    Number timestampLocalTime = (Number) jsDate.callMember("getTime");
    return new Date(timestampLocalTime.longValue());
  }

  @SuppressWarnings("unchecked")
  private static Instant toInstant(final Object obj) {
    // nulls will be nulls
    if (obj == null) {
      return null;
    }
    // if we're already working with the correct type return identity
    if (obj instanceof Instant) {
      return (Instant) obj;
    }
    // rely on nashorn to do the intermediate representation for us
    final ScriptObjectMirror jsDate = ScriptUtils.wrap(obj);

    if (!"Date".equals(jsDate.getClassName())) {
      throw new ClassCastException("Object is not a Date");
    }

    Number timestampLocalTime = (Number) jsDate.callMember("getTime");
    return Instant.ofEpochMilli(timestampLocalTime.longValue());
  }
}
