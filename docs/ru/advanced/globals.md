# Глобальные объекты

GraalJS - чистый движок JavaScript. Это значит, что некоторые глобальные объекты (не являющиеся стандартными, но при этом общепринятые) отсутствуют. ES4X пытается добавить отсутствующие возможности или улучшить стандартные:

## require()

Официальная спецификация не определяет функцию `require()`, ES4X содержит собственную реализацию, описанную в [commonjs](./commonjs.md).

## setTimeout()

Метод `setTimeout()` создает таймер, который вызывает функцию или определенный фрагмент кода по завершению таймера.
Этот метод добавлен в глобальную область применения и использует `Vert.x Timers`:

```js
setTimeout(handler => {
  console.log('Привет из будущего!')
}, 2000);
```

## setInterval()

Метод `setInterval()` создает таймер, который вызывает функцию или определенный фрагмент кода периодически по завершению таймера.
Этот метод добавлен в глобальную область применения и использует `Vert.x Timers`:

```js
setInterval(handler => {
  console.log('Снова привет из будущего!')
}, 2000);
```

## setImmediate()

Метод `setImmediate()` вызывает функцию или определенный фрагмент кода, как только будет доступен слот в событийном цикле.
Этот метод добавлен в глобальную область применения и использует `Vert.x executeOnContext()`:

```js
setImmediate(handler => {
  console.log('Снова привет из будущего!')
});
```

## clearTimeout()

Сбрасывает тайм-аут.


## clearInterval()

Сбрасывает тайм-аут.

## clearImmediate()

::: Внимание
Эта функция создана лишь для того, чтобы некоторые библиотеки не ломались, **НО** она не делает ничего из-за того, как работает с функциями обратного вызова Vert.x Event Loop.
:::

## Объект process

Объект process (популярный из-за `nodejs`) также доступен в ES4X, хоть и с меньшим количеством свойств:

```js
{
  env,          // переменные окружения (только для чтения)
  pid,          // id текущего процесса
  engine,       // постоянная 'graaljs'
  exit,         // функция завершения процесса с опциональным кодом ошибки
  nextTick,     // функция добавления в очередь функции обратного вызова для запуска в следующем доступном слоте
                // событийного цикла
                // Заметка: поведение отличается от nodejs
  on,           // привязка транслятора событий
  stdout,       // JVM System.out
  stderr,       // JVM System.err
  stdin,        // JVM System.in (ВНИМАНИЕ блокирует событийный цикл)
  properties,   // JVM System properties (read, write)
  cwd           // функция, возвращающая CWD
}
```

## Объект console

ES4X добавляет console. У данного объекта типичный API:

```js
console.debug('Hello', 'World', '!')
console.info('Hello', 'World', '!')
console.log('Hello', 'World', '!')
console.warn('Hello', 'World', '!')
console.error('Hello', 'World', '!')
```

Трассировка стека (и JS, и JVM) может быть выведена так:

```js
try {
  throw new Error('Ой!')
} catch (e) {
  console.trace(e);
}
```

## Трассировка асинхронных ошибок

Рассмотрим следующий фрагмент кода:

```js
function one() {
   two(function(err) {
     if(err) throw err;
     console.log("Второй - выполнен");
   });
}

function two(callback) {
  setTimeout(function () {
    three(function(err) {
      if(err) return callback(err);
      console.log("Третий - выполнен");
      callback();
    });
  }, 0);
}

function three(callback)
{
  setTimeout(function () {
    four(function(err) {
      if(err) return callback(err);
      console.log("Четвертый - выполнен");
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

Если вы запустите этот код, вы увидите, что у вашей ошибки следующая трассировка:

```
Error
    at Timer.callback (example.js:34)
```

которая не сильно помогает в процессе поиска ошибки.

Для таких случаев сделан собранный модуль ES4X, который соединяет ваши исключения вместе, каждый раз, когда вы
обрабатываете функцию обратного вызова вместо передачи ошибки напрямую, вы оборачиваете ее во вспомогательную функцию.

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

    console.log("Второй - выполнен");
    should.fail("До сюда дойти не должно");
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

      console.log("Третий - выполнен");
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

      console.log("Четвертый - выполнен");
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

Если файл `durpa/durp.txt` не существует, то теперь вы получите следующее:

```
Error: File not found!
    at stacktraces/jserror.js:24:20
    at stacktraces/jserror.js:40:20
    at stacktraces/jserror.js:53:14
    at stacktraces/jserror.js:53:25
    at classpath:io/reactiverse/es4x/polyfill/global.js:25:18
```

если ошибка - это объект `Error` JS, или:

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

если ошибка - Java Throwable.

## Date

Многие APIs из Vert.x возвращают `Instant` как временной тип. Для того, чтобы использовать его в JS, добавлена
вспомогательная статическая функция объекту `Date`:

```js
let instant = someJVMInstant
let d = Date.fromInstant(instant)
```

## ArrayBuffer

ArrayBuffer - это встроенный тип, однако если требуется функциональная совместимость, тогда JVM `ByteArray` должен быть
передан в конструктор, что позволит получить доступ к нижележащему буферу без копирования:

```js
let javaBuffer = someJavaBuffer
let b = new ArrayBuffer(javaBuffer)
// нижележащий буфер может быть прочтен с помощью
b.nioByteBuffer
```
