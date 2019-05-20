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
import io.reactiverse.es4x.impl.EventEmitterImpl;
import io.reactiverse.es4x.impl.ScriptException;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.graalvm.polyglot.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public class GraalRuntime extends EventEmitterImpl implements Runtime<Value> {

  private final Context context;
  private final Value bindings;
  private final Value module;

  private static final Source[] POLYFILLS = new Source[] {
    Source.newBuilder("js", GraalRuntime.class.getResource("/io/reactiverse/es4x/polyfill/json.js")).name("es4x_internal/json.js").buildLiteral(),
    Source.newBuilder("js", GraalRuntime.class.getResource("/io/reactiverse/es4x/polyfill/global.js")).name("es4x_internal/global.js").buildLiteral(),
    Source.newBuilder("js", GraalRuntime.class.getResource("/io/reactiverse/es4x/polyfill/date.js")).name("es4x_internal/date.js").buildLiteral(),
    Source.newBuilder("js", GraalRuntime.class.getResource("/io/reactiverse/es4x/polyfill/console.js")).name("es4x_internal/console.js").buildLiteral(),
    Source.newBuilder("js", GraalRuntime.class.getResource("/io/reactiverse/es4x/polyfill/promise.js")).name("es4x_internal/promise.js").buildLiteral(),
    Source.newBuilder("js", GraalRuntime.class.getResource("/io/reactiverse/es4x/polyfill/worker.js")).name("es4x_internal/worker.js").buildLiteral(),
    Source.newBuilder("js", GraalRuntime.class.getResource("/io/reactiverse/es4x/jvm-npm.js")).name("es4x_internal/cjs-loader.js").buildLiteral()
  };

  public GraalRuntime(final Vertx vertx, Context context) {

    this.context = context;
    this.bindings = this.context.getBindings("js");

    // remove specific features that we don't want to expose
    for (String identifier : Arrays.asList("exit", "quit", "Packages", "java", "javafx", "javax", "com", "org", "edu")) {
      bindings.removeMember(identifier);
    }
    // add vertx as a global
    bindings.putMember("vertx", vertx);

    // clean up the current working dir
    final String cwd = "file://" + VertxFileSystem.getCWD();

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
              source = Source.newBuilder("js", script, "<module-wrapper>").cached(false).build();
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
    bindings.putMember("verticle", this);
    for (int i = 0; i < POLYFILLS.length - 1; i++) {
      context.eval(POLYFILLS[i]);
    }
    bindings.removeMember("verticle");

    // keep a reference to module
    module = context.eval(POLYFILLS[POLYFILLS.length -1]);
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
    if (main.equals(".") || main.equals("..")) {
      // invoke the main script
      return invokeMethod(module, "runMain", main + "/");
    }
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
  public Value eval(String script, String name, String contentType, boolean interactive) {
    final Source source = Source
      .newBuilder("js", script, name)
      .interactive(interactive)
      .mimeType(contentType)
      .buildLiteral();

    if (interactive) {
      try {
        return context.eval(source);
      } catch (PolyglotException e) {
        // in this special case we wrap and hide the polyglot type
        // so we can keep a contract outside graal
        throw new ScriptException(e, e.isIncompleteSource(), e.isExit());
      }
    } else {
      return context.eval(source);
    }
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
}
