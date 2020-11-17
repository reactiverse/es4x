/*
 * Copyright 2018 Red Hat, Inc.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  The Apache License v2.0 is available at
 *  http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */
package io.reactiverse.es4x;

import io.netty.buffer.Unpooled;
import io.reactiverse.es4x.impl.JSObjectMessageCodec;
import io.reactiverse.es4x.impl.VertxFileSystem;
import io.reactiverse.es4x.jul.ES4XFormatter;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.impl.VertxInternal;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.graalvm.polyglot.*;
import org.graalvm.polyglot.io.FileSystem;
import org.graalvm.polyglot.proxy.Proxy;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static io.reactiverse.es4x.impl.AsyncError.parseStrackTraceElement;

public final class ECMAEngine {

  private static final Logger LOG = Logger.getLogger(ECMAEngine.class.getName());

  private static Pattern[] allowedHostClassFilters() {
    String hostClassFilter = System.getProperty("es4x.host.class.filter", System.getenv("ES4XHOSTCLASSFILTER"));
    if (hostClassFilter == null || hostClassFilter.length() == 0) {
      return null;
    }

    String[] glob = hostClassFilter.split(",");
    Pattern[] patterns = new Pattern[glob.length];
    for (int i = 0; i < patterns.length; i++) {
      boolean negate = false;
      String regex = glob[i];

      if (glob[i].charAt(0) == '!') {
        negate = true;
        regex = glob[i].substring(1);
      }

      patterns[i] = Pattern.compile(
        (negate ? "^(?!" : "") +
          regex
            // replace dots
            .replace(".", "\\.")
            // replace stars
            .replace("*", "[^\\.]+")
            // replace double stars
            .replace("[^\\.]+[^\\.]+", ".*")
            // replace ?
            .replace("?", "\\w") +
          (negate ? "$).*$" : "")
      );
    }

    return patterns;
  }

  private final Vertx vertx;
  private final Engine engine;
  private final HostAccess hostAccess;
  private final FileSystem fileSystem;
  private final PolyglotAccess polyglotAccess;
  private final Source objLookup;
  private final Source arrLookup;

  // lazy install the codec
  private final AtomicBoolean codecInstalled = new AtomicBoolean(false);
  private static boolean nag = true;

  public ECMAEngine(Vertx vertx) {
    this.vertx = vertx;
    final Handler logHandler = new ConsoleHandler();
    // customize the formatter
    logHandler.setFormatter(new ES4XFormatter());
    // build it
    this.engine = Engine.newBuilder()
      .logHandler(logHandler)
      .build();

    if (!engine.getLanguages().containsKey("js")) {
      throw new IllegalStateException("A language with id 'js' is not installed");
    }

    if (nag) {
      nag = false;
      if ("Interpreted".equalsIgnoreCase(engine.getImplementationName())) {
        LOG.warning("ES4X is using graaljs in interpreted mode! Add the JVMCI compiler module in order to run in optimal mode!");
      }
    }

    // enable or disable the polyglot access
    polyglotAccess = Boolean.getBoolean("es4x.polyglot") ? PolyglotAccess.ALL : PolyglotAccess.NONE;

    // source caches
    objLookup = Source.newBuilder("js", "(function (fn) { fn({}); })", "<cache#objLookup>")
      .cached(true)
      .internal(true)
      .buildLiteral();
    arrLookup = Source.newBuilder("js", "(function (fn) { fn([]); })", "<cache#arrLookup>")
      .cached(true)
      .internal(true)
      .buildLiteral();

    hostAccess = HostAccess.newBuilder(HostAccess.ALL)
      /// Highest Precedence
      /// accepts is null, so we can quickly assert the type

      // [] -> Object
      .targetTypeMapping(
        List.class,
        Object.class,
        null,
        v -> v,
        HostAccess.TargetMappingPrecedence.HIGHEST)
      // number -> Byte
      .targetTypeMapping(
        Number.class,
        Byte.class,
        null,
        Number::byteValue,
        HostAccess.TargetMappingPrecedence.HIGHEST)

      /// High Precedence
      /// Most usual types

      // [...] -> JsonArray
      .targetTypeMapping(
        Value.class,
        JsonArray.class,
        Value::hasArrayElements,
        v -> {
          if (v.isProxyObject()) {
            final Proxy p = v.asProxyObject();
            // special case, if the value is a proxy and JsonObject, just unwrap
            if (p instanceof JsonArray) {
              return (JsonArray) p;
            }
          }
          return new JsonArray(v.as(List.class));
        },
        HostAccess.TargetMappingPrecedence.HIGH)
      // Thenable -> Future
      .targetTypeMapping(
        Map.class,
        Future.class,
        v -> v.containsKey("then") && v.get("then") instanceof Function,
        v -> {
          final Promise<Object> promise = ((VertxInternal) vertx).promise();
          ((Function<Object[], Object>) v.get("then")).apply(new Object[] {
            (Consumer<Object>) promise::complete,
            (Consumer<Object>) failure -> {
              if (failure instanceof Throwable) {
                promise.fail((Throwable) failure);
              } else {
                if (failure instanceof Map) {
                  final Map map = (Map) failure;
                  if (map.containsKey("name") && map.containsKey("message")) {
                    promise.fail(
                      wrap(
                        (String) map.get("name"),
                        (String) map.get("message"),
                        (String) map.get("stack")));
                  } else {
                    promise.fail(failure.toString());
                  }
                } else {
                  promise.fail(failure == null ? null : failure.toString());
                }
              }
            }
          });
          return promise.future();
        },
        HostAccess.TargetMappingPrecedence.HIGH)
      // {...} -> JsonObject
      .targetTypeMapping(
        Value.class,
        JsonObject.class,
        v -> v.hasMembers() && !v.hasArrayElements(),
        v -> {
          if (v.isProxyObject()) {
            final Proxy p = v.asProxyObject();
            // special case, if the value is a proxy and JsonObject, just unwrap
            if (p instanceof JsonObject) {
              return (JsonObject) p;
            }
          }
          return new JsonObject(v.as(Map.class));
        },
        HostAccess.TargetMappingPrecedence.HIGH)

      /// Low Precedence
      /// Either because the higher ones are more important, or are not expected to be used often

      // ArrayBuffer -> Buffer
      .targetTypeMapping(
        Value.class,
        Buffer.class,
        v -> v.hasMember("__jbuffer"),
        v -> Buffer.buffer(Unpooled.wrappedBuffer(v.getMember("__jbuffer").as(ByteBuffer.class))),
        HostAccess.TargetMappingPrecedence.LOW)
      // [...] -> java.util.Set
      .targetTypeMapping(
        Value.class,
        Set.class,
        Value::hasArrayElements,
        v -> new HashSet(v.as(List.class)),
        HostAccess.TargetMappingPrecedence.LOW)
      // Error -> Throwable
      .targetTypeMapping(
        Value.class,
        Throwable.class,
        v -> v.hasMember("name") && v.hasMember("message"),
        v -> {
          if (v.hasMember("stack")) {
            return wrap(
              v.getMember("name").asString(),
              v.getMember("message").asString(),
              v.getMember("stack").asString());
          } else {
            return wrap(
              v.getMember("name").asString(),
              v.getMember("message").asString(),
              null);
          }
        },
        HostAccess.TargetMappingPrecedence.LOW)
      .build();

    fileSystem = new VertxFileSystem(vertx, ".mjs", ".js");
  }

