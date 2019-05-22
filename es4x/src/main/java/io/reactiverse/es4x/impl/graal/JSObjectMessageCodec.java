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

import io.reactiverse.es4x.impl.AbstractJSObjectMessageCodec;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.util.List;
import java.util.Map;

final class JSObjectMessageCodec<T> extends AbstractJSObjectMessageCodec<T> {

  @Override
  public void encodeToWire(Buffer buffer, T jsObject) {

    final Value value = Value.asValue(jsObject);

    if (value.isHostObject() || value.isString() || value.isNumber() || value.isBoolean() || value.isNativePointer() || value.isProxyObject()) {
      throw new ClassCastException("type is not Object or Array");
    }

    if (value.isNull()) {
      buffer.appendInt(0);
      return;
    }


    if (value.hasArrayElements()) {
      final Buffer encoded = new JsonArray(value.as(List.class)).toBuffer();
      buffer.appendInt(encoded.length());
      buffer.appendBuffer(buffer);
    }

    if (value.hasMembers()) {
      final Buffer encoded = new JsonObject(value.as(Map.class)).toBuffer();
      buffer.appendInt(encoded.length());
      buffer.appendBuffer(buffer);
    }

    throw new ClassCastException("type is not Object or Array");
  }

  @Override
  public Object transform(T jsObject) {

    final Value value = Value.asValue(jsObject);

    if (value.isHostObject() || value.isString() || value.isNumber() || value.isBoolean() || value.isNativePointer() || value.isProxyObject()) {
      throw new ClassCastException("type is not Object or Array");
    }

    if (value.isNull()) {
      return null;
    }

    if (value.hasArrayElements()) {
      return new JsonArray(value.as(List.class));
    }

    if (value.hasMembers()) {
      return new JsonObject(value.as(Map.class));
    }

    throw new ClassCastException("type is not Object or Array");
  }
}
