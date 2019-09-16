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

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Map;

public final class JSObjectMessageCodec implements MessageCodec<Object, Object> {

  private final String codecName;

  public JSObjectMessageCodec(String codecName) {
    this.codecName = codecName;
  }

  @Override
  public Object decodeFromWire(int pos, Buffer buffer) {
    int length = buffer.getInt(pos);
    pos += 4;

    if (length == 0) {
      return null;
    }

    byte b = buffer.getByte(pos);

    // encoded messages are expected not to be pretty printed
    if (b == '{') {
      return new JsonObject(buffer.getBuffer(pos, pos + length));
    }
    if (b == '[') {
      return new JsonArray(buffer.getBuffer(pos, pos + length));
    }

    throw new ClassCastException("type is not Object or Array");
  }

  @Override
  public String name() {
    return codecName;
  }

  @Override
  public byte systemCodecID() {
    return -1;
  }

  @Override
  public void encodeToWire(Buffer buffer, Object jsObject) {

    if (jsObject == null) {
      buffer.appendInt(0);
      return;
    }

    if (jsObject instanceof List) {
      final Buffer encoded = new JsonArray((List) jsObject).toBuffer();
      buffer.appendInt(encoded.length());
      buffer.appendBuffer(buffer);
      return;
    }

    if (jsObject instanceof Map) {
      final Buffer encoded = new JsonObject((Map) jsObject).toBuffer();
      buffer.appendInt(encoded.length());
      buffer.appendBuffer(buffer);
      return;
    }

    throw new ClassCastException("type is not Object or Array");
  }

  @Override
  public Object transform(Object jsObject) {

    if (jsObject == null) {
      return null;
    }

    if (jsObject instanceof List) {
      return new JsonArray((List) jsObject).copy();
    }

    if (jsObject instanceof Map) {
      return new JsonObject((Map) jsObject).copy();
    }

    throw new ClassCastException("type is not Object or Array");
  }
}