  private static Throwable wrap(String name, String message, String stack) {
    // empty message fields usually it JS prints the name field
    final Throwable t = new Throwable("".equals(message) ? name : message);
    // the stacktrace for JS is a single string and we need to parse it back to a Java friendly way
    if (stack != null) {
      String[] sel = stack.split("\n");
      StackTraceElement[] elements = new StackTraceElement[sel.length - 1];
      for (int i = 1; i < sel.length; i++) {
        elements[i - 1] = parseStrackTraceElement(sel[i]);
      }
      t.setStackTrace(elements);
    }

    return t;
  }

  private void registerCodec(Class className) {
    vertx.eventBus()
      .unregisterDefaultCodec(className)
      .registerDefaultCodec(className, new JSObjectMessageCodec(className.getName()));
  }

  /**
   * return a new context for this engine.
   *
   * @param scripts scripts that shall be run at the creation of the runtime,
   *                usually for polyfills, initialization, customization.
   * @return new context.
   */
  public synchronized Runtime newContext(Source... scripts) {

    final Pattern[] allowedHostAccessClassFilters = ECMAEngine.allowedHostClassFilters();

    final Context.Builder builder = Context.newBuilder("js")
      .engine(engine)
      .fileSystem(fileSystem)
      // IO is allowed because it delegates to the
      // vertx filesystem implementation
      .allowIO(true)
      // do not allow creation of threads as it breaks the JS model
      // multi threading is allowed using workers
      .allowCreateThread(false)
      // host access is required to function properly however
      // users might declare filters
      .allowHostClassLookup(fqcn -> {
        if (allowedHostAccessClassFilters == null) {
          return true;
        } else {
          for (Pattern filter : allowedHostAccessClassFilters) {
            if (filter.matcher(fqcn).matches()) {
              return true;
            }
          }
          return false;
        }
      })
      .allowHostAccess(hostAccess)
      .allowPolyglotAccess(polyglotAccess);

    // allow specifying the custom ecma version
    if (System.getProperty("js.ecmascript-version") != null) {
      builder.option("js.ecmascript-version", System.getProperty("js.ecmascript-version"));
    }

    // the instance
    final Context context = builder.build();

    // install the codec if needed
    if (codecInstalled.compareAndSet(false, true)) {
      // register a default codec to allow JSON messages directly from GraalJS to the JVM world
      final Consumer<?> callback = value -> registerCodec(value.getClass());
      context.eval(objLookup).execute(callback);
      context.eval(arrLookup).execute(callback);
    }

    // setup complete
    return new Runtime(vertx, context, scripts);
  }

  public void close() {
    engine.close();
  }
}
