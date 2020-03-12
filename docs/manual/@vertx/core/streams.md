# Streams

There are several objects in Vert.x that allow items to be read from and
written.

In previous versions the `io.vertx.core.streams` package was
manipulating `Buffer` objects exclusively. From now, streams are not
coupled to buffers anymore and they work with any kind of objects.

In Vert.x, write calls return immediately, and writes are queued
internally.

It’s not hard to see that if you write to an object faster than it can
actually write the data to its underlying resource, then the write queue
can grow unbounded - eventually resulting in memory exhaustion.

To solve this problem a simple flow control (*back-pressure*) capability
is provided by some objects in the Vert.x API.

Any flow control aware object that can be *written-to* implements
`WriteStream`, while any flow control object that can be *read-from* is
said to implement `ReadStream`.

Let’s take an example where we want to read from a `ReadStream` then
write the data to a `WriteStream`.

A very simple example would be reading from a `NetSocket` then writing
back to the same `NetSocket` - since `NetSocket` implements both
`ReadStream` and `WriteStream`. Note that this works between any
`ReadStream` and `WriteStream` compliant object, including HTTP
requests, HTTP responses, async files I/O, WebSockets, etc.

A naive way to do this would be to directly take the data that has been
read and immediately write it to the `NetSocket`:

``` js
let server = vertx.createNetServer(new NetServerOptions()
  .setPort(1234)
  .setHost("localhost"));
server.connectHandler((sock) => {
  sock.handler((buffer) => {
    // Write the data straight back
    sock.write(buffer);
  });
}).listen();
```

There is a problem with the example above: if data is read from the
socket faster than it can be written back to the socket, it will build
up in the write queue of the `NetSocket`, eventually running out of RAM.
This might happen, for example if the client at the other end of the
socket wasn’t reading fast enough, effectively putting back-pressure on
the connection.

Since `NetSocket` implements `WriteStream`, we can check if the
`WriteStream` is full before writing to it:

``` js
let server = vertx.createNetServer(new NetServerOptions()
  .setPort(1234)
  .setHost("localhost"));
server.connectHandler((sock) => {
  sock.handler((buffer) => {
    if (!sock.writeQueueFull()) {
      sock.write(buffer);
    }
  });

}).listen();
```

This example won’t run out of RAM but we’ll end up losing data if the
write queue gets full. What we really want to do is pause the
`NetSocket` when the write queue is full:

``` js
let server = vertx.createNetServer(new NetServerOptions()
  .setPort(1234)
  .setHost("localhost"));
server.connectHandler((sock) => {
  sock.handler((buffer) => {
    sock.write(buffer);
    if (sock.writeQueueFull()) {
      sock.pause();
    }
  });
}).listen();
```

We’re almost there, but not quite. The `NetSocket` now gets paused when
the file is full, but we also need to unpause it when the write queue
has processed its backlog:

``` js
let server = vertx.createNetServer(new NetServerOptions()
  .setPort(1234)
  .setHost("localhost"));
server.connectHandler((sock) => {
  sock.handler((buffer) => {
    sock.write(buffer);
    if (sock.writeQueueFull()) {
      sock.pause();
      sock.drainHandler((done) => {
        sock.resume();
      });
    }
  });
}).listen();
```

And there we have it. The `drainHandler` event handler will get called
when the write queue is ready to accept more data, this resumes the
`NetSocket` that allows more data to be read.

Wanting to do this is quite common while writing Vert.x applications, so
we added the `pipeTo` method that does all of this hard work for you.
You just feed it the `WriteStream` and use it:

``` js
let server = vertx.createNetServer(new NetServerOptions()
  .setPort(1234)
  .setHost("localhost"));
server.connectHandler((sock) => {
  sock.pipeTo(sock);
}).listen();
```

This does exactly the same thing as the more verbose example, plus it
handles stream failures and termination: the destination `WriteStream`
is ended when the pipe completes with success or a failure.

You can be notified when the operation completes:

``` js
server.connectHandler((sock) => {

  // Pipe the socket providing an handler to be notified of the result
  sock.pipeTo(sock, (ar) => {
    if (ar.succeeded()) {
      console.log("Pipe succeeded");
    } else {
      console.log("Pipe failed");
    }
  });
}).listen();
```

When you deal with an asynchronous destination, you can create a `Pipe`
instance that pauses the source and resumes it when the source is piped
to the destination:

