package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.graalvm.polyglot.Value;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.reactiverse.es4x.test.JS.commonjs;

@RunWith(VertxUnitRunner.class)
public class JSONTest {

  private Runtime runtime;

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    runtime = commonjs(rule.vertx());
  }

  @Test
  public void shouldStringify() {
    runtime.eval("console.log(JSON.stringify({}))");
    runtime.eval("console.log(JSON.stringify([]))");
    runtime.eval("console.log(JSON.stringify({K:1}))");
    runtime.eval("console.log(JSON.stringify([1,2,'', null]))");
    // these tests show the interop
    runtime.eval("var JsonObject = Java.type('io.vertx.core.json.JsonObject'); console.log(JSON.stringify(new JsonObject()))");
    runtime.eval("var JsonObject = Java.type('io.vertx.core.json.JsonObject'); var x = new JsonObject(); x['k'] = 1; console.log(JSON.stringify(x))");
    runtime.eval("var JsonObject = Java.type('io.vertx.core.json.JsonObject'); var x = new JsonObject(); x.k = 1; console.log(JSON.stringify(x))");
  }

  @Test
  public void shouldStringifyNativeJsonTypes(TestContext should) {
    Value res =
      runtime.eval("var JsonObject = Java.type('io.vertx.core.json.JsonObject'); var x = new JsonObject(); x['k'] = 1; JSON.stringify(x)");

    should.assertNotNull(res.as(String.class));

    try {
      res.as(Buffer.class);
      should.fail("Output should be Buffer");
    } catch (ClassCastException e) {
      // expected
    }

    should.assertEquals("{\"k\":1}", res.asString());

    Value res2 =
      runtime.eval("var JsonObject = Java.type('io.vertx.core.json.JsonObject'); var x = new JsonObject(); x['k'] = 1; JSON.stringify(x)");

    try {
      res2.as(Buffer.class);
      should.fail("Output should be String");
    } catch (ClassCastException e) {
      // expected
    }
    Assert.assertNotNull(res2.as(String.class));
  }
}
