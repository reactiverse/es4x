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
public class JSONTest {

  private Runtime runtime;

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    runtime = new GraalEngine(rule.vertx()).newContext();
  }

  @Test
  public void shouldStringify() throws Exception {
    runtime.eval("console.log(JSON.stringify({}))");
    runtime.eval("console.log(JSON.stringify([]))");
    runtime.eval("console.log(JSON.stringify({K:1}))");
    runtime.eval("console.log(JSON.stringify([1,2,'', null]))");
    runtime.eval("var JsonObject = Java.type('io.vertx.core.json.JsonObject'); console.log(JSON.stringify(new JsonObject()))");
    runtime.eval("var JsonObject = Java.type('io.vertx.core.json.JsonObject'); console.log(JSON.stringify(new JsonObject().put('k', 1)))");
  }
}
