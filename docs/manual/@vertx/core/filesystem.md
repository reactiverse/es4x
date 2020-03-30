# Using the file system with Vert.x

The Vert.x `FileSystem` object provides many operations for manipulating
the file system.

There is one file system object per Vert.x instance, and you obtain it
with `fileSystem`.

A blocking and a non blocking version of each operation is provided. The
non blocking versions take a handler which is called when the operation
completes or an error occurs.

Here’s an example of an asynchronous copy of a file:

``` js
let fs = vertx.fileSystem();

// Copy file from foo.txt to bar.txt
fs.copy("foo.txt", "bar.txt", (res) => {
  if (res.succeeded()) {
    // Copied ok!
  } else {
    // Something went wrong
  }
});
```

The blocking versions are named `xxxBlocking` and return the results or
throw exceptions directly. In many cases, depending on the operating
system and file system, some of the potentially blocking operations can
return quickly, which is why we provide them, but it’s highly
recommended that you test how long they take to return in your
particular application before using them from an event loop, so as not
to break the Golden Rule.

Here’s the copy using the blocking API:

``` js
let fs = vertx.fileSystem();

// Copy file from foo.txt to bar.txt synchronously
fs.copyBlocking("foo.txt", "bar.txt");
```

Many operations exist to copy, move, truncate, chmod and many other file
operations. We won’t list them all here, please consult the `API docs`
for the full list.

Let’s see a couple of examples using asynchronous methods:

``` js
import { Buffer } from "@vertx/core"
// Read a file
vertx.fileSystem().readFile("target/classes/readme.txt", (result) => {
  if (result.succeeded()) {
    console.log(result.result());
  } else {
    console.error("Oh oh ..." + result.cause());
  }
});

// Copy a file
vertx.fileSystem().copy("target/classes/readme.txt", "target/classes/readme2.txt", (result) => {
  if (result.succeeded()) {
    console.log("File copied");
  } else {
    console.error("Oh oh ..." + result.cause());
  }
});

// Write a file
vertx.fileSystem().writeFile("target/classes/hello.txt", Buffer.buffer("Hello"), (result) => {
  if (result.succeeded()) {
    console.log("File written");
  } else {
    console.error("Oh oh ..." + result.cause());
  }
});

// Check existence and delete
vertx.fileSystem().exists("target/classes/junk.txt", (result) => {
  if (result.succeeded() && result.result()) {
    vertx.fileSystem().delete("target/classes/junk.txt", (r) => {
      console.log("File deleted");
    });
  } else {
    console.error("Oh oh ... - cannot delete the file: " + result.cause());
  }
});
```

## Asynchronous files

Vert.x provides an asynchronous file abstraction that allows you to
manipulate a file on the file system.

You open an `AsyncFile` as follows:

``` js
let options = new OpenOptions();
fileSystem.open("myfile.txt", options, (res) => {
  if (res.succeeded()) {
    let file = res.result();
  } else {
    // Something went wrong!
  }
});
```

`AsyncFile` implements `ReadStream` and `WriteStream` so you can *pipe*
files to and from other stream objects such as net sockets, http
requests and responses, and WebSockets.

They also allow you to read and write directly to them.

### Random access writes

To use an `AsyncFile` for random access writing you use the `write`
method.

The parameters to the method are:

  - `buffer`: the buffer to write.

  - `position`: an integer position in the file where to write the
    buffer. If the position is greater or equal to the size of the file,
    the file will be enlarged to accommodate the offset.

  - `handler`: the result handler

Here is an example of random access writes:

``` js
import { Buffer } from "@vertx/core"
vertx.fileSystem().open("target/classes/hello.txt", new OpenOptions(), (result) => {
  if (result.succeeded()) {
    let file = result.result();
    let buff = Buffer.buffer("foo");
    for (let i = 0;i < 5;i++) {
      file.write(buff, buff.length() * i, (ar) => {
        if (ar.succeeded()) {
          console.log("Written ok!");
          // etc
        } else {
          console.error("Failed to write: " + ar.cause());
        }
      });
    }
  } else {
    console.error("Cannot open file " + result.cause());
  }
});
```