``` js
server.connectHandler((sock) => {

  // Create a pipe to use asynchronously
  let pipe = sock.pipe();

  // Open a destination file
  fs.open("/path/to/file", new OpenOptions(), (ar) => {
    if (ar.succeeded()) {
      let file = ar.result();

      // Pipe the socket to the file and close the file at the end
      pipe.to(file);
    } else {
      sock.close();
    }
  });
}).listen();
```

When you need to abort the transfer, you need to close it:

``` js
vertx.createHttpServer().requestHandler((request) => {

  // Create a pipe that to use asynchronously
  let pipe = request.pipe();

  // Open a destination file
  fs.open("/path/to/file", new OpenOptions(), (ar) => {
    if (ar.succeeded()) {
      let file = ar.result();

      // Pipe the socket to the file and close the file at the end
      pipe.to(file);
    } else {
      // Close the pipe and resume the request, the body buffers will be discarded
      pipe.close();

      // Send an error response
      request.response().setStatusCode(500).end();
    }
  });
}).listen(8080);
```

When the pipe is closed, the streams handlers are unset and the
`ReadStream` resumed.

As seen above, by default the destination is always ended when the
stream completes, you can control this behavior on the pipe object:

  - `endOnFailure` controls the behavior when a failure happens

  - `endOnSuccess` controls the behavior when the read stream ends

  - `endOnComplete` controls the behavior in all cases

Here is a short example:

``` js
import { Buffer } from "@vertx/core"
src.pipe().endOnSuccess(false).to(dst, (rs) => {
  // Append some text and close the file
  dst.end(Buffer.buffer("done"));
});
```

Let’s now look at the methods on `ReadStream` and `WriteStream` in more
detail:

## ReadStream

`ReadStream` is implemented by `HttpClientResponse`, `DatagramSocket`,
`HttpClientRequest`, `HttpServerFileUpload`, `HttpServerRequest`,
`MessageConsumer`, `NetSocket`, `WebSocket`, `TimeoutStream`,
`AsyncFile`.

Functions:

  - `handler`: set a handler which will receive items from the
    ReadStream.

  - `pause`: pause the handler. When paused no items will be received in
    the handler.

  - `resume`: resume the handler. The handler will be called if any item
    arrives.

  - `exceptionHandler`: Will be called if an exception occurs on the
    ReadStream.

  - `endHandler`: Will be called when end of stream is reached. This
    might be when EOF is reached if the ReadStream represents a file, or
    when end of request is reached if it’s an HTTP request, or when the
    connection is closed if it’s a TCP socket.

## WriteStream

`WriteStream` is implemented by `HttpClientRequest`,
`HttpServerResponse` `WebSocket`, `NetSocket`, `AsyncFile`, and
`MessageProducer`

Functions:

  - `write`: write an object to the WriteStream. This method will never
    block. Writes are queued internally and asynchronously written to
    the underlying resource.

  - `setWriteQueueMaxSize`: set the number of object at which the write
    queue is considered *full*, and the method `writeQueueFull` returns
    `true`. Note that, when the write queue is considered full, if write
    is called the data will still be accepted and queued. The actual
    number depends on the stream implementation, for `Buffer` the size
    represents the actual number of bytes written and not the number of
    buffers.

  - `writeQueueFull`: returns `true` if the write queue is considered
    full.

  - `exceptionHandler`: Will be called if an exception occurs on the
    `WriteStream`.

  - `drainHandler`: The handler will be called if the `WriteStream` is
    considered no longer full.

## Pump

The pump exposes a subset of the pipe API and only transfers the items
between streams, it does not handle the completion or failure of the
transfer operation.

``` js
import { Pump } from "@vertx/core"
let server = vertx.createNetServer(new NetServerOptions()
  .setPort(1234)
  .setHost("localhost"));
server.connectHandler((sock) => {
  Pump.pump(sock, sock).start();
}).listen();
```

> **Important**
>
> Before Vert.x 3.7 the `Pump` was the advocated API for transferring a
> read stream to a write stream. Since 3.7 the pipe API supersedes the
> pump API.

Instances of Pump have the following methods:

  - `start`: Start the pump.

  - `stop`: Stops the pump. When the pump starts it is in stopped mode.

  - `setWriteQueueMaxSize`: This has the same meaning as
    `setWriteQueueMaxSize` on the `WriteStream`.

A pump can be started and stopped multiple times.

When a pump is first created it is *not* started. You need to call the
`start()` method to start it.
