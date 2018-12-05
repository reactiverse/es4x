package io.reactiverse.es4x.impl.graal;

import io.reactiverse.es4x.ECMAEngine;
import io.reactiverse.es4x.Runtime;
import io.reactiverse.es4x.jul.ES4XFormatter;
import io.vertx.core.Vertx;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.regex.Pattern;

public class GraalEngine implements ECMAEngine {

  private final Vertx vertx;
  private final Engine engine;
  private final AtomicBoolean codecInstalled = new AtomicBoolean(false);

  public GraalEngine(Vertx vertx) {
    this.vertx = vertx;
    final Handler logHandler = new ConsoleHandler();
    // customize the formatter
    logHandler.setFormatter(new ES4XFormatter());
    // build it
    this.engine = Engine.newBuilder()
      .logHandler(logHandler)
      .build();
  }

  @Override
  public String name() {
    return "graaljs";
  }

  @Override
  @SuppressWarnings("unchecked")
  public Runtime<Value> newContext() {
    final Context.Builder builder = Context.newBuilder("js")
      .engine(engine)
      .allowHostAccess(true)
      .allowCreateThread(true);

    final Pattern[] allowedHostAccessClassFilters = allowedHostClassFilters();

    if (allowedHostAccessClassFilters != null) {
      builder.hostClassFilter(fqcn -> {
        for (Pattern filter : allowedHostAccessClassFilters) {
          if (filter.matcher(fqcn).matches()) {
            return true;
          }
        }
        return false;
      });
    }

    // the instance
    final Context context = builder.build();

    // install the codec if needed
    if (codecInstalled.compareAndSet(false, true)) {
      // register a default codec to allow JSON messages directly from GraalJS to the JVM world
      final Consumer callback = value -> vertx.eventBus()
        .unregisterDefaultCodec(value.getClass())
        .registerDefaultCodec(value.getClass(), new JSObjectMessageCodec());

      context.eval(
        Source.newBuilder("js", "(function (fn) { fn({}); })", "<class-lookup>").internal(true).buildLiteral()
      ).execute(callback);
    }

    // setup complete
    return new GraalRuntime(vertx, context);
  }
}
