package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
import io.reactiverse.es4x.impl.graal.GraalEngine;
import io.reactiverse.es4x.impl.nashorn.NashornEngine;
import io.vertx.core.Vertx;

import java.util.regex.Pattern;

import static org.junit.Assume.assumeTrue;

public class Helper {

  public static Runtime getRuntime(Vertx vertx, String name) {

    switch (name) {
      case "Nashorn":
        try {
          System.setProperty("es4x.engine", name.toLowerCase());
          return new NashornEngine(vertx).newContext();
        } catch (Throwable t) {
          assumeTrue(name + " is not available", false);
        }
        break;
      case "GraalJS":
        try {
          System.setProperty("es4x.engine", name.toLowerCase());
          return new GraalEngine(vertx).newContext();
        } catch (Throwable t) {
          assumeTrue(name + " is not available", false);
        }
        break;
      default:
        throw new RuntimeException(name + " is not a valid runtime");
    }

    return null;
  }
}
