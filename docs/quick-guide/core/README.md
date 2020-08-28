---
prev: ../
next: false
sidebarDepth: 2
---

# Vert.x core examples

Here you will find examples demonstrating Vert.x core in action.

Vert.x core provides fairly low level functionality for a diverse range of functions including HTTP, TCP, UDP,
WebSockets, file system access, timers, verticles and more. For detailed documentation, consult the
Vert.x [core manual](https://vertx.io/docs).


## Project setup

To use es4x your own project use the following project as a template:

<<< @/docs/quick-guide/core/package.json

## Net examples

These examples demonstrate usage of Vert.x net servers and clients - these are used for TCP (and SSL) servers and clients.

### Echo

This example consists of an echo server verticle which serves TCP connections, and simply echoes back on the connection
whatever it receives.

You can run the echo server then run `telnet localhost 1234` from a console to connect to it. Type some stuff and see it
echoed back to you.

<<< @/docs/quick-guide/core/net/echo/server.js

It also contains an echo client, which creates a connection to the server, sends some data and logs out what it receives
back. You can use that as an alternative to connecting via telnet.

<<< @/docs/quick-guide/core/net/echo/client.js


### Echo SSL

This is the same as the [Echo](#echo) example but using SSL to encrypt connections.

The server:

<<< @/docs/quick-guide/core/net/echossl/server.js

The client:

<<< @/docs/quick-guide/core/net/echossl/client.js

## HTTP examples

These examples demonstrate usage of HTTP with Vert.x.

### Simple

A very simple HTTP server which always responds with the same response:

<<< @/docs/quick-guide/core/http/simple/server.js

You can run the server then open a browser and point it at [http://localhost:8080](http://localhost:8080)

And a simple HTTP client which makes a request to the server.

<<< @/docs/quick-guide/core/http/simple/client.js

### HTTPS

Like the [simple](#simple) example, but using HTTPS instead of HTTP

<<< @/docs/quick-guide/core/http/https/server.js

You can run the server then open a browser and point it at [http://localhost:4443](http://localhost:4443)

And a simple HTTPS client which makes a request to the server.

<<< @/docs/quick-guide/core/http/https/client.js

### Proxy connect

Connecting to a web server using a proxy.

<<< @/docs/quick-guide/core/http/proxyconnect/client.js

The proxy receives requests and connects to the endpoint server using a socket, then pass
all the events between the client and the proxied server.

<<< @/docs/quick-guide/core/http/proxyconnect/proxy.js

### Proxy

A simple toy HTTP proxy. The proxy receives requests and forwards them to the endpoint server, it also takes responses
from the other server and passes them back to the client.

<<< @/docs/quick-guide/core/http/proxy/proxy.js

### Sendfile

This example demonstrates how you can serve static files from disk using a Vert.x http server.

<<< @/docs/quick-guide/core/http/sendfile/send_file.js

You can run the server then open a browser and point it at [http://localhost:8080](http://localhost:8080).

::: tip
In practice you would probably actually use Vert.x-Web for this rather than writing a web server at this low level. Serving
files manually like this can leave you open to security exploits, e.g. by clients crafting URI paths which try to access
resources outside of the permitted area. Vert.x-Web provides URI path normalisation to avoid these kinds of exploits and comes
with a static file handler which also handles caching headers and other features that you would probably want when serving
static files in a web application.
:::

### Simple form

This example demonstrates how you can handle an HTML form on the server.

<<< @/docs/quick-guide/core/http/simpleform/simple_form_server.js

You can run the server then open a browser and point it at [http://localhost:8080](http://localhost:8080).

::: tip
In practice you would probably also use Vert.x-Web for this rather than writing a server at this low level. Vert.x-Web
provides built in support for HTML forms, and avoids some of the security issues due to maliciously crafted URI paths.
:::

### Simple form file upload

This example demonstrates how you can handle file uploads from an HTML form submission.

<<< @/docs/quick-guide/core/http/simpleformupload/simple_form_upload_server.js

You can run the server then open a browser and point it at [http://localhost:8080](http://localhost:8080).

::: tip
In practice you would probably also use Vert.x-Web for this rather than writing a server at this low level. Vert.x-Web
provides built in support for HTML forms, and avoids some of the security issues due to maliciously crafted URI paths.
:::

### Http request body upload

This example demonstrates an HTTP server receiving a request and pumping the request body to a file on disk without
ever storing the entire request body fully in memory.

<<< @/docs/quick-guide/core/http/upload/server.js

There's also a client which sends a request to the server and pumps a file from disk to the HTTP request body. The file
is uploaded successfully even if the file is very large (GigaBytes).

<<< @/docs/quick-guide/core/http/upload/client.js

### HTTP Server Sharing

A server that illustrates the round robin orchestrated by vert.x when several verticles are opening HTTP servers on the same port:

<<< @/docs/quick-guide/core/http/sharing/server.js

The `Server` deploys two instances of the `HttpServerVerticle` verticle.

<<< @/docs/quick-guide/core/http/sharing/http_server_verticle.js

You can run the server then open a browser and point it at [http://localhost:8080](http://localhost:8080). Requests will be handled by an instance after the other.

The `Client` illustrates the round robin by periodically requesting the server and displays the response content.

<<< @/docs/quick-guide/core/http/sharing/client.js

You can directly launch the `HTTPServerVerticle` using the `es4x run` command. Then you can set the number of instance you want:

```
es4x run http_server_verticle.js -instances 4
```

### WebSockets echo example

This example shows a Vert.x HTTP server which handles websockets connections. This example simply echoes back to the client
whatever it receives on the websocket.

<<< @/docs/quick-guide/core/http/websockets/server.js

There's also a client which connects to the server, sends some data and logs out what it receives.

<<< @/docs/quick-guide/core/http/websockets/client.js

Javascript WebSockets client example:

<<< @/docs/quick-guide/core/http/websockets/ws.html

You can run the server then open a browser and point it at [http://localhost:8080](http://localhost:8080).

::: tip
In practice you would probably use Vert.x-Web to build a web application that uses WebSockets
:::

## HTTP/2 examples

These examples demonstrate usage of HTTP/2 with Vert.x.

### Simple

A very simple HTTP/2 server which always responds with the same response:

<<< @/docs/quick-guide/core/http2/simple/server.js

You can run the server then open a browser and point it at [http://localhost:8080](http://localhost:8080).

And a simple HTTP/2 client which makes a request to the server.

<<< @/docs/quick-guide/core/http2/simple/client.js


### Push

This example shows HTTP/2 push.

The server pushes `script.js` along with `index.html`:

<<< @/docs/quick-guide/core/http2/push/server.js

You can run the server then open a browser and point it at [http://localhost:8080](http://localhost:8080).

And a client sets a push handler to be notified of the incoming server side pushes:

<<< @/docs/quick-guide/core/http2/push/client.js

### H2C

Like the simple server but using clear text, also known as _h2c_, without TLS:

<<< @/docs/quick-guide/core/http2/h2c/server.js

::: warning
This example won't work with browsers are they don't support h2c
:::

And a client to connect to the server:

<<< @/docs/quick-guide/core/http2/h2c/client.js

### Custom frames

HTTP/2 can be extended with custom frames, this example shows how to write custom frames:

<<< @/docs/quick-guide/core/http2/customframes/server.js

This examples shows how to receive custom frames:

<<< @/docs/quick-guide/core/http2/customframes/client.js

## Event bus examples

These examples demonstrate usage of the event bus in Vert.x

### Point to point

This example demonstrates point to point messaging between a receiver and a sender.

The receiver listens on an address on the event bus for incoming messages. When it receives a message it replies to it.

<<< @/docs/quick-guide/core/eventbus/pointtopoint/receiver.js

The sender sends a message to that address every second, when it receives a reply it logs it.

<<< @/docs/quick-guide/core/eventbus/pointtopoint/sender.js

You can run the sender and receiver at the command line.

At the command line you should run Sender and Receiver in different consoles using the `-cluster` flag:

```sh
es4x run eventbus/pointtopoint/receiver.js -cluster

es4x run eventbus/pointtopoint/sender.js -cluster
```

The `-cluster` flag allows different Vert.x instances on the network to cluster the event bus together into a single
event bus.

### Publish / Subscribe

This example demonstrates publish / subscribe messaging between a receivers and a sender. With pub/sub messaging
you can have multiple subscribers who all receive messages from publishers.

A receiver listens on an address on the event bus for incoming messages. When it receives a message it logs it.

<<< @/docs/quick-guide/core/eventbus/pubsub/receiver.js

The sender sends a message to that address every second, when it receives a reply it logs it.

<<< @/docs/quick-guide/core/eventbus/pubsub/sender.js


You can start as many senders or receivers as you like at the command line.

At the command line you should run Sender and Receiver in different consoles using the `-cluster` flag:

```sh
es4x run eventbus/pubsub/receiver.js -cluster

es4x run eventbus/pubsub/sender.js -cluster
```

The `-cluster` flag allows different Vert.x instances on the network to cluster the event bus together into a single
event bus.

### SSL

This example demonstrates point to point messaging between a receiver and a sender with a transport level encryption.

The receiver listens on an address on the event bus for incoming messages. When it receives a message it replies to it.

<<< @/docs/quick-guide/core/eventbus/ssl/receiver.js

The sender sends a message to that address every second, when it receives a reply it logs it.

<<< @/docs/quick-guide/core/eventbus/ssl/sender.js

You can run the Java sender and receiver in the command line.

At the command line you should run Sender and Receiver in different consoles using the `-cluster` flag:

```sh
es4x run  eventbus/ssl/receiver.js -cluster

es4x run eventbus/ssl/sender.js -cluster
```

The `-cluster` flag allows different Vert.x instances on the network to cluster the event bus together into a single
event bus.

## Future

es4x handler [Vert.x Future](https://vertx-web-site.github.io/docs/vertx-core/java/#_async_coordination)'s in a special
way. Behind the scenes, Vert.x Future's are enhanced to be a `Thenable` js object which means you can `await` on then
just like a js [Promise](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise).

You can still use the use the Vert.x API, for example, composition can be done as:

<<< @/docs/quick-guide/core/future/compose_example.js

## Verticle examples

These examples show verticles being deployed and undeployed.

### Deploy example

This example shows a verticle deploying another verticle in several different ways including:

* Deploying without waiting for it to deploy
* Deploying and waiting for it to deploy
* Passing configuration to another verticle during deploy
* Deploying more than one instance
* Deploying as a worker verticle
* Undeploying a verticle deployment explicitly

<<< @/docs/quick-guide/core/verticle/deploy/deploy_example.js

### Asynchronous deployment example

This is similar to the deployment example, but it shows how the start and stop of a verticle can be asynchronous. This
is useful if the verticle has some startup or cleanup to do that takes some time, and we wish to avoid blocking the
an event loop.

<<< @/docs/quick-guide/core/verticle/asyncstart/deploy_example.js

### Worker Verticle example

A simple example illustrating how worker verticle can be created and the thread switches when interacting with them. The worker verticle is not           System.out.println(Thread.currentThread());
ed in the event loop and so can do blocking operations.

<<< @/docs/quick-guide/core/verticle/worker/main_verticle.js
<<< @/docs/quick-guide/core/verticle/worker/worker_verticle.js

## High Availability

This example demonstrates the high availability feature of vert.x. When enabled, vert.x redeploys verticles to another
 node when the original node dies abruptly.

<<< @/docs/quick-guide/core/ha/server.js

To run this example, you need to have a working cluster. Configure Hazelcast and append the required `cluster-host`
to the commands if needed.

To see the HA (high-availability) behavior you need three terminals.

In the first terminal, go the the _core-examples` directory and launch:

```sh
es4x run io.vertx.example.core.ha.Server -ha
```

Open a browser to [http://localhost:8080](http://localhost:8080). You should see something like:

```
Happily served by 97284
```

Be displayed id is OS and JVM specific, so you may have something completely different.

In the second terminal, launch:

```
es4x bare
```

In the third terminal, display the list of the Java process and kill the first one (smaller pid):

```
> jps | grep Launcher
97297 Launcher
97284 Launcher
> kill -9 97284
```

In your browser, refresh the page, you should see a different id such as:

```
Happily served by 97297
```

The verticle has been migrated.
