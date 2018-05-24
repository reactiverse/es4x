package io.reactiverse.es4x.dynalink;

import io.reactiverse.es4x.Loader;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

@RunWith(VertxUnitRunner.class)
public class DinalynkTest {

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  private Loader loader;

  @Before
  public void before() {
    System.setProperty("es4x.engine", "Nashorn");
    loader = Loader.create(rule.vertx());
    assumeTrue(loader.name().equalsIgnoreCase("Nashorn"));
  }

  public static String testJSON(JsonObject o) {
    return o.encodePrettily();
  }

  public static String testDataObject(HttpServerOptions o) {
    return o.toJson().encodePrettily();
  }

  @Test(timeout = 10000)
  public void testCasting(TestContext should) throws Exception {
    final Async test = should.async();

    should.assertEquals("{\n  \"foo\" : \"bar\"\n}", loader.eval(
      "var DynalinkTest = Java.type('io.reactiverse.es4x.dynalink.DinalynkTest');\n" +
      "DynalinkTest.testJSON({foo: 'bar'});\n"));

    test.complete();
  }

  @Test(timeout = 10000)
  public void testDataObject(TestContext should) throws Exception {
    final Async test = should.async();

    should.assertEquals("{\n  \"foo\" : \"bar\"\n}", loader.eval(
      "var DynalinkTest = Java.type('io.reactiverse.es4x.dynalink.DinalynkTest');\n" +
        "DynalinkTest.testDataObject({foo: 'bar'});\n"));

    test.complete();
  }
}
