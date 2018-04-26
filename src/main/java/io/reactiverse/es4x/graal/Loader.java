package io.reactiverse.es4x.graal;

import io.reactiverse.es4x.graal.runtime.VertxRuntime;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.intellij.lang.annotations.Language;

public class Loader {

  private final Context context;


  private static final String INSTALL_GLOBAL = "(function(k, v) global[k] = v)";
  private static final String UNINSTALL_GLOBAL = "(function(k) delete global[k])";

  Loader(final Vertx vertx) {
    // create a engine instance
    context = Context.newBuilder("js").allowAllAccess(true).build();

    // remove the exit and quit functions
    context.eval("js", UNINSTALL_GLOBAL).execute("exit");
    context.eval("js", UNINSTALL_GLOBAL).execute("quit");
    // add vertx as a global
    context.eval("js", INSTALL_GLOBAL).execute("vertx", vertx);

    // install the vert.x runtime
    VertxRuntime.install(context);

    // install the commonjs loader
    context.eval("js", vertx.fileSystem().readFileBlocking("io/reactiverse/es4x/jvm-npm.js").toString("UTF-8"));
    // add polyfills
    context.eval("js", vertx.fileSystem().readFileBlocking("io/reactiverse/es4x/polyfill.js").toString("UTF-8"));
  }

  void config(final JsonObject config) {
    if (config != null) {
      // add vertx as a global
      context.eval("js", INSTALL_GLOBAL).execute("config", config.getMap());
    }
  }

  Value require(String main) {
    return context.eval("js", "require").execute(main);
  }

  Value main(String main) {
    // patch the main path to be a relative path
    if (!main.startsWith("./") && !main.startsWith("/")) {
      main = "./" + main;
    }
    // invoke the main script
    return require(main);
  }

  Value eval(@Language("JavaScript") String script) {
    return context.eval("js", script);
  }

  public void close() {
    context.close();
  }
}
