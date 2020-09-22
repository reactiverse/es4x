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

  private static final Pattern CRLF = Pattern.compile("\\r?\\n");
  private static final Pattern STACKTRACE = Pattern.compile("at (<?.+?>? )?\\(?(.+?):(\\d+)(:\\d+)?\\)?");

  private AsyncError() throws IllegalAccessException {
    throw new IllegalAccessException("Not Instantiable");
  }

  private static Throwable combine(Throwable throwable, String[] jsAsyncStackLine) {
    int len = jsAsyncStackLine.length - 2;
    // there is a error
    if (len >= 0) {
      // if there is already a stacktrace available
      StackTraceElement[] stack = throwable.getStackTrace();
      if (stack != null) {
        // start stitching the 2 traces
        StackTraceElement[] nstack = new StackTraceElement[stack.length + len];
        for (int i = 0; i < len; i++) {
          nstack[i] = parseStrackTraceElement(jsAsyncStackLine[2 + i]);
        }
        System.arraycopy(stack, 0, nstack, len, stack.length);
        // replace the stack on the original throwable
        throwable.setStackTrace(nstack);
      }
      // no stacktrace, so lets create an error out of this object
      else {
        // start stitching the 2 traces
        StackTraceElement[] nstack = new StackTraceElement[1];
        for (int i = 0; i < len; i++) {
          nstack[i] = parseStrackTraceElement(jsAsyncStackLine[2 + i]);
        }
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
   * @param jsAsyncStackLines the js async stack lines to be inserted
   * @return the enhanced throwable
   */
  public static Throwable combine(Throwable throwable, String jsAsyncStackLines) {
    if (jsAsyncStackLines == null) {
      return throwable;
    }
    return combine(throwable, CRLF.split(jsAsyncStackLines));
  }

  /**
   * Stitches the given cause of the asyncResult with the current
   * execution stack trace at the moment this function is invoked.
   *
   * @param asyncResult a asyncResult object
   * @param jsAsyncStackLines the js async stack lines to be inserted
   * @param <T> the kind of async result
   * @return the enhanced throwable
   */
  public static <T> Throwable combine(AsyncResult<T> asyncResult, String jsAsyncStackLines) {
    if (jsAsyncStackLines == null) {
      return asyncResult.cause();
    }
    return combine(asyncResult.cause(), CRLF.split(jsAsyncStackLines));
  }

  /**
   * Stiches the given 2 strings in js stack format.
   *
   * @param currentStackLines the js current stack lines
   * @param asyncStackLines the js async stack lines to be inserted
   * @return the enhanced stack
   */
  public static String combine(String currentStackLines, String asyncStackLines) {
    String[] current = CRLF.split(currentStackLines);
    String[] async = CRLF.split(asyncStackLines);
    final int len = async.length - 2;

    if (len >= 0) {
      String[] full = new String[current.length + len + 1];
      // start stitching
      full[0] = current[0];
      System.arraycopy(async, 2, full, 1, len);
      full[len + 1] = "Async Stacktrace:";
      System.arraycopy(current, 1, full, len + 2, current.length - 1);

      return String.join(System.lineSeparator(), full);
    }

    return currentStackLines;
  }

  public static StackTraceElement parseStrackTraceElement(String element) {
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
