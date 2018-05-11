package io.reactiverse.es4x.impl.graal;

import io.reactiverse.es4x.Loader;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.intellij.lang.annotations.Language;

import java.util.concurrent.atomic.AtomicReference;

public class GraalLoader implements Loader<Value> {

  private final Context context;


  private static final String INSTALL_GLOBAL = "(function(k, v) global[k] = v)";
  private static final String UNINSTALL_GLOBAL = "(function(k) delete global[k])";

  public GraalLoader(final Vertx vertx) {
    // create a engine instance
    context = Context.newBuilder("js").allowAllAccess(true).build();

    // remove the exit and quit functions
    context.eval("js", UNINSTALL_GLOBAL).execute("exit");
    context.eval("js", UNINSTALL_GLOBAL).execute("quit");
    // add vertx as a global
    context.eval("js", INSTALL_GLOBAL).execute("vertx", vertx);

    // register a default codec to allow JSON messages directly from nashorn to the JVM world
    final AtomicReference holder = new AtomicReference();
    context.eval("js", "(function (fn) { fn({}); })").execute((Handler) holder::set);

    vertx.eventBus().unregisterDefaultCodec(holder.get().getClass());
    vertx.eventBus().registerDefaultCodec(holder.get().getClass(), new JSObjectMessageCodec<>(context.eval("js", "JSON"), context));

    // add polyfills
    context.eval("js", vertx.fileSystem().readFileBlocking("io/reactiverse/es4x/polyfill/object.js").toString("UTF-8"));
    context.eval("js", vertx.fileSystem().readFileBlocking("io/reactiverse/es4x/polyfill/json.js").toString("UTF-8"));
    context.eval("js", vertx.fileSystem().readFileBlocking("io/reactiverse/es4x/polyfill/global.js").toString("UTF-8"));
    context.eval("js", vertx.fileSystem().readFileBlocking("io/reactiverse/es4x/polyfill/console.js").toString("UTF-8"));
    context.eval("js", vertx.fileSystem().readFileBlocking("io/reactiverse/es4x/polyfill/promise.js").toString("UTF-8"));
    // install the commonjs loader
    context.eval("js", vertx.fileSystem().readFileBlocking("io/reactiverse/es4x/jvm-npm.js").toString("UTF-8"));
  }

  @Override
  public String name() {
    return "GraalVM";
  }

  @Override
  public void config(final JsonObject config) {
    if (config != null) {
      // add vertx as a global
      context.eval("js", INSTALL_GLOBAL).execute("config", config.getMap());
    }
  }

  @Override
  public Value require(String main) {
    return context.eval("js", "require").execute(main);
  }

  @Override
  public Value main(String main) {
    // patch the main path to be a relative path
    if (!main.startsWith("./") && !main.startsWith("/")) {
      main = "./" + main;
    }
    // invoke the main script
    return require(main);
  }

  @Override
  public Value eval(@Language("JavaScript") String script) {
    return context.eval("js", script);
  }

  @Override
  public Value invokeMethod(Object thiz, String method, Object... args) {
    if (thiz instanceof Value) {
      Value fn = ((Value) thiz).getMember(method);
      if (fn != null) {
        return fn.execute(args);
      }
    }
    return null;
  }

  @Override
  public void put(String name, Object value) {
    context.getBindings("js").putMember(name, value);
  }

  @Override
  public void close() {
    context.close();
  }
}
