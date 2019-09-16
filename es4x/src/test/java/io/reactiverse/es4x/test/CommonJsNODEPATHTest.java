package io.reactiverse.es4x.test;

import io.reactiverse.es4x.ECMAEngine;
import io.reactiverse.es4x.Runtime;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.reactiverse.es4x.test.JS.getMember;
import static org.junit.Assert.assertEquals;

@RunWith(VertxUnitRunner.class)
public class CommonJsNODEPATHTest {

  private Runtime runtime;

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    runtime = new ECMAEngine(rule.vertx()).newContext();
  }

  @Test
  public void shouldLoadAModuleFromACustomROOT() throws Exception {
    // this test shows that neither the path.js from the jar root or the node_modules module
    // is loaded as the node path takes precedence
    runtime.eval("process.env = { NODE_PATH: '" + "./src/test/resources/dist" + "' }");
    Object mod = runtime.require("./path");
    assertEquals("dist/path", getMember(mod, "message", String.class));
  }
}
