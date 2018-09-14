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
package io.reactiverse.es4x.impl.nashorn;

import io.netty.util.CharsetUtil;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

final class JSObjectMessageCodec implements MessageCodec<ScriptObjectMirror, Object> {

  private final JSObject JSON;
  private final JSObject stringify;

  JSObjectMessageCodec(JSObject JSON) {
    this.JSON = JSON;
    this.stringify = (JSObject) JSON.getMember("stringify");
  }

  @Override
  public void encodeToWire(Buffer buffer, ScriptObjectMirror jsObject) {
    String strJson = (String) stringify.call(JSON, jsObject);
    byte[] encoded = strJson.getBytes(CharsetUtil.UTF_8);
    buffer.appendInt(encoded.length);
    Buffer buff = Buffer.buffer(encoded);
    buffer.appendBuffer(buff);
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
  public Object transform(ScriptObjectMirror jsObject) {
    if (!jsObject.isFunction() && !jsObject.isStrictFunction()) {
      // it's an Array
      if (jsObject.isArray()) {
        return new JsonArray((String) stringify.call(JSON, jsObject));
      }
      // it's an Object
      return new JsonObject((String) stringify.call(JSON, jsObject));
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
}
