package io.reactiverse.es4x.test;

import io.reactiverse.es4x.impl.nashorn.NashornJSRuntime;
import io.vertx.core.Vertx;
import jdk.nashorn.api.scripting.AbstractJSObject;
import jdk.nashorn.api.scripting.JSObject;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class JSRuntimeTest {

  final JSObject JSON = new AbstractJSObject() {
    @Override
    public Object call(Object thiz, Object... args) {
      return super.call(thiz, args);
    }
  };

  @Test
  public void shouldCreateAVertxInstance() {
    Vertx vertx = NashornJSRuntime.install(JSON, new HashMap<>());
    assertNotNull(vertx);
    assertFalse(vertx.isClustered());
    vertx.close();
  }

  @Test
  public void shouldCreateAClusteredVertxInstance() {
    final Map<String, Object> arguments = new HashMap<>();
    arguments.put("clustered", true);
    Vertx vertx = NashornJSRuntime.install(JSON, arguments);
    assertNotNull(vertx);
    assertTrue(vertx.isClustered());
    vertx.close();
  }
}
