package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
import io.reactiverse.es4x.Loader;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.junit.RunTestOnContext;
import org.graalvm.polyglot.Value;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeTrue;

@RunWith(Parameterized.class)
public class NativeJSONTest {

  @Parameterized.Parameters
  public static List<String> engines() {
    return Arrays.asList("Nashorn", "GraalJS");
  }

  private Loader loader;
  private Object JSON;

  private final String engineName;

  public NativeJSONTest(String engine) {
    System.setProperty("es4x.engine", engine);
    engineName = engine;
  }

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() throws Exception {
    loader = Runtime.getCurrent().loader(rule.vertx());
    assumeTrue(loader.name().equalsIgnoreCase(engineName));
    JSON = loader.eval("JSON");
  }

  private Object stringify(Object... args) {
    Object res = loader.invokeMethod(JSON, "stringify", args);
    // Graal engine always wraps
    if (res instanceof Value) {
      return ((Value) res).asString();
    }

    return res;
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
    Object result = loader.eval("JSON.stringify({foo: 'bar'})");
    assertNotNull(result);
    // Graal engine always wraps
    if (result instanceof Value) {
      result = ((Value) result).asString();
    }
    assertEquals("{\"foo\":\"bar\"}", result);
  }

  @Test
  public void testOriginalArray() throws Exception {
    Object result = loader.eval("JSON.stringify(['foo', 'bar'])");
    assertNotNull(result);
    // Graal engine always wraps
    if (result instanceof Value) {
      result = ((Value) result).asString();
    }
    assertEquals("[\"foo\",\"bar\"]", result);
  }
}
