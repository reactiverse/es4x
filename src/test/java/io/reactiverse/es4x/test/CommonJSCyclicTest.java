package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Loader;
import io.vertx.core.Vertx;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import static io.reactiverse.es4x.test.JS.*;

@RunWith(Parameterized.class)
public class CommonJSCyclicTest {

  @Parameterized.Parameters
  public static List<String> engines() {
    return Arrays.asList("Nashorn", "GraalVM");
  }

  final String engineName;
  final Loader loader;

  public CommonJSCyclicTest(String engine) {
    System.setProperty("es4x.engine", engine);
    engineName = engine;
    loader = Loader.create(Vertx.vertx());
  }

  @Before
  public void initialize() {
    assumeTrue(loader.name().equalsIgnoreCase(engineName));
  }

  @Test
  public void shouldHaveTheSameSenseOfAnObjectInAllPlaces() {
    Object stream = loader.require("./lib/cyclic2/stream.js");
    assertTrue(isFunction(stream));
    Object readable = getMember(stream, "Readable");
    assertTrue(isFunction(readable));
    Object stream2 = getMember(readable, "Stream");
    assertTrue(isFunction(stream2));
  }
}
