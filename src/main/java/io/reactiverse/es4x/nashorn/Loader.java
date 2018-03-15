package io.reactiverse.es4x.nashorn;

import io.reactiverse.es4x.nashorn.runtime.VertxRuntime;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import jdk.nashorn.api.scripting.NashornScriptEngine;

import javax.script.*;

class Loader {

  private final NashornScriptEngine engine;

  Loader(final Vertx vertx) throws ScriptException, NoSuchMethodException {
    // enable ES6 features
    if (System.getProperty("nashorn.args") == null) {
      System.setProperty("nashorn.args", "--language=es6");
    }
    // create a engine instance
    engine = (NashornScriptEngine) new ScriptEngineManager().getEngineByName("nashorn");

    final Bindings engineBindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
    // remove the exit and quit functions
    engineBindings.remove("exit");
    engineBindings.remove("quit");

    final Bindings globalBindings = new SimpleBindings();
    // add vertx as a global
    globalBindings.put("vertx", vertx);
    // add the global reference to the bindings
    globalBindings.put("global", engine.eval("this"));

    engine.setBindings(globalBindings, ScriptContext.GLOBAL_SCOPE);

    // install the vert.x runtime
    VertxRuntime.install(globalBindings);

    // install the commonjs loader
    engine.invokeFunction("load", "classpath:jvm-npm.js");
    // add support for promises
    engine.invokeFunction("load", "classpath:promise.js");
  }

  ScriptEngine getEngine() {
    return engine;
  }

  void config(final JsonObject config) throws ScriptException {
    final Bindings engineBindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
    // expose the config as a global
    engineBindings.put("config", config != null ? config.getMap() : null);
  }

  void main(String main) throws ScriptException, NoSuchMethodException {
    // patch the main path to be a relative path
    if (!main.startsWith("./") && !main.startsWith("/")) {
      main = "./" + main;
    }
    // invoke the main script
    engine.invokeFunction("require", main);
  }
}
