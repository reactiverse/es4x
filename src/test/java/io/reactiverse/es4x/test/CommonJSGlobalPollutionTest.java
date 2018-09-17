package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Loader;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.junit.RunTestOnContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assume.*;

@RunWith(Parameterized.class)
public class CommonJSGlobalPollutionTest {

  @Parameterized.Parameters
  public static List<String> engines() {
    return Arrays.asList("Nashorn", "GraalVM");
  }

  final String engineName;
  private Loader loader;

  public CommonJSGlobalPollutionTest(String engine) {
    System.setProperty("es4x.engine", engine);
    engineName = engine;
  }

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    loader = Loader.create(rule.vertx());
    assumeTrue(loader.name().equalsIgnoreCase(engineName));
  }

  @Test
  public void shouldHaveSideEffects() {
    try {
      // this test verifies that the pollution of the global context behaves like on node
      loader.require("./pollution/a.js");
      fail("should throw");
    } catch (Exception e) {
      assertEquals("Error: engine is tainted: b", e.getMessage());
    }
  }
}
