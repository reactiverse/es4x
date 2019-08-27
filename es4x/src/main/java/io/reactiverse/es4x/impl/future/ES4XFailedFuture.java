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
package io.reactiverse.es4x.impl.future;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.impl.NoStackTraceThrowable;
import org.graalvm.polyglot.Value;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class ES4XFailedFuture<T> implements Future<T>, Promise<T>, Thenable {

  private final Throwable cause;

  /**
   * Create a future that has already failed
   * @param t the throwable
   */
  ES4XFailedFuture(Throwable t) {
    cause = t != null ? t : new NoStackTraceThrowable(null);
  }

  /**
   * Create a future that has already failed
   * @param failureMessage the failure message
   */
  ES4XFailedFuture(String failureMessage) {
    this(new NoStackTraceThrowable(failureMessage));
  }

  @Override
  public boolean isComplete() {
    return true;
  }

  @Override
  public Future<T> setHandler(Handler<AsyncResult<T>> handler) {
    handler.handle(this);
    return this;
  }

  @Override
  public void then(Value onFulfilled, Value onRejected) {
    if (onRejected != null) {
      onRejected.execute(cause());
    }
  }

  @Override
  public Handler<AsyncResult<T>> getHandler() {
    return null;
  }

  @Override
  public void complete(T result) {
    throw new IllegalStateException("Result is already complete: failed");
  }

  @Override
  public void complete() {
    throw new IllegalStateException("Result is already complete: failed");
  }

  @Override
  public void fail(Throwable cause) {
    throw new IllegalStateException("Result is already complete: failed");
  }

  @Override
  public void fail(String failureMessage) {
    throw new IllegalStateException("Result is already complete: failed");
  }

  @Override
  public boolean tryComplete(T result) {
    return false;
  }

  @Override
  public boolean tryComplete() {
    return false;
  }

  @Override
  public boolean tryFail(Throwable cause) {
    return false;
  }

  @Override
  public boolean tryFail(String failureMessage) {
    return false;
  }

  @Override
  public T result() {
    return null;
  }

  @Override
  public Throwable cause() {
    return cause;
  }

  @Override
  public boolean succeeded() {
    return false;
  }

  @Override
  public boolean failed() {
    return true;
  }

  @Override
  public void handle(AsyncResult<T> asyncResult) {
    throw new IllegalStateException("Result is already complete: failed");
  }

  @Override
  public Future<T> future() {
    return this;
  }

  @Override
  public String toString() {
    return "Future{cause=" + cause.getMessage() + "}";
  }
}
