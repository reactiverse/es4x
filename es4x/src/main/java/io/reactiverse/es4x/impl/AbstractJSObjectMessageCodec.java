package io.reactiverse.es4x.impl;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public abstract class AbstractJSObjectMessageCodec<T> implements MessageCodec<T, Object> {

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
    return getClass().getSimpleName();
  }

  @Override
  public byte systemCodecID() {
    return -1;
  }
}
