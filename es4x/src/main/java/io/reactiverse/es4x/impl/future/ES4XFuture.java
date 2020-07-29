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
import io.vertx.core.impl.ContextInternal;
import io.vertx.core.impl.NoStackTraceThrowable;
import io.vertx.core.impl.PromiseInternal;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import org.graalvm.polyglot.Value;

import java.util.ArrayList;
import java.util.Objects;

class ES4XFuture<T> implements PromiseInternal<T>, Future<T>, Thenable {

  private static final Logger LOG = LoggerFactory.getLogger(ES4XFuture.class.getSimpleName());

  @Override
  public void then(Value onFulfilled, Value onRejected) {
    // Both onFulfilled and onRejected are optional arguments

    onComplete(ar -> {
      if (ar.succeeded()) {
        try {
          if (onFulfilled != null) {
            onFulfilled.executeVoid(ar.result());
          } else {
            LOG.warn("Possible Unhandled Promise: " + this);
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
        } else {
          LOG.warn("Possible Unhandled Promise: " + this);
        }
      }
    });
  }

  private final ContextInternal context;
  private boolean failed;
  private boolean succeeded;
  private Handler<AsyncResult<T>> handler;
  private T result;
  private Throwable throwable;

  /**
   * Create a future that hasn't completed yet
   */
  ES4XFuture() {
    this(null);
  }


  /**
   * Create a future that hasn't completed yet
   */
  ES4XFuture(ContextInternal context) {
    this.context = context;
  }

  public ContextInternal context() {
    return context;
  }

  /**
   * The result of the operation. This will be null if the operation failed.
   */
  public synchronized T result() {
    return result;
  }

  /**
   * An exception describing failure. This will be null if the operation succeeded.
   */
  public synchronized Throwable cause() {
    return throwable;
  }

  /**
   * Did it succeeed?
   */
  public synchronized boolean succeeded() {
    return succeeded;
  }

  /**
   * Did it fail?
   */
  public synchronized boolean failed() {
    return failed;
  }

  /**
   * Has it completed?
   */
  public synchronized boolean isComplete() {
    return failed || succeeded;
  }

  @Override
  public Future<T> onComplete(Handler<AsyncResult<T>> h) {
    Objects.requireNonNull(h, "No null handler accepted");
    synchronized (this) {
      if (!isComplete()) {
        if (handler == null) {
          handler = h;
        } else {
          addHandler(h);
        }
        return this;
      }
    }
    dispatch(h);
    return this;
  }

  private void addHandler(Handler<AsyncResult<T>> h) {
    ES4XFuture.Handlers<T> handlers;
    if (handler instanceof ES4XFuture.Handlers) {
      handlers = (ES4XFuture.Handlers<T>) handler;
    } else {
      handlers = new ES4XFuture.Handlers<>();
      handlers.add(handler);
      handler = handlers;
    }
    handlers.add(h);
  }

  protected void dispatch(Handler<AsyncResult<T>> handler) {
    if (handler instanceof ES4XFuture.Handlers) {
      for (Handler<AsyncResult<T>> h : (ES4XFuture.Handlers<T>)handler) {
        doDispatch(h);
      }
    } else {
      doDispatch(handler);
    }
  }

  private void doDispatch(Handler<AsyncResult<T>> handler) {
    if (context != null) {
      context.dispatch(this, handler);
    } else {
      handler.handle(this);
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
      dispatch(h);
    }
    return true;
  }

  public void handle(Future<T> ar) {
    if (ar.succeeded()) {
      complete(ar.result());
    } else {
      fail(ar.cause());
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
      dispatch(h);
    }
    return true;
  }

  @Override
  public Future<T> future() {
    return this;
  }

  @Override
  public void operationComplete(io.netty.util.concurrent.Future<T> future) {
    if (future.isSuccess()) {
      complete(future.getNow());
    } else {
      fail(future.cause());
    }
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


  private static class Handlers<T> extends ArrayList<Handler<AsyncResult<T>>> implements Handler<AsyncResult<T>> {
    @Override
    public void handle(AsyncResult<T> res) {
      for (Handler<AsyncResult<T>> handler : this) {
        handler.handle(res);
      }
    }
  }
}