### Random access reads

To use an `AsyncFile` for random access reads you use the `read` method.

The parameters to the method are:

  - `buffer`: the buffer into which the data will be read.

  - `offset`: an integer offset into the buffer where the read data will
    be placed.

  - `position`: the position in the file where to read data from.

  - `length`: the number of bytes of data to read

  - `handler`: the result handler

Here’s an example of random access reads:

``` js
import { Buffer } from "@vertx/core"
vertx.fileSystem().open("target/classes/les_miserables.txt", new OpenOptions(), (result) => {
  if (result.succeeded()) {
    let file = result.result();
    let buff = Buffer.buffer(1000);
    for (let i = 0;i < 10;i++) {
      file.read(buff, i * 100, i * 100, 100, (ar) => {
        if (ar.succeeded()) {
          console.log("Read ok!");
        } else {
          console.error("Failed to write: " + ar.cause());
        }
      });
    }
  } else {
    console.error("Cannot open file " + result.cause());
  }
});
```

### Opening Options

When opening an `AsyncFile`, you pass an `OpenOptions` instance. These
options describe the behavior of the file access. For instance, you can
configure the file permissions with the `setRead`, `setWrite` and
`setPerms` methods.

You can also configure the behavior if the open file already exists with
`setCreateNew` and `setTruncateExisting`.

You can also mark the file to be deleted on close or when the JVM is
shutdown with `setDeleteOnClose`.

### Flushing data to underlying storage.

In the `OpenOptions`, you can enable/disable the automatic
synchronisation of the content on every write using `setDsync`. In that
case, you can manually flush any writes from the OS cache by calling the
`flush` method.

This method can also be called with an handler which will be called when
the flush is complete.

### Using AsyncFile as ReadStream and WriteStream

`AsyncFile` implements `ReadStream` and `WriteStream`. You can then use
them with a *pipe* to pipe data to and from other read and write
streams. For example, this would copy the content to another
`AsyncFile`:

``` js
let output = vertx.fileSystem().openBlocking("target/classes/plagiary.txt", new OpenOptions());

vertx.fileSystem().open("target/classes/les_miserables.txt", new OpenOptions(), (result) => {
  if (result.succeeded()) {
    let file = result.result();
    file.pipeTo(output, (ar) => {
      if (ar.succeeded()) {
        console.log("Copy done");
      }
    });
  } else {
    console.error("Cannot open file " + result.cause());
  }
});
```

You can also use the *pipe* to write file content into HTTP responses,
or more generally in any `WriteStream`.

### Accessing files from the classpath

When vert.x cannot find the file on the filesystem it tries to resolve
the file from the class path. Note that classpath resource paths never
start with a `/`.

Due to the fact that Java does not offer async access to classpath
resources, the file is copied to the filesystem in a worker thread when
the classpath resource is accessed the very first time and served from
there asynchronously. When the same resource is accessed a second time,
the file from the filesystem is served directly from the filesystem. The
original content is served even if the classpath resource changes (e.g.
in a development system).

This caching behaviour can be set on the `setFileCachingEnabled` option.
The default value of this option is `true` unless the system property
`vertx.disableFileCaching` is defined.

The path where the files are cached is `.vertx` by default and can be
customized by setting the system property `vertx.cacheDirBase`.

The whole classpath resolving feature can be disabled system-wide by
setting the system property `vertx.disableFileCPResolving` to `true`.

> **Note**
> 
> these system properties are evaluated once when the the
> `io.vertx.core.file.FileSystemOptions` class is loaded, so these
> properties should be set before loading this class or as a JVM system
> property when launching it.

If you want to disable classpath resolving for a particular application
but keep it enabled by default system-wide, you can do so via the
`setClassPathResolvingEnabled` option.

### Closing an AsyncFile

To close an `AsyncFile` call the `close` method. Closing is asynchronous
and if you want to be notified when the close has been completed you can
specify a handler function as an argument.
