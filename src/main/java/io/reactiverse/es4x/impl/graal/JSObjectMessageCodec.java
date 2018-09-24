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
package io.reactiverse.es4x.impl.graal;

import io.netty.util.CharsetUtil;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.graalvm.polyglot.Value;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class JSObjectMessageCodec<T> implements MessageCodec<T, Object> {

  JSObjectMessageCodec() {
  }

  @Override
  public void encodeToWire(Buffer buffer, T jsObject) {

    final Value value = Value.asValue(jsObject);

    if (value.hasArrayElements()) {
      Buffer encoded = Json.encodeToBuffer(asArray(value));
      buffer.appendInt(encoded.length());
      buffer.appendBuffer(buffer);
      return;
    }
    if (value.hasMembers()) {
      Buffer encoded = Json.encodeToBuffer(asObject(value));
      buffer.appendInt(encoded.length());
      buffer.appendBuffer(buffer);
      return;
    }

    // it's likely a Function
    throw new ClassCastException("type is not Object or Array");
  }

  @Override
  public JsonObject decodeFromWire(int pos, Buffer buffer) {
    int length = buffer.getInt(pos);
    pos += 4;
    byte[] encoded = buffer.getBytes(pos, pos + length);
    String str = new String(encoded, CharsetUtil.UTF_8);
    return new JsonObject(str);
  }

  @Override
  public Object transform(T jsObject) {
    final Value value = Value.asValue(jsObject);

    if (value.hasArrayElements()) {
      return new JsonArray(asArray(value));
    }
    if (value.hasMembers()) {
      return new JsonObject(asObject(value));
    }

    // it's likely a Function
    throw new ClassCastException("type is not Object or Array");
  }

  @Override
  public String name() {
    return this.getClass().getSimpleName();
  }

  @Override
  public byte systemCodecID() {
    return -1;
  }

  private Map<String, Object> asObject(Value value) {

    Map<String, Object> object = new LinkedHashMap<>();

    for (String key: value.getMemberKeys()) {
      Value val = value.getMember(key);

      if (val.isNull()) {
        object.put(key, null);
      }
      if (val.isBoolean()) {
        object.put(key, val.asBoolean());
      }
      if (val.isNumber()) {
        object.put(key, asNumber(val));
      }
      if (val.isString()) {
        object.put(key, val.asString());
      }
      if (val.hasArrayElements()) {
        object.put(key, asArray(val));
      }
      if (val.hasMembers()) {
        object.put(key, asObject(val));
      }
      // host objects added as is
      if (val.isHostObject()) {
        object.put(key, val.asHostObject());
      }
    }

    return object;
  }

  private List<Object> asArray(Value arr) {
    final long len = arr.getArraySize();
    if (len > Integer.MAX_VALUE) {
      throw new ArrayIndexOutOfBoundsException("Cannot create array of size: " + len);
    }
    List<Object> array = new ArrayList<>((int) len);
    for (int i = 0; i < len; i++) {
      Value val = arr.getArrayElement(i);
      if (val.isNull()) {
        array.add(i, null);
      }
      if (val.isBoolean()) {
        array.add(i, val.asBoolean());
      }
      if (val.isNumber()) {
        array.add(i, asNumber(val));
      }
      if (val.isString()) {
        array.add(i, val.asString());
      }
      if (val.hasArrayElements()) {
        array.add(i, asArray(val));
      }
      if (val.hasMembers()) {
        array.add(i, asObject(val));
      }
      // host objects added as is
      if (val.isHostObject()) {
        array.add(i, val.asHostObject());
      }
    }

    return array;
  }

  private Number asNumber(Value value) {
    if (value.fitsInByte()) {
      return value.asByte();
    }
    if (value.fitsInShort()) {
      return value.asShort();
    }
    if (value.fitsInInt()) {
      return value.asInt();
    }
    if (value.fitsInFloat()) {
      return value.asFloat();
    }
    if (value.fitsInLong()) {
      return value.asLong();
    }
    if (value.fitsInDouble()) {
      return value.asDouble();
    }

    throw new RuntimeException("Unsupported type: " + value);
  }
}
