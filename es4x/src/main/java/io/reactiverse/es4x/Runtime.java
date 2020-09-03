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

import io.reactiverse.es4x.impl.EventEmitterImpl;
import io.reactiverse.es4x.impl.VertxFileSystem;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public final class Runtime extends EventEmitterImpl {

  private final Context context;
  private final Value bindings;

  Runtime(final Vertx vertx, final Context context, Source... scripts) {

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
              source = Source.newBuilder("js", script, uri.getPath()).uri(uri).build();
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
    if(scripts != null) {
      for (Source script : scripts) {
        context.eval(script);
      }
    }
    bindings.removeMember("verticle");
  }

  /**
   * passes the given configuration to the runtime.
   *
   * @param config given configuration.
   */
  public void config(final JsonObject config) {
    if (config != null) {
      // add config as a global
      bindings.putMember("config", config);
    }
  }

  /**
   * Evals a given script string.
   *
   * @param script string containing code.
   * @param name string containing name of the script (e.g.: the filename).
   * @param contentType the script content type
   * @param interactive literals are non listed on debug sessions
   * @return returns the evaluation result.
   */
  public Value eval(String script, String name, String contentType, boolean interactive) {
    final Source source = Source
      .newBuilder("js", script, name)
      .interactive(interactive)
      //TODO: if contentType === "application" should look if code contains "import " with space if yes it is application/javascript+module
      .mimeType(contentType)
      .buildLiteral();

    return context.eval(source);
  }

  /**
   * Evals a given script string.
   *
   * @param source source containing code.
   * @return returns the evaluation result.
   */
  public Value eval(Source source) {
    return context.eval(source);
  }

  /**
   * Puts a value to the global scope.
   *
   * @param name the key to identify the value in the global scope
   * @param value the value to store.
   */
  public void put(String name, Object value) {
    bindings.putMember(name, value);
  }

  /**
   * Gets a value from the global scope.
   *
   * @param name the key to identify the value in the global scope
   * @return the value
   */
  public Value get(String name) {
    return bindings.getMember(name);
  }

  /**
   * close the current runtime and shutdown all the engine related resources.
   */
  public void close() {
    context.close();
  }

  /**
   * explicitly enter the script engine scope.
   */
  public void enter() {
    context.enter();
  }

  /**
   * explicitly leave the script engine scope.
   */
  public void leave() {
    context.leave();
  }

  //TODO: needs switch to choose if string is esm or cjs
  /**
   * Evals a given sript string.
   *
   * @param script string containing code.
   * @param interactive literals are non listed on debug sessions
   * @return returns the evaluation result.
   */
  public Value eval(String script, boolean interactive) {
    return eval(script, "<eval>", interactive);
  }

  /**
   * Evals a given sript string.
   *
   * @param script string containing code.
   * @param name string containing name of the script (e.g.: the filename).
   * @param literal literals are non listed on debug sessions
   * @return returns the evaluation result.
   */
  public Value eval(String script, String name, boolean literal) {
    if (name.endsWith(".cjs")) {
      return eval(script, name, "application/javascript", literal);
    }
    if (name.endsWith(".mjs")) {
      return eval(script, name, "application/javascript+module", literal);
    } else {
      //TODO: This should call with "application" and eval should look if code contains "import " with space if yes it is .mjs
      return eval(script, name, "application/javascript", literal);
    }
  }

  /**
   * Evals a given sript string.
   *
   * @param script string containing code.
   * @return returns the evaluation result.
   */
  public Value eval(String script) {
    return eval(script, false);
  }
}
