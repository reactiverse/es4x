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
package io.reactiverse.es4x.impl.graal;

import io.reactiverse.es4x.Runtime;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.graalvm.polyglot.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

public class GraalRuntime implements Runtime<Value> {

  // this will keep a reference to the type used between the 2 runtimes
  // the time should not change across instances so it is safe to assume static
  private static final AtomicReference<Class<?>> INTEROP_TYPE = new AtomicReference<>();

  static {
    // if this code is used in a native image avoid the reflection guess game
    // and hard code the expected class to be a PolyglotMap
    if (System.getProperty("org.graalvm.nativeimage.imagecode") != null) {
      try {
        INTEROP_TYPE.set(Class.forName("com.oracle.truffle.polyglot.PolyglotMap"));
      } catch (ClassNotFoundException e) {
        // swallow the exception and try to get the right class at runtime
      }
    }
  }

  private static final Object lock = new Object();
  // lazily create a polyglot engine
  private static Engine engine;

  private final Context context;
  private final Value bindings;
  private final Value module;

  private static Engine engine() {
    synchronized (lock) {
      if (engine == null) {
        engine = Engine.create();
      }
    }
    return engine;
  }

  public GraalRuntime(final Vertx vertx) {
    this(
      vertx,
      // create the default context
      Context
        .newBuilder("js")
        .allowHostAccess(true)
        .allowCreateThread(true)
        // by sharing the same engine we allow
        // easier debugging and a single remote
        // chromeinspector url
        .engine(engine())
        .build()
    );
  }

  private static String getCWD() {
    // clean up the current working dir
    String cwdOverride = System.getProperty("vertx.cwd");
    String cwd;
    // are the any overrides?
    if (cwdOverride != null) {
      cwd = new File(cwdOverride).getAbsolutePath();
    } else {
      cwd = System.getProperty("user.dir");
    }
    // ensure it's not null
    if (cwd == null) {
      cwd = "";
    }

    // all paths are unix paths
    cwd = cwd.replace('\\', '/');
    // ensure it ends with /
    if (cwd.charAt(cwd.length() - 1) != '/') {
      cwd += '/';
    }

    // append the required prefix
    return "file://" + cwd;
  }

  public GraalRuntime(final Vertx vertx, Context context) {

    // register the codec
    if (INTEROP_TYPE.get() == null) {
      final Consumer callback = value -> INTEROP_TYPE.compareAndSet(null, value.getClass());
      context.eval(
        Source.newBuilder("js", "(function (fn) { fn({}); })", "<class-lookup>").internal(true).buildLiteral()
      ).execute(callback);
    }

    // register a default codec to allow JSON messages directly from GraalVM to the JVM world
    vertx.eventBus()
      .unregisterDefaultCodec(INTEROP_TYPE.get())
      .registerDefaultCodec(INTEROP_TYPE.get(), new JSObjectMessageCodec<>());

    this.context = context;
    this.bindings = this.context.getBindings("js");

    // remove the exit and quit functions
    bindings.removeMember("exit");
    bindings.removeMember("quit");
    // add vertx as a global
    bindings.putMember("vertx", vertx);

    // clean up the current working dir
    final String cwd = getCWD();

    // override the default load function to allow proper mapping of file for debugging
    bindings.putMember("load", new Function<Object, Value>() {
      @Override
      public Value apply(Object value) {

        try {
          final Source source;

          if (value instanceof String) {
            // a path or url in string format
            try {
              // try to parse as URL
              return apply(new URL((String) value));
            } catch (MalformedURLException murle) {
              // on failure fallback to file
              return apply(new File((String) value));
            }
          }
          else if (value instanceof URL) {
            // a url
            source = Source.newBuilder("js", (URL) value).build();
          }
          else if (value instanceof File) {
            // a local file
            source = Source.newBuilder("js", (File) value).build();
          }
          else if (value instanceof Map) {
            // a json document
            final CharSequence script = (CharSequence) ((Map) value).get("script");
            // might be optional
            final CharSequence name = (CharSequence) ((Map) value).get("name");

            if (name != null && name.length() > 0) {
              final URI uri;
              if (name.charAt(0) != '/') {
                // relative uri
                uri = new URI(cwd + name);
              } else {
                // absolute uri
                uri = new URI("file://" + name);
              }

              source = Source.newBuilder("js", script, name.toString()).uri(uri).build();
            } else {
              source = Source.newBuilder("js", script, "<module-wrapper>").build();
            }
          } else {
            throw new RuntimeException("TypeError: cannot load [" + value.getClass() + "]");
          }

          return context.eval(source);
        } catch (IOException | URISyntaxException e) {
          throw new RuntimeException(e);
        }
      }
    });

    // load all the polyfills
    try {
      context.eval(Source.newBuilder("js", Runtime.class.getResource("/io/reactiverse/es4x/polyfill/json.js")).build());
      context.eval(Source.newBuilder("js", Runtime.class.getResource("/io/reactiverse/es4x/polyfill/global.js")).build());
      context.eval(Source.newBuilder("js", Runtime.class.getResource("/io/reactiverse/es4x/polyfill/date.js")).build());
      context.eval(Source.newBuilder("js", Runtime.class.getResource("/io/reactiverse/es4x/polyfill/console.js")).build());
      context.eval(Source.newBuilder("js", Runtime.class.getResource("/io/reactiverse/es4x/polyfill/promise.js")).build());
      context.eval(Source.newBuilder("js", Runtime.class.getResource("/io/reactiverse/es4x/polyfill/worker.js")).build());
      module = context.eval(Source.newBuilder("js", Runtime.class.getResource("/io/reactiverse/es4x/jvm-npm.js")).build());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String name() {
    return "GraalJS";
  }

  @Override
  public void config(final JsonObject config) {
    if (config != null) {
      // add config as a global
      bindings.putMember("config", config.getMap());
    }
  }

  @Override
  public Value require(String main) {
    return bindings.getMember("require").execute(main);
  }

  @Override
  public Value main(String main) {
    // patch the main path to be a relative path
    if (!main.startsWith("./") && !main.startsWith("/")) {
      main = "./" + main;
    }
    // invoke the main script
    return invokeMethod(module, "runMain", main);
  }

  @Override
  public Value worker(String main, String address) {
    // invoke the main script
    return invokeMethod(module, "runWorker", main, address);
  }

  @Override
  public Value eval(String script) {
    return context.eval("js", script);
  }

  @Override
  public Value evalLiteral(CharSequence literal) {
    final Source source = Source
      .newBuilder("js", literal, "<shell>")
      .interactive(true)
      .buildLiteral();

    return context.eval(source);
  }

  @Override
  public boolean hasMember(Value object, String key) {
    if (object != null) {
      return object.hasMember(key);
    }
    return false;
  }

  @Override
  public Value invokeMethod(Value thiz, String method, Object... args) {
    Value fn = thiz.getMember(method);
    if (fn != null && !fn.isNull()) {
      return fn.execute(args);
    }
    return null;
  }

  @Override
  public Value invokeFunction(String function, Object... args) {
    return context.eval("js", function).execute(args);
  }

  @Override
  public void put(String name, Object value) {
    bindings.putMember(name, value);
  }

  @Override
  public void close() {
    context.close();
  }

  @Override
  public void enter() {
    context.enter();
  }

  @Override
  public void leave() {
    context.leave();
  }

  public Engine getEngine() {
    return context.getEngine();
  }

  public Value eval(Source source) {
    return context.eval(source);
  }
}
