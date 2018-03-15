package io.reactiverse.es4x.nashorn.runtime;

import io.netty.util.CharsetUtil;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.util.List;
import java.util.Map;

final class NashornJSObjectMessageCodec implements MessageCodec<ScriptObjectMirror, Object> {

  private final JSObject JSON;
  private final JSObject Java;

  private final JSObject stringify;
  private final JSObject asJSONCompatible;

  NashornJSObjectMessageCodec(JSObject JSON, JSObject Java) {
    this.JSON = JSON;
    this.Java = Java;

    this.stringify = (JSObject) JSON.getMember("stringify");
    this.asJSONCompatible = (JSObject) Java.getMember("asJSONCompatible");
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
    Object compat = asJSONCompatible.call(Java, jsObject);
    if (compat instanceof Map) {
      return new JsonObject((Map) compat);
    }
    if (compat instanceof List) {
      return new JsonArray((List) compat);
    }

    return compat;
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
