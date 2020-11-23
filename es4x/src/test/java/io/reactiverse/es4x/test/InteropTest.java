package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.graalvm.polyglot.Source;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static io.reactiverse.es4x.test.JS.commonjs;

@RunWith(VertxUnitRunner.class)
public class InteropTest {

  private Runtime runtime;

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    runtime = commonjs(rule.vertx());
  }

  @Test
  public void testJSONObjectInterop() {
    runtime.eval(
      "var Interop = Java.type('io.reactiverse.es4x.test.Interop');" +
        "var interop = new Interop();" +
        "interop.printObject({a:1});");
  }

  @Test
  public void testJSONArrayInterop() {
    runtime.eval(
      "var Interop = Java.type('io.reactiverse.es4x.test.Interop');" +
        "var interop = new Interop();" +
        "interop.printArray([1, 2]);");
  }

  @Test
  public void testInstantInterop() {
    runtime.eval(
      "var Interop = Java.type('io.reactiverse.es4x.test.Interop');" +
        "var interop = new Interop();" +
        "interop.printInstant(new Date());");
  }

  @Test
  public void testSetInterop() {
    runtime.eval(
      "var Interop = Java.type('io.reactiverse.es4x.test.Interop');" +
        "var interop = new Interop();" +
        "interop.printSet(['1', '2', '3']);");
  }

  @Test
  public void testAutocastJSObjectToMap() {
    runtime.eval(
      "var Interop = Java.type('io.reactiverse.es4x.test.Interop');" +
        "var interop = new Interop();" +
        "interop.shouldBaAMap({k:1});");
  }

  @Test
  public void testAutocastJSObjectToList() {
    runtime.eval(
      "var Interop = Java.type('io.reactiverse.es4x.test.Interop');" +
        "var interop = new Interop();" +
        "interop.shouldBaAList([1,null,true]);");
  }

  @Test
  public void testMapInterop() {
    runtime.eval(
      "var Interop = Java.type('io.reactiverse.es4x.test.Interop');" +
        "var interop = new Interop();" +
        "var m = interop.getMap();" +
        "print(Object.values(m));" +
        "print(m); print(m['k1']); print(m.k2); print(m.k3); print(m.k4);");
  }

  @Test
  public void testMapBaseInterop() {
    runtime.eval(
      "var HashMap = Java.type('java.util.HashMap');\n" +
        "var map = new HashMap();\n" +
        "map.put(1, \"a\");\n" +
        "print(map.get(1));" +
        "for (var key in map) {\n" +
        "    print(key);\n" +
        "    print(map[key]);\n" +
        "}");
  }

  @Test
  public void testListInterop() {
    runtime.eval(
      "var Interop = Java.type('io.reactiverse.es4x.test.Interop');" +
        "var interop = new Interop();" +
        "var l = interop.getList();" +
        "print(l); print(l[0]); print(l[1]);" +
        "l[2] = true;");
  }

  @Test
  public void testErrorInterop() {
    runtime.eval(
      "var Interop = Java.type('io.reactiverse.es4x.test.Interop');\n" +
        "var interop = new Interop();\n" +
        "interop.printThrowable(new TypeError());\n");
  }

  @Test
  public void testListPrintInterop() {
    // console log was broken for array proxies as they do not follow the full js object spec
    runtime.eval(
      "var Interop = Java.type('io.reactiverse.es4x.test.Interop');" +
        "var interop = new Interop();" +
        "var l = interop.getList();" +
        "console.log(l);");
  }

  @Test
  public void testByteInterop() {
    runtime.eval(
      "var Interop = Java.type('io.reactiverse.es4x.test.Interop');" +
        "var interop = new Interop();" +
        "interop.passByte(0xca);");
  }

  @Test
  public void testByteArrayInterop() {
    runtime.eval(
      "var Interop = Java.type('io.reactiverse.es4x.test.Interop');" +
        "var interop = new Interop();" +
        "interop.passBytes([0xca, 0xfe, 0xba, 0xbe]);");
  }

  @Test
  public void testSameArityJSONInterop() {
    runtime.eval(
      "var Interop = Java.type('io.reactiverse.es4x.test.Interop');\n" +
        "var interop = new Interop();\n" +
        // JS Native Array can convert to Java Map (but it's not desired in this case)
        "interop.sameArityJson([ \"a\", \"b\", \"c\" ]);\n" +
        // Right type is picked
        "interop.sameArityJson({ name:\"vv\", age:18 });\n" +
        // JS Native Array can convert to Java Map (but it's not desired in this case)
        // what about empty
        "interop.sameArityJson({});\n" +
        "interop.sameArityJson([]);\n");
  }

  @Test
  public void testSameArityJSONInterop2() {
    runtime.eval(
      "var Interop = Java.type('io.reactiverse.es4x.test.Interop');\n" +
        "var interop = new Interop();\n" +
        // JS Native Array can convert to Java Map (but it's not desired in this case)
        "interop.end(JSON.stringify([ \"a\", \"b\", \"c\" ]));\n" +
        // Right type is picked
        "interop.end(JSON.stringify({ name:\"vv\", age:18 }));\n");
  }

  @Test
  public void testProxyInterop() {
    runtime.eval(
      "var Interop = Java.type('io.reactiverse.es4x.test.Interop');\n" +
        "var JsonObject = Java.type('io.vertx.core.json.JsonObject');\n" +
        "var JsonArray = Java.type('io.vertx.core.json.JsonArray');\n" +
        "var interop = new Interop();\n" +
        // JS Native Array can convert to Java Map (but it's not desired in this case)
        "interop.end(JSON.stringify(new JsonArray()));\n" +
        // Right type is picked
        "interop.end(JSON.stringify(new JsonObject()));\n");
  }

  @Test
  public void testSameArityJSONInterop3() {

    Source script = Source.create("js", "var JsonObject = Java.type('io.vertx.core.json.JsonObject');\n" +
      "var Interop = Java.type('io.reactiverse.es4x.test.Interop');\n" +
      "var interop = new Interop();\n" +
      "var json = {};\n" +
      "json.name = 'vv'; json.age = 18;\n" +
      // Right type is picked
      "interop.end(JSON.stringify(json));\n");

    for (int i = 0; i < 10_000; i++) {
      runtime.eval(script);
    }
    int iter = 10;
    long[] results = new long[iter];

    for (int j = 0; j < iter; j++) {
      long t1 = System.currentTimeMillis();
      for (int i = 0; i < 20_000; i++) {
        runtime.eval(script);
      }
      long t2 = System.currentTimeMillis();
      results[j] = (t2 - t1);
      System.out.println(t2 - t1);
    }
    System.out.println("---");
    Arrays.sort(results);
    long sum = 0;
    for (int i = 0; i < iter - 2; i++) {
      sum += results[i];
    }
    System.out.println((double) sum / (double) (iter - 2));

  }
}
