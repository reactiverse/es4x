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
package io.reactiverse.es4x.impl;

import io.vertx.core.Future;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.Proxy;
import org.graalvm.polyglot.proxy.ProxyArray;

import java.util.List;
import java.util.Map;

/**
 * This class provides a default implementation for the graalvm proxy interface methods, to be used by
 * vertx json types.
 */
public final class ProxyUtil {

  private static final Logger LOG = LoggerFactory.getLogger(ProxyUtil.class.getSimpleName());

  public static void putMember(JsonObject self, String key, Value value) {
    self.put(key, value.isHostObject() ? value.asHostObject() : value);
  }

  public static boolean hasMember(JsonObject self, String key) {
    return self.containsKey(key);
  }

  public static Object getMemberKeys(JsonObject self) {
    return new ProxyArray() {
      private final Object[] keys = self.fieldNames().toArray();

      public void set(long index, Value value) {
        putMember(self, (String) get(index), value);
      }

      public long getSize() {
        return this.keys.length;
      }

      public Object get(long index) {
        if (index >= 0L && index <= Integer.MAX_VALUE) {
          return this.keys[(int) index];
        } else {
          throw new ArrayIndexOutOfBoundsException();
        }
      }
    };
  }

  public static Object getMember(JsonObject self, String key) {
    return wrapJsonValue(self.getMap().get(key));
  }

  public static boolean removeMember(JsonObject self, String key) {
    if (self.containsKey(key)) {
      self.remove(key);
      return true;
    } else {
      return false;
    }
  }

  public static Object get(JsonArray self, long index) {
    checkIndex(index);
    return wrapJsonValue(self.getList().get((int) index));
  }

  public static void set(JsonArray self, long index, Value value) {
    checkIndex(index);
    self.set((int) index, value.isHostObject() ? value.asHostObject() : value);
  }

  private static void checkIndex(long index) {
    if (index > Integer.MAX_VALUE || index < 0L) {
      throw new ArrayIndexOutOfBoundsException("invalid index.");
    }
  }

  public static long getSize(JsonArray self) {
    return self.size();
  }


  public static boolean remove(JsonArray self, long index) {
    checkIndex(index);
    if (index < 0 || index >= self.size()) {
      return false;
    }
    self.remove((int) index);
    return true;
  }

  public static void then(Future<?> self, Value onFulfilled, Value onRejected) {
    self.onComplete(ar -> {
      if (ar.succeeded()) {
        if (onFulfilled != null) {
          onFulfilled.executeVoid(ar.result());
        } else {
          LOG.warn("Possible Unhandled Promise: " + self);
        }
      } else {
        if (onRejected != null) {
          onRejected.execute(ar.cause());
        } else {
          LOG.warn("Possible Unhandled Rejection: " + self);
        }
      }
    });
  }

  private static Object wrapJsonValue(Object val) {
    if (val == null) {
      return null;
    }

    // perform wrapping
    if (val instanceof Map) {
      val = new JsonObject((Map) val);
    } else if (val instanceof List) {
      val = new JsonArray((List) val);
    }

    return val;
  }

  public static boolean isJavaObject(Value value) {
    if (value == null) {
      return false;
    }

    if (value.isHostObject()) {
      return true;
    }

    if (value.isProxyObject()) {
      final Proxy unwrap = value.asProxyObject();

      return
        unwrap instanceof JsonObject || unwrap instanceof JsonArray;
    }

    return false;
  }
}
