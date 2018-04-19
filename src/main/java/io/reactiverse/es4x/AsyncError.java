package io.reactiverse.es4x;

import io.vertx.core.AsyncResult;

public final class AsyncError {

  private static Throwable patchStackTrace(Throwable throwable) {
    // there is a error
    if (throwable != null) {
      // locate the caller
      final StackTraceElement stackTraceElement = new Throwable("<AsyncError>").getStackTrace()[2];

      // if there is already a stacktrace available
      StackTraceElement[] stack = throwable.getStackTrace();
      if (stack != null) {
        // start stitching the 2 traces
        StackTraceElement[] nstack = new StackTraceElement[stack.length + 1];
        nstack[0] = stackTraceElement;
        System.arraycopy(stack, 0, nstack, 1, stack.length);
        // replace the stack on the original throwable
        throwable.setStackTrace(nstack);
      }
      // no stacktrace, so lets create an error out of this object
      else {
        // start stitching the 2 traces
        StackTraceElement[] nstack = new StackTraceElement[1];
        nstack[0] = stackTraceElement;
        // replace the stack on the original throwable
        throwable.setStackTrace(nstack);
      }
    }

    return throwable;
  }

  public static Throwable asyncError(Throwable throwable) {
    return patchStackTrace(throwable);
  }

  public static <T> Throwable asyncError(AsyncResult<T> asyncResult) {
    return patchStackTrace(asyncResult.cause());
  }

  private AsyncError() {
    throw new RuntimeException("Not Instantiable");
  }
}
