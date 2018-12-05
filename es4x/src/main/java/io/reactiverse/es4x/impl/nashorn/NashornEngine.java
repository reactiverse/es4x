package io.reactiverse.es4x.impl.nashorn;

import io.reactiverse.es4x.ECMAEngine;
import io.vertx.core.Vertx;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.ScriptEngine;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

public class NashornEngine implements ECMAEngine {

  private final Vertx vertx;
  private final NashornScriptEngineFactory factory;
  private final AtomicBoolean codecInstalled = new AtomicBoolean(false);

  public NashornEngine(Vertx vertx) {
    this.vertx = vertx;
    // enable ES6 features
    if (System.getProperty("nashorn.args") == null) {
      System.setProperty("nashorn.args", "--language=es6");
    }
    // create the factory
    this.factory = new NashornScriptEngineFactory();
  }

  @Override
  public String name() {
    return "nashorn";
  }

  @Override
  @SuppressWarnings("unchecked")
  public NashornRuntime newContext() {

    final ScriptEngine engine;

    final Pattern[] allowedHostAccessClassFilters = allowedHostClassFilters();

    if (allowedHostAccessClassFilters != null) {
      engine = factory.getScriptEngine(fqcn -> {
        for (Pattern filter : allowedHostAccessClassFilters) {
          if (filter.matcher(fqcn).matches()) {
            return true;
          }
        }
        return false;
      });
    } else {
      engine = factory.getScriptEngine();
    }

    // install the codec if needed
    if (codecInstalled.compareAndSet(false, true)) {
      // register a default codec to allow JSON messages directly from Nashorn to the JVM world
      vertx.eventBus()
        .unregisterDefaultCodec(ScriptObjectMirror.class)
        .registerDefaultCodec(ScriptObjectMirror.class, new JSObjectMessageCodec());
    }

    return new NashornRuntime(vertx, (NashornScriptEngine) engine);
  }
}
