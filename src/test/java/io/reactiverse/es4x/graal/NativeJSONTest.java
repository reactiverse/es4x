//package io.reactiverse.es4x.graal;
//
//import io.vertx.core.Vertx;
//import io.vertx.core.json.JsonArray;
//import io.vertx.core.json.JsonObject;
//import jdk.nashorn.api.scripting.JSObject;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import javax.script.ScriptEngine;
//import javax.script.ScriptException;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//
//public class NativeJSONTest {
//
//  private static ScriptEngine engine;
//  private static JSObject JSON;
//
//  public static Object stringify(Object... args) {
//    return ((JSObject) JSON.getMember("stringify")).call(JSON, args);
//  }
//
//  public static Object parse(Object... args) {
//    return ((JSObject) JSON.getMember("parse")).call(JSON, args);
//  }
//
//  @BeforeClass
//  public static void beforeClass() throws ScriptException, NoSuchMethodException {
//    Loader loader = new Loader(Vertx.vertx());
//    engine = loader.getEngine();
//    JSON = (JSObject) engine.get("JSON");
//  }
//
//  @Test
//  public void testNativeJsonObject() {
//    Object JSON = engine.get("JSON");
//
//    Object result = stringify(new JsonObject().put("foo", "bar"));
//    assertNotNull(result);
//    assertEquals("{\"foo\":\"bar\"}", result);
//  }
//
//  @Test
//  public void testNativeJsonArray() {
//    Object JSON = engine.get("JSON");
//
//    Object result = stringify(new JsonArray().add("foo").add("bar"));
//    assertNotNull(result);
//    assertEquals("[\"foo\",\"bar\"]", result);
//  }
//
//  @Test
//  public void testOriginalObject() throws ScriptException {
//    Object result = engine.eval("JSON.stringify({foo: 'bar'})");
//    assertNotNull(result);
//    assertEquals("{\"foo\":\"bar\"}", result);
//  }
//
//  @Test
//  public void testOriginalArray() throws ScriptException {
//    Object result = engine.eval("JSON.stringify(['foo', 'bar'])");
//    assertNotNull(result);
//    assertEquals("[\"foo\",\"bar\"]", result);
//  }
//}
