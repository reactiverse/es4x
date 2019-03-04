package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
import io.vertx.ext.unit.junit.RunTestOnContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

import static io.reactiverse.es4x.test.Helper.getRuntime;

@RunWith(Parameterized.class)
public class JSONTest {

  @Parameterized.Parameters
  public static List<String> engines() {
    return Arrays.asList("Nashorn", "GraalJS");
  }

  private final String engineName;
  private Runtime runtime;

  public JSONTest(String engine) {
    engineName = engine;
  }

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    runtime = getRuntime(rule.vertx(), engineName);
  }

  @Test
  public void shouldStringify() throws Exception {
    runtime.eval("console.log(JSON.stringify({}))");
    runtime.eval("console.log(JSON.stringify([]))");
    runtime.eval("console.log(JSON.stringify({K:1}))");
    runtime.eval("console.log(JSON.stringify([1,2,'', null]))");
    runtime.eval("var JsonObject = Java.type('io.vertx.core.json.JsonObject'); console.log(JSON.stringify(new JsonObject()))");
    runtime.eval("var JsonObject = Java.type('io.vertx.core.json.JsonObject'); console.log(JSON.stringify(new JsonObject().put('k', 1)))");
//    runtime.eval("console.log(JSON.stringify({}))");
//    runtime.eval("console.log(JSON.stringify({}))");
//    runtime.eval("console.log(JSON.stringify({}))");
//    runtime.eval("console.log(JSON.stringify({}))");
  }
}
