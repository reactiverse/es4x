package io.reactiverse.es4x.test;

import org.graalvm.polyglot.*;
import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;

public class GraalJSReproducer {

  @Test
  public void test() {

    //@language=js
    String script =
      "const Logger = Java.type('java.util.logging.Logger');\n" +
      "const Level = Java.type('java.util.logging.Level');\n" +
      "\n" +
      "const log = Logger.getLogger('es4x');\n";



    try (Context context = Context.newBuilder("js")
      .allowHostClassLookup(fqcn -> true)
      .allowHostAccess(
        HostAccess.newBuilder(HostAccess.ALL)
          .targetTypeMapping(
            Value.class,
            Throwable.class,
            it -> true,
            it -> null)
          .build())
      .build()) {
      context.eval(
        Source.newBuilder(
          "js",
          script,
          "script.js").buildLiteral());

      // failure
      context.eval(
        Source.newBuilder(
          "js",
          "log.log(Level.INFO, 'some text', [])",
          "script.js").buildLiteral());
    }
  }

  @Test
  public void test21_0_0_DEV() {

    try (Context context = Context.newBuilder("js")
      .allowHostClassLookup(fqcn -> true)
      .allowHostAccess(HostAccess.ALL)
      .build()) {

      Value bindings = context.getBindings("js");

      bindings.putMember("mybinding", new ConcurrentHashMap<>());

      context.eval(
        Source.newBuilder(
          "js",
          "print(mybinding);",
          "script.js").buildLiteral());

      // failure
      bindings.removeMember("mybinding");
    }
  }
}
