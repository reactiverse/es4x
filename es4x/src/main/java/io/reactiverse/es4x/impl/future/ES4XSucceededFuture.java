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
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import org.graalvm.polyglot.Value;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
class ES4XSucceededFuture<T> implements Future<T>, Promise<T>, Thenable {

  private static final Logger LOG = LoggerFactory.getLogger(ES4XSucceededFuture.class);

  private final T result;

  /**
   * Create a future that has already succeeded
   * @param result the result
   */
  ES4XSucceededFuture(T result) {
    this.result = result;
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

    try {
      if (onFulfilled != null) {
        onFulfilled.executeVoid(result());
      }
    } catch (RuntimeException e) {
      // resolve failed, attempt to reject
      if (onRejected != null) {
        onRejected.execute(e);
      } else {
        LOG.warn("Possible Unhandled Promise Rejection: " + e.getMessage());
      }
    }
  }

  @Override
  public Handler<AsyncResult<T>> getHandler() {
    return null;
  }

  @Override
  public void complete(T result) {
    throw new IllegalStateException("Result is already complete: succeeded");
  }

  @Override
  public void complete() {
    throw new IllegalStateException("Result is already complete: succeeded");
  }

  @Override
  public void fail(Throwable cause) {
    throw new IllegalStateException("Result is already complete: succeeded");
  }

  @Override
  public void fail(String failureMessage) {
    throw new IllegalStateException("Result is already complete: succeeded");
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
    return result;
  }

  @Override
  public Throwable cause() {
    return null;
  }

  @Override
  public boolean succeeded() {
    return true;
  }

  @Override
  public boolean failed() {
    return false;
  }

  @Override
  public void handle(AsyncResult<T> asyncResult) {
    throw new IllegalStateException("Result is already complete: succeeded");
  }

  @Override
  public Future<T> future() {
    return this;
  }

  @Override
  public String toString() {
    return "Future{result=" + result + "}";
  }
}
