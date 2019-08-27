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

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.impl.NoStackTraceThrowable;
import org.graalvm.polyglot.Value;

public class VertxPromise<T> implements Promise<T> {

  private final Value thenable;

  private boolean failed;
  private boolean succeeded;

  VertxPromise(Value thenable) {
    this.thenable = thenable;
  }

  @Override
  public void handle(AsyncResult<T> asyncResult) {

  }

  @Override
  public void complete(T result) {
    if (!tryComplete(result)) {
      throw new IllegalStateException("Result is already complete: " + (succeeded ? "succeeded" : "failed"));
    }
  }

  @Override
  public void complete() {
    if (!tryComplete()) {
      throw new IllegalStateException("Result is already complete: " + (succeeded ? "succeeded" : "failed"));
    }
  }

  @Override
  public void fail(Throwable cause) {
    if (!tryFail(cause)) {
      throw new IllegalStateException("Result is already complete: " + (succeeded ? "succeeded" : "failed"));
    }
  }

  @Override
  public void fail(String failureMessage) {
    if (!tryFail(failureMessage)) {
      throw new IllegalStateException("Result is already complete: " + (succeeded ? "succeeded" : "failed"));
    }
  }

  @Override
  public boolean tryComplete(T t) {
    synchronized (this) {
      if (succeeded || failed) {
        return false;
      }
      succeeded = true;
    }
    thenable.invokeMember("then", t, null);
    return true;
  }

  @Override
  public boolean tryComplete() {
    return tryComplete(null);
  }

  @Override
  public boolean tryFail(Throwable cause) {
    synchronized (this) {
      if (succeeded || failed) {
        return false;
      }
      failed = true;
    }
    thenable.invokeMember("then", null, cause != null ? cause : new NoStackTraceThrowable(null));
    return true;
  }

  @Override
  public boolean tryFail(String failureMessage) {
    return tryFail(new NoStackTraceThrowable(failureMessage));
  }

  @Override
  public Future<T> future() {
    throw new RuntimeException("JS Promise has no Vert.x Future");
  }
}
