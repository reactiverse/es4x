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

final class JSObjectMessageCodec<T> extends AbstractJSObjectMessageCodec<T> {

  private static final Source stringify = Source.newBuilder("js", "JSON.stringify", "<codec>").internal(true).buildLiteral();

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

    final Context ctx = Context.getCurrent();
    Buffer encoded = Buffer.buffer(ctx.eval(stringify).execute(value).asString());
    buffer.appendInt(encoded.length());
    buffer.appendBuffer(buffer);
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

    final Context ctx = Context.getCurrent();
    String encoded = ctx.eval(stringify).execute(value).asString();

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
