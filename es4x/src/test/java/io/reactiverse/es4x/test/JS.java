package io.reactiverse.es4x.test;

import io.reactiverse.es4x.ECMAEngine;
import io.reactiverse.es4x.impl.JSVerticleFactory;
import io.vertx.core.Vertx;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import io.reactiverse.es4x.Runtime;

public final class JS {

  private JS () {
    throw new RuntimeException("Do not instantiate!");
  }

  static boolean isFunction(Value obj) {
    return obj.canExecute();
  }

  static Value getMember(Value obj, String member) {
    return obj.getMember(member);
  }

  static <T> T getMember(Value obj, String member, Class<T> asClass) {
    return obj.getMember(member).as(asClass);
  }

  static Value call(Object thiz, Value fn, Object... args) {
    return fn.execute(args);
  }

  static <T> T callAs(Object thiz, Value fn, Class<T> asType, Object... args) {
    return fn.execute(args).as(asType);
  }

  static Value require(Runtime runtime, String module) {
    return runtime.get("require").execute(module);
  }

  static Runtime commonjs(Vertx vertx) {
    return new ECMAEngine(vertx).newContext(
      Source.newBuilder("js", JS.class.getResource("../polyfill/json.js")).buildLiteral(),
      Source.newBuilder("js", JS.class.getResource("../polyfill/global.js")).buildLiteral(),
      Source.newBuilder("js", JS.class.getResource("../polyfill/date.js")).buildLiteral(),
      Source.newBuilder("js", JS.class.getResource("../polyfill/console.js")).buildLiteral(),
      Source.newBuilder("js", JS.class.getResource("../polyfill/worker.js")).buildLiteral(),
      Source.newBuilder("js", JS.class.getResource("../polyfill/arraybuffer.js")).buildLiteral(),
      // commonjs loader
      Source.newBuilder("js", JS.class.getResource("../jvm-npm.js")).buildLiteral()
    );
  }

  static Runtime esm(Vertx vertx) {
    return new ECMAEngine(vertx).newContext(
      Source.newBuilder("js", JSVerticleFactory.class.getResource("../polyfill/json.js")).buildLiteral(),
      Source.newBuilder("js", JSVerticleFactory.class.getResource("../polyfill/global.js")).buildLiteral(),
      Source.newBuilder("js", JSVerticleFactory.class.getResource("../polyfill/date.js")).buildLiteral(),
      Source.newBuilder("js", JSVerticleFactory.class.getResource("../polyfill/console.js")).buildLiteral(),
      Source.newBuilder("js", JSVerticleFactory.class.getResource("../polyfill/worker.js")).buildLiteral(),
      Source.newBuilder("js", JSVerticleFactory.class.getResource("../polyfill/arraybuffer.js")).buildLiteral()
    );
  }
}
