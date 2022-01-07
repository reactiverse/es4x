package regression;

import io.reactiverse.es4x.ECMAEngine;
import io.reactiverse.es4x.impl.ESModuleIO;
import io.reactiverse.es4x.impl.VertxFileSystem;
import io.vertx.core.Vertx;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.PolyglotAccess;
import org.graalvm.polyglot.Source;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Regression21_3 {

  static {
    System.setProperty("polyglot.inspect", "9229");
    System.setProperty("polyglot.inspect.Suspend", "true");
    System.setProperty("polyglot.inspect.Internal", "true");
  }

  @Test
  @Ignore
  public void regression() throws InterruptedException, IOException {

    Context context = Context.newBuilder("js")
      .allowIO(true)
      .allowCreateThread(false)
      .allowHostAccess(HostAccess.newBuilder(HostAccess.ALL).allowBufferAccess(true).build())
      .allowPolyglotAccess(PolyglotAccess.NONE)
      .allowHostClassLookup(x -> true)
      .allowExperimentalOptions(true)
      .build();

    Source source = Source
      .newBuilder("js", new File("/home/paulo/index.js").getAbsoluteFile())
      // strip the shebang if present
      .content("const System = Java.type('java.lang.System');\n" +
        "let env = System.getenv();\n" +
//          "let env = new Proxy({}, {\n" +
//          "      get: function (obj, prop) {\n" +
//          "        return System.getenv(prop);\n" +
//          "      }\n" +
//          "    });\n" +
        "print(env.PATH);\n" +
        "env;")
      .cached(true)
      .interactive(false)
      .mimeType("application/javascript+module")
      .buildLiteral();


//    Source source = Source
//      .newBuilder("js",
//        "const System = Java.type('java.lang.System');\n" +
//          "let env = System.getenv();\n" +
////          "let env = new Proxy({}, {\n" +
////          "      get: function (obj, prop) {\n" +
////          "        return System.getenv(prop);\n" +
////          "      }\n" +
////          "    });\n" +
//          "print(env.PATH);\n" +
//          "env;",
//        "index.js")
//      .cached(true)
//      .interactive(false)
//      .mimeType("application/javascript+module")
//      .build();

    System.out.println(
      context.eval(source).as(Map.class).get("PATH"));


    Thread.sleep(90_000);
  }
}
