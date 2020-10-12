# Globales

GraalJS es un motor de JavaScript puro. Esto significa que algunos objetos globales (Global Objects) (que no son estandares pero comunes)
son perdidos. ES4X trato de añadirlos a las caracteristicas perdidas o mejorar los predeterminados como sigue:

## require()

La especificacion original no define la funcion `require()`, ES4X tiene su propia implementacion describida en
[commonjs](./commonjs.md).

## setTimeout()

El metodo `setTimeout()` crea un temporizador que ejecuta una funcion o un codigo especifico cuando se termina el tiempo.
Este metodo se añade al scope global y utiliza `Vert.x Timers`:

```js
setTimeout(handler => {
  console.log('Saludos desde el futuro!')
}, 2000);
```

## setInterval()

El metodo `setInterval()` crea un temporizador que repite la ejecucion de una funcion o un codigo especifico basado en el tiempo.
Este metodo se añade al scope global y utiliza `Vert.x Timers`:

```js
setInterval(handler => {
  console.log('Saludos de nuevo desde el futuro!')
}, 2000);
```

## setImmediate()

El metodo `setImmediate()` ejecuta una funcion o un codigo especifico en la siguiente ranura del bucle de eventos.
Este metodo se añade al scope global y utiliza `Vert.x executeOnContext()`:

```js
setImmediate(handler => {
  console.log('Hello again from the future!')
});
```

## clearTimeout()

Borra un timeout.


## clearInterval()

Borra un timeout.

## clearImmediate()

::: Advertencia
Esta funcion existe para asegurar que muchas librerias no se rompen, **PERO** no tiene ningun efecto debido a la forma
en la que los callbacks se programan en los bucles de evento de Vert.x.
:::

## Objecto process

El objecto process (popular en `nodejs`) tambien esta disponible en ES4X, aunque tiene menos propiedades:

```js
{
  env,          // variables process de entorno (solo lectura)
  pid,          // id del process actual
  engine,       // constante 'graaljs'
  exit,         // Funcion que termina process con codigo de error opcional
  nextTick,     // poner en la cola un callback ejecutable en el siguiente evento de bucle
                // NOTA: este comportamiento es diferente al de nodejs
  on,           // enlazante de funciones con el emisor de eventos
  stdout,       // JVM System.out
  stderr,       // JVM System.err
  stdin,        // JVM System.in (ADVERTENCIA esto bloqueara el bucle de eventos)
  properties,   // JVM propiedades del sistema (lectura, escritura)
  cwd           // Funcion que devuelve el CWD
}
```

## Objeto console

ES4X añade la consola. Este objeto tiene una API tipica:

```js
console.debug('Hello', 'World', '!')
console.info('Hello', 'World', '!')
console.log('Hello', 'World', '!')
console.warn('Hello', 'World', '!')
console.error('Hello', 'World', '!')
```

Los rastros del stack (tanto JS como JVM) se pueden imprimir con:

```js
try {
  throw new Error('durp!')
} catch (e) {
  console.trace(e);
}
```

## Rastreo de Error Asincronico

Imagina el siguiente codigo:

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

Si quisieras ejecutar este codigo verias que tu error tiene el siguiente rastro:

```
Error
    at Timer.callback (example.js:34)
```

Lo cual no es muy util cuando tienes que depurar de errores (debug).

Para facilitar esto se incluye un modulo con ES4X que une tus excepciones,
cada vez que tuvieras que tratar con un callback, en lugar de pasar el error
directamente, lo envuelves en una funcion ayudante (helper).

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

Sabiendo que el archivo `durpa/durp.txt` no existe, ahora verias:

```
Error: File not found!
    at stacktraces/jserror.js:24:20
    at stacktraces/jserror.js:40:20
    at stacktraces/jserror.js:53:14
    at stacktraces/jserror.js:53:25
    at classpath:io/reactiverse/es4x/polyfill/global.js:25:18
```

Si el error que se ha propagado en el objecto `Erro` de JS o:

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

Si el error es algo que lanza Java.

## Fecha

Muchas APIs de Vert.x devuelven `Instant` como tipo temporal, para usarlo desde JS se añade una function estatica ayudante
al objecto `Date`:

```js
let instant = someJVMInstant
let d = Date.fromInstant(instant)
```

## ArrayBuffer


Los búferes de matriz son de tipo incorporado, sin embargo si interop es necesario, un `ByteArray` de JVM deberia pasarse al
constructor y esto permite el acceso al bufer subyacente sin involucrar copias:

```js
let javaBuffer = someJavaBuffer
let b = new ArrayBuffer(javaBuffer)
// the underlying buffer can be read using
b.nioByteBuffer
```
