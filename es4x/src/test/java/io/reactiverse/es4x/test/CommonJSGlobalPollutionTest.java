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
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class CommonJSGlobalPollutionTest {

  @Parameterized.Parameters
  public static List<String> engines() {
    return Arrays.asList("Nashorn", "GraalJS");
  }

  private final String engineName;
  private Runtime runtime;

  public CommonJSGlobalPollutionTest(String engine) {
    engineName = engine;
  }

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    runtime = getRuntime(rule.vertx(), engineName);
  }

  @Test
  public void shouldHaveSideEffects() {
    try {
      // this test verifies that the pollution of the global context behaves like on node
      runtime.require("./pollution/a.js");
      fail("should throw");
    } catch (Exception e) {
      assertEquals("Error: engine is tainted: b", e.getMessage());
    }
  }
}
