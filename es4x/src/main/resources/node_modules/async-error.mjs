const asyncError = Java.type('io.reactiverse.es4x.impl.AsyncError').asyncError;
const AsyncResult = Java.type('io.vertx.core.AsyncResult');
const Throwable = Java.type('java.lang.Throwable');

export default function asyncError(err) {

  let currentStack = new Error().stack;

  if (currentStack && currentStack.length > 2) {
    let asyncStackLine = currentStack.split("\n")[2];

    if (err instanceof AsyncResult || err instanceof Throwable) {
      return asyncError(err, asyncStackLine);
    }

    // if the err is not a Error object make it one
    if (!(err instanceof Error)) {
      err = new Error(err);
    }

    //split stack by line
    let stackParts = err.stack.split("\n");
    stackParts.splice(1,0,asyncStackLine);
    //join the stacktrace
    err.stack = stackParts.join("\n");

    return err;
  }

  // nothing could be inferred (return as is)
  return err;
};
