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
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.util.Arrays;

public final class Runtime extends EventEmitterImpl {

  private final Context context;
  private final Value bindings;

  Runtime(final Vertx vertx, final Context context, Source... scripts) {

    this.context = context;
    this.bindings = this.context.getBindings("js");

    // remove specific features that we don't want to expose
    for (String identifier : Arrays.asList("load", "exit", "quit", "Packages", "java", "javafx", "javax", "com", "org", "edu")) {
      bindings.removeMember(identifier);
    }
    // add vertx as a global
    bindings.putMember("vertx", vertx);

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
    if (name.endsWith(".mjs")) {
      return eval(script, name, "application/javascript+module", literal);
    } else {
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
