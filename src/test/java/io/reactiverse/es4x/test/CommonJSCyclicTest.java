package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Loader;
import io.reactiverse.es4x.Runtime;
import io.vertx.ext.unit.junit.RunTestOnContext;
import org.junit.Before;
import org.junit.Rule;
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
    return Arrays.asList("Nashorn", "GraalJS");
  }

  private final String engineName;
  private Loader loader;

  public CommonJSCyclicTest(String engine) {
    System.setProperty("es4x.engine", engine);
    engineName = engine;
  }

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    loader = Runtime.getCurrent().loader(rule.vertx());
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
