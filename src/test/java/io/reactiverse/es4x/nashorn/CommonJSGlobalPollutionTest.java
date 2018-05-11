package io.reactiverse.es4x.nashorn;

import io.reactiverse.es4x.Loader;
import io.vertx.core.Vertx;
import org.junit.Before;
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
  final Loader loader;

  public CommonJSGlobalPollutionTest(String engine) {
    System.setProperty("es4x.engine", engine);
    engineName = engine;
    loader = Loader.create(Vertx.vertx());
  }

  @Before
  public void initialize() {
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
