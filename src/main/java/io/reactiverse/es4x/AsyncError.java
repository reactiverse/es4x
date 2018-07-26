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

import io.vertx.core.AsyncResult;

/**
 * Utility functions to stitch exception from 2 different sources, moments together in order
 * to preserve some connection between 2 handlers.
 *
 * @author Paulo Lopes
 */
public final class AsyncError {

  private static final int STACK_ELEMENT = 3;


  private static Throwable patchStackTrace(Throwable throwable) {
    // there is a error
    if (throwable != null) {
      // locate the caller
      final StackTraceElement[] currentStack =  Thread.currentThread().getStackTrace();
      if (currentStack.length <= STACK_ELEMENT) {
        // strange enough i can't locate the source...
        return throwable;
      }

      // if there is already a stacktrace available
      StackTraceElement[] stack = throwable.getStackTrace();
      if (stack != null) {
        // start stitching the 2 traces
        StackTraceElement[] nstack = new StackTraceElement[stack.length + 1];
        nstack[0] = currentStack[STACK_ELEMENT];
        System.arraycopy(stack, 0, nstack, 1, stack.length);
        // replace the stack on the original throwable
        throwable.setStackTrace(nstack);
      }
      // no stacktrace, so lets create an error out of this object
      else {
        // start stitching the 2 traces
        StackTraceElement[] nstack = new StackTraceElement[1];
        nstack[0] = currentStack[STACK_ELEMENT];
        // replace the stack on the original throwable
        throwable.setStackTrace(nstack);
      }
    }

    return throwable;
  }

  /**
   * Stitches the given throwable with the current execution stack trace
   * at the moment this function is invoked.
   *
   * @param throwable a throwable object
   * @return the enhanced throwable
   */
  public static Throwable asyncError(Throwable throwable) {
    return patchStackTrace(throwable);
  }

  /**
   * Stitches the given cause of the asyncResult with the current
   * execution stack trace at the moment this function is invoked.
   *
   * @param asyncResult a asyncResult object
   * @return the enhanced throwable
   */
  public static <T> Throwable asyncError(AsyncResult<T> asyncResult) {
    return patchStackTrace(asyncResult.cause());
  }

  private AsyncError() {
    throw new RuntimeException("Not Instantiable");
  }
}
