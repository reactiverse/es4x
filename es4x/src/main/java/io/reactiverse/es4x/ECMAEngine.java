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
import io.reactiverse.es4x.jul.ANSIFormatter;
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
import java.util.*;
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
    logHandler.setFormatter(new ANSIFormatter());
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
    polyglotAccess = Boolean.getBoolean("es4x.no-polyglot-access") ? PolyglotAccess.NONE : PolyglotAccess.ALL;

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
      // temp workaround for 21.1.0 regression
      .allowBufferAccess(true)

      /// Highest Precedence
      /// accepts is null, so we can quickly assert the type

      // Goal: [] -> Object
      // When the target type is "exactly" <java.lang.Object> and the source is a array like object (json array for
      // example) then we want it to be a <java.util.List>, otherwise the default behavior of graal is <java.util.Map>
      .targetTypeMapping(
        List.class,
        Object.class,
        Objects::nonNull,
        v -> v,
        HostAccess.TargetMappingPrecedence.HIGHEST)
      // Goal: number -> Byte
      // Graal already converts numeric types to byte, however most JS APIs assume bytes to be treated as unsigned
      // values, which is not the default behaviour of the JVM. Here we override the default and "explicitly" declare
      // that if the target is "java.lang.Byte" always assume the source to be "unsigned"
      .targetTypeMapping(
        Number.class,
        Byte.class,
        Objects::nonNull,
        Number::byteValue,
        HostAccess.TargetMappingPrecedence.HIGHEST)

      /// High Precedence (default precedence)
      /// Most usual types

      // Goal: [...] -> JsonArray
      // Convert array like object to <io.vertx.core.json.JsonArray> and unwrap if the source is a proxy that extends
      // the JsonArray class.
      .targetTypeMapping(
        Value.class,
        JsonArray.class,
        Value::hasArrayElements,
        v -> {
          if (v.isProxyObject()) {
            final Proxy p = v.asProxyObject();
            // special case, if the value is a proxy and JsonArray, just unwrap
            if (p instanceof JsonArray) {
              return (JsonArray) p;
            }
          }
          return new JsonArray(v.as(List.class));
        },
        HostAccess.TargetMappingPrecedence.HIGH)
      // Goal: Thenable -> Future
      // This shall be heavily used. When the source object is a "Thenable" object and the target a
      // <io.vertx.core.Future> wrap it to match the desired target.
      .targetTypeMapping(
        Map.class,
        Future.class,
        v -> v.get("then") instanceof Function,
        v -> {
          final Promise<Object> promise = ((VertxInternal) vertx).promise();
          ((Function<Object[], Object>) v.get("then")).apply(new Object[] {
            (Consumer<Object>) promise::complete,
            (Consumer<Object>) failure -> {
              if (failure instanceof Throwable) {
                promise.fail((Throwable) failure);
              } else {
                if (failure instanceof Map) {
                  // this happens when JS error messages are bubbled up from the thenable
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
      // Goal: {...} -> JsonObject
      // By default this is the catch all for JS object, just like the Array above, if the object is a Proxy and an
      // instance of JsonObject then unwrap
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

      // Goal: ArrayBuffer -> Buffer
      // This is a "power user" feature and not expected to be used often. If the object contains a property "__jbuffer"
      // assume it is a wrapped Buffer and get it underlying <java.nio.ByteBuffer> and adapt to vertx Buffer type.
      .targetTypeMapping(
        Value.class,
        Buffer.class,
        Value::hasBufferElements,
        v -> {
          if (v.hasMember("__jbuffer")) {
            return Buffer.buffer(Unpooled.wrappedBuffer(v.getMember("__jbuffer").as(ByteBuffer.class)));
          } else {
            // slow path
            long size = v.getBufferSize();
            Buffer b = Buffer.buffer((int) size);
            for (long i = 0; i < size; i++) {
              b.appendByte(v.readBufferByte(i));
            }
            return b;
          }
        },
        HostAccess.TargetMappingPrecedence.LOW)
      // Goal: [...] -> java.util.Set
      // Sets are used sporadically on vert.x APIs, as converting from JS Set to <java.util.Set> is a bit cumbersome
      // this mapping assumes sources to be array like objects and tries to wrap as a <java.util.HashSet>. As the
      // precedence is low, when on doubt graal shall pick the mapping above first
      .targetTypeMapping(
        Value.class,
        Set.class,
        Value::hasArrayElements,
        v -> new HashSet(v.as(List.class)),
        HostAccess.TargetMappingPrecedence.LOW)
      // Goal: Error -> Throwable
      // Errors are expected to be used sporadically too, this helper is just extracting the default error fields from
      // a JS error to build a <java.util.Throwable> including the stacktrace if possible.
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
  public synchronized Runtime newContext(FileSystem fileSystem, Source... scripts) {

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
      .allowPolyglotAccess(polyglotAccess)
      .allowEnvironmentAccess(EnvironmentAccess.INHERIT)
      .option("js.foreign-object-prototype", "true");

    // allow specifying the custom ecma version
    setBuilderOption(builder, "js.ecmascript-version");

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

  private static void setBuilderOption(Context.Builder builder, String option) {
    String value = System.getProperty(option);
    if (value != null) {
      builder.option(option, value);
    }
  }
}
