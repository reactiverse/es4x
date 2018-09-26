package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
import io.vertx.core.Vertx;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

@RunWith(Parameterized.class)
public class JSRuntimeTest {

  @Parameterized.Parameters
  public static List<String> engines() {
    return Arrays.asList("Nashorn", "GraalJS");
  }

  private final String engineName;
  private final Runtime<?> runtime;


  public JSRuntimeTest(String engine) {
    System.setProperty("es4x.engine", engine);
    engineName = engine;
    runtime = Runtime.getCurrent();
  }

  @Before
  public void initialize() {
    assumeTrue(runtime.name().equalsIgnoreCase(engineName));
  }

  @Test
  public void shouldCreateAVertxInstance() {
    Vertx vertx = runtime.vertx(new HashMap<>());
    assertNotNull(vertx);
    assertFalse(vertx.isClustered());
    vertx.close();
  }

  @Test
  public void shouldCreateAClusteredVertxInstance() {
    final Map<String, Object> arguments = new HashMap<>();
    arguments.put("clustered", true);
    Vertx vertx = runtime.vertx(arguments);
    assertNotNull(vertx);
    assertTrue(vertx.isClustered());
    vertx.close();
  }
}
