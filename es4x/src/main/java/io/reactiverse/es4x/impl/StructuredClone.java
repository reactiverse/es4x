/*
 * Copyright 2019 Red Hat, Inc.
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

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.graalvm.polyglot.Value;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class StructuredClone {

  public static Object cloneObject(Value oToBeCloned) {
    return cloneObject(oToBeCloned, new HashMap<>());
  }

  private static Object cloneObject(Value oToBeCloned, Map<Object, Object> dejaVu) {
    // null
    if (oToBeCloned == null || oToBeCloned.isNull()) {
      return null;
    }

    // circular
    if (dejaVu.containsKey(oToBeCloned)) {
      return dejaVu.get(oToBeCloned);
    }

    // primitives

    if (oToBeCloned.isNumber()) {
      return oToBeCloned.as(Number.class);
    }

    if (oToBeCloned.isBoolean()) {
      return oToBeCloned.asBoolean();
    }

    if (oToBeCloned.isString()) {
      return oToBeCloned.asString();
    }

    // temporal types

    if (oToBeCloned.isDate()) {
      return oToBeCloned.asDate();
    }

    if (oToBeCloned.isDuration()) {
      return oToBeCloned.asDuration();
    }

    if (oToBeCloned.isInstant()) {
      return oToBeCloned.asInstant();
    }

    if (oToBeCloned.isTime()) {
      return oToBeCloned.asTime();
    }

    if (oToBeCloned.isTimeZone()) {
      return oToBeCloned.asTimeZone();
    }

    final String fConstrName = getConstructorName(oToBeCloned);

    Object oClone;

    switch (fConstrName) {
      case "Object":
        oClone = new JsonObject();
        dejaVu.put(oToBeCloned, oClone);
        for (String key : oToBeCloned.getMemberKeys()) {
          ((JsonObject) oClone).put(key, cloneObject(oToBeCloned.getMember(key), dejaVu));
        }
        break;
      case "Array":
        oClone = new JsonArray();
        dejaVu.put(oToBeCloned, oClone);
        for (long l = 0; l < oToBeCloned.getArraySize(); l++) {
          ((JsonArray) oClone).add(cloneObject(oToBeCloned.getArrayElement(l), dejaVu));
        }
        break;

      case "RegExp":
        int flags = 0;
        if (oToBeCloned.getMember("global").asBoolean()) {
          flags |= Pattern.DOTALL;
        }
        if (oToBeCloned.getMember("ignoreCase").asBoolean()) {
          flags |= Pattern.CASE_INSENSITIVE;
        }
        if (oToBeCloned.getMember("multiline").asBoolean()) {
          flags |= Pattern.MULTILINE;
        }
        oClone = flags != 0 ? Pattern.compile(oToBeCloned.getMember("source").asString(), flags) : Pattern.compile(oToBeCloned.getMember("source").asString());
        dejaVu.put(oToBeCloned, oClone);
        break;
      case "ArrayBuffer":
      case "EArrayBuffer":
        oClone = extractArrayBuffer(oToBeCloned);
        if (oClone != null) {
          dejaVu.put(oToBeCloned, oClone);
          break;
        }
        throw new IllegalStateException("ArrayBuffer not backed by j.n.ByteBuffer not allowed, use a TypedArray instead!");
      case "Int8Array":
        oClone = extractTypedArray(oToBeCloned);
        if (oClone != null) {
          dejaVu.put(oToBeCloned, oClone);
        } else {
          long size = oToBeCloned.getArraySize();
          oClone = Buffer.buffer((int) size);
          dejaVu.put(oToBeCloned, oClone);
          for (int i = 0; i < (int) size; i++) {
            ((Buffer) oClone).setByte(i, oToBeCloned.getArrayElement(i).asByte());
          }
        }
        break;
      case "Uint8Array":
      case "Uint8ClampedArray":
        oClone = extractTypedArray(oToBeCloned);
        if (oClone != null) {
          dejaVu.put(oToBeCloned, oClone);
        } else {
          long size = oToBeCloned.getArraySize();
          oClone = Buffer.buffer((int) size);
          dejaVu.put(oToBeCloned, oClone);
          for (int i = 0; i < (int) size; i++) {
            ((Buffer) oClone).setUnsignedByte(i, oToBeCloned.getArrayElement(i).asShort());
          }
        }
        break;
      case "Int16Array":
        oClone = extractTypedArray(oToBeCloned);
        if (oClone != null) {
          dejaVu.put(oToBeCloned, oClone);
        } else {
          long size = oToBeCloned.getArraySize();
          oClone = Buffer.buffer((int) size * 2);
          dejaVu.put(oToBeCloned, oClone);
          for (int i = 0; i < (int) size; i++) {
            ((Buffer) oClone).setShort(i, oToBeCloned.getArrayElement(i).asShort());
          }
        }
        break;
      case "Uint16Array":
        oClone = extractTypedArray(oToBeCloned);
        if (oClone != null) {
          dejaVu.put(oToBeCloned, oClone);
        } else {
          long size = oToBeCloned.getArraySize();
          oClone = Buffer.buffer((int) size * 2);
          dejaVu.put(oToBeCloned, oClone);
          for (int i = 0; i < (int) size; i++) {
            ((Buffer) oClone).setUnsignedShort(i, oToBeCloned.getArrayElement(i).asInt());
          }
        }
        break;
      case "Int32Array":
        oClone = extractTypedArray(oToBeCloned);
        if (oClone != null) {
          dejaVu.put(oToBeCloned, oClone);
        } else {
          long size = oToBeCloned.getArraySize();
          oClone = Buffer.buffer((int) size * 4);
          dejaVu.put(oToBeCloned, oClone);
          for (int i = 0; i < (int) size; i++) {
            ((Buffer) oClone).setInt(i, oToBeCloned.getArrayElement(i).asInt());
          }
        }
        break;
      case "Uint32Array":
        oClone = extractTypedArray(oToBeCloned);
        if (oClone != null) {
          dejaVu.put(oToBeCloned, oClone);
        } else {
          long size = oToBeCloned.getArraySize();
          oClone = Buffer.buffer((int) size * 4);
          dejaVu.put(oToBeCloned, oClone);
          for (int i = 0; i < (int) size; i++) {
            ((Buffer) oClone).setUnsignedInt(i, oToBeCloned.getArrayElement(i).asLong());
          }
        }
        break;
      case "Float32Array":
        oClone = extractTypedArray(oToBeCloned);
        if (oClone != null) {
          dejaVu.put(oToBeCloned, oClone);
        } else {
          long size = oToBeCloned.getArraySize();
          oClone = Buffer.buffer((int) size * 4);
          dejaVu.put(oToBeCloned, oClone);
          for (int i = 0; i < (int) size; i++) {
            ((Buffer) oClone).setFloat(i, oToBeCloned.getArrayElement(i).asFloat());
          }
        }
        break;
      case "Float64Array":
        oClone = extractTypedArray(oToBeCloned);
        if (oClone != null) {
          dejaVu.put(oToBeCloned, oClone);
        } else {
          long size = oToBeCloned.getArraySize();
          oClone = Buffer.buffer((int) size * 8);
          dejaVu.put(oToBeCloned, oClone);
          for (int i = 0; i < (int) size; i++) {
            ((Buffer) oClone).setDouble(i, oToBeCloned.getArrayElement(i).asDouble());
          }
        }
        break;
      case "BigInt64Array":
      case "BigUint64Array":
        oClone = extractTypedArray(oToBeCloned);
        if (oClone != null) {
          dejaVu.put(oToBeCloned, oClone);
        } else {
          long size = oToBeCloned.getArraySize();
          oClone = Buffer.buffer((int) size * 8);
          dejaVu.put(oToBeCloned, oClone);
          for (int i = 0; i < (int) size; i++) {
            ((Buffer) oClone).setLong(i, oToBeCloned.getArrayElement(i).asLong());
          }
        }
        break;
      case "Error":
        oClone = new RuntimeException(oToBeCloned.getMember("message").asString());
        dejaVu.put(oToBeCloned, oClone);
        break;
      default:
//          oClone = new fConstr();
//          for (var sProp in oToBeCloned) { oClone[sProp] = clone(oToBeCloned[sProp], cloned, clonedpairs); }
        throw new IllegalStateException("Type not allowed: " + fConstrName);
    }
    return oClone;
  }

  private static ByteBuffer extractArrayBuffer(Value arrayBuffer) {
    // null
    if (arrayBuffer == null || arrayBuffer.isNull()) {
      return null;
    }

    if (arrayBuffer.hasMember("__jbuffer")) {
      Value jbuffer = arrayBuffer.getMember("__jbuffer");
      // null
      if (jbuffer == null || jbuffer.isNull()) {
        return null;
      }

      // extract the j.n.ByteBuffer
      return jbuffer.as(ByteBuffer.class);
    }

    return null;
  }

  private static ByteBuffer extractTypedArray(Value typedArray) {
    ByteBuffer jbuffer = extractArrayBuffer(typedArray.getMember("buffer"));

    if (jbuffer != null) {
      int offset = typedArray.getMember("byteOffset").asInt();
      int length = typedArray.getMember("byteLength").asInt();

      if (jbuffer.position() != offset || jbuffer.limit() != length) {
        jbuffer
          // reset read index
          .position(offset)
          // reset the capacity
          .limit(length);
      }

      return jbuffer;
    }

    return null;
  }

  private static String getConstructorName(Value oToBeCloned) {
    // proxy objects do not behave like native Object/Array, so we need to mask it
    if (oToBeCloned.isProxyObject()) {
      if (oToBeCloned.hasArrayElements()) {
        return "Array";
      }
      if (oToBeCloned.hasMembers()) {
        return "Object";
      }
    }
    // other types, get constructor or return "undefined"
    Value fConstr = oToBeCloned.getMember("constructor");
    return fConstr == null ? "undefined" : fConstr.getMember("name").asString();
  }
}
