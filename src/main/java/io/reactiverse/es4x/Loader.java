package io.reactiverse.es4x;

import io.reactiverse.es4x.impl.graal.GraalLoader;
import io.reactiverse.es4x.impl.nashorn.NashornLoader;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public interface Loader<T> {

  static Loader create(Vertx vertx) {
    String rtName = System.getProperty("es4x.engine");
    // rt name takes precedence in the choice
    if (rtName == null) {
      String vmName = System.getProperty("java.vm.name");
      if (vmName != null && vmName.startsWith("GraalVM")) {
        rtName = "GraalVM";
      }
    }

    if (rtName != null && rtName.equalsIgnoreCase("GraalVM")) {
      // attempt to load graal loader
      try {
        return new GraalLoader(vertx);
      } catch (RuntimeException e) {
        // Ignore...
      }
    }
    // fallback (nashorn)
    return new NashornLoader(vertx);
  }

  String name();

  void config(final JsonObject config);

  T require(String main);

  T main(String main);

  T eval(String script) throws Exception;

  T invokeMethod(Object thiz, String method, Object... args);

  void put(String name, Object value);

  void close();
}
