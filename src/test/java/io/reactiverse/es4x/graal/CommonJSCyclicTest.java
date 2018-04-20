package io.reactiverse.es4x.graal;

import io.vertx.core.Vertx;
import org.graalvm.polyglot.Value;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CommonJSCyclicTest {

  private static final Vertx vertx = Vertx.vertx();
  private static Loader loader;

  @BeforeClass
  public static void beforeClass() {
    loader = new Loader(vertx);
  }

  @Test
  public void shouldHaveTheSameSenseOfAnObjectInAllPlaces() {
    Value stream = loader.require("./lib/cyclic2/stream.js");
    assertTrue(stream.canExecute());
    Value readable = stream.getMember("Readable");
    assertTrue(readable.canExecute());
    Value stream2 = readable.getMember("Stream");
    assertTrue(stream2.canExecute());
  }
}
