# Obiekty globalne

GraalJS jest silnikiem JavaScript. To znaczy, że kilku obiektów globalnych (które nie są standardem, ale są popularne)
brakuje. ES4X próbuje dodać brakujące funkcje lub rozbudowuje domyślne w przy użyciu poniższych metod:

## require()

Oficjalna specyfikacja nie definiuje funkcji `require()`, ES4X ma swoją własną implementację opisaną w [commonjs](./commonjs.md).

## setTimeout()

Metoda `setTimeout()` ustawia timer, który wykonuje funkcję lub określony kawałek kodu, kiedy czas upłynie. Ta metoda
dodaje do global scope i używa `Vert.x Timers`:

```js
setTimeout(handler => {
  console.log('Hello from the future!')
}, 2000);
```

## setInterval()

Metoda `setInterval()` ustawia timer, który wykonuje funkcję lub określony kawałek kodu wielokrotnie, co ustalony czas.
Ta metoda jest dodawana do global scope i używa `Vert.x Timers`:

```js
setInterval(handler => {
  console.log('Hello again from the future!')
}, 2000);
```

## setImmediate()

Metoda `setImmediate()` ustawia timer, który wykonuje funkcję lub określony kawałek kodu w następnym wywołaniu event
loop. Ta metoda jest dodawana do global scope i używa `Vert.x executeOnContext()`:

```js
setImmediate(handler => {
  console.log('Hello again from the future!')
});
```

## clearTimeout()

Czyści timeout.


## clearInterval()

Czyści timeout.

## clearImmediate()

::: warning
Obecność tej funkcji zapewnia, że wiele bibliotek działa poprawnie, **ALE** nie wywołują żadnego efektu, z powodu
sposobu w jaki są rozplanowane callbacki w Vert.x Event Loop.
:::

## Obiekt process

Obiekt process (popularny w `nodejs`) jest również dostępny w ES4X, jednakże ma mniej własności:

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

## Obiekt console

Console jest dodawany przez ES4X. Ten obiekt posiada typowe API:

```js
console.debug('Hello', 'World', '!')
console.info('Hello', 'World', '!')
console.log('Hello', 'World', '!')
console.warn('Hello', 'World', '!')
console.error('Hello', 'World', '!')
```

Stack trace'y (zarówno z JS jak i z JVM) mogą być wypisane za pomocą:

```js
try {
  throw new Error('durp!')
} catch (e) {
  console.trace(e);
}
```

## Async Error Tracing

Wyobraź sobie następujący kawałek kodu:

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

Jeśli wykonasz ten kod zobaczysz error z następującym punktem odniesienia:

```
Error
    at Timer.callback (example.js:34)
```

Przyznaj, że nie jest to zbyt pomocne w debugowaniu.

Aby ułatwić posługiwanie się tym do ES4X dołączono moduł, który spina wszystkie rzucone wyjątki razem, więc za każdym
razem gdy wymagane będzie zajęcie się danym callbackiem zamiast przekazywać error, będziesz mógł opakowac go funkcją
pomocniczą.

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

Wiedząc, że plik `durpa/durp.txt` nie istnieje, teraz zamiast lakonicznej informacji zobaczysz...:

```
Error: File not found!
    at stacktraces/jserror.js:24:20
    at stacktraces/jserror.js:40:20
    at stacktraces/jserror.js:53:14
    at stacktraces/jserror.js:53:25
    at classpath:io/reactiverse/es4x/polyfill/global.js:25:18
```

...jeśli error jest obiektem `Error` z JS lub:

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

...jeśli error jest Java Throwable.
If the error is a Java Throwable.

## Date

Wiele API z Vert.x zwraca `Instant` jako tymczasowy typ. Aby użyć tego za pomocą JS do obiektu `Date` dodawana jest pomocnicza funkcja
statyczna:

```js
let instant = someJVMInstant
let d = Date.fromInstant(instant)
```

## ArrayBuffer

ArrayBuffers są typem wbudowanym, jednak jeśli interop jest wymagany, wtedy powinien zostać użyty konstruktor `ByteArray`,
co pozwoli na dostępo do podstawowego bufora bez używania kopii.

```js
let javaBuffer = someJavaBuffer
let b = new ArrayBuffer(javaBuffer)
// the underlying buffer can be read using
b.nioByteBuffer
```
