package io.reactiverse.es4x.nashorn;

import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.spi.VerticleFactory;
import jdk.nashorn.api.scripting.NashornScriptEngine;

public class NashornVerticleFactory implements VerticleFactory {

  private Vertx vertx;

  @Override
  public void init(Vertx vertx) {
    this.vertx = vertx;
  }

  @Override
  public String prefix() {
    return "js";
  }

  @Override
  public Verticle createVerticle(String verticleName, ClassLoader classLoader) throws Exception {

    final Loader loader;

    synchronized (this) {
      // create a new CommonJS loader
      loader = new Loader(vertx);
    }

    return new Verticle() {

      private Vertx vertx;
      private Context context;

      @Override
      public Vertx getVertx() {
        return vertx;
      }

      @Override
      public void init(Vertx vertx, Context context) {
        this.vertx = vertx;
        this.context = context;
      }

      @Override
      public void start(Future<Void> startFuture) throws Exception {
        // expose config
        if (context != null && context.config() != null) {
          loader.config(context.config());
        }

        // extract prefix if present
        if (verticleName.startsWith(prefix() + ":")) {
          loader.main(verticleName.substring(prefix().length() + 1));
        } else {
          loader.main(verticleName);
        }

        startFuture.complete();
      }

      @Override
      public void stop(Future<Void> stopFuture) throws Exception {
        NashornScriptEngine engine = (NashornScriptEngine) loader.getEngine();
        try {
          engine.invokeMethod(engine.get("process"), "stop", stopFuture);
        } catch (RuntimeException e) {
          stopFuture.fail(e);
        }
      }
    };
  }
}
