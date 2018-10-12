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

import io.reactiverse.es4x.impl.AbstractJSObjectMessageCodec;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

final class JSObjectMessageCodec extends AbstractJSObjectMessageCodec<ScriptObjectMirror> {

  @Override
  public void encodeToWire(Buffer buffer, ScriptObjectMirror jsObject) {

    if (jsObject == null) {
      buffer.appendInt(0);
      return;
    }

    final JSObject JSON = (JSObject) jsObject.eval("JSON");
    final JSObject stringify = (JSObject) JSON.getMember("stringify");

    Buffer encoded = Buffer.buffer((String) stringify.call(JSON, jsObject));
    buffer.appendInt(encoded.length());
    buffer.appendBuffer(buffer);
  }

  @Override
  public Object transform(ScriptObjectMirror jsObject) {

    if (jsObject == null) {
      return null;
    }

    final JSObject JSON = (JSObject) jsObject.eval("JSON");
    final JSObject stringify = (JSObject) JSON.getMember("stringify");
    final String encoded = (String) stringify.call(JSON, jsObject);
    char c = encoded.charAt(0);

    // encoded messages are expected not to be pretty printed
    if (c == '{') {
      return new JsonObject(encoded);
    }
    if (c == '[') {
      return new JsonArray(encoded);
    }

    throw new ClassCastException("type is not Object or Array");
  }
}
