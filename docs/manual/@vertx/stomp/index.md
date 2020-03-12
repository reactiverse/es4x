STOMP is the Simple (or Streaming) Text Orientated Messaging Protocol.
STOMP provides an interoperable wire format so that STOMP clients can
communicate with any STOMP message broker to provide easy and widespread
messaging interoperability among many languages, platforms and brokers.
Get more details about STOMP on <https://stomp.github.io/index.html>.

Vertx-Stomp is an implementation of a STOMP server and client. You can
use the STOMP server with other clients and use the STOMP client with
other servers. The server and the client supports the version 1.0, 1.1
and 1.2 of the STOMP protocol (see
<https://stomp.github.io/stomp-specification-1.2.html>). The STOMP
server can also be used as a bridge with the vert.x event bus, or
directly with web sockets (using StompJS).

# Using vertx-stomp

To use the Vert.x Stomp server and client, add the following dependency
to the *dependencies* section of your build descriptor:

  - Maven (in your `pom.xml`):

<!-- end list -->

``` xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-stomp</artifactId>
 <version>${maven.version}</version>
</dependency>
```

  - Gradle (in your `build.gradle` file):

<!-- end list -->

``` groovy
compile 'io.vertx:vertx-stomp:${maven.version}'
```

# STOMP server

## Creating a STOMP server

The simplest way to create an STOMP server, using all default options is
as follows:

``` js
import { StompServerHandler } from "@vertx/stomp"
import { StompServer } from "@vertx/stomp"
let server = StompServer.create(vertx).handler(StompServerHandler.create(vertx)).listen();
```

This creates a STOMP server listening on `localhost:61613` that is
compliant with the STOMP specification.

You can configure the port and host in the `listen` method:

``` js
import { StompServerHandler } from "@vertx/stomp"
import { StompServer } from "@vertx/stomp"
let server = StompServer.create(vertx).handler(StompServerHandler.create(vertx)).listen(1234, "0.0.0.0");
```

If you pass `-1` as port, the TCP server would not be started. This is
useful when using the websocket bridge. To be notified when the server
is ready, use a handler as follows:

``` js
import { StompServerHandler } from "@vertx/stomp"
import { StompServer } from "@vertx/stomp"
let server = StompServer.create(vertx).handler(StompServerHandler.create(vertx)).listen((ar, ar_err) => {
  if (ar.failed()) {
    console.log("Failing to start the STOMP server : " + ar.cause().getMessage());
  } else {
    console.log("Ready to receive STOMP frames");
  }
});
```

The handler receive a reference on the `StompServer`.

You can also configure the host and port in `StompServerOptions`:

``` js
import { StompServerHandler } from "@vertx/stomp"
import { StompServer } from "@vertx/stomp"
let server = StompServer.create(vertx, new StompServerOptions()
  .setPort(1234)
  .setHost("0.0.0.0")).handler(StompServerHandler.create(vertx)).listen();
```

## Closing a STOMP server

STOMP servers are closed as follows:

``` js
server.close((ar, ar_err) => {
  if (ar.succeeded()) {
    console.log("The STOMP server has been closed");
  } else {
    console.log("The STOMP server failed to close : " + ar.cause().getMessage());
  }
});
```

## Configuration

The `StompServerOptions` let you configure some aspects of the STOMP
server.

First, the STOMP server is based on a `NetServer`, so you can configure
the underlying `NetServer` from the `StompServerOptions`. Alternatively
you can also pass the `NetServer` you want to use:

``` js
import { StompServerHandler } from "@vertx/stomp"
import { StompServer } from "@vertx/stomp"
let server = StompServer.create(vertx, netServer).handler(StompServerHandler.create(vertx)).listen();
```

The `StompServerOptions` let you configure:

  - the host and port of the STOMP server - defaults to `0.0.0.0:61613`.

  - whether or not the STOMP server is secured - defaults to `false`

  - the max STOMP frame body - default to 10 Mb

  - the maximum number of headers accepted in a STOMP frame - defaults
    to 1000

  - the max length of a header line in a STOMP frame - defaults to 10240

  - the STOMP heartbeat time - default to `1000, 1000`

  - the supported STOMP protocol versions (1.0, 1.1 and 1.2 by default)

  - the maximum number of frame allowed in a transaction (defaults to
    1000)

  - the size of the transaction chunk - defaults to 1000 (see
    `setTransactionChunkSize`)

  - the maximum number of subscriptions a client can handle - defaults
    to 1000

The STOMP heartbeat is configured using a JSON object as follows:

``` js
import { StompServerHandler } from "@vertx/stomp"
import { StompServer } from "@vertx/stomp"
let server = StompServer.create(vertx, new StompServerOptions()
  .setHeartbeat({
    "x" : 1000,
    "y" : 1000
  })).handler(StompServerHandler.create(vertx)).listen();
```

Enabling security requires an additional `AuthProvider` handling the
authentication requests:

``` js
import { StompServerHandler } from "@vertx/stomp"
import { StompServer } from "@vertx/stomp"
let server = StompServer.create(vertx, new StompServerOptions()
  .setSecured(true)).handler(StompServerHandler.create(vertx).authProvider(provider)).listen();
```

More information about `AuthProvider` is available
[here](http://vertx.io/docs/#authentication_and_authorisation).

If a frame exceeds one of the size limits, the frame is rejected and the
client receives an `ERROR` frame. As the specification requires, the
client connection is closed immediately after having sent the error. The
same behavior happens with the other thresholds.

## Subscriptions

The default STOMP server handles subscription destination as opaque
Strings. So it does not promote a structure and it not hierarchic. By
default the STOMP server follow a *topic* semantic (so messages are
dispatched to all subscribers).

## Type of destinations

By default, the STOMP server manages *destinations* as topics. So
messages are dispatched to all subscribers. You can configure the server
to use queues, or mix both types:

``` js
import { Destination } from "@vertx/stomp"
import { StompServerHandler } from "@vertx/stomp"
import { StompServer } from "@vertx/stomp"
let server = StompServer.create(vertx).handler(StompServerHandler.create(vertx).destinationFactory((v, name) => {
  if (name.startsWith("/queue")) {
    return Destination.queue(vertx, name)
  } else {
    return Destination.topic(vertx, name)
  }
})).listen();
```

In the last example, all destination starting with `/queue` are queues
while others are topics. The destination is created when the first
subscription on this destination is received.

A server can decide to reject the destination creation by returning
`null`:

``` js
import { Destination } from "@vertx/stomp"
import { StompServerHandler } from "@vertx/stomp"
import { StompServer } from "@vertx/stomp"
let server = StompServer.create(vertx).handler(StompServerHandler.create(vertx).destinationFactory((v, name) => {
  if (name.startsWith("/forbidden")) {
    return null
  } else if (name.startsWith("/queue")) {
    return Destination.queue(vertx, name)
  } else {
    return Destination.topic(vertx, name)
  }
})).listen();
```

In this case, the subscriber received an `ERROR` frame.

Queues dispatches messages using a round-robin strategies.

## Providing your own type of destination

On purpose the STOMP server does not implement any advanced feature. IF
you need more advanced dispatching policy, you can implement your own
type of destination by providing a `DestinationFactory` returning your
own `Destination` object.

## Acknowledgment

By default, the STOMP server does nothing when a message is not
acknowledged. You can customize this by providing your own `Destination`
implementation.

The custom destination should call the

`onAck` and `onNack` method in order to let the `StompServerHandler`
customizes the behavior:

``` js
import { StompServerHandler } from "@vertx/stomp"
import { StompServer } from "@vertx/stomp"
let server = StompServer.create(vertx).handler(StompServerHandler.create(vertx).onAckHandler((acknowledgement) => {
  // Action to execute when the frames (one in `client-individual` mode, several
  // in `client` mode are acknowledged.
}).onNackHandler((acknowledgement) => {
  // Action to execute when the frames (1 in `client-individual` mode, several in
  // `client` mode are not acknowledged.
})).listen();
```

## Customizing the STOMP server

In addition to the handlers seen above, you can configure almost all
aspects of the STOMP server, such as the actions made when specific
frames are received, the `ping` to sent to the client (to implement the
heartbeat). Here are some examples:

``` js
import { StompServerHandler } from "@vertx/stomp"
import { StompServer } from "@vertx/stomp"
let server = StompServer.create(vertx).handler(StompServerHandler.create(vertx).closeHandler((connection) => {
  // client connection closed
}).beginHandler((frame) => {
  // transaction starts
}).commitHandler((frame) => {
  // transaction committed
})).listen();
```

Be aware that changing the default behavior may break the compliance
with the STOMP specification. So, please look at the default
implementations.

# STOMP client

STOMP clients connect to STOMP server and can send and receive frames.

## Creating a STOMP client

You create a `StompClient` instance with default options as follows:

``` js
import { StompClient } from "@vertx/stomp"
let client = StompClient.create(vertx).connect((ar, ar_err) => {
  if (ar.succeeded()) {
    let connection = ar.result();

  } else {
    console.log("Failed to connect to the STOMP server: " + ar.cause().toString());
  }
});
```

The previous snippet creates a STOMP client connecting to
"0.0.0.0:61613". Once connected, you get a `StompClientConnection` that
let you interact with the server. You can configure the host and port as
follows:

``` js
import { StompClient } from "@vertx/stomp"
let client = StompClient.create(vertx).connect(61613, "0.0.0.0", (ar, ar_err) => {
  if (ar.succeeded()) {
    let connection = ar.result();

  } else {
    console.log("Failed to connect to the STOMP server: " + ar.cause().toString());
  }
});
```

To catch connection errors due to authentication issues, or whatever
error frames sent by the server during the connection negotiation, you
can register a *error handler* on the Stomp Client. All connections
created with the client inherit of the error handler (but can have their
own):

``` js
import { StompClient } from "@vertx/stomp"
let client = StompClient.create(vertx).errorFrameHandler((frame) => {
  // Received the ERROR frame
}).connect(61613, "0.0.0.0", (ar, ar_err) => {
  if (ar.succeeded()) {
    let connection = ar.result();

  } else {
    console.log("Failed to connect to the STOMP server: " + ar.cause().toString());
  }
});
```

You can also configure the host and port in the `StompClientOptions`:

``` js
import { StompClient } from "@vertx/stomp"
let client = StompClient.create(vertx, new StompClientOptions()
  .setHost("localhost")
  .setPort(1234)).connect((ar, ar_err) => {
  if (ar.succeeded()) {
    let connection = ar.result();

  } else {
    console.log("Failed to connect to the STOMP server: " + ar.cause().toString());
  }
});
```

## Closing a STOMP client

You can close a STOMP client:

``` js
import { StompClient } from "@vertx/stomp"
let client = StompClient.create(vertx, new StompClientOptions()
  .setHost("localhost")
  .setPort(1234)).connect((ar, ar_err) => {
  if (ar.succeeded()) {
    let connection = ar.result();

  } else {
    console.log("Failed to connect to the STOMP server: " + ar.cause().toString());
  }
});

client.close();
```

However, this way would not notify the server of the disconnection. To
cleanly close the connection, you should use the `disconnect` method:

``` js
import { StompClient } from "@vertx/stomp"
let client = StompClient.create(vertx, new StompClientOptions()
  .setHost("localhost")
  .setPort(1234)).connect((ar, ar_err) => {
  if (ar.succeeded()) {
    let connection = ar.result();

    connection.disconnect();
  } else {
    console.log("Failed to connect to the STOMP server: " + ar.cause().toString());
  }
});
```

If the heartbeat is enabled and if the client did not detect server
activity after the configured timeout, the connection is automatically
closed.

## Handling errors

On the `StompClientConnection`, you can register an error handler
receiving `ERROR` frames sent by the server. Notice that the server
closes the connection with the client after having sent such frame:

``` js
import { StompClient } from "@vertx/stomp"
let client = StompClient.create(vertx, new StompClientOptions()
  .setHost("localhost")
  .setPort(1234)).connect((ar, ar_err) => {
  if (ar.succeeded()) {
    let connection = ar.result();
    connection.errorHandler((frame) => {
      console.log("ERROR frame received : " + frame);
    });
  } else {
    console.log("Failed to connect to the STOMP server: " + ar.cause().toString());
  }
});
```

The client can also be notified when a connection drop has been
detected. Connection failures are detected using the STOMP heartbeat
mechanism. When the server has not sent a message in the heartbeat time
window, the connection is closed and the `connectionDroppedHandler` is
called (if set). To configure a `connectionDroppedHandler`, call
`connectionDroppedHandler`. The handler can for instance tries to
reconnect to the server:

``` js
import { Buffer } from "@vertx/core"
import { StompClient } from "@vertx/stomp"
let client = StompClient.create(vertx).connect((ar, ar_err) => {
  if (ar.succeeded()) {
    let connection = ar.result();
    connection.connectionDroppedHandler((con) => {
      // The connection has been lost
      // You can reconnect or switch to another server.
    });

    connection.send("/queue", Buffer.buffer("Hello"), (frame) => {
      console.log("Message processed by the server");
    });
  } else {
    console.log("Failed to connect to the STOMP server: " + ar.cause().toString());
  }
});
```

## Configuration

You can configure various aspect by passing a `StompClientOptions` when
creating the `StompClient`. As the STOMP client relies on a `NetClient`,
you can configure the underlying Net Client from the
`StompClientOptions`. Alternatively, you can pass the `NetClient` you
want to use in the `connect` method:

``` js
import { StompClient } from "@vertx/stomp"
let client = StompClient.create(vertx).connect(netClient, (ar, ar_err) => {
  if (ar.succeeded()) {
    let connection = ar.result();
    connection.errorHandler((frame) => {
      console.log("ERROR frame received : " + frame);
    });
  } else {
    console.log("Failed to connect to the STOMP server: " + ar.cause().toString());
  }
});
```

The `StompClientOptions` let you configure:

  - the host and port ot the STOMP server

  - the login and passcode to connect to the server

  - whether or not the `content-length` header should be added to the
    frame if not set explicitly. (enabled by default)

  - whether or not the `STOMP` command should be used instead of the
    `CONNECT` command (disabled by default)

  - whether or not the `host` header should be ignored in the `CONNECT`
    frame (disabled by default)

  - the heartbeat configuration (1000, 1000 by default)

## Subscribing to destinations

To subscribe to a destination, use:

``` js
import { StompClient } from "@vertx/stomp"
let client = StompClient.create(vertx).connect((ar, ar_err) => {
  if (ar.succeeded()) {
    let connection = ar.result();
    connection.subscribe("/queue", (frame) => {
      console.log("Just received a frame from /queue : " + frame);
    });
  } else {
    console.log("Failed to connect to the STOMP server: " + ar.cause().toString());
  }
});
```

To unsubscribe, use:

``` js
import { StompClient } from "@vertx/stomp"
let client = StompClient.create(vertx).connect((ar, ar_err) => {
  if (ar.succeeded()) {
    let connection = ar.result();
    connection.subscribe("/queue", (frame) => {
      console.log("Just received a frame from /queue : " + frame);
    });

    // ....

    connection.unsubscribe("/queue");
  } else {
    console.log("Failed to connect to the STOMP server: " + ar.cause().toString());
  }
});
```

## Sending messages

To send a message, use:

``` js
import { Buffer } from "@vertx/core"
import { StompClient } from "@vertx/stomp"
let client = StompClient.create(vertx).connect((ar, ar_err) => {
  if (ar.succeeded()) {
    let connection = ar.result();
    let headers = {};
    headers["header1"] = "value1";
    connection.send("/queue", headers, Buffer.buffer("Hello"));
    // No headers:
    connection.send("/queue", Buffer.buffer("World"));
  } else {
    console.log("Failed to connect to the STOMP server: " + ar.cause().toString());
  }
});
```

## Acknowledgements

Clients can send `ACK` and `NACK` frames:

``` js
import { StompClient } from "@vertx/stomp"
let client = StompClient.create(vertx).connect((ar, ar_err) => {
  if (ar.succeeded()) {
    let connection = ar.result();
    connection.subscribe("/queue", (frame) => {
      connection.ack(frame.ack);
      // OR
      connection.nack(frame.ack);
    });
  } else {
    console.log("Failed to connect to the STOMP server: " + ar.cause().toString());
  }
});
```

## Transactions

Clients can also create transactions. `ACK`, `NACK` and `SEND` frames
sent in the transaction will be delivery only when the transaction is
committed.

``` js
import { Buffer } from "@vertx/core"
import { StompClient } from "@vertx/stomp"
let client = StompClient.create(vertx).connect((ar, ar_err) => {
  if (ar.succeeded()) {
    let connection = ar.result();
    let headers = {};
    headers["transaction"] = "my-transaction";
    connection.beginTX("my-transaction");
    connection.send("/queue", headers, Buffer.buffer("Hello"));
    connection.send("/queue", headers, Buffer.buffer("World"));
    connection.send("/queue", headers, Buffer.buffer("!!!"));
    connection.commit("my-transaction");
    // OR
    connection.abort("my-transaction");
  } else {
    console.log("Failed to connect to the STOMP server: " + ar.cause().toString());
  }
});
```

## Receipt

Each sent commands can have a *receipt* handler, notified when the
server has processed the message:

``` js
import { Buffer } from "@vertx/core"
import { StompClient } from "@vertx/stomp"
let client = StompClient.create(vertx).connect((ar, ar_err) => {
  if (ar.succeeded()) {
    let connection = ar.result();

    connection.send("/queue", Buffer.buffer("Hello"), (frame) => {
      console.log("Message processed by the server");
    });
  } else {
    console.log("Failed to connect to the STOMP server: " + ar.cause().toString());
  }
});
```

# Using the STOMP server as a bridge to the vert.x Event Bus

The STOMP server can be used as a bridge to the vert.x Event Bus. The
bridge is bi-directional meaning the STOMP frames are translated to
Event Bus messages and Event Bus messages are translated to STOMP
frames.

To enable the bridge you need to configure the inbound and outbound
addresses. Inbound addresses are STOMP destination that are transferred
to the event bus. The STOMP destination is used as the event bus
address. Outbound addresses are event bus addresses that are transferred
to STOMP.

``` js
import { StompServerHandler } from "@vertx/stomp"
import { StompServer } from "@vertx/stomp"
let server = StompServer.create(vertx).handler(StompServerHandler.create(vertx).bridge(new BridgeOptions()
  .setInboundPermitteds([new PermittedOptions()
    .setAddress("/toBus")])
  .setOutboundPermitteds([new PermittedOptions()
    .setAddress("/toStomp")]))).listen();
```

By default, the bridge use a publish/subscribe delivery (topic). You can
configure it to use a point to point delivery where only one STOMP
client or Event Bus consumer is invoked:

``` js
import { StompServerHandler } from "@vertx/stomp"
import { StompServer } from "@vertx/stomp"
let server = StompServer.create(vertx).handler(StompServerHandler.create(vertx).bridge(new BridgeOptions()
  .setInboundPermitteds([new PermittedOptions()
    .setAddress("/toBus")])
  .setOutboundPermitteds([new PermittedOptions()
    .setAddress("/toStomp")])
  .setPointToPoint(true))).listen();
```

The permitted options can also be expressed as a "regex" or with a
*match*. A *match* is a structure that the message payload must meet.
For instance, in the next examples, the payload must contains the field
"foo" set to "bar". Structure match only supports JSON object.

``` js
import { StompServerHandler } from "@vertx/stomp"
import { StompServer } from "@vertx/stomp"
let server = StompServer.create(vertx).handler(StompServerHandler.create(vertx).bridge(new BridgeOptions()
  .setInboundPermitteds([new PermittedOptions()
    .setAddress("/toBus")
    .setMatch({
      "foo" : "bar"
    })])
  .setOutboundPermitteds([new PermittedOptions()
    .setAddress("/toStomp")])
  .setPointToPoint(true))).listen();
```

# Using the STOMP server with web sockets

If you want to connect a JavaScript client (node.js or a browser)
directly with the STOMP server, you can use a web socket. The STOMP
protocol has been adapted to work over web sockets in
[StompJS](http://jmesnil.net/stomp-websocket/doc/). The JavaScript
connects directly to the STOMP server and send STOMP frames on the web
socket. It also receives the STOMP frame directly on the web socket.

To configure the server to use StompJS, you need to:

1.  Enable the web socket bridge and configure the path of the listening
    web socket (`/stomp` by default).

2.  Import [StompJS](http://jmesnil.net/stomp-websocket/doc/#download)
    in your application (as a script on an HTML page, or as an npm
    module (<https://www.npmjs.com/package/stompjs>).

3.  Connect to the server

To achieve the first step, you would need a HTTP server, and pass the
`webSocketHandler` result to `webSocketHandler`:

``` js
import { StompServerHandler } from "@vertx/stomp"
import { StompServer } from "@vertx/stomp"
let server = StompServer.create(vertx, new StompServerOptions()
  .setPort(-1)
  .setWebsocketBridge(true)
  .setWebsocketPath("/stomp")).handler(StompServerHandler.create(vertx));

let http = vertx.createHttpServer(new HttpServerOptions()
  .setWebSocketSubProtocols(["v10.stomp, v11.stomp"])).webSocketHandler(server.webSocketHandler()).listen(8080);
```

Don’t forget to declare the supported sub-protocols. Without this, the
connection will be rejected.

Then follow the instructions from [the StompJS
documentation](http://jmesnil.net/stomp-websocket/doc/) to connect to
the server. Here is a simple example:

``` javascript
var url = "ws://localhost:8080/stomp";
var client = Stomp.client(url);
var callback = function(frame) {
  console.log(frame);
};

client.connect({}, function() {
var subscription = client.subscribe("foo", callback);
});
```

# Registering received and writing frame handlers

STOMP clients, client’s connections and server handlers support
registering a received `Frame` handler that would be notified every time
a frame is received from the wire. It lets you log the frames, or
implement custom behavior. The handler is already called for `PING`
frames, and *illegal / unknown* frames:

``` js
import { StompServerHandler } from "@vertx/stomp"
import { StompServer } from "@vertx/stomp"
import { StompClient } from "@vertx/stomp"
let server = StompServer.create(vertx).handler(StompServerHandler.create(vertx).receivedFrameHandler((sf) => {
  console.log(sf.frame());
})).listen();

let client = StompClient.create(vertx).receivedFrameHandler((frame) => {
  console.log(frame);
});
```

The handler is called before the frame is processed, so you can also
*modify* the frame.

Frames not using a valid STOMP command use the `UNKNOWN` command. The
original command is written in the headers using the
`Frame.STOMP_FRAME_COMMAND` key.

You can also register a handler to be notified when a frame is going to
be sent (written to the wire):

``` js
import { StompServerHandler } from "@vertx/stomp"
import { StompServer } from "@vertx/stomp"
import { StompClient } from "@vertx/stomp"
let server = StompServer.create(vertx).handler(StompServerHandler.create(vertx)).writingFrameHandler((sf) => {
  console.log(sf.frame());
}).listen();

let client = StompClient.create(vertx).writingFrameHandler((frame) => {
  console.log(frame);
});
```
