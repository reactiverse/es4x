package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
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

import static io.reactiverse.es4x.test.Helper.getRuntime;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Parameterized.class)
public class InteropTest {

  @Parameterized.Parameters
  public static List<String> engines() {
    return Arrays.asList(/* "Nashorn", */"GraalJS");
  }

  private Runtime runtime;

  private final String engineName;

  public InteropTest(String engine) {
    engineName = engine;
  }

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() throws Exception {
    runtime = getRuntime(rule.vertx(), engineName);
  }

  @Test
  public void testJSONObjectInterop() throws Exception {
    runtime.eval(
      "var Interop = Java.type('io.reactiverse.es4x.test.Interop');" +
      "var interop = new Interop();" +
        "interop.printObject({a:1});");
  }

  @Test
  public void testJSONArrayInterop() throws Exception {
    runtime.eval(
      "var Interop = Java.type('io.reactiverse.es4x.test.Interop');" +
        "var interop = new Interop();" +
        "interop.printArray([1, 2]);");
  }

  @Test
  public void testInstantInterop() throws Exception {
    runtime.eval(
      "var Interop = Java.type('io.reactiverse.es4x.test.Interop');" +
        "var interop = new Interop();" +
        "interop.printInstant(new Date());");
  }
}
