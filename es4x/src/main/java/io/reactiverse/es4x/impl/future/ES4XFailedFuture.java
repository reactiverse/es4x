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
import org.graalvm.polyglot.Value;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 * @author <a href="mailto:plopes@redhat.com">Paulo Lopes</a>
 */
public class ES4XFailedFuture<T> implements Future<T>, Thenable {

  /**
   * Create a future that has already failed
   * @param t the throwable
   */
  ES4XFailedFuture(ContextInternal context, Throwable t) {
    this.context = context;
    this.cause = t != null ? t : new NoStackTraceThrowable(null);
  }

  /**
   * Create a future that has already failed
   * @param failureMessage the failure message
   */
  ES4XFailedFuture(ContextInternal context, String failureMessage) {
    this(context, new NoStackTraceThrowable(failureMessage));
  }

  @Override
  public void then(Value onFulfilled, Value onRejected) {
    if (onRejected != null) {
      onRejected.execute(cause());
    }
  }

  /**
   * From this point forward the code is exactly as {@link io.vertx.core.impl.FailedFuture}
   */

  private final ContextInternal context;
  private final Throwable cause;

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
  public String toString() {
    return "Future{cause=" + cause.getMessage() + "}";
  }
}
