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
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import org.graalvm.polyglot.Value;

class ES4XFuture<T> implements Promise<T>, Future<T>, Thenable {

  private static final Logger LOG = LoggerFactory.getLogger(ES4XFuture.class);

  private boolean failed;
  private boolean succeeded;
  private Handler<AsyncResult<T>> handler;
  private T result;
  private Throwable throwable;

  /**
   * Create a future that hasn't completed yet
   */
  ES4XFuture() {
  }

  /**
   * The result of the operation. This will be null if the operation failed.
   */
  @Override
  public synchronized T result() {
    return result;
  }

  /**
   * An exception describing failure. This will be null if the operation succeeded.
   */
  @Override
  public synchronized Throwable cause() {
    return throwable;
  }

  /**
   * Did it succeeed?
   */
  @Override
  public synchronized boolean succeeded() {
    return succeeded;
  }

  /**
   * Did it fail?
   */
  @Override
  public synchronized boolean failed() {
    return failed;
  }

  /**
   * Has it completed?
   */
  @Override
  public synchronized boolean isComplete() {
    return failed || succeeded;
  }

  /**
   * Set a handler for the result. It will get called when it's complete
   */
  @Override
  public Future<T> setHandler(Handler<AsyncResult<T>> handler) {
    boolean callHandler;
    synchronized (this) {
      callHandler = isComplete();
      if (!callHandler) {
        this.handler = handler;
      }
    }
    if (callHandler) {
      handler.handle(this);
    }
    return this;
  }

  @Override
  public void then(Value onFulfilled, Value onRejected) {
    // Both onFulfilled and onRejected are optional arguments

    setHandler(ar -> {
      if (ar.succeeded()) {
        try {
          if (onFulfilled != null) {
            onFulfilled.executeVoid(ar.result());
          }
        } catch (RuntimeException e) {
          // resolve failed, attempt to reject
          if (onRejected != null) {
            onRejected.execute(e);
          } else {
            LOG.warn("Possible Unhandled Promise Rejection: " + e.getMessage());
          }
        }
      } else {
        if (onRejected != null) {
          onRejected.execute(ar.cause());
        }
      }
    });
  }

  @Override
  public synchronized Handler<AsyncResult<T>> getHandler() {
    return handler;
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
  public boolean tryComplete(T result) {
    Handler<AsyncResult<T>> h;
    synchronized (this) {
      if (succeeded || failed) {
        return false;
      }
      this.result = result;
      succeeded = true;
      h = handler;
      handler = null;
    }
    if (h != null) {
      h.handle(this);
    }
    return true;
  }

  @Override
  public boolean tryComplete() {
    return tryComplete(null);
  }

  @Override
  public void handle(AsyncResult<T> asyncResult) {
    if (asyncResult.succeeded()) {
      complete(asyncResult.result());
    } else {
      fail(asyncResult.cause());
    }
  }

  @Override
  public boolean tryFail(Throwable cause) {
    Handler<AsyncResult<T>> h;
    synchronized (this) {
      if (succeeded || failed) {
        return false;
      }
      this.throwable = cause != null ? cause : new NoStackTraceThrowable(null);
      failed = true;
      h = handler;
      handler = null;
    }
    if (h != null) {
      h.handle(this);
    }
    return true;
  }

  @Override
  public boolean tryFail(String failureMessage) {
    return tryFail(new NoStackTraceThrowable(failureMessage));
  }

  @Override
  public Future<T> future() {
    return this;
  }

  @Override
  public String toString() {
    synchronized (this) {
      if (succeeded) {
        return "Future{result=" + result + "}";
      }
      if (failed) {
        return "Future{cause=" + throwable.getMessage() + "}";
      }
      return "Future{unresolved}";
    }
  }
}
