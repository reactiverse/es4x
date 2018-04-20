package io.reactiverse.es4x.graal;

import io.vertx.core.Vertx;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class CommonJSGlobalPollutionTest {

  private static final Vertx vertx = Vertx.vertx();
  private static Loader loader;

  @BeforeClass
  public static void beforeClass() {
    loader = new Loader(vertx);
  }

  @Test
  public void shouldHaveSideEffects() {
    try {
      // this test verifies that the pollution of the global context behaves like on node
      loader.require("./pollution/a.js");
      fail("should throw");
    } catch (RuntimeException e) {
      assertEquals("Error: engine is tainted: b", e.getMessage());
    }
  }
}
