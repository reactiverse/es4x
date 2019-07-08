package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
import io.reactiverse.es4x.impl.graal.GraalEngine;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.graalvm.polyglot.Value;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(VertxUnitRunner.class)
public class NativeJSONTest {

  private Runtime runtime;
  private Value JSON;

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() throws Exception {
    runtime = new GraalEngine(rule.vertx()).newContext();
    JSON = runtime.eval("JSON");
  }

  private Object stringify(Object... args) {
    return JSON.invokeMember("stringify", args).asString();
  }

  @Test
  public void testNativeJsonObject() {
    Object result = stringify(new JsonObject().put("foo", "bar"));
    assertNotNull(result);
    assertEquals("{\"foo\":\"bar\"}", result);
  }

  @Test
  public void testNativeJsonArray() {
    Object result = stringify(new JsonArray().add("foo").add("bar"));
    assertNotNull(result);
    assertEquals("[\"foo\",\"bar\"]", result);
  }

  @Test
  public void testOriginalObject() throws Exception {
    Object result = runtime.eval("JSON.stringify({foo: 'bar'})");
    assertNotNull(result);
    // Graal engine always wraps
    result = ((Value) result).asString();
    assertEquals("{\"foo\":\"bar\"}", result);
  }

  @Test
  public void testOriginalArray() throws Exception {
    Object result = runtime.eval("JSON.stringify(['foo', 'bar'])");
    assertNotNull(result);
    // Graal engine always wraps
    result = ((Value) result).asString();
    assertEquals("[\"foo\",\"bar\"]", result);
  }
}
