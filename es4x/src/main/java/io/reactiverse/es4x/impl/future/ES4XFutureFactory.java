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

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.spi.FutureFactory;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 * @author <a href="mailto:plopes@redhat.com">Paulo Lopes</a>
 */
public class ES4XFutureFactory implements FutureFactory {

  private static final ES4XSucceededFuture EMPTY = new ES4XSucceededFuture<>(null);

  @Override
  public <T> Promise<T> promise() {
    return new ES4XFuture<>();
  }

  @Override
  public <T> Future<T> future() {
    return new ES4XFuture<>();
  }

  @Override
  public <T> Future<T> succeededFuture() {
    @SuppressWarnings("unchecked")
    Future<T> fut = EMPTY;
    return fut;
  }

  @Override
  public <T> Future<T> succeededFuture(T result) {
    return new ES4XSucceededFuture<>(result);
  }

  @Override
  public <T> Future<T> failedFuture(Throwable t) {
    return new ES4XFailedFuture<>(t);
  }

  @Override
  public <T> Future<T> failureFuture(String failureMessage) {
    return new ES4XFailedFuture<>(failureMessage);
  }
}
