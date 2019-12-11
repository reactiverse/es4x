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
import io.vertx.core.impl.ContextInternal;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import org.graalvm.polyglot.Value;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 * @author <a href="mailto:plopes@redhat.com">Paulo Lopes</a>
 */
class ES4XSucceededFuture<T> implements Future<T>, Thenable {

  private static final Logger LOG = LoggerFactory.getLogger(ES4XSucceededFuture.class);

  /**
   * Create a future that has already succeeded
   * @param context the context
   * @param result the result
   */
  ES4XSucceededFuture(ContextInternal context, T result) {
    this.context = context;
    this.result = result;
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

  /**
   * From this point forward the code is exactly as {@link io.vertx.core.impl.SucceededFuture}
   */

  private final ContextInternal context;
  private final T result;

  @Override
  public ContextInternal context() {
    return context;
  }

  @Override
  public boolean isComplete() {
    return true;
  }

  @Override
  public Future<T> onComplete(Handler<AsyncResult<T>> handler) {
    handler.handle(this);
    return this;
  }

  @Override
  public Handler<AsyncResult<T>> getHandler() {
    return null;
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
  public String toString() {
    return "Future{result=" + result + "}";
  }
}
