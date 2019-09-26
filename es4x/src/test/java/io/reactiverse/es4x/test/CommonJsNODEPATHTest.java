package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.graalvm.polyglot.Value;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.reactiverse.es4x.test.JS.*;
import static org.junit.Assert.assertEquals;

@RunWith(VertxUnitRunner.class)
public class CommonJsNODEPATHTest {

  private Runtime runtime;

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    runtime = commonjs(rule.vertx());
  }

  @Test
  public void shouldLoadAModuleFromACustomROOT() {
    // this test shows that neither the path.js from the jar root or the node_modules module
    // is loaded as the node path takes precedence
    runtime.eval("process.env = { NODE_PATH: '" + "./src/test/resources/dist" + "' }");
    Value mod = require(runtime, "./path");
    assertEquals("dist/path", getMember(mod, "message", String.class));
  }
}
