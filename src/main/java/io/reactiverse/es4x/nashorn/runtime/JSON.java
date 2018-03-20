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
package io.reactiverse.es4x.nashorn.runtime;

import com.fasterxml.jackson.core.type.TypeReference;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import jdk.nashorn.api.scripting.AbstractJSObject;
import jdk.nashorn.api.scripting.JSObject;

import java.util.*;

public final class JSON {

  private JSON() {
    throw new RuntimeException("Should not be instantiated");
  }


  public static void install(Map<String, Object> bindings) {

    // get a reference to the global object
    final JSObject global = (JSObject) bindings.get("global");
    assert global != null;

    // get a reference to the "JSON" object
    final JSObject json = (JSObject) global.getMember("JSON");
    assert json != null;

    // the original functions
    final JSObject parse = (JSObject) json.getMember("parse");
    final JSObject stringify = (JSObject) json.getMember("stringify");

    // patch
    json.setMember("parse", new AbstractJSObject() {
      @Override
      public boolean isFunction() {
        return true;
      }

      @Override
      public Object call(Object thiz, Object... args) {
        if (args != null && args.length > 0) {
          int len = args.length;
          Object val = args[0];

          if (len == 2) {
            // special case, try to fall back to jackson
            if (args[1] instanceof Class) {
              if (val instanceof String) {
                return Json.decodeValue((String) val, (Class) args[1]);
              }
              if (val instanceof Buffer) {
                return Json.decodeValue((Buffer) val, (Class) args[1]);
              }
            }

            if (args[1] instanceof TypeReference) {
              if (val instanceof String) {
                return Json.decodeValue((String) val, (TypeReference) args[1]);
              }
              if (val instanceof Buffer) {
                return Json.decodeValue((Buffer) val, (TypeReference) args[1]);
              }
            }
          } else {
            if (val instanceof JsonArray) {
              return ((JsonArray) val).getList();
            }
            if (val instanceof JsonObject) {
              return ((JsonObject) val).getMap();
            }
          }
        }
        // fallback to the original fn
        return parse.call(thiz, args);
      }
    });

    json.setMember("stringify", new AbstractJSObject() {
      @Override
      public boolean isFunction() {
        return true;
      }

      @Override
      public Object call(Object thiz, Object... args) {
        if (args != null && args.length > 0) {
          Object val = args[0];

          if (val instanceof JSObject) {
            return stringify.call(thiz, args);
          }

          if (val instanceof JsonArray) {
            return ((JsonArray) val).encode();
          }
          if (val instanceof JsonObject) {
            return ((JsonObject) val).encode();
          }

          // convert from map to object
          if (val instanceof Map) {
            return new JsonObject(((Map) val)).encode();
          }
          // convert from list to array
          if (val instanceof List) {
            return new JsonArray(((List) val)).encode();
          }

        }
        return stringify.call(thiz, args);
      }
    });

    // get a reference to the "Java" object
    final JSObject java = (JSObject) global.getMember("Java");
    assert java != null;

    final JSObject asJSONCompatible = (JSObject) java.getMember("asJSONCompatible");
    assert asJSONCompatible != null;

    // extension that converts json to java native objects
    json.setMember("wrap", new AbstractJSObject() {
      @Override
      public boolean isFunction() {
        return true;
      }

      @Override
      public Object call(Object thiz, Object... args) {
        if (args == null || args.length == 0) {
          return null;
        }

        Object javaType = asJSONCompatible.call(java, args[0]);

        if (javaType instanceof List) {
          return new JsonArray((List) javaType);
        }

        if (javaType instanceof Map) {
          return new JsonObject((Map) javaType);
        }

        // fallback
        return javaType;
      }
    });
  }
}
