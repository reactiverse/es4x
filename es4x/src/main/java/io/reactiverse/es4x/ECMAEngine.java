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
  private final Source arrayFrom;
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
    arrayFrom = Source.newBuilder("js", "Array.from", "<cache#1>")
      .cached(true)
      .internal(true)
      .buildLiteral();
    objLookup = Source.newBuilder("js", "(function (fn) { fn({}); })", "<cache#2>")
      .cached(true)
      .internal(true)
      .buildLiteral();
    arrLookup = Source.newBuilder("js", "(function (fn) { fn([]); })", "<cache#3>")
      .cached(true)
      .internal(true)
      .buildLiteral();

    hostAccess = HostAccess.newBuilder(HostAccess.ALL)
      /// High Precedence
      // [] -> Object
      .targetTypeMapping(
        List.class,
        Object.class,
        null,
        v -> v)
      /// High precedence Number => (Byte)
      // number -> Byte
      .targetTypeMapping(
        Number.class,
        Byte.class,
        null,
        Number::byteValue)
      /// High precedence Map => (Thenable, Error)
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
                  if (((Map) failure).containsKey("name") && ((Map) failure).containsKey("message")) {
                    promise.fail(wrap((Map) failure));
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
        })
      // Error -> Throwable
      .targetTypeMapping(
        Map.class,
        Throwable.class,
        v -> v.containsKey("name") && v.containsKey("message"),
        ECMAEngine::wrap)
      // ArrayBuffer -> Buffer
      .targetTypeMapping(
        Value.class,
        Buffer.class,
        v -> typeOf(v, "EArrayBuffer"),
        v -> {
          if (v.hasMember("__jbuffer")) {
            return Buffer.buffer(Unpooled.wrappedBuffer(v.getMember("__jbuffer").as(ByteBuffer.class)));
          } else {
            throw new ClassCastException("Cannot cast ArrayBuffer without j.n.ByteBuffer to Buffer");
          }
        })
      // Set -> java.util.Set
      .targetTypeMapping(
        Value.class,
        Set.class,
        v -> typeOf(v,"Set"),
        v ->
          new HashSet(
            v.getContext()
              .eval(arrayFrom)
              .execute(v)
              .as(List.class)))
      // [...] -> JsonArray
      .targetTypeMapping(
        Value.class,
        JsonArray.class,
        Value::hasArrayElements,
        v -> {
          // special case, if the value is a proxy and JsonObject, just unwrap
          if (v.isProxyObject()) {
            final Proxy p = v.asProxyObject();
            if (p instanceof JsonArray) {
              return (JsonArray) p;
            }
          }
          return new JsonArray(v.as(List.class));
        })
      // {...} -> JsonObject
      .targetTypeMapping(
        Value.class,
        JsonObject.class,
        v -> v.hasMembers() && !v.hasArrayElements(),
        v -> {
          // special case, if the value is a proxy and JsonObject, just unwrap
          if (v.isProxyObject()) {
            final Proxy p = v.asProxyObject();
            if (p instanceof JsonObject) {
              return (JsonObject) p;
            }
          }
          return new JsonObject(v.as(Map.class));
        })
      .build();

    fileSystem = new VertxFileSystem(vertx, ".mjs", ".js");
  }

  /**
   * This is a simple and Naive way to perform inheritance checks on JavaScript without
   * (hopefully) relying on running code on the engine.
   * @param obj the object to check
   * @param match any of the expected type name to be matched
   * @return true if the prototype constructor name matches
   */
  private boolean typeOf(Value obj, String match) {
    if (obj == null) {
      return false;
    }

    if (!obj.hasMembers() && !obj.isProxyObject()) {
      return false;
    }

    Value meta = obj.getMetaObject();
    if (meta == null) {
      return false;
    }
    if (meta.isMetaObject()) {
      final String mqn = meta.getMetaQualifiedName();
      if (mqn != null) {
        return match.equals(mqn);
      }
    }

    return false;
  }

  private static Throwable wrap(Map v) {
    // an error has 2 fields: name + message
    final String nameField = v.get("name").toString();
    final String messageField = v.get("message").toString();
    // empty message fields usually it JS prints the name field
    final Throwable t = new Throwable("".equals(messageField) ? nameField : messageField);
    // the stacktrace for JS is a single string and we need to parse it back to a Java friendly way
    if (v.containsKey("stack")) {
      String stack = v.get("stack").toString();
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
