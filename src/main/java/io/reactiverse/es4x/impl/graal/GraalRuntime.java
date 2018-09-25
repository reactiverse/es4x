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

import io.reactiverse.es4x.Loader;
import io.reactiverse.es4x.Runtime;
import io.vertx.core.Vertx;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class GraalRuntime implements Runtime<Value> {

  // Graal AOT config
  private static final String EVENTBUS_JSOBJECT_AOT_CLASS;

  static {
    // if this code is used in a native image avoid the reflection guess game
    // and hard code the expected class to be a PolyglotMap
    if (System.getProperty("org.graalvm.nativeimage.imagecode") != null) {
      EVENTBUS_JSOBJECT_AOT_CLASS = "com.oracle.truffle.polyglot.PolyglotMap";
    } else {
      EVENTBUS_JSOBJECT_AOT_CLASS = null;
    }
  }

  private final AtomicReference<Class<?>> interopType = new AtomicReference<>();

  @Override
  public String name() {
    return "GraalJS";
  }

  @Override
  public Runtime<Value> registerCodec(Vertx vertx) {

    try {
      if (interopType.get() == null) {
        if (EVENTBUS_JSOBJECT_AOT_CLASS != null) {
          interopType.set(Class.forName(EVENTBUS_JSOBJECT_AOT_CLASS));
        } else {
          final Consumer callback = value -> interopType.set(value.getClass());
          Context ctx;
          boolean close;
          try {
            ctx = Context.getCurrent();
            close = false;
          } catch (IllegalStateException e) {
            // will create a basic context to lookup the type
            ctx = Context
              .newBuilder("js")
              .allowHostAccess(true)
              .build();
            close = true;
          }

          ctx.eval(
            Source.newBuilder("js", "(function (fn) { fn({}); })", "<class-lookup>").internal(true).buildLiteral()
          ).execute(callback);

          if (close) {
            // type is acquired, so we can discard this context
            ctx.close(true);
          }
        }
      }

      // register a default codec to allow JSON messages directly from GraalVM to the JVM world
      vertx.eventBus()
        .unregisterDefaultCodec(interopType.get())
        .registerDefaultCodec(interopType.get(), new JSObjectMessageCodec<>());

      return this;
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Returns a module loader for the given runtime.
   *
   * @return loader
   */
  @Override
  public Loader<Value> loader(Vertx vertx) {
    return new GraalLoader(vertx);
  }
}
