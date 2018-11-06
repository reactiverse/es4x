package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
import io.vertx.core.Vertx;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class JSRuntimeTest {

  @Test
  public void shouldCreateAVertxInstance() {
    Vertx vertx = Runtime.vertx(new HashMap<>());
    assertNotNull(vertx);
    assertFalse(vertx.isClustered());
    vertx.close();
  }

  @Test
  public void shouldCreateAClusteredVertxInstance() {
    final Map<String, Object> arguments = new HashMap<>();
    arguments.put("clustered", true);
    Vertx vertx = Runtime.vertx(arguments);
    assertNotNull(vertx);
    assertTrue(vertx.isClustered());
    vertx.close();
  }
}
