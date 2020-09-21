# Globals

GraalJS is a pure JavaScript engine. This means that a few Global Objects (which are not standard but common) are
missing. ES4X tried to add the missing features or Enhanced the defaults as follows:

## require()

The official spec does not define the function `require()`, ES4X has it's own implementation as described in
[commonjs](./commonjs.md).

## setTimeout()

The `setTimeout()` method sets a timer which executes a function or specified piece of code once the timer expires.
This method is added to the global scope and uses `Vert.x Timers`:

```js
setTimeout(handler => {
  console.log('Hello from the future!')
}, 2000);
```

## setInterval()

The `setInterval()` method sets a timer which executes a function or specified piece of code repeatably the timer.
This method is added to the global scope and uses `Vert.x Timers`:

```js
setInterval(handler => {
  console.log('Hello again from the future!')
}, 2000);
```

## setImmediate()

The `setImmediate()` method executes a function or specified piece of code on the next event loop slot.
This method is added to the global scope and uses `Vert.x executeOnContext()`:

```js
setImmediate(handler => {
  console.log('Hello again from the future!')
});
```

## clearTimeout()

Clears a timeout.


## clearInterval()

Clears a timeout.

## clearImmediate()

::: warning
This function is present to ensure many libraries don't break, **BUT** it has no effect due to the way callbacks are
scheduled on the Vert.x Event Loop.
:::

## process Object

The process object (popular by `nodejs`) is also available on ES4X, however it has less properties:

```js
{
  env,          // process environment variables (read only)
  pid,          // current process id
  engine,       // constant 'graaljs'
  exit,         // function that terminates the process with optional error code
  nextTick,     // enqueue a callback to be executed on the next event loop slot
                // NOTE: this behavior is different from nodejs
  on,           // event emmiter function binding
  stdout,       // JVM System.out
  stderr,       // JVM System.err
  stdin,        // JVM System.in (WARNING this will block the event loop)
  properties,   // JVM System properties (read, write)
  cwd           // Function that returns the CWD
}
```

## console Object

Console is added by ES4X. This object has the typical API:

```js
console.debug('Hello', 'World', '!')
console.info('Hello', 'World', '!')
console.log('Hello', 'World', '!')
console.warn('Hello', 'World', '!')
console.error('Hello', 'World', '!')
```

Stack traces (both JS and JVM) can be printed with:

```js
try {
  throw new Error('durp!')
} catch (e) {
  console.trace(e);
}
```

## Async Error Tracing

Imagine the following piece of code:

```js
function one() {
   two(function(err) {
     if(err) throw err;
     console.log("two finished");
   });
}

function two(callback) {
  setTimeout(function () {
    three(function(err) {
      if(err) return callback(err);
      console.log("three finished");
      callback();
    });
  }, 0);
}

function three(callback)
{
  setTimeout(function () {
    four(function(err) {
      if(err) return callback(err);
      console.log("four finished");
      callback();
    });
  }, 0);
}

function four(callback) {
  setTimeout(function(){
    callback(new Error());
  }, 0);
}

one();
```

If you run this code you will see that your error has the following trace:

```
Error
    at Timer.callback (example.js:34)
```

Which is not very useful if you need to debug.

In order to ease this there is a bundled module with ES4X that will stitch your
exceptions together, every time you would handle a callback instead of passing the
error directly you wrap it with a helper function.

```js
var asyncError = require('async-error');
var fs = vertx.fileSystem();

function one() {
  two(function (err) {
    if (err) {
      console.trace(err);
      test.complete();
      return;
    }

    console.log("two finished");
    should.fail("Should not reach here");
  });
}

function two(callback) {
  setTimeout(function () {
    three(function (err) {
      if (err) {
        setTimeout(function () {
          callback(asyncError(err));
        }, 0);
        return;
      }

      console.log("three finished");
      callback();
    });
  }, 0);
}

function three(callback) {
  setTimeout(function () {
    four(function (err) {
      if (err) {
        setTimeout(function () {
          callback(asyncError(err));
        }, 0);
        return;
      }

      console.log("four finished");
      callback();
    });
  }, 0);
}

function four(callback) {
  setTimeout(function () {
    fs.readFile("durpa/durp.txt", function (ar) {
      if (ar.failed()) {
        callback(asyncError(ar));
      }
    });
  }, 0);
}

one();
```

Knowing that the file `durpa/durp.txt` doesn't exist, now you would see:

```
Error: File not found!
    at stacktraces/jserror.js:24:20
    at stacktraces/jserror.js:40:20
    at stacktraces/jserror.js:53:14
    at stacktraces/jserror.js:53:25
    at classpath:io/reactiverse/es4x/polyfill/global.js:25:18
```

If the error that is being propagated in a JS `Error` object or:

```
io.vertx.core.file.FileSystemException: java.nio.file.NoSuchFileException: durpa/durp.txt
	at <async>.<anonymous> (stacktraces/index.js:30)
	at <async>.<anonymous> (stacktraces/index.js:46)
	at <async>.<anonymous> (stacktraces/index.js:61)
	at io.vertx.core.file.impl.FileSystemImpl$13.perform(FileSystemImpl.java:740)
	at io.vertx.core.file.impl.FileSystemImpl$13.perform(FileSystemImpl.java:732)
	at io.vertx.core.impl.ContextImpl.lambda$executeBlocking$1(ContextImpl.java:275)
	at io.vertx.core.impl.TaskQueue.run(TaskQueue.java:76)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
	at java.lang.Thread.run(Thread.java:748)
Caused by: java.nio.file.NoSuchFileException: durpa/durp.txt
	at sun.nio.fs.UnixException.translateToIOException(UnixException.java:86)
	at sun.nio.fs.UnixException.rethrowAsIOException(UnixException.java:102)
	at sun.nio.fs.UnixException.rethrowAsIOException(UnixException.java:107)
	at sun.nio.fs.UnixFileSystemProvider.newByteChannel(UnixFileSystemProvider.java:214)
	at java.nio.file.Files.newByteChannel(Files.java:361)
	at java.nio.file.Files.newByteChannel(Files.java:407)
	at java.nio.file.Files.readAllBytes(Files.java:3152)
	at io.vertx.core.file.impl.FileSystemImpl$13.perform(FileSystemImpl.java:736)
	... 7 more
```

If the error is a Java Throwable.

## Date

Many APIs from Vert.x return an `Instant` as temporal type, in order to use it from JS an helper static function is
added to the `Date` object:

```js
let instant = someJVMInstant
let d = Date.fromInstant(instant)
```

## ArrayBuffer

Array buffers are a builtin type, however if interop is required, then a JVM `ByteArray` should be passed to the
constructor and this allows accessing the underlying buffer without copies involved:

```js
let javaBuffer = someJavaBuffer
let b = new ArrayBuffer(javaBuffer)
// the underlying buffer can be read using
b.nioByteBuffer
```
