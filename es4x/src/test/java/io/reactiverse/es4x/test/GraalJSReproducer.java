package io.reactiverse.es4x.test;

import org.graalvm.polyglot.*;
import org.junit.Ignore;
import org.junit.Test;

public class GraalJSReproducer {

  @Test
  @Ignore
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
}
