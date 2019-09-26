package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.reactiverse.es4x.test.JS.commonjs;
import static io.reactiverse.es4x.test.JS.require;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(VertxUnitRunner.class)
public class CommonJSGlobalPollutionTest {

  private Runtime runtime;

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    runtime = commonjs(rule.vertx());
  }

  @Test
  public void shouldHaveSideEffects() {
    try {
      // this test verifies that the pollution of the global context behaves like on node
      require(runtime, "./pollution/a.js");
      fail("should throw");
    } catch (Exception e) {
      assertEquals("Error: engine is tainted: b", e.getMessage());
    }
  }
}
