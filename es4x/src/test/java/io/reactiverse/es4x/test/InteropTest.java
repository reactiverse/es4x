package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

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
      "var Interop = Java.type('io.reactiverse.es4x.test.Interop');" +
        "var interop = new Interop();" +
        // JS Native Array can convert to Java Map (but it's not desired in this case)
        "interop.sameArityJson([ \"a\", \"b\", \"c\" ]);" +
        // Right type is picked
        "interop.sameArityJson({ name:\"vv\", age:18 });" +
        // JS Native Array can convert to Java Map (but it's not desired in this case)
        "interop.sameArityJson([]);");
  }
}
