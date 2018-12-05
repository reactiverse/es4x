package io.reactiverse.es4x;

import io.reactiverse.es4x.impl.graal.GraalEngine;
import io.reactiverse.es4x.impl.nashorn.NashornEngine;
import io.vertx.core.Vertx;

import java.util.regex.Pattern;

public interface ECMAEngine {

  static ECMAEngine newEngine(Vertx vertx) {
    String userSelectedEngine = System.getProperty("es4x.engine", "graaljs");

    if ("graaljs".equals(userSelectedEngine)) {
      try {

        System.setProperty("es4x.engine", "graaljs");
        return new GraalEngine(vertx);
      } catch (NoClassDefFoundError | IllegalStateException e0) {
        // in the case classes are missing, the graal bits are missing
        // so fallback to Nashorn

        // we could also have an illegal state when the graal is missing
        // the language bits, in that case also try to fallback to nashorn

        // force the engine to nashorn
        userSelectedEngine = "nashorn";
      }
    }

    if ("nashorn".equals(userSelectedEngine)) {
      try {
        System.setProperty("es4x.engine", "nashorn");
        return new NashornEngine(vertx);
      } catch (NoClassDefFoundError | IllegalStateException e1) {
        // in the case classes are missing, nashorn is removed from the JDK
      }
    }

    throw new RuntimeException("Unsupported runtime [" + userSelectedEngine + "]");
  }

  default Pattern[] allowedHostClassFilters() {
    String hostClassFilter = System.getProperty("es4x.host.class.filter", System.getenv("ES4XHOSTCLASSFILTER"));
    if (hostClassFilter == null || hostClassFilter.length() == 0) {
      return null;
    }

    String[] glob = hostClassFilter.split(",");
    Pattern[] patterns = new Pattern[glob.length];
    for (int i = 0; i < patterns.length; i++) {
      patterns[i] = Pattern.compile(glob[i]
        // replace dots
        .replace(".", "\\.")
        // replace stars
        .replace("*", "[^\\.]+")
        // replace double stars
        .replace("[^\\.]+[^\\.]+", ".*")
        // replace ?
        .replace("?", "\\w")
      );
    }

    return patterns;
  }

  /**
   * return the engine name
   *
   * @return engine name.
   */
  String name();

  /**
   * return a new context for this engine.
   *
   * @return new context.
   */
  <T> Runtime<T> newContext();
}
