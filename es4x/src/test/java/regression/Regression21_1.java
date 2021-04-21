package regression;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.PolyglotAccess;
import org.graalvm.polyglot.Source;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class Regression21_1 {

  @Test
  public void regression() {
    Context context = Context.newBuilder("js")
      .allowIO(true)
      .allowCreateThread(false)
      .allowHostAccess(HostAccess.newBuilder(HostAccess.ALL).allowBufferAccess(true).build())
      .allowPolyglotAccess(PolyglotAccess.NONE)
      .allowHostClassLookup(x -> true)
      .build();
    Source source = Source
      .newBuilder("js",
        "const System = Java.type('java.lang.System');\n" +
          "let env = System.getenv();\n" +
//          "let env = new Proxy({}, {\n" +
//          "      get: function (obj, prop) {\n" +
//          "        return System.getenv(prop);\n" +
//          "      }\n" +
//          "    });\n" +
          "print(env.PATH);\n" +
          "env;",
        "main.js")
      .buildLiteral();

    System.out.println(
      context.eval(source).as(Map.class).get("PATH"));
  }
}
