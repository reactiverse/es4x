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
package io.reactiverse.es4x.impl;

import io.vertx.core.AsyncResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility functions to stitch exception from 2 different sources, moments together in order
 * to preserve some connection between 2 handlers.
 *
 * @author Paulo Lopes
 */
public final class AsyncError {

  private static Throwable patchStackTrace(Throwable throwable, String jsAsyncStackLine) {
    // there is a error
    if (throwable != null && jsAsyncStackLine != null) {
      // if there is already a stacktrace available
      StackTraceElement[] stack = throwable.getStackTrace();
      if (stack != null) {
        // start stitching the 2 traces
        StackTraceElement[] nstack = new StackTraceElement[stack.length + 1];
        nstack[0] = parseStrackTraceElement(jsAsyncStackLine);
        System.arraycopy(stack, 0, nstack, 1, stack.length);
        // replace the stack on the original throwable
        throwable.setStackTrace(nstack);
      }
      // no stacktrace, so lets create an error out of this object
      else {
        // start stitching the 2 traces
        StackTraceElement[] nstack = new StackTraceElement[1];
        nstack[0] = parseStrackTraceElement(jsAsyncStackLine);
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
  public static Throwable asyncError(Throwable throwable, String jsAsyncStackLine) {
    return patchStackTrace(throwable, jsAsyncStackLine);
  }

  /**
   * Stitches the given cause of the asyncResult with the current
   * execution stack trace at the moment this function is invoked.
   *
   * @param asyncResult a asyncResult object
   * @return the enhanced throwable
   */
  public static <T> Throwable asyncError(AsyncResult<T> asyncResult, String jsAsyncStackLine) {
    return patchStackTrace(asyncResult.cause(), jsAsyncStackLine);
  }

  private AsyncError() throws IllegalAccessException {
    throw new IllegalAccessException("Not Instantiable");
  }

  private static final Pattern STACKTRACE = Pattern.compile("at (<.+?> )?\\(?(.+?):(\\d+)(:\\d+)?\\)?");

  private static StackTraceElement parseStrackTraceElement(String element) {
    String methodName = null;
    String filename = "";
    int lineNumber = 0;

    if (element != null) {
      final Matcher matcher = STACKTRACE.matcher(element);
      if (matcher.find()) {
        methodName = matcher.group(1);
        filename = matcher.group(2);
        try {
          lineNumber = Integer.parseInt(matcher.group(3));
        } catch (NumberFormatException e) {
          // we couldn't parse it...
          lineNumber = -1;
        }
      }
    }
    return new StackTraceElement("<async>", methodName == null ? "<error>" : methodName, filename, lineNumber);
  }
}
