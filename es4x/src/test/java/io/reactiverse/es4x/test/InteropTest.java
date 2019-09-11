package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
import io.reactiverse.es4x.impl.graal.GraalEngine;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class InteropTest {

  private Runtime runtime;

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    runtime = new GraalEngine(rule.vertx()).newContext();
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

  @Test
  public void testMapInterop() throws Exception {
    runtime.eval(
      "var Interop = Java.type('io.reactiverse.es4x.test.Interop');" +
        "var interop = new Interop();" +
        "var m = interop.getMap();" +
        "print(m); print(m['k1']); print(m.k2); print(m.k3); print(m.k4);");
  }

  @Test
  public void testMapBaseInterop() throws Exception {
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
  public void testListInterop() throws Exception {
    runtime.eval(
      "var Interop = Java.type('io.reactiverse.es4x.test.Interop');" +
        "var interop = new Interop();" +
        "var l = interop.getList();" +
        "print(l); print(l[0]); print(l[1]);");
  }
}
