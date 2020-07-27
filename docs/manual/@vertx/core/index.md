At the heart of Vert.x is a set of Java APIs that we call **Vert.x
Core**

[Repository](https://github.com/eclipse/vert.x).

Vert.x core provides functionality for things like:

  - Writing TCP clients and servers

  - Writing HTTP clients and servers including support for WebSockets

  - The Event bus

  - Shared data - local maps and clustered distributed maps

  - Periodic and delayed actions

  - Deploying and undeploying Verticles

  - Datagram Sockets

  - DNS client

  - File system access

  - High availability

  - Native transports

  - Clustering

The functionality in core is fairly low level - you won’t find stuff
like database access, authorisation or high level web functionality here
- that kind of stuff you’ll find in **Vert.x ext** (extensions).

Vert.x core is small and lightweight. You just use the parts you want.
It’s also entirely embeddable in your existing applications - we don’t
force you to structure your applications in a special way just so you
can use Vert.x.

You can use core from any of the other languages that Vert.x supports.
But here’a a cool bit - we don’t force you to use the Java API directly
from, say, JavaScript or Ruby - after all, different languages have
different conventions and idioms, and it would be odd to force Java
idioms on Ruby developers (for example). Instead, we automatically
generate an **idiomatic** equivalent of the core Java APIs for each
language.

From now on we’ll just use the word **core** to refer to Vert.x core.

If you are using Maven or Gradle, add the following dependency to the
*dependencies* section of your project descriptor to access the Vert.x
Core API:

  - Maven (in your `pom.xml`):

<!-- end list -->

``` xml
<dependency>
  <groupId>io.vertx</groupId>
  <artifactId>vertx-core</artifactId>
  <version>3.6.3</version>
</dependency>
```

  - Gradle (in your `build.gradle` file):

<!-- end list -->

``` groovy
dependencies {
  compile 'io.vertx:vertx-core:3.6.3'
}
```

Let’s discuss the different concepts and features in core.

# In the beginning there was Vert.x

You can’t do much in Vert.x-land unless you can communicate with a
{@link io.vertx.core.Vertx} object\!

It’s the control centre of Vert.x and is how you do pretty much
everything, including creating clients and servers, getting a reference
to the event bus, setting timers, as well as many other things.

So how do you get an instance?

If you’re embedding Vert.x then you simply create an instance as
follows:

``` $lang
{@link examples.CoreExamples#example1}
```

> **Note**
> 
> Most applications will only need a single Vert.x instance, but it’s
> possible to create multiple Vert.x instances if you require, for
> example, isolation between the event bus or different groups of
> servers and clients.

## Specifying options when creating a Vertx object

When creating a Vert.x object you can also specify options if the
defaults aren’t right for you:

``` $lang
{@link examples.CoreExamples#example2}
```

The {@link io.vertx.core.VertxOptions} object has many settings and
allows you to configure things like clustering, high availability, pool
sizes and various other settings.

## Creating a clustered Vert.x object

If you’re creating a **clustered Vert.x** (See the section on the [event
bus](#event_bus) for more information on clustering the event bus), then
you will normally use the asynchronous variant to create the Vertx
object.

This is because it usually takes some time (maybe a few seconds) for the
different Vert.x instances in a cluster to group together. During that
time, we don’t want to block the calling thread, so we give the result
to you asynchronously.

# Are you fluent?

You may have noticed that in the previous examples a **fluent** API was
used.

A fluent API is where multiple methods calls can be chained together.
For example:

``` js
request.response().putHeader("Content-Type", "text/plain").write("some text").end();
```

This is a common pattern throughout Vert.x APIs, so get used to it.

Chaining calls like this allows you to write code that’s a little bit
less verbose. Of course, if you don’t like the fluent approach **we
don’t force you** to do it that way, you can happily ignore it if you
prefer and write your code like this:

``` js
let response = request.response();
response.putHeader("Content-Type", "text/plain");
response.write("some text");
response.end();
```

# Don’t call us, we’ll call you.

The Vert.x APIs are largely *event driven*. This means that when things
happen in Vert.x that you are interested in, Vert.x will call you by
sending you events.

Some example events are:

  - a timer has fired

  - some data has arrived on a socket,

  - some data has been read from disk

  - an exception has occurred

  - an HTTP server has received a request

You handle events by providing *handlers* to the Vert.x APIs. For
example to receive a timer event every second you would do:

``` js
vertx.setPeriodic(1000, (id) => {
  // This handler will get called every second
  console.log("timer fired!");
});
```

Or to receive an HTTP request:

``` js
// Respond to each http request with "Hello World"
server.requestHandler((request) => {
  // This handler will be called every time an HTTP request is received at the server
  request.response().end("hello world!");
});
```

Some time later when Vert.x has an event to pass to your handler Vert.x
will call it **asynchronously**.

This leads us to some important concepts in Vert.x:

# Don’t block me\!

With very few exceptions (i.e. some file system operations ending in
'Sync'), none of the APIs in Vert.x block the calling thread.

If a result can be provided immediately, it will be returned
immediately, otherwise you will usually provide a handler to receive
events some time later.

Because none of the Vert.x APIs block threads that means you can use
Vert.x to handle a lot of concurrency using just a small number of
threads.

With a conventional blocking API the calling thread might block when:

  - Reading data from a socket

  - Writing data to disk

  - Sending a message to a recipient and waiting for a reply.

  - …​ Many other situations

In all the above cases, when your thread is waiting for a result it
can’t do anything else - it’s effectively useless.

This means that if you want a lot of concurrency using blocking APIs
then you need a lot of threads to prevent your application grinding to a
halt.

Threads have overhead in terms of the memory they require (e.g. for
their stack) and in context switching.

For the levels of concurrency required in many modern applications, a
blocking approach just doesn’t scale.

# Reactor and Multi-Reactor

We mentioned before that Vert.x APIs are event driven - Vert.x passes
events to handlers when they are available.

In most cases Vert.x calls your handlers using a thread called an
**event loop**.

As nothing in Vert.x or your application blocks, the event loop can
merrily run around delivering events to different handlers in succession
as they arrive.

Because nothing blocks, an event loop can potentially deliver huge
amounts of events in a short amount of time. For example a single event
loop can handle many thousands of HTTP requests very quickly.

We call this the [Reactor
Pattern](http://en.wikipedia.org/wiki/Reactor_pattern).

You may have heard of this before - for example Node.js implements this
pattern.

In a standard reactor implementation there is a **single event loop**
thread which runs around in a loop delivering all events to all handlers
as they arrive.

The trouble with a single thread is it can only run on a single core at
any one time, so if you want your single threaded reactor application
(e.g. your Node.js application) to scale over your multi-core server you
have to start up and manage many different processes.

Vert.x works differently here. Instead of a single event loop, each
Vertx instance maintains **several event loops**. By default we choose
the number based on the number of available cores on the machine, but
this can be overridden.

This means a single Vertx process can scale across your server, unlike
Node.js.

We call this pattern the **Multi-Reactor Pattern** to distinguish it
from the single threaded reactor pattern.

> **Note**
> 
> Even though a Vertx instance maintains multiple event loops, any
> particular handler will never be executed concurrently, and in most
> cases (with the exception of [worker verticles](#worker_verticles))
> will always be called using the **exact same event loop**.

# The Golden Rule - Don’t Block the Event Loop

We already know that the Vert.x APIs are non blocking and won’t block
the event loop, but that’s not much help if you block the event loop
**yourself** in a handler.

If you do that, then that event loop will not be able to do anything
else while it’s blocked. If you block all of the event loops in Vertx
instance then your application will grind to a complete halt\!

So don’t do it\! **You have been warned**.

Examples of blocking include:

  - Thread.sleep()

  - Waiting on a lock

  - Waiting on a mutex or monitor (e.g. synchronized section)

  - Doing a long lived database operation and waiting for a result

  - Doing a complex calculation that takes some significant time.

  - Spinning in a loop

If any of the above stop the event loop from doing anything else for a
**significant amount of time** then you should go immediately to the
naughty step, and await further instructions.

So…​ what is a **significant amount of time**?

How long is a piece of string? It really depends on your application and
the amount of concurrency you require.

If you have a single event loop, and you want to handle 10000 http
requests per second, then it’s clear that each request can’t take more
than 0.1 ms to process, so you can’t block for any more time than that.

**The maths is not hard and shall be left as an exercise for the
reader.**

If your application is not responsive it might be a sign that you are
blocking an event loop somewhere. To help you diagnose such issues,
Vert.x will automatically log warnings if it detects an event loop
hasn’t returned for some time. If you see warnings like these in your
logs, then you should investigate.

    Thread vertx-eventloop-thread-3 has been blocked for 20458 ms

Vert.x will also provide stack traces to pinpoint exactly where the
blocking is occurring.

If you want to turn off these warnings or change the settings, you can
do that in the `VertxOptions` object before creating the Vertx object.

# Running blocking code

In a perfect world, there will be no war or hunger, all APIs will be
written asynchronously and bunny rabbits will skip hand-in-hand with
baby lambs across sunny green meadows.

**But…​ the real world is not like that. (Have you watched the news
lately?)**

Fact is, many, if not most libraries, especially in the JVM ecosystem
have synchronous APIs and many of the methods are likely to block. A
good example is the JDBC API - it’s inherently synchronous, and no
matter how hard it tries, Vert.x cannot sprinkle magic pixie dust on it
to make it asynchronous.

We’re not going to rewrite everything to be asynchronous overnight so we
need to provide you a way to use "traditional" blocking APIs safely
within a Vert.x application.

As discussed before, you can’t call blocking operations directly from an
event loop, as that would prevent it from doing any other useful work.
So how can you do this?

It’s done by calling `executeBlocking` specifying both the blocking code
to execute and a result handler to be called back asynchronous when the
blocking code has been executed.

``` js
vertx.executeBlocking((promise) => {
  // Call some blocking API that takes a significant amount of time to return
  let result = someAPI.blockingMethod("hello");
  promise.complete(result);
}, (res) => {
  console.log("The result is: " + res.result());
});
```

> **Warning**
> 
> Blocking code should block for a reasonable amount of time (i.e no
> more than a few seconds). Long blocking operations or polling
> operations (i.e a thread that spin in a loop polling events in a
> blocking fashion) are precluded. When the blocking operation lasts
> more than the 10 seconds, a message will be printed on the console by
> the blocked thread checker. Long blocking operations should use a
> dedicated thread managed by the application, which can interact with
> verticles using the event-bus or `runOnContext`

By default, if executeBlocking is called several times from the same
context (e.g. the same verticle instance) then the different
executeBlocking are executed *serially* (i.e. one after another).

If you don’t care about ordering you can call `executeBlocking`
specifying `false` as the argument to `ordered`. In this case any
executeBlocking may be executed in parallel on the worker pool.

An alternative way to run blocking code is to use a [worker
verticle](#worker_verticles)

A worker verticle is always executed with a thread from the worker pool.

By default blocking code is executed on the Vert.x worker pool,
configured with `setWorkerPoolSize`.

Additional pools can be created for different purposes:

``` js
let executor = vertx.createSharedWorkerExecutor("my-worker-pool");
executor.executeBlocking((promise) => {
  // Call some blocking API that takes a significant amount of time to return
  let result = someAPI.blockingMethod("hello");
  promise.complete(result);
}, (res) => {
  console.log("The result is: " + res.result());
});
```

The worker executor must be closed when it’s not necessary anymore:

``` js
executor.close();
```

When several workers are created with the same name, they will share the
same pool. The worker pool is destroyed when all the worker executors
using it are closed.

When an executor is created in a Verticle, Vert.x will close it
automatically for you when the Verticle is undeployed.

Worker executors can be configured when created:

``` js
//
// 10 threads max
let poolSize = 10;

// 2 minutes
let maxExecuteTime = 2;
let maxExecuteTimeUnit = TimeUnit.MINUTES;

let executor = vertx.createSharedWorkerExecutor("my-worker-pool", poolSize, maxExecuteTime, maxExecuteTimeUnit);
```

> **Note**
> 
> the configuration is set when the worker pool is created

# Async coordination

Coordination of multiple asynchronous results can be achieved with
Vert.x `futures`. It supports concurrent composition (run several async
operations in parallel) and sequential composition (chain async
operations).

## Concurrent composition

`CompositeFuture.all` takes several futures arguments (up to 6) and
returns a future that is *succeeded* when all the futures are and
*failed* when at least one of the futures is failed:

``` js
import { Future } from "@vertx/core"
import { CompositeFuture } from "@vertx/core"
let httpServerFuture = Future.future((promise) => {
  httpServer.listen(promise);
});

let netServerFuture = Future.future((promise) => {
  netServer.listen(promise);
});

CompositeFuture.all(httpServerFuture, netServerFuture).onComplete((ar) => {
  if (ar.succeeded()) {
    // All servers started
  } else {
    // At least one server failed
  }
});
```

The operations run concurrently, the `Handler` attached to the returned
future is invoked upon completion of the composition. When one of the
operation fails (one of the passed future is marked as a failure), the
resulting future is marked as failed too. When all the operations
succeed, the resulting future is completed with a success.

Alternatively, you can pass a list (potentially empty) of futures:

``` js
import { CompositeFuture } from "@vertx/core"
CompositeFuture.all([future1, future2, future3]);
```

While the `all` composition *waits* until all futures are successful (or
one fails), the `any` composition *waits* for the first succeeded
future. `CompositeFuture.any` takes several futures arguments (up to 6)
and returns a future that is succeeded when one of the futures is, and
failed when all the futures are failed:

``` js
import { CompositeFuture } from "@vertx/core"
CompositeFuture.any(future1, future2).onComplete((ar) => {
  if (ar.succeeded()) {
    // At least one is succeeded
  } else {
    // All failed
  }
});
```

A list of futures can be used also:

``` js
import { CompositeFuture } from "@vertx/core"
CompositeFuture.any([f1, f2, f3]);
```

The `join` composition *waits* until all futures are completed, either
with a success or a failure. `CompositeFuture.join` takes several
futures arguments (up to 6) and returns a future that is succeeded when
all the futures are succeeded, and failed when all the futures are
completed and at least one of them is failed:

``` js
import { CompositeFuture } from "@vertx/core"
CompositeFuture.join(future1, future2, future3).onComplete((ar) => {
  if (ar.succeeded()) {
    // All succeeded
  } else {
    // All completed and at least one failed
  }
});
```

A list of futures can be used also:

``` js
import { CompositeFuture } from "@vertx/core"
CompositeFuture.join([future1, future2, future3]);
```

## Sequential composition

While `all` and `any` are implementing concurrent composition, `compose`
can be used for chaining futures (so sequential composition).

``` js
import { Future } from "@vertx/core"
import { Buffer } from "@vertx/core"

let fs = vertx.fileSystem();

let fut1 = Future.future((promise) => {
  fs.createFile("/foo", promise);
});

let startFuture = fut1.compose((v) => {
  // When the file is created (fut1), execute this:
  return Future.future((promise) => {
    fs.writeFile("/foo", Buffer.buffer(), promise);
  })
}).compose((v) => {
  // When the file is written (fut2), execute this:
  return Future.future((promise) => {
    fs.move("/foo", "/bar", promise);
  })
});
```

In this example, 3 operations are chained:

1.  a file is created (`fut1`)

2.  something is written in the file (`fut2`)

3.  the file is moved (`startFuture`)

When these 3 steps are successful, the final future (`startFuture`) is
succeeded. However, if one of the steps fails, the final future is
failed.

This example uses:

  - `compose`: when the current future completes, run the given
    function, that returns a future. When this returned future
    completes, it completes the composition.

  - `compose`: when the current future completes, run the given handler
    that completes the given `future` (next).

In this second case, the `Handler` should complete the `next` future to
report its success or failure.

## CompletionStage interoperability

The Vert.x `Future` API offers compatibility *from* and *to*
`CompletionStage` which is the JDK interface for composable asynchronous
operations.

We can go from a Vert.x `Future` to a `CompletionStage` using the
`toCompletionStage` method, as in:

``` js
import { Promise } from "@vertx/core"
let promise = Promise.promise();
vertx.createDnsClient().lookup("vertx.io", promise);
promise.future().toCompletionStage().whenComplete((ip, err) => {
  if ((err !== null && err !== undefined)) {
    console.error("Could not resolve vertx.io");
    err.printStackTrace();
  } else {
    console.log("vertx.io => " + ip);
  }
});
```

We can conversely go from a `CompletionStage` to Vert.x `Future` using
`Future.fromCompletionStage`. There are 2 variants:

1.  the first variant takes just a `CompletionStage` and calls the
    `Future` methods from the thread that resolves the `CompletionStage`
    instance, and

2.  the second variant takes an extra `Context` parameter to call the
    `Future` methods on a Vert.x context.

> **Important**
> 
> In most cases the variant with a `CompletionStage` and a `Context` is
> the one you will want to use to respect the Vert.x threading model,
> since Vert.x `Future` are more likely to be used with Vert.x code,
> libraries and clients.

Here is an example of going from a `CompletionStage` to a Vert.x
`Future` and dispatching on a context:

``` js
import { Future } from "@vertx/core"
Future.fromCompletionStage(completionStage, vertx.getOrCreateContext()).flatMap((str) => {
  let key = Java.type("java.util.UUID").randomUUID().toString();
  return storeInDb(key, str)
}).onSuccess((str) => {
  console.log("We have a result: " + str);
}).onFailure((err) => {
  console.error("We have a problem");
  err.printStackTrace();
});
```

# Verticles

Vert.x comes with a simple, scalable, *actor-like* deployment and
concurrency model out of the box that you can use to save you writing
your own.

**This model is entirely optional and Vert.x does not force you to
create your applications in this way if you don’t want to.**.

The model does not claim to be a strict actor-model implementation, but
it does share similarities especially with respect to concurrency,
scaling and deployment.

To use this model, you write your code as set of **verticles**.

Verticles are chunks of code that get deployed and run by Vert.x. A
Vert.x instance maintains N event loop threads (where N by default is
core\*2) by default. Verticles can be written in any of the languages
that Vert.x supports and a single application can include verticles
written in multiple languages.

You can think of a verticle as a bit like an actor in the [Actor
Model](http://en.wikipedia.org/wiki/Actor_model).

An application would typically be composed of many verticle instances
running in the same Vert.x instance at the same time. The different
verticle instances communicate with each other by sending messages on
the [event bus](#event_bus).

## Writing Verticles

Verticle classes must implement the {@link io.vertx.core.Verticle}
interface.

They can implement it directly if you like but usually it’s simpler to
extend the abstract class {@link io.vertx.core.AbstractVerticle}.

Here’s an example verticle:

    public class MyVerticle extends AbstractVerticle {
    
      // Called when verticle is deployed
      public void start() {
      }
    
      // Optional - called when verticle is undeployed
      public void stop() {
      }
    
    }

Normally you would override the start method like in the example above.

When Vert.x deploys the verticle it will call the start method, and when
the method has completed the verticle will be considered started.

You can also optionally override the stop method. This will be called by
Vert.x when the verticle is undeployed and when the method has completed
the verticle will be considered stopped.

## Asynchronous Verticle start and stop

Sometimes you want to do something in your verticle start-up which takes
some time and you don’t want the verticle to be considered deployed
until that happens. For example you might want to start an HTTP server
in the start method and propagate the asynchronous result of the server
`listen` method.

You can’t block waiting for the HTTP server to bind in your start method
as that would break the [Golden Rule](#golden_rule).

So how can you do this?

The way to do it is to implement the **asynchronous** start method. This
version of the method takes a Future as a parameter. When the method
returns the verticle will **not** be considered deployed.

Some time later, after you’ve done everything you need to do (e.g. start
the HTTP server), you can call complete on the Future (or fail) to
signal that you’re done.

Here’s an example:

    public class MyVerticle extends AbstractVerticle {
    
      private HttpServer server;
    
      public void start(Promise<Void> startPromise) {
        server = vertx.createHttpServer().requestHandler(req -> {
          req.response()
            .putHeader("content-type", "text/plain")
            .end("Hello from Vert.x!");
          });
    
        // Now bind the server:
        server.listen(8080, res -> {
          if (res.succeeded()) {
            startPromise.complete();
          } else {
            startPromise.fail(res.cause());
          }
        });
      }
    }

Similarly, there is an asynchronous version of the stop method too. You
use this if you want to do some verticle cleanup that takes some time.

    public class MyVerticle extends AbstractVerticle {
    
      public void start() {
        // Do something
      }
    
      public void stop(Future<Void> stopFuture) {
        obj.doSomethingThatTakesTime(res -> {
          if (res.succeeded()) {
            stopFuture.complete();
          } else {
            stopFuture.fail();
          }
        });
      }
    }

INFO: You don’t need to manually stop the HTTP server started by a
verticle, in the verticle’s stop method. Vert.x will automatically stop
any running server when the verticle is undeployed.

## Verticle Types

There are two different types of verticles:

  - Standard Verticles  
    These are the most common and useful type - they are always executed
    using an event loop thread. We’ll discuss this more in the next
    section.

  - Worker Verticles  
    These run using a thread from the worker pool. An instance is never
    executed concurrently by more than one thread.

## Standard verticles

Standard verticles are assigned an event loop thread when they are
created and the start method is called with that event loop. When you
call any other methods that takes a handler on a core API from an event
loop then Vert.x will guarantee that those handlers, when called, will
be executed on the same event loop.

This means we can guarantee that all the code in your verticle instance
is always executed on the same event loop (as long as you don’t create
your own threads and call it\!).

This means you can write all the code in your application as single
threaded and let Vert.x worry about the threading and scaling. No more
worrying about synchronized and volatile any more, and you also avoid
many other cases of race conditions and deadlock so prevalent when doing
hand-rolled 'traditional' multi-threaded application development.

## Worker verticles

A worker verticle is just like a standard verticle but it’s executed
using a thread from the Vert.x worker thread pool, rather than using an
event loop.

Worker verticles are designed for calling blocking code, as they won’t
block any event loops.

If you don’t want to use a worker verticle to run blocking code, you can
also run [inline blocking code](#blocking_code) directly while on an
event loop.

If you want to deploy a verticle as a worker verticle you do that with
`setWorker`.

``` js
let options = new DeploymentOptions()
  .setWorker(true);
vertx.deployVerticle("com.mycompany.MyOrderProcessorVerticle", options);
```

Worker verticle instances are never executed concurrently by Vert.x by
more than one thread, but can executed by different threads at different
times.

## Deploying verticles programmatically

You can deploy a verticle using one of the `deployVerticle` method,
specifying a verticle name or you can pass in a verticle instance you
have already created yourself.

> **Note**
> 
> Deploying Verticle **instances** is Java only.

``` java
let myVerticle = new (Java.type("examples.CoreExamples.MyVerticle"))();
vertx.deployVerticle(myVerticle);
```

You can also deploy verticles by specifying the verticle **name**.

The verticle name is used to look up the specific `VerticleFactory` that
will be used to instantiate the actual verticle instance(s).

Different verticle factories are available for instantiating verticles
in different languages and for various other reasons such as loading
services and getting verticles from Maven at run-time.

This allows you to deploy verticles written in any language from any
other language that Vert.x supports.

Here’s an example of deploying some different types of verticles:

``` js
// Deploy a Java verticle - the name is the fully qualified class name of the verticle class
vertx.deployVerticle("com.mycompany.MyOrderProcessorVerticle");

// Deploy a JavaScript verticle
vertx.deployVerticle("verticles/myverticle.js");

// Deploy a Ruby verticle verticle
vertx.deployVerticle("verticles/my_verticle.rb");
```

## Rules for mapping a verticle name to a verticle factory

When deploying verticle(s) using a name, the name is used to select the
actual verticle factory that will instantiate the verticle(s).

Verticle names can have a prefix - which is a string followed by a
colon, which if present will be used to look-up the factory, e.g.

js:foo.js // Use the JavaScript verticle factory
groovy:com.mycompany.SomeGroovyCompiledVerticle // Use the Groovy
verticle factory service:com.mycompany:myorderservice // Uses the
service verticle factory

If no prefix is present, Vert.x will look for a suffix and use that to
lookup the factory, e.g.

foo.js // Will also use the JavaScript verticle factory
SomeScript.groovy // Will use the Groovy verticle factory

If no prefix or suffix is present, Vert.x will assume it’s a Java fully
qualified class name (FQCN) and try and instantiate that.

## How are Verticle Factories located?

Most Verticle factories are loaded from the classpath and registered at
Vert.x startup.

You can also programmatically register and unregister verticle factories
using `registerVerticleFactory` and `unregisterVerticleFactory` if you
wish.

## Waiting for deployment to complete

Verticle deployment is asynchronous and may complete some time after the
call to deploy has returned.

If you want to be notified when deployment is complete you can deploy
specifying a completion handler:

``` js
vertx.deployVerticle("com.mycompany.MyOrderProcessorVerticle", (res) => {
  if (res.succeeded()) {
    console.log("Deployment id is: " + res.result());
  } else {
    console.log("Deployment failed!");
  }
});
```

The completion handler will be passed a result containing the deployment
ID string, if deployment succeeded.

This deployment ID can be used later if you want to undeploy the
deployment.

## Undeploying verticle deployments

Deployments can be undeployed with `undeploy`.

Un-deployment is itself asynchronous so if you want to be notified when
un-deployment is complete you can deploy specifying a completion
handler:

``` js
vertx.undeploy(deploymentID, (res) => {
  if (res.succeeded()) {
    console.log("Undeployed ok");
  } else {
    console.log("Undeploy failed!");
  }
});
```

## Specifying number of verticle instances

When deploying a verticle using a verticle name, you can specify the
number of verticle instances that you want to deploy:

``` js
let options = new DeploymentOptions()
  .setInstances(16);
vertx.deployVerticle("com.mycompany.MyOrderProcessorVerticle", options);
```

This is useful for scaling easily across multiple cores. For example you
might have a web-server verticle to deploy and multiple cores on your
machine, so you want to deploy multiple instances to utilise all the
cores.

## Passing configuration to a verticle

Configuration in the form of JSON can be passed to a verticle at
deployment time:

``` $lang
{@link examples.CoreExamples#example13}
```

This configuration is then available via the {@link
io.vertx.core.Context} object or directly using the {@link
io.vertx.core.AbstractVerticle\#config()} method. The configuration is
returned as a JSON object so you can retrieve data as follows:

``` $lang
{@link examples.ConfigurableVerticleExamples#start()}
```

## Accessing environment variables in a Verticle

Environment variables and system properties are accessible using the
Java API:

``` $lang
{@link examples.CoreExamples#systemAndEnvProperties()}
```

## Verticle Isolation Groups

By default, Vert.x has a *flat classpath*. I.e, when Vert.x deploys
verticles it does so with the current classloader - it doesn’t create a
new one. In the majority of cases this is the simplest, clearest, and
sanest thing to do.

However, in some cases you may want to deploy a verticle so the classes
of that verticle are isolated from others in your application.

This might be the case, for example, if you want to deploy two different
versions of a verticle with the same class name in the same Vert.x
instance, or if you have two different verticles which use different
versions of the same jar library.

When using an isolation group you provide a list of the class names that
you want isolated using `setIsolatedClasses`- an entry can be a fully
qualified classname such as `com.mycompany.myproject.engine.MyClass` or
it can be a wildcard which will match any classes in a package and any
sub-packages, e.g. `com.mycompany.myproject.*` would match any classes
in the package `com.mycompany.myproject` or any sub-packages.

Please note that *only* the classes that match will be isolated - any
other classes will be loaded by the current class loader.

Extra classpath entries can also be provided with `setExtraClasspath` so
if you want to load classes or resources that aren’t already present on
the main classpath you can add this.

> **Warning**
> 
> Use this feature with caution. Class-loaders can be a can of worms,
> and can make debugging difficult, amongst other things.

Here’s an example of using an isolation group to isolate a verticle
deployment.

``` js
let options = new DeploymentOptions()
  .setIsolationGroup("mygroup");
options.isolatedClasses = ["com.mycompany.myverticle.*", "com.mycompany.somepkg.SomeClass", "org.somelibrary.*"];
vertx.deployVerticle("com.mycompany.myverticle.VerticleClass", options);
```

## High Availability

Verticles can be deployed with High Availability (HA) enabled. In that
context, when a verticle is deployed on a vert.x instance that dies
abruptly, the verticle is redeployed on another vert.x instance from the
cluster.

To run an verticle with the high availability enabled, just append the
`-ha` switch:

    vertx run my-verticle.js -ha

When enabling high availability, no need to add `-cluster`.

More details about the high availability feature and configuration in
the [High Availability and Fail-Over](#_high_availability_and_fail_over)
section.

## Running Verticles from the command line

You can use Vert.x directly in your Maven or Gradle projects in the
normal way by adding a dependency to the Vert.x core library and hacking
from there.

However you can also run Vert.x verticles directly from the command line
if you wish.

To do this you need to download and install a Vert.x distribution, and
add the `bin` directory of the installation to your `PATH` environment
variable. Also make sure you have a Java 8 JDK on your `PATH`.

> **Note**
> 
> The JDK is required to support on the fly compilation of Java code.

You can now run verticles by using the `vertx run` command. Here are
some examples:

    # Run a JavaScript verticle
    vertx run my_verticle.js
    
    # Run a Ruby verticle
    vertx run a_n_other_verticle.rb
    
    # Run a Groovy script verticle, clustered
    vertx run FooVerticle.groovy -cluster

You can even run Java source verticles without compiling them first\!

    vertx run SomeJavaSourceFile.java

Vert.x will compile the Java source file on the fly before running it.
This is really useful for quickly prototyping verticles and great for
demos. No need to set-up a Maven or Gradle build first to get going\!

For full information on the various options available when executing
`vertx` on the command line, type `vertx` at the command line.

## Causing Vert.x to exit

Threads maintained by Vert.x instances are not daemon threads so they
will prevent the JVM from exiting.

If you are embedding Vert.x and you have finished with it, you can call
`close` to close it down.

This will shut-down all internal thread pools and close other resources,
and will allow the JVM to exit.

## The Context object

When Vert.x provides an event to a handler or calls the start or stop
methods of a `Verticle`, the execution is associated with a `Context`.
Usually a context is an **event-loop context** and is tied to a specific
event loop thread. So executions for that context always occur on that
exact same event loop thread. In the case of worker verticles and
running inline blocking code a worker context will be associated with
the execution which will use a thread from the worker thread pool.

To retrieve the context, use the `getOrCreateContext` method:

``` js
let context = vertx.getOrCreateContext();
```

If the current thread has a context associated with it, it reuses the
context object. If not a new instance of context is created. You can
test the *type* of context you have retrieved:

``` js
import { Context } from "@vertx/core"
let context = vertx.getOrCreateContext();
if (context.isEventLoopContext()) {
  console.log("Context attached to Event Loop");
} else if (context.isWorkerContext()) {
  console.log("Context attached to Worker Thread");
} else if (context.isMultiThreadedWorkerContext()) {
  console.log("Context attached to Worker Thread - multi threaded worker");
} else if (!Context.isOnVertxThread()) {
  console.log("Context not attached to a thread managed by vert.x");
}
```

When you have retrieved the context object, you can run code in this
context asynchronously. In other words, you submit a task that will be
eventually run in the same context, but later:

``` js
vertx.getOrCreateContext().runOnContext((v) => {
  console.log("This will be executed asynchronously in the same context");
});
```

When several handlers run in the same context, they may want to share
data. The context object offers methods to store and retrieve data
shared in the context. For instance, it lets you pass data to some
action run with `runOnContext`:

``` js
let context = vertx.getOrCreateContext();
context.put("data", "hello");
context.runOnContext((v) => {
  let hello = context.get("data");
});
```

The context object also let you access verticle configuration using the
`config` method. Check the [Passing configuration to a
verticle](#_passing_configuration_to_a_verticle) section for more
details about this configuration.

## Executing periodic and delayed actions

It’s very common in Vert.x to want to perform an action after a delay,
or periodically.

In standard verticles you can’t just make the thread sleep to introduce
a delay, as that will block the event loop thread.

Instead you use Vert.x timers. Timers can be **one-shot** or
**periodic**. We’ll discuss both

### One-shot Timers

A one shot timer calls an event handler after a certain delay, expressed
in milliseconds.

To set a timer to fire once you use `setTimer` method passing in the
delay and a handler

``` js
let timerID = vertx.setTimer(1000, (id) => {
  console.log("And one second later this is printed");
});

console.log("First this is printed");
```

The return value is a unique timer id which can later be used to cancel
the timer. The handler is also passed the timer id.

### Periodic Timers

You can also set a timer to fire periodically by using `setPeriodic`.

There will be an initial delay equal to the period.

The return value of `setPeriodic` is a unique timer id (long). This can
be later used if the timer needs to be cancelled.

The argument passed into the timer event handler is also the unique
timer id:

Keep in mind that the timer will fire on a periodic basis. If your
periodic treatment takes a long amount of time to proceed, your timer
events could run continuously or even worse : stack up.

In this case, you should consider using `setTimer` instead. Once your
treatment has finished, you can set the next timer.

``` js
let timerID = vertx.setPeriodic(1000, (id) => {
  console.log("And every second this is printed");
});

console.log("First this is printed");
```

### Cancelling timers

To cancel a periodic timer, call `cancelTimer` specifying the timer id.
For example:

``` js
vertx.cancelTimer(timerID);
```

### Automatic clean-up in verticles

If you’re creating timers from inside verticles, those timers will be
automatically closed when the verticle is undeployed.

## Verticle worker pool

Verticles use the Vert.x worker pool for executing blocking actions, i.e
`executeBlocking` or worker verticle.

A different worker pool can be specified in deployment options:

``` js
vertx.deployVerticle("the-verticle", new DeploymentOptions()
  .setWorkerPoolName("the-specific-pool"));
```

# The Event Bus

The `event bus` is the **nervous system** of Vert.x.

There is a single event bus instance for every Vert.x instance and it is
obtained using the method `eventBus`.

The event bus allows different parts of your application to communicate
with each other, irrespective of what language they are written in, and
whether they’re in the same Vert.x instance, or in a different Vert.x
instance.

It can even be bridged to allow client-side JavaScript running in a
browser to communicate on the same event bus.

The event bus forms a distributed peer-to-peer messaging system spanning
multiple server nodes and multiple browsers.

The event bus supports publish/subscribe, point-to-point, and
request-response messaging.

The event bus API is very simple. It basically involves registering
handlers, unregistering handlers and sending and publishing messages.

First some theory:

## The Theory

### Addressing

Messages are sent on the event bus to an **address**.

Vert.x doesn’t bother with any fancy addressing schemes. In Vert.x an
address is simply a string. Any string is valid. However it is wise to
use some kind of scheme, *e.g.* using periods to demarcate a namespace.

Some examples of valid addresses are europe.news.feed1,
acme.games.pacman, sausages, and X.

### Handlers

Messages are received by handlers. You register a handler at an address.

Many different handlers can be registered at the same address.

A single handler can be registered at many different addresses.

### Publish / subscribe messaging

The event bus supports **publishing** messages.

Messages are published to an address. Publishing means delivering the
message to all handlers that are registered at that address.

This is the familiar **publish/subscribe** messaging pattern.

### Point-to-point and Request-Response messaging

The event bus also supports **point-to-point** messaging.

Messages are sent to an address. Vert.x will then route them to just one
of the handlers registered at that address.

If there is more than one handler registered at the address, one will be
chosen using a non-strict round-robin algorithm.

With point-to-point messaging, an optional reply handler can be
specified when sending the message.

When a message is received by a recipient, and has been handled, the
recipient can optionally decide to reply to the message. If they do so,
the reply handler will be called.

When the reply is received back by the sender, it too can be replied to.
This can be repeated *ad infinitum*, and allows a dialog to be set up
between two different verticles.

This is a common messaging pattern called the **request-response**
pattern.

### Best-effort delivery

Vert.x does its best to deliver messages and won’t consciously throw
them away. This is called **best-effort** delivery.

However, in case of failure of all or parts of the event bus, there is a
possibility messages might be lost.

If your application cares about lost messages, you should code your
handlers to be idempotent, and your senders to retry after recovery.

### Types of messages

Out of the box Vert.x allows any primitive/simple type, String, or
`buffers` to be sent as messages.

However it’s a convention and common practice in Vert.x to send messages
as [JSON](http://json.org/)

JSON is very easy to create, read and parse in all the languages that
Vert.x supports so it has become a kind of *lingua franca* for Vert.x.

However you are not forced to use JSON if you don’t want to.

The event bus is very flexible and also supports sending arbitrary
objects over the event bus. You can do this by defining a `codec` for
the objects you want to send.

## The Event Bus API

Let’s jump into the API.

### Getting the event bus

You get a reference to the event bus as follows:

``` js
let eb = vertx.eventBus();
```

There is a single instance of the event bus per Vert.x instance.

### Registering Handlers

This simplest way to register a handler is using `consumer`. Here’s an
example:

``` js
let eb = vertx.eventBus();

eb.consumer("news.uk.sport", (message) => {
  console.log("I have received a message: " + message.body());
});
```

When a message arrives for your handler, your handler will be called,
passing in the `message`.

The object returned from call to consumer() is an instance of
`MessageConsumer`.

This object can subsequently be used to unregister the handler, or use
the handler as a stream.

Alternatively you can use `consumer` to return a MessageConsumer with no
handler set, and then set the handler on that. For example:

``` js
let eb = vertx.eventBus();

let consumer = eb.consumer("news.uk.sport");
consumer.handler((message) => {
  console.log("I have received a message: " + message.body());
});
```

When registering a handler on a clustered event bus, it can take some
time for the registration to reach all nodes of the cluster.

If you want to be notified when this has completed, you can register a
`completion handler` on the MessageConsumer object.

``` js
consumer.completionHandler((res) => {
  if (res.succeeded()) {
    console.log("The handler registration has reached all nodes");
  } else {
    console.log("Registration failed!");
  }
});
```

### Un-registering Handlers

To unregister a handler, call `unregister`.

If you are on a clustered event bus, un-registering can take some time
to propagate across the nodes. If you want to be notified when this is
complete, use `unregister`.

``` js
consumer.unregister((res) => {
  if (res.succeeded()) {
    console.log("The handler un-registration has reached all nodes");
  } else {
    console.log("Un-registration failed!");
  }
});
```

### Publishing messages

Publishing a message is simple. Just use `publish` specifying the
address to publish it to.

``` js
eventBus.publish("news.uk.sport", "Yay! Someone kicked a ball");
```

That message will then be delivered to all handlers registered against
the address news.uk.sport.

### Sending messages

Sending a message will result in only one handler registered at the
address receiving the message. This is the point-to-point messaging
pattern. The handler is chosen in a non-strict round-robin fashion.

You can send a message with `send`.

``` js
eventBus.send("news.uk.sport", "Yay! Someone kicked a ball");
```

### Setting headers on messages

Messages sent over the event bus can also contain headers. This can be
specified by providing a {@link io.vertx.core.eventbus.DeliveryOptions}
when sending or publishing:

``` $lang
{@link docoverride.eventbus.Examples#headers(io.vertx.core.eventbus.EventBus)}
```

### Message ordering

Vert.x will deliver messages to any particular handler in the same order
they were sent from any particular sender.

### The Message object

The object you receive in a message handler is a `Message`.

The `body` of the message corresponds to the object that was sent or
published.

The headers of the message are available with `headers`.

### Acknowledging messages / sending replies

When using `send` the event bus attempts to deliver the message to a
`MessageConsumer` registered with the event bus.

In some cases it’s useful for the sender to know when the consumer has
received the message and "processed" it using **request-response**
pattern.

To acknowledge that the message has been processed, the consumer can
reply to the message by calling `reply`.

When this happens it causes a reply to be sent back to the sender and
the reply handler is invoked with the reply.

An example will make this clear:

The receiver:

``` js
let consumer = eventBus.consumer("news.uk.sport");
consumer.handler((message) => {
  console.log("I have received a message: " + message.body());
  message.reply("how interesting!");
});
```

The sender:

``` js
eventBus.request("news.uk.sport", "Yay! Someone kicked a ball across a patch of grass", (ar) => {
  if (ar.succeeded()) {
    console.log("Received reply: " + ar.result().body());
  }
});
```

The reply can contain a message body which can contain useful
information.

What the "processing" actually means is application-defined and depends
entirely on what the message consumer does and is not something that the
Vert.x event bus itself knows or cares about.

Some examples:

  - A simple message consumer which implements a service which returns
    the time of the day would acknowledge with a message containing the
    time of day in the reply body

  - A message consumer which implements a persistent queue, might
    acknowledge with `true` if the message was successfully persisted in
    storage, or `false` if not.

  - A message consumer which processes an order might acknowledge with
    `true` when the order has been successfully processed so it can be
    deleted from the database

### Sending with timeouts

When sending a message with a reply handler, you can specify a timeout
in the `DeliveryOptions`.

If a reply is not received within that time, the reply handler will be
called with a failure.

The default timeout is 30 seconds.

### Send Failures

Message sends can fail for other reasons, including:

  - There are no handlers available to send the message to

  - The recipient has explicitly failed the message using `fail`

In all cases, the reply handler will be called with the specific
failure.

### Message Codecs

You can send any object you like across the event bus if you define and
register a {@link io.vertx.core.eventbus.MessageCodec message codec} for
it.

Message codecs have a name and you specify that name in the {@link
io.vertx.core.eventbus.DeliveryOptions} when sending or publishing the
message:

``` java
{@link docoverride.eventbus.Examples#example10}
```

If you always want the same codec to be used for a particular type then
you can register a default codec for it, then you don’t have to specify
the codec on each send in the delivery options:

``` java
{@link docoverride.eventbus.Examples#example11}
```

You unregister a message codec with {@link
io.vertx.core.eventbus.EventBus\#unregisterCodec}.

Message codecs don’t always have to encode and decode as the same type.
For example you can write a codec that allows a MyPOJO class to be sent,
but when that message is sent to a handler it arrives as a MyOtherPOJO
class.

### Clustered Event Bus

The event bus doesn’t just exist in a single Vert.x instance. By
clustering different Vert.x instances together on your network they can
form a single, distributed event bus.

### Clustering programmatically

If you’re creating your Vert.x instance programmatically you get a
clustered event bus by configuring the Vert.x instance as clustered;

``` js
import { Vertx } from "@vertx/core"
let options = new VertxOptions();
Vertx.clusteredVertx(options, (res) => {
  if (res.succeeded()) {
    let vertx = res.result();
    let eventBus = vertx.eventBus();
    console.log("We now have a clustered event bus: " + eventBus);
  } else {
    console.log("Failed: " + res.cause());
  }
});
```

You should also make sure you have a `ClusterManager` implementation on
your classpath, for example the Hazelcast cluster manager.

### Clustering on the command line

You can run Vert.x clustered on the command line with

    vertx run my-verticle.js -cluster

## Automatic clean-up in verticles

If you’re registering event bus handlers from inside verticles, those
handlers will be automatically unregistered when the verticle is
undeployed.

# Configuring the event bus

The event bus can be configured. It is particularly useful when the
event bus is clustered. Under the hood the event bus uses TCP
connections to send and receive messages, so the {@link
io.vertx.core.eventbus.EventBusOptions} let you configure all aspects of
these TCP connections. As the event bus acts as a server and client, the
configuration is close to {@link io.vertx.core.net.NetClientOptions} and
{@link io.vertx.core.net.NetServerOptions}.

``` $lang
{@link examples.EventBusExamples#example13}
```

The previous snippet depicts how you can use SSL connections for the
event bus, instead of plain TCP connections.

**WARNING**: to enforce the security in clustered mode, you **must**
configure the cluster manager to use encryption or enforce security.
Refer to the documentation of the cluster manager for further details.

The event bus configuration needs to be consistent in all the cluster
nodes.

The {@link io.vertx.core.eventbus.EventBusOptions} also lets you specify
whether or not the event bus is clustered, the port and host.

When used in containers, you can also configure the public host and
port:

``` $lang
{@link examples.EventBusExamples#example14}
```

# JSON

Unlike some other languages, Java does not have first class support for
[JSON](http://json.org/) so we provide two classes to make handling JSON
in your Vert.x applications a bit easier.

## JSON objects

The {@link io.vertx.core.json.JsonObject} class represents JSON objects.

A JSON object is basically just a map which has string keys and values
can be of one of the JSON supported types (string, number, boolean).

JSON objects also support null values.

### Creating JSON objects

Empty JSON objects can be created with the default constructor.

You can create a JSON object from a string JSON representation as
follows:

``` java
{@link docoverride.json.Examples#example0_1}
```

You can create a JSON object from a map as follows:

``` java
{@link docoverride.json.Examples#exampleCreateFromMap}
```

### Putting entries into a JSON object

Use the {@link io.vertx.core.json.JsonObject\#put} methods to put values
into the JSON object.

The method invocations can be chained because of the fluent API:

``` java
{@link docoverride.json.Examples#example1}
```

### Getting values from a JSON object

You get values from a JSON object using the `getXXX` methods, for
example:

``` java
{@link docoverride.json.Examples#example2}
```

### Mapping between JSON objects and Java objects

You can create a JSON object from the fields of a Java object as
follows:

You can instantiate a Java object and populate its fields from a JSON
object as follows:

``` java
{@link docoverride.json.Examples#mapToPojo}
```

Note that both of the above mapping directions use Jackson’s
`ObjectMapper#convertValue()` to perform the mapping. See the Jackson
documentation for information on the impact of field and constructor
visibility, caveats on serialization and deserialization across object
references, etc.

However, in the simplest case, both `mapFrom` and `mapTo` should succeed
if all fields of the Java class are public (or have public
getters/setters), and if there is a public default constructor (or no
defined constructors).

Referenced objects will be transitively serialized/deserialized to/from
nested JSON objects as long as the object graph is acyclic.

### Encoding a JSON object to a String

You use {@link io.vertx.core.json.JsonObject\#encode} to encode the
object to a String form.

## JSON arrays

The {@link io.vertx.core.json.JsonArray} class represents JSON arrays.

A JSON array is a sequence of values (string, number, boolean).

JSON arrays can also contain null values.

### Creating JSON arrays

Empty JSON arrays can be created with the default constructor.

You can create a JSON array from a string JSON representation as
follows:

``` java
{@link docoverride.json.Examples#example0_2}
```

### Adding entries into a JSON array

You add entries to a JSON array using the {@link
io.vertx.core.json.JsonArray\#add} methods.

``` java
{@link docoverride.json.Examples#example3}
```

### Getting values from a JSON array

You get values from a JSON array using the `getXXX` methods, for
example:

``` java
{@link docoverride.json.Examples#example4}
```

### Encoding a JSON array to a String

You use {@link io.vertx.core.json.JsonArray\#encode} to encode the array
to a String form.

### Creating arbitrary JSON

Creating JSON object and array assumes you are using valid string
representation.

When you are unsure of the string validity then you should use instead
`{@link io.vertx.core.json.Json#decodeValue(java.lang.String)}`

``` java
{@link docoverride.json.Examples#example5}
```

# Json Pointers

Vert.x provides an implementation of [Json Pointers from
RFC6901](https://tools.ietf.org/html/rfc6901). You can use pointers both
for querying and for writing. You can build your `JsonPointer` using a
string, a URI or manually appending paths:

``` java
import { JsonPointer } from "@vertx/core"
// Build a pointer from a string
let pointer1 = JsonPointer.from("/hello/world");
// Build a pointer manually
let pointer2 = JsonPointer.create().append("hello").append("world");
```

After instantiating your pointer, use `queryJson` to query a JSON value.
You can update a Json Value using `writeJson`:

``` java
// Query a JsonObject
let result1 = objectPointer.queryJson(jsonObject);
// Query a JsonArray
let result2 = arrayPointer.queryJson(jsonArray);
// Write starting from a JsonObject
objectPointer.writeJson(jsonObject, "new element");
// Write starting from a JsonObject
arrayPointer.writeJson(jsonArray, "new element");
```

You can use Vert.x Json Pointer with any object model by providing a
custom implementation of `JsonPointerIterator`

# Buffers

Most data is shuffled around inside Vert.x using buffers.

A buffer is a sequence of zero or more bytes that can read from or
written to and which expands automatically as necessary to accommodate
any bytes written to it. You can perhaps think of a buffer as smart byte
array.

## Creating buffers

Buffers can create by using one of the static `Buffer.buffer` methods.

Buffers can be initialised from strings or byte arrays, or empty buffers
can be created.

Here are some examples of creating buffers:

Create a new empty buffer:

``` js
import { Buffer } from "@vertx/core"
let buff = Buffer.buffer();
```

Create a buffer from a String. The String will be encoded in the buffer
using UTF-8.

``` js
import { Buffer } from "@vertx/core"
let buff = Buffer.buffer("some string");
```

Create a buffer from a String: The String will be encoded using the
specified encoding, e.g:

``` js
import { Buffer } from "@vertx/core"
let buff = Buffer.buffer("some string", "UTF-16");
```

Create a buffer from a byte\[\]

``` java
{@link docoverride.buffer.Examples#example4}
```

Create a buffer with an initial size hint. If you know your buffer will
have a certain amount of data written to it you can create the buffer
and specify this size. This makes the buffer initially allocate that
much memory and is more efficient than the buffer automatically resizing
multiple times as data is written to it.

Note that buffers created this way **are empty**. It does not create a
buffer filled with zeros up to the specified size.

``` js
import { Buffer } from "@vertx/core"
let buff = Buffer.buffer(10000);
```

## Writing to a Buffer

There are two ways to write to a buffer: appending, and random access.
In either case buffers will always expand automatically to encompass the
bytes. It’s not possible to get an `IndexOutOfBoundsException` with a
buffer.

### Appending to a Buffer

To append to a buffer, you use the `appendXXX` methods. Append methods
exist for appending various different types.

The return value of the `appendXXX` methods is the buffer itself, so
these can be chained:

``` js
import { Buffer } from "@vertx/core"
let buff = Buffer.buffer();

buff.appendInt(123).appendString("hello\n");

socket.write(buff);
```

### Random access buffer writes

You can also write into the buffer at a specific index, by using the
`setXXX` methods. Set methods exist for various different data types.
All the set methods take an index as the first argument - this
represents the position in the buffer where to start writing the data.

The buffer will always expand as necessary to accommodate the data.

``` js
import { Buffer } from "@vertx/core"
let buff = Buffer.buffer();

buff.setInt(1000, 123);
buff.setString(0, "hello");
```

## Reading from a Buffer

Data is read from a buffer using the `getXXX` methods. Get methods exist
for various datatypes. The first argument to these methods is an index
in the buffer from where to get the data.

``` js
import { Buffer } from "@vertx/core"
let buff = Buffer.buffer();
for (let i = 0; i < buff.length(); 4) {
  console.log("int value at " + i + " is " + buff.getInt(i));
}
```

## Working with unsigned numbers

Unsigned numbers can be read from or appended/set to a buffer with the
`getUnsignedXXX`, `appendUnsignedXXX` and `setUnsignedXXX` methods. This
is useful when implementing a codec for a network protocol optimized to
minimize bandwidth consumption.

In the following example, value 200 is set at specified position with
just one byte:

``` js
import { Buffer } from "@vertx/core"
let buff = Buffer.buffer(128);
let pos = 15;
buff.setUnsignedByte(pos, 200);
console.log(buff.getUnsignedByte(pos));
```

The console shows '200'.

## Buffer length

Use `length` to obtain the length of the buffer. The length of a buffer
is the index of the byte in the buffer with the largest index + 1.

## Copying buffers

Use `copy` to make a copy of the buffer

## Slicing buffers

A sliced buffer is a new buffer which backs onto the original buffer,
i.e. it does not copy the underlying data. Use `slice` to create a
sliced buffers

## Buffer re-use

After writing a buffer to a socket or other similar place, they cannot
be re-used.

# Writing TCP servers and clients

Vert.x allows you to easily write non blocking TCP clients and servers.

## Creating a TCP server

The simplest way to create a TCP server, using all default options is as
follows:

``` js
let server = vertx.createNetServer();
```

## Configuring a TCP server

If you don’t want the default, a server can be configured by passing in
a `NetServerOptions` instance when creating it:

``` js
let options = new NetServerOptions()
  .setPort(4321);
let server = vertx.createNetServer(options);
```

## Start the Server Listening

To tell the server to listen for incoming requests you use one of the
`listen` alternatives.

To tell the server to listen at the host and port as specified in the
options:

``` js
let server = vertx.createNetServer();
server.listen();
```

Or to specify the host and port in the call to listen, ignoring what is
configured in the options:

``` js
let server = vertx.createNetServer();
server.listen(1234, "localhost");
```

The default host is `0.0.0.0` which means 'listen on all available
addresses' and the default port is `0`, which is a special value that
instructs the server to find a random unused local port and use that.

The actual bind is asynchronous so the server might not actually be
listening until some time **after** the call to listen has returned.

If you want to be notified when the server is actually listening you can
provide a handler to the `listen` call. For example:

``` js
let server = vertx.createNetServer();
server.listen(1234, "localhost", (res) => {
  if (res.succeeded()) {
    console.log("Server is now listening!");
  } else {
    console.log("Failed to bind!");
  }
});
```

## Listening on a random port

If `0` is used as the listening port, the server will find an unused
random port to listen on.

To find out the real port the server is listening on you can call
`actualPort`.

``` js
let server = vertx.createNetServer();
server.listen(0, "localhost", (res) => {
  if (res.succeeded()) {
    console.log("Server is now listening on actual port: " + server.actualPort());
  } else {
    console.log("Failed to bind!");
  }
});
```

## Getting notified of incoming connections

To be notified when a connection is made you need to set a
`connectHandler`:

``` js
let server = vertx.createNetServer();
server.connectHandler((socket) => {
  // Handle the connection in here
});
```

When a connection is made the handler will be called with an instance of
`NetSocket`.

This is a socket-like interface to the actual connection, and allows you
to read and write data as well as do various other things like close the
socket.

## Reading data from the socket

To read data from the socket you set the `handler` on the socket.

This handler will be called with an instance of `Buffer` every time data
is received on the socket.

``` js
let server = vertx.createNetServer();
server.connectHandler((socket) => {
  socket.handler((buffer) => {
    console.log("I received some bytes: " + buffer.length());
  });
});
```

## Writing data to a socket

You write to a socket using one of `write`.

``` js
import { Buffer } from "@vertx/core"

// Write a buffer
let buffer = Buffer.buffer().appendFloat(12.34).appendInt(123);
socket.write(buffer);

// Write a string in UTF-8 encoding
socket.write("some data");

// Write a string using the specified encoding
socket.write("some data", "UTF-16");
```

Write operations are asynchronous and may not occur until some time
after the call to write has returned.

## Closed handler

If you want to be notified when a socket is closed, you can set a
`closeHandler` on it:

``` js
socket.closeHandler((v) => {
  console.log("The socket has been closed");
});
```

## Handling exceptions

You can set an `exceptionHandler` to receive any exceptions that happen
on the socket.

You can set an `exceptionHandler` to receive any exceptions that happens
before the connection is passed to the `connectHandler` , e.g during the
TLS handshake.

## Event bus write handler

Every socket automatically registers a handler on the event bus, and
when any buffers are received in this handler, it writes them to itself.
Those are local subscriptions not routed on the cluster.

This enables you to write data to a socket which is potentially in a
completely different verticle by sending the buffer to the address of
that handler.

The address of the handler is given by `writeHandlerID`

## Local and remote addresses

The local address of a `NetSocket` can be retrieved using
`localAddress`.

The remote address, (i.e. the address of the other end of the
connection) of a `NetSocket` can be retrieved using `remoteAddress`.

## Sending files or resources from the classpath

Files and classpath resources can be written to the socket directly
using `sendFile`. This can be a very efficient way to send files, as it
can be handled by the OS kernel directly where supported by the
operating system.

Please see the chapter about [serving files from the
classpath](#classpath) for restrictions of the classpath resolution or
disabling it.

``` js
socket.sendFile("myfile.dat");
```

## Streaming sockets

Instances of `NetSocket` are also `ReadStream` and `WriteStream`
instances so they can be used to pipe data to or from other read and
write streams.

See the chapter on [streams](#streams) for more information.

## Upgrading connections to SSL/TLS

A non SSL/TLS connection can be upgraded to SSL/TLS using
`upgradeToSsl`.

The server or client must be configured for SSL/TLS for this to work
correctly. Please see the [chapter on SSL/TLS](#ssl) for more
information.

## Closing a TCP Server

Call `close` to close the server. Closing the server closes any open
connections and releases all server resources.

The close is actually asynchronous and might not complete until some
time after the call has returned. If you want to be notified when the
actual close has completed then you can pass in a handler.

This handler will then be called when the close has fully completed.

``` js
server.close((res) => {
  if (res.succeeded()) {
    console.log("Server is now closed");
  } else {
    console.log("close failed");
  }
});
```

## Automatic clean-up in verticles

If you’re creating TCP servers and clients from inside verticles, those
servers and clients will be automatically closed when the verticle is
undeployed.

## Scaling - sharing TCP servers

The handlers of any TCP server are always executed on the same event
loop thread.

This means that if you are running on a server with a lot of cores, and
you only have this one instance deployed then you will have at most one
core utilised on your server.

In order to utilise more cores of your server you will need to deploy
more instances of the server.

You can instantiate more instances programmatically in your code:

``` js
// Create a few instances so we can utilise cores

for (let i = 0;i < 10;i++) {
  let server = vertx.createNetServer();
  server.connectHandler((socket) => {
    socket.handler((buffer) => {
      // Just echo back the data
      socket.write(buffer);
    });
  });
  server.listen(1234, "localhost");
}
```

or, if you are using verticles you can simply deploy more instances of
your server verticle by using the `-instances` option on the command
line:

vertx run com.mycompany.MyVerticle -instances 10

or when programmatically deploying your verticle

``` js
let options = new DeploymentOptions()
  .setInstances(10);
vertx.deployVerticle("com.mycompany.MyVerticle", options);
```

Once you do this you will find the echo server works functionally
identically to before, but all your cores on your server can be utilised
and more work can be handled.

At this point you might be asking yourself **'How can you have more than
one server listening on the same host and port? Surely you will get port
conflicts as soon as you try and deploy more than one instance?'**

*Vert.x does a little magic here.\**

When you deploy another server on the same host and port as an existing
server it doesn’t actually try and create a new server listening on the
same host/port.

Instead it internally maintains just a single server, and, as incoming
connections arrive it distributes them in a round-robin fashion to any
of the connect handlers.

Consequently Vert.x TCP servers can scale over available cores while
each instance remains single threaded.

## Creating a TCP client

The simplest way to create a TCP client, using all default options is as
follows:

``` js
let client = vertx.createNetClient();
```

## Configuring a TCP client

If you don’t want the default, a client can be configured by passing in
a `NetClientOptions` instance when creating it:

``` js
let options = new NetClientOptions()
  .setConnectTimeout(10000);
let client = vertx.createNetClient(options);
```

## Making connections

To make a connection to a server you use `connect`, specifying the port
and host of the server and a handler that will be called with a result
containing the `NetSocket` when connection is successful or with a
failure if connection failed.

``` js
let options = new NetClientOptions()
  .setConnectTimeout(10000);
let client = vertx.createNetClient(options);
client.connect(4321, "localhost", (res) => {
  if (res.succeeded()) {
    console.log("Connected!");
    let socket = res.result();
  } else {
    console.log("Failed to connect: " + res.cause().getMessage());
  }
});
```

## Configuring connection attempts

A client can be configured to automatically retry connecting to the
server in the event that it cannot connect. This is configured with
`setReconnectInterval` and `setReconnectAttempts`.

> **Note**
> 
> Currently Vert.x will not attempt to reconnect if a connection fails,
> reconnect attempts and interval only apply to creating initial
> connections.

``` js
let options = new NetClientOptions()
  .setReconnectAttempts(10)
  .setReconnectInterval(500);

let client = vertx.createNetClient(options);
```

By default, multiple connection attempts are disabled.

## Logging network activity

For debugging purposes, network activity can be logged:

``` js
let options = new NetServerOptions()
  .setLogActivity(true);

let server = vertx.createNetServer(options);
```

for the client

``` js
let options = new NetClientOptions()
  .setLogActivity(true);

let client = vertx.createNetClient(options);
```

Network activity is logged by Netty with the `DEBUG` level and with the
`io.netty.handler.logging.LoggingHandler` name. When using network
activity logging there are a few things to keep in mind:

  - logging is not performed by Vert.x logging but by Netty

  - this is **not** a production feature

You should read the [Netty logging](#netty-logging) section.

## Configuring servers and clients to work with SSL/TLS

TCP clients and servers can be configured to use [Transport Layer
Security](http://en.wikipedia.org/wiki/Transport_Layer_Security) -
earlier versions of TLS were known as SSL.

The APIs of the servers and clients are identical whether or not SSL/TLS
is used, and it’s enabled by configuring the `NetClientOptions` or
`NetServerOptions` instances used to create the servers or clients.

### Enabling SSL/TLS on the server

SSL/TLS is enabled with `ssl`.

By default it is disabled.

### Specifying key/certificate for the server

SSL/TLS servers usually provide certificates to clients in order verify
their identity to clients.

Certificates/keys can be configured for servers in several ways:

The first method is by specifying the location of a Java key-store which
contains the certificate and private key.

Java key stores can be managed with the
[keytool](http://docs.oracle.com/javase/6/docs/technotes/tools/solaris/keytool.html)
utility which ships with the JDK.

The password for the key store should also be provided:

``` js
let options = new NetServerOptions()
  .setSsl(true)
  .setKeyStoreOptions(new JksOptions()
    .setPath("/path/to/your/server-keystore.jks")
    .setPassword("password-of-your-keystore"));
let server = vertx.createNetServer(options);
```

Alternatively you can read the key store yourself as a buffer and
provide that directly:

``` js
let myKeyStoreAsABuffer = vertx.fileSystem().readFileBlocking("/path/to/your/server-keystore.jks");
let jksOptions = new JksOptions()
  .setValue(myKeyStoreAsABuffer)
  .setPassword("password-of-your-keystore");
let options = new NetServerOptions()
  .setSsl(true)
  .setKeyStoreOptions(jksOptions);
let server = vertx.createNetServer(options);
```

Key/certificate in PKCS\#12 format
(<http://en.wikipedia.org/wiki/PKCS_12>), usually with the `.pfx` or the
`.p12` extension can also be loaded in a similar fashion than JKS key
stores:

``` js
let options = new NetServerOptions()
  .setSsl(true)
  .setPfxKeyCertOptions(new PfxOptions()
    .setPath("/path/to/your/server-keystore.pfx")
    .setPassword("password-of-your-keystore"));
let server = vertx.createNetServer(options);
```

Buffer configuration is also supported:

``` js
let myKeyStoreAsABuffer = vertx.fileSystem().readFileBlocking("/path/to/your/server-keystore.pfx");
let pfxOptions = new PfxOptions()
  .setValue(myKeyStoreAsABuffer)
  .setPassword("password-of-your-keystore");
let options = new NetServerOptions()
  .setSsl(true)
  .setPfxKeyCertOptions(pfxOptions);
let server = vertx.createNetServer(options);
```

Another way of providing server private key and certificate separately
using `.pem` files.

``` js
let options = new NetServerOptions()
  .setSsl(true)
  .setPemKeyCertOptions(new PemKeyCertOptions()
    .setKeyPath("/path/to/your/server-key.pem")
    .setCertPath("/path/to/your/server-cert.pem"));
let server = vertx.createNetServer(options);
```

Buffer configuration is also supported:

``` js
let myKeyAsABuffer = vertx.fileSystem().readFileBlocking("/path/to/your/server-key.pem");
let myCertAsABuffer = vertx.fileSystem().readFileBlocking("/path/to/your/server-cert.pem");
let pemOptions = new PemKeyCertOptions()
  .setKeyValue(myKeyAsABuffer)
  .setCertValue(myCertAsABuffer);
let options = new NetServerOptions()
  .setSsl(true)
  .setPemKeyCertOptions(pemOptions);
let server = vertx.createNetServer(options);
```

Vert.x supports reading of unencrypted RSA and/or ECC based private keys
from PKCS8 PEM files. RSA based private keys can also be read from PKCS1
PEM files. X.509 certificates can be read from PEM files containing a
textual encoding of the certificate as defined by [RFC 7468,
Section 5](https://tools.ietf.org/html/rfc7468#section-5).

> **Warning**
> 
> Keep in mind that the keys contained in an unencrypted PKCS8 or a
> PKCS1 PEM file can be extracted by anybody who can read the file.
> Thus, make sure to put proper access restrictions on such PEM files in
> order to prevent misuse.

### Specifying trust for the server

SSL/TLS servers can use a certificate authority in order to verify the
identity of the clients.

Certificate authorities can be configured for servers in several ways:

Java trust stores can be managed with the
[keytool](http://docs.oracle.com/javase/6/docs/technotes/tools/solaris/keytool.html)
utility which ships with the JDK.

The password for the trust store should also be provided:

``` js
let options = new NetServerOptions()
  .setSsl(true)
  .setClientAuth("REQUIRED")
  .setTrustStoreOptions(new JksOptions()
    .setPath("/path/to/your/truststore.jks")
    .setPassword("password-of-your-truststore"));
let server = vertx.createNetServer(options);
```

Alternatively you can read the trust store yourself as a buffer and
provide that directly:

``` js
let myTrustStoreAsABuffer = vertx.fileSystem().readFileBlocking("/path/to/your/truststore.jks");
let options = new NetServerOptions()
  .setSsl(true)
  .setClientAuth("REQUIRED")
  .setTrustStoreOptions(new JksOptions()
    .setValue(myTrustStoreAsABuffer)
    .setPassword("password-of-your-truststore"));
let server = vertx.createNetServer(options);
```

Certificate authority in PKCS\#12 format
(<http://en.wikipedia.org/wiki/PKCS_12>), usually with the `.pfx` or the
`.p12` extension can also be loaded in a similar fashion than JKS trust
stores:

``` js
let options = new NetServerOptions()
  .setSsl(true)
  .setClientAuth("REQUIRED")
  .setPfxTrustOptions(new PfxOptions()
    .setPath("/path/to/your/truststore.pfx")
    .setPassword("password-of-your-truststore"));
let server = vertx.createNetServer(options);
```

Buffer configuration is also supported:

``` js
let myTrustStoreAsABuffer = vertx.fileSystem().readFileBlocking("/path/to/your/truststore.pfx");
let options = new NetServerOptions()
  .setSsl(true)
  .setClientAuth("REQUIRED")
  .setPfxTrustOptions(new PfxOptions()
    .setValue(myTrustStoreAsABuffer)
    .setPassword("password-of-your-truststore"));
let server = vertx.createNetServer(options);
```

Another way of providing server certificate authority using a list
`.pem` files.

``` js
let options = new NetServerOptions()
  .setSsl(true)
  .setClientAuth("REQUIRED")
  .setPemTrustOptions(new PemTrustOptions()
    .setCertPaths(["/path/to/your/server-ca.pem"]));
let server = vertx.createNetServer(options);
```

Buffer configuration is also supported:

``` js
let myCaAsABuffer = vertx.fileSystem().readFileBlocking("/path/to/your/server-ca.pfx");
let options = new NetServerOptions()
  .setSsl(true)
  .setClientAuth("REQUIRED")
  .setPemTrustOptions(new PemTrustOptions()
    .setCertValues([myCaAsABuffer]));
let server = vertx.createNetServer(options);
```

### Enabling SSL/TLS on the client

Net Clients can also be easily configured to use SSL. They have the
exact same API when using SSL as when using standard sockets.

To enable SSL on a NetClient the function setSSL(true) is called.

### Client trust configuration

If the `trustALl` is set to true on the client, then the client will
trust all server certificates. The connection will still be encrypted
but this mode is vulnerable to 'man in the middle' attacks. I.e. you
can’t be sure who you are connecting to. Use this with caution.
Default value is false.

``` js
let options = new NetClientOptions()
  .setSsl(true)
  .setTrustAll(true);
let client = vertx.createNetClient(options);
```

If `trustAll` is not set then a client trust store must be configured
and should contain the certificates of the servers that the client
trusts.

By default, host verification is disabled on the client. To enable host
verification, set the algorithm to use on your client (only HTTPS and
LDAPS is currently supported):

``` js
let options = new NetClientOptions()
  .setSsl(true)
  .setHostnameVerificationAlgorithm("HTTPS");
let client = vertx.createNetClient(options);
```

Likewise server configuration, the client trust can be configured in
several ways:

The first method is by specifying the location of a Java trust-store
which contains the certificate authority.

It is just a standard Java key store, the same as the key stores on the
server side. The client trust store location is set by using the
function `path` on the `jks options`. If a server presents a certificate
during connection which is not in the client trust store, the connection
attempt will not succeed.

``` js
let options = new NetClientOptions()
  .setSsl(true)
  .setTrustStoreOptions(new JksOptions()
    .setPath("/path/to/your/truststore.jks")
    .setPassword("password-of-your-truststore"));
let client = vertx.createNetClient(options);
```

Buffer configuration is also supported:

``` js
let myTrustStoreAsABuffer = vertx.fileSystem().readFileBlocking("/path/to/your/truststore.jks");
let options = new NetClientOptions()
  .setSsl(true)
  .setTrustStoreOptions(new JksOptions()
    .setValue(myTrustStoreAsABuffer)
    .setPassword("password-of-your-truststore"));
let client = vertx.createNetClient(options);
```

Certificate authority in PKCS\#12 format
(<http://en.wikipedia.org/wiki/PKCS_12>), usually with the `.pfx` or the
`.p12` extension can also be loaded in a similar fashion than JKS trust
stores:

``` js
let options = new NetClientOptions()
  .setSsl(true)
  .setPfxTrustOptions(new PfxOptions()
    .setPath("/path/to/your/truststore.pfx")
    .setPassword("password-of-your-truststore"));
let client = vertx.createNetClient(options);
```

Buffer configuration is also supported:

``` js
let myTrustStoreAsABuffer = vertx.fileSystem().readFileBlocking("/path/to/your/truststore.pfx");
let options = new NetClientOptions()
  .setSsl(true)
  .setPfxTrustOptions(new PfxOptions()
    .setValue(myTrustStoreAsABuffer)
    .setPassword("password-of-your-truststore"));
let client = vertx.createNetClient(options);
```

Another way of providing server certificate authority using a list
`.pem` files.

``` js
let options = new NetClientOptions()
  .setSsl(true)
  .setPemTrustOptions(new PemTrustOptions()
    .setCertPaths(["/path/to/your/ca-cert.pem"]));
let client = vertx.createNetClient(options);
```

Buffer configuration is also supported:

``` js
let myTrustStoreAsABuffer = vertx.fileSystem().readFileBlocking("/path/to/your/ca-cert.pem");
let options = new NetClientOptions()
  .setSsl(true)
  .setPemTrustOptions(new PemTrustOptions()
    .setCertValues([myTrustStoreAsABuffer]));
let client = vertx.createNetClient(options);
```

### Specifying key/certificate for the client

If the server requires client authentication then the client must
present its own certificate to the server when connecting. The client
can be configured in several ways:

The first method is by specifying the location of a Java key-store which
contains the key and certificate. Again it’s just a regular Java key
store. The client keystore location is set by using the function `path`
on the `jks options`.

``` js
let options = new NetClientOptions()
  .setSsl(true)
  .setKeyStoreOptions(new JksOptions()
    .setPath("/path/to/your/client-keystore.jks")
    .setPassword("password-of-your-keystore"));
let client = vertx.createNetClient(options);
```

Buffer configuration is also supported:

``` js
let myKeyStoreAsABuffer = vertx.fileSystem().readFileBlocking("/path/to/your/client-keystore.jks");
let jksOptions = new JksOptions()
  .setValue(myKeyStoreAsABuffer)
  .setPassword("password-of-your-keystore");
let options = new NetClientOptions()
  .setSsl(true)
  .setKeyStoreOptions(jksOptions);
let client = vertx.createNetClient(options);
```

Key/certificate in PKCS\#12 format
(<http://en.wikipedia.org/wiki/PKCS_12>), usually with the `.pfx` or the
`.p12` extension can also be loaded in a similar fashion than JKS key
stores:

``` js
let options = new NetClientOptions()
  .setSsl(true)
  .setPfxKeyCertOptions(new PfxOptions()
    .setPath("/path/to/your/client-keystore.pfx")
    .setPassword("password-of-your-keystore"));
let client = vertx.createNetClient(options);
```

Buffer configuration is also supported:

``` js
let myKeyStoreAsABuffer = vertx.fileSystem().readFileBlocking("/path/to/your/client-keystore.pfx");
let pfxOptions = new PfxOptions()
  .setValue(myKeyStoreAsABuffer)
  .setPassword("password-of-your-keystore");
let options = new NetClientOptions()
  .setSsl(true)
  .setPfxKeyCertOptions(pfxOptions);
let client = vertx.createNetClient(options);
```

Another way of providing server private key and certificate separately
using `.pem` files.

``` js
let options = new NetClientOptions()
  .setSsl(true)
  .setPemKeyCertOptions(new PemKeyCertOptions()
    .setKeyPath("/path/to/your/client-key.pem")
    .setCertPath("/path/to/your/client-cert.pem"));
let client = vertx.createNetClient(options);
```

Buffer configuration is also supported:

``` js
let myKeyAsABuffer = vertx.fileSystem().readFileBlocking("/path/to/your/client-key.pem");
let myCertAsABuffer = vertx.fileSystem().readFileBlocking("/path/to/your/client-cert.pem");
let pemOptions = new PemKeyCertOptions()
  .setKeyValue(myKeyAsABuffer)
  .setCertValue(myCertAsABuffer);
let options = new NetClientOptions()
  .setSsl(true)
  .setPemKeyCertOptions(pemOptions);
let client = vertx.createNetClient(options);
```

Keep in mind that pem configuration, the private key is not crypted.

### Self-signed certificates for testing and development purposes

> **Caution**
> 
> Do not use this in production settings, and note that the generated
> keys are very insecure.

It is very often the case that self-signed certificates are required, be
it for unit / integration tests or for running a development version of
an application.

`SelfSignedCertificate` can be used to provide self-signed PEM
certificate helpers and give `KeyCertOptions` and `TrustOptions`
configurations:

``` js
import { SelfSignedCertificate } from "@vertx/core"
let certificate = SelfSignedCertificate.create();

let serverOptions = new NetServerOptions()
  .setSsl(true)
  .setKeyCertOptions(certificate.keyCertOptions())
  .setTrustOptions(certificate.trustOptions());

let server = vertx.createNetServer(serverOptions).connectHandler((socket) => {
  socket.write("Hello!").end();
}).listen(1234, "localhost");

let clientOptions = new NetClientOptions()
  .setSsl(true)
  .setKeyCertOptions(certificate.keyCertOptions())
  .setTrustOptions(certificate.trustOptions());

let client = vertx.createNetClient(clientOptions);
client.connect(1234, "localhost", (ar) => {
  if (ar.succeeded()) {
    ar.result().handler((buffer) => {
      console.log(buffer);
    });
  } else {
    console.error("Woops: " + ar.cause().getMessage());
  }
});
```

The client can also be configured to trust all certificates:

``` js
let clientOptions = new NetClientOptions()
  .setSsl(true)
  .setTrustAll(true);
```

Note that self-signed certificates also work for other TCP protocols
like HTTPS:

``` js
import { SelfSignedCertificate } from "@vertx/core"
let certificate = SelfSignedCertificate.create();

vertx.createHttpServer(new HttpServerOptions()
  .setSsl(true)
  .setKeyCertOptions(certificate.keyCertOptions())
  .setTrustOptions(certificate.trustOptions())).requestHandler((req) => {
  req.response().end("Hello!");
}).listen(8080);
```

### Revoking certificate authorities

Trust can be configured to use a certificate revocation list (CRL) for
revoked certificates that should no longer be trusted. The `crlPath`
configures the crl list to use:

``` js
let options = new NetClientOptions()
  .setSsl(true)
  .setTrustStoreOptions(trustOptions)
  .setCrlPaths(["/path/to/your/crl.pem"]);
let client = vertx.createNetClient(options);
```

Buffer configuration is also supported:

``` js
let myCrlAsABuffer = vertx.fileSystem().readFileBlocking("/path/to/your/crl.pem");
let options = new NetClientOptions()
  .setSsl(true)
  .setTrustStoreOptions(trustOptions)
  .setCrlValues([myCrlAsABuffer]);
let client = vertx.createNetClient(options);
```

### Configuring the Cipher suite

By default, the TLS configuration will use the Cipher suite of the JVM
running Vert.x. This Cipher suite can be configured with a suite of
enabled ciphers:

``` js
let options = new NetServerOptions()
  .setSsl(true)
  .setKeyStoreOptions(keyStoreOptions)
  .setEnabledCipherSuites(["ECDHE-RSA-AES128-GCM-SHA256", "ECDHE-ECDSA-AES128-GCM-SHA256", "ECDHE-RSA-AES256-GCM-SHA384", "CDHE-ECDSA-AES256-GCM-SHA384"]);
let server = vertx.createNetServer(options);
```

Cipher suite can be specified on the `NetServerOptions` or
`NetClientOptions` configuration.

### Configuring TLS protocol versions

By default, the TLS configuration will use the following protocol
versions: SSLv2Hello, TLSv1, TLSv1.1 and TLSv1.2. Protocol versions can
be configured by explicitly adding enabled protocols:

``` js
Code not translatable
```

Protocol versions can be specified on the `NetServerOptions` or
`NetClientOptions` configuration.

### SSL engine

The engine implementation can be configured to use
[OpenSSL](https://www.openssl.org) instead of the JDK implementation.
OpenSSL provides better performances and CPU usage than the JDK engine,
as well as JDK version independence.

The engine options to use is

  - the `getSslEngineOptions` options when it is set

  - otherwise `JdkSSLEngineOptions`

<!-- end list -->

``` js
// Use JDK SSL engine
let options = new NetServerOptions()
  .setSsl(true)
  .setKeyStoreOptions(keyStoreOptions);

// Use JDK SSL engine explicitly
options = new NetServerOptions()
  .setSsl(true)
  .setKeyStoreOptions(keyStoreOptions)
  .setJdkSslEngineOptions(new JdkSSLEngineOptions());

// Use OpenSSL engine
options = new NetServerOptions()
  .setSsl(true)
  .setKeyStoreOptions(keyStoreOptions)
  .setOpenSslEngineOptions(new OpenSSLEngineOptions());
```

### Server Name Indication (SNI)

Server Name Indication (SNI) is a TLS extension by which a client
specifies a hostname attempting to connect: during the TLS handshake the
client gives a server name and the server can use it to respond with a
specific certificate for this server name instead of the default
deployed certificate. If the server requires client authentication the
server can use a specific trusted CA certificate depending on the
indicated server name.

When SNI is active the server uses

  - the certificate CN or SAN DNS (Subject Alternative Name with DNS) to
    do an exact match, e.g `www.example.com`

  - the certificate CN or SAN DNS certificate to match a wildcard name,
    e.g `*.example.com`

  - otherwise the first certificate when the client does not present a
    server name or the presented server name cannot be matched

When the server additionally requires client authentication:

  - if `JksOptions` were used to set the trust options (`options`) then
    an exact match with the trust store alias is done

  - otherwise the available CA certificates are used in the same way as
    if no SNI is in place

You can enable SNI on the server by setting `setSni` to `true` and
configured the server with multiple key/certificate pairs.

Java KeyStore files or PKCS12 files can store multiple key/cert pairs
out of the box.

``` js
let keyCertOptions = new JksOptions()
  .setPath("keystore.jks")
  .setPassword("wibble");

let netServer = vertx.createNetServer(new NetServerOptions()
  .setKeyStoreOptions(keyCertOptions)
  .setSsl(true)
  .setSni(true));
```

`PemKeyCertOptions` can be configured to hold multiple entries:

``` js
let keyCertOptions = new PemKeyCertOptions()
  .setKeyPaths(["default-key.pem", "host1-key.pem", "etc..."])
  .setCertPaths(["default-cert.pem", "host2-key.pem", "etc..."]);

let netServer = vertx.createNetServer(new NetServerOptions()
  .setPemKeyCertOptions(keyCertOptions)
  .setSsl(true)
  .setSni(true));
```

The client implicitly sends the connecting host as an SNI server name
for Fully Qualified Domain Name (FQDN).

You can provide an explicit server name when connecting a socket

``` js
let client = vertx.createNetClient(new NetClientOptions()
  .setTrustStoreOptions(trustOptions)
  .setSsl(true));

// Connect to 'localhost' and present 'server.name' server name
client.connect(1234, "localhost", "server.name", (res) => {
  if (res.succeeded()) {
    console.log("Connected!");
    let socket = res.result();
  } else {
    console.log("Failed to connect: " + res.cause().getMessage());
  }
});
```

It can be used for different purposes:

  - present a server name different than the server host

  - present a server name while connecting to an IP

  - force to present a server name when using shortname

### Application-Layer Protocol Negotiation (ALPN)

Application-Layer Protocol Negotiation (ALPN) is a TLS extension for
application layer protocol negotiation. It is used by HTTP/2: during the
TLS handshake the client gives the list of application protocols it
accepts and the server responds with a protocol it supports.

If you are using Java 9, you are fine and you can use HTTP/2 out of the
box without extra steps.

Java 8 does not supports ALPN out of the box, so ALPN should be enabled
by other means:

  - *OpenSSL* support

  - *Jetty-ALPN* support

The engine options to use is

  - the `getSslEngineOptions` options when it is set

  - `JdkSSLEngineOptions` when ALPN is available for JDK

  - `OpenSSLEngineOptions` when ALPN is available for OpenSSL

  - otherwise it fails

#### OpenSSL ALPN support

OpenSSL provides native ALPN support.

OpenSSL requires to configure `setOpenSslEngineOptions` and use
[netty-tcnative](http://netty.io/wiki/forked-tomcat-native.html) jar on
the classpath. Using tcnative may require OpenSSL to be installed on
your OS depending on the tcnative implementation.

#### Jetty-ALPN support

Jetty-ALPN is a small jar that overrides a few classes of Java 8
distribution to support ALPN.

The JVM must be started with the *alpn-boot-${version}.jar* in its
`bootclasspath`:

    -Xbootclasspath/p:/path/to/alpn-boot${version}.jar

where ${version} depends on the JVM version, e.g. *8.1.7.v20160121* for
*OpenJDK 1.8.0u74* . The complete list is available on the [Jetty-ALPN
page](http://www.eclipse.org/jetty/documentation/current/alpn-chapter.html).

The main drawback is that the version depends on the JVM.

To solve this problem the *[Jetty ALPN
agent](https://github.com/jetty-project/jetty-alpn-agent)* can be use
instead. The agent is a JVM agent that will chose the correct ALPN
version for the JVM running it:

    -javaagent:/path/to/alpn/agent

## Using a proxy for client connections

The `NetClient` supports either a HTTP/1.x *CONNECT*, *SOCKS4a* or
*SOCKS5* proxy.

The proxy can be configured in the `NetClientOptions` by setting a
`ProxyOptions` object containing proxy type, hostname, port and
optionally username and password.

Here’s an example:

``` js
let options = new NetClientOptions()
  .setProxyOptions(new ProxyOptions()
    .setType("SOCKS5")
    .setHost("localhost")
    .setPort(1080)
    .setUsername("username")
    .setPassword("secret"));
let client = vertx.createNetClient(options);
```

The DNS resolution is always done on the proxy server, to achieve the
functionality of a SOCKS4 client, it is necessary to resolve the DNS
address locally.

# Writing HTTP servers and clients

Vert.x allows you to easily write non blocking HTTP clients and servers.

Vert.x supports the HTTP/1.0, HTTP/1.1 and HTTP/2 protocols.

The base API for HTTP is the same for HTTP/1.x and HTTP/2, specific API
features are available for dealing with the HTTP/2 protocol.

## Creating an HTTP Server

The simplest way to create an HTTP server, using all default options is
as follows:

``` js
let server = vertx.createHttpServer();
```

## Configuring an HTTP server

If you don’t want the default, a server can be configured by passing in
a `HttpServerOptions` instance when creating it:

``` js
let options = new HttpServerOptions()
  .setMaxWebSocketFrameSize(1000000);

let server = vertx.createHttpServer(options);
```

## Configuring an HTTP/2 server

Vert.x supports HTTP/2 over TLS `h2` and over TCP `h2c`.

  - `h2` identifies the HTTP/2 protocol when used over TLS negotiated by
    [Application-Layer Protocol
    Negotiation](https://en.wikipedia.org/wiki/Application-Layer_Protocol_Negotiation)
    (ALPN)

  - `h2c` identifies the HTTP/2 protocol when using in clear text over
    TCP, such connections are established either with an HTTP/1.1
    upgraded request or directly

To handle `h2` requests, TLS must be enabled along with `setUseAlpn`:

``` js
let options = new HttpServerOptions()
  .setUseAlpn(true)
  .setSsl(true)
  .setKeyStoreOptions(new JksOptions()
    .setPath("/path/to/my/keystore"));

let server = vertx.createHttpServer(options);
```

ALPN is a TLS extension that negotiates the protocol before the client
and the server start to exchange data.

Clients that don’t support ALPN will still be able to do a *classic* SSL
handshake.

ALPN will usually agree on the `h2` protocol, although `http/1.1` can be
used if the server or the client decides so.

To handle `h2c` requests, TLS must be disabled, the server will upgrade
to HTTP/2 any request HTTP/1.1 that wants to upgrade to HTTP/2. It will
also accept a direct `h2c` connection beginning with the `PRI *
HTTP/2.0\r\nSM\r\n` preface.

> **Warning**
> 
> most browsers won’t support `h2c`, so for serving web sites you should
> use `h2` and not `h2c`.

When a server accepts an HTTP/2 connection, it sends to the client its
`initial settings`. The settings define how the client can use the
connection, the default initial settings for a server are:

  - `getMaxConcurrentStreams`: `100` as recommended by the HTTP/2 RFC

  - the default HTTP/2 settings values for the others

> **Note**
> 
> Worker Verticles are not compatible with HTTP/2

## Logging network server activity

For debugging purposes, network activity can be logged.

``` js
let options = new HttpServerOptions()
  .setLogActivity(true);

let server = vertx.createHttpServer(options);
```

See the chapter on [logging network activity](#logging_network_activity)
for a detailed explanation.

## Start the Server Listening

To tell the server to listen for incoming requests you use one of the
`listen` alternatives.

To tell the server to listen at the host and port as specified in the
options:

``` js
let server = vertx.createHttpServer();
server.listen();
```

Or to specify the host and port in the call to listen, ignoring what is
configured in the options:

``` js
let server = vertx.createHttpServer();
server.listen(8080, "myhost.com");
```

The default host is `0.0.0.0` which means 'listen on all available
addresses' and the default port is `80`.

The actual bind is asynchronous so the server might not actually be
listening until some time **after** the call to listen has returned.

If you want to be notified when the server is actually listening you can
provide a handler to the `listen` call. For example:

``` js
let server = vertx.createHttpServer();
server.listen(8080, "myhost.com", (res) => {
  if (res.succeeded()) {
    console.log("Server is now listening!");
  } else {
    console.log("Failed to bind!");
  }
});
```

## Getting notified of incoming requests

To be notified when a request arrives you need to set a
`requestHandler`:

``` js
let server = vertx.createHttpServer();
server.requestHandler((request) => {
  // Handle the request in here
});
```

## Handling requests

When a request arrives, the request handler is called passing in an
instance of `HttpServerRequest`. This object represents the server side
HTTP request.

The handler is called when the headers of the request have been fully
read.

If the request contains a body, that body will arrive at the server some
time after the request handler has been called.

The server request object allows you to retrieve the `uri`, `path`,
`params` and `headers`, amongst other things.

Each server request object is associated with one server response
object. You use `response` to get a reference to the
`HttpServerResponse` object.

Here’s a simple example of a server handling a request and replying with
"hello world" to it.

``` js
vertx.createHttpServer().requestHandler((request) => {
  request.response().end("Hello world");
}).listen(8080);
```

### Request version

The version of HTTP specified in the request can be retrieved with
`version`

### Request method

Use `method` to retrieve the HTTP method of the request. (i.e. whether
it’s GET, POST, PUT, DELETE, HEAD, OPTIONS, etc).

### Request URI

Use `uri` to retrieve the URI of the request.

Note that this is the actual URI as passed in the HTTP request, and it’s
almost always a relative URI.

The URI is as defined in [Section 5.1.2 of the HTTP specification -
Request-URI](http://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html)

### Request path

Use `path` to return the path part of the URI

For example, if the request URI was:

a/b/c/page.html?param1=abc\&param2=xyz

Then the path would be

/a/b/c/page.html

### Request query

Use `query` to return the query part of the URI

For example, if the request URI was:

a/b/c/page.html?param1=abc\&param2=xyz

Then the query would be

param1=abc\&param2=xyz

### Request headers

Use `headers` to return the headers of the HTTP request.

This returns an instance of `MultiMap` - which is like a normal Map or
Hash but allows multiple values for the same key - this is because HTTP
allows multiple header values with the same key.

It also has case-insensitive keys, that means you can do the following:

``` js
let headers = request.headers();

// Get the User-Agent:
console.log("User agent is " + headers.get("user-agent"));

// You can also do this and get the same result:
console.log("User agent is " + headers.get("User-Agent"));
```

### Request host

Use `host` to return the host of the HTTP request.

For HTTP/1.x requests the `host` header is returned, for HTTP/1 requests
the `:authority` pseudo header is returned.

### Request parameters

Use `params` to return the parameters of the HTTP request.

Just like `headers` this returns an instance of `MultiMap` as there can
be more than one parameter with the same name.

Request parameters are sent on the request URI, after the path. For
example if the URI was:

/page.html?param1=abc\&param2=xyz

Then the parameters would contain the following:

    param1: 'abc'
    param2: 'xyz

Note that these request parameters are retrieved from the URL of the
request. If you have form attributes that have been sent as part of the
submission of an HTML form submitted in the body of a
`multi-part/form-data` request then they will not appear in the params
here.

### Remote address

The address of the sender of the request can be retrieved with
`remoteAddress`.

### Absolute URI

The URI passed in an HTTP request is usually relative. If you wish to
retrieve the absolute URI corresponding to the request, you can get it
with `absoluteURI`

### End handler

The `endHandler` of the request is invoked when the entire request,
including any body has been fully read.

### Reading Data from the Request Body

Often an HTTP request contains a body that we want to read. As
previously mentioned the request handler is called when just the headers
of the request have arrived so the request object does not have a body
at that point.

This is because the body may be very large (e.g. a file upload) and we
don’t generally want to buffer the entire body in memory before handing
it to you, as that could cause the server to exhaust available memory.

To receive the body, you can use the `handler` on the request, this will
get called every time a chunk of the request body arrives. Here’s an
example:

``` js
request.handler((buffer) => {
  console.log("I have received a chunk of the body of length " + buffer.length());
});
```

The object passed into the handler is a `Buffer`, and the handler can be
called multiple times as data arrives from the network, depending on the
size of the body.

In some cases (e.g. if the body is small) you will want to aggregate the
entire body in memory, so you could do the aggregation yourself as
follows:

``` js
import { Buffer } from "@vertx/core"

// Create an empty buffer
let totalBuffer = Buffer.buffer();

request.handler((buffer) => {
  console.log("I have received a chunk of the body of length " + buffer.length());
  totalBuffer.appendBuffer(buffer);
});

request.endHandler((v) => {
  console.log("Full body received, length = " + totalBuffer.length());
});
```

This is such a common case, that Vert.x provides a `bodyHandler` to do
this for you. The body handler is called once when all the body has been
received:

``` js
request.bodyHandler((totalBuffer) => {
  console.log("Full body received, length = " + totalBuffer.length());
});
```

### Streaming requests

The request object is a `ReadStream` so you can pipe the request body to
any `WriteStream` instance.

See the chapter on [streams](#streams) for a detailed explanation.

### Handling HTML forms

HTML forms can be submitted with either a content type of
`application/x-www-form-urlencoded` or `multipart/form-data`.

For url encoded forms, the form attributes are encoded in the url, just
like normal query parameters.

For multi-part forms they are encoded in the request body, and as such
are not available until the entire body has been read from the wire.

Multi-part forms can also contain file uploads.

If you want to retrieve the attributes of a multi-part form you should
tell Vert.x that you expect to receive such a form **before** any of the
body is read by calling `setExpectMultipart` with true, and then you
should retrieve the actual attributes using `formAttributes` once the
entire body has been read:

``` js
server.requestHandler((request) => {
  request.setExpectMultipart(true);
  request.endHandler((v) => {
    // The body has now been fully read, so retrieve the form attributes
    let formAttributes = request.formAttributes();
  });
});
```

### Handling form file uploads

Vert.x can also handle file uploads which are encoded in a multi-part
request body.

To receive file uploads you tell Vert.x to expect a multi-part form and
set an `uploadHandler` on the request.

This handler will be called once for every upload that arrives on the
server.

The object passed into the handler is a `HttpServerFileUpload` instance.

``` js
server.requestHandler((request) => {
  request.setExpectMultipart(true);
  request.uploadHandler((upload) => {
    console.log("Got a file upload " + upload.name());
  });
});
```

File uploads can be large we don’t provide the entire upload in a single
buffer as that might result in memory exhaustion, instead, the upload
data is received in chunks:

``` js
request.uploadHandler((upload) => {
  upload.handler((chunk) => {
    console.log("Received a chunk of the upload of length " + chunk.length());
  });
});
```

The upload object is a `ReadStream` so you can pipe the request body to
any `WriteStream` instance. See the chapter on [streams](#streams) for a
detailed explanation.

If you just want to upload the file to disk somewhere you can use
`streamToFileSystem`:

``` js
request.uploadHandler((upload) => {
  upload.streamToFileSystem("myuploads_directory/" + upload.filename());
});
```

> **Warning**
> 
> Make sure you check the filename in a production system to avoid
> malicious clients uploading files to arbitrary places on your
> filesystem. See [security notes](#_security_notes) for more
> information.

### Handling cookies

You use `getCookie` to retrieve a cookie by name, or use `cookieMap` to
retrieve all the cookies.

To remove a cookie, use `removeCookie`.

To add a cookie use `addCookie`.

The set of cookies will be written back in the response automatically
when the response headers are written so the browser can store them.

Cookies are described by instances of `Cookie`. This allows you to
retrieve the name, value, domain, path and other normal cookie
properties.

Same Site Cookies let servers require that a cookie shouldn’t be sent
with cross-site (where Site is defined by the registrable domain)
requests, which provides some protection against cross-site request
forgery attacks. This kind of cookies are enabled using the setter:
`setSameSite`.

Same site cookies can have one of 3 values:

  - None - The browser will send cookies with both cross-site requests
    and same-site requests.

  - Strict - he browser will only send cookies for same-site requests
    (requests originating from the site that set the cookie). If the
    request originated from a different URL than the URL of the current
    location, none of the cookies tagged with the Strict attribute will
    be included.

  - Lax - Same-site cookies are withheld on cross-site subrequests, such
    as calls to load images or frames, but will be sent when a user
    navigates to the URL from an external site; for example, by
    following a link.

Here’s an example of querying and adding cookies:

``` js
import { Cookie } from "@vertx/core"
let someCookie = request.getCookie("mycookie");
let cookieValue = someCookie.getValue();

// Do something with cookie...

// Add a cookie - this will get written back in the response automatically
request.response().addCookie(Cookie.cookie("othercookie", "somevalue"));
```

### Handling compressed body

Vert.x can handle compressed body payloads which are encoded by the
client with the *deflate* or *gzip* algorithms.

To enable decompression set `setDecompressionSupported` on the options
when creating the server.

By default decompression is disabled.

### Receiving custom HTTP/2 frames

HTTP/2 is a framed protocol with various frames for the HTTP
request/response model. The protocol allows other kind of frames to be
sent and received.

To receive custom frames, you can use the `customFrameHandler` on the
request, this will get called every time a custom frame arrives. Here’s
an example:

``` js
request.customFrameHandler((frame) => {

  console.log("Received a frame type=" + frame.type() + " payload" + frame.payload().toString());
});
```

HTTP/2 frames are not subject to flow control - the frame handler will
be called immediatly when a custom frame is received whether the request
is paused or is not

### Non standard HTTP methods

The `OTHER` HTTP method is used for non standard methods, in this case
`rawMethod` returns the HTTP method as sent by the client.

## Sending back responses

The server response object is an instance of `HttpServerResponse` and is
obtained from the request with `response`.

You use the response object to write a response back to the HTTP client.

### Setting status code and message

The default HTTP status code for a response is `200`, representing `OK`.

Use `setStatusCode` to set a different code.

You can also specify a custom status message with `setStatusMessage`.

If you don’t specify a status message, the default one corresponding to
the status code will be used.

> **Note**
> 
> for HTTP/2 the status won’t be present in the response since the
> protocol won’t transmit the message to the client

### Writing HTTP responses

To write data to an HTTP response, you use one of the `write`
operations.

These can be invoked multiple times before the response is ended. They
can be invoked in a few ways:

With a single buffer:

``` js
let response = request.response();
response.write(buffer);
```

With a string. In this case the string will encoded using UTF-8 and the
result written to the wire.

``` js
let response = request.response();
response.write("hello world!");
```

With a string and an encoding. In this case the string will encoded
using the specified encoding and the result written to the wire.

``` js
let response = request.response();
response.write("hello world!", "UTF-16");
```

Writing to a response is asynchronous and always returns immediately
after the write has been queued.

If you are just writing a single string or buffer to the HTTP response
you can write it and end the response in a single call to the `end`

The first call to write results in the response header being written to
the response. Consequently, if you are not using HTTP chunking then you
must set the `Content-Length` header before writing to the response,
since it will be too late otherwise. If you are using HTTP chunking you
do not have to worry.

### Ending HTTP responses

Once you have finished with the HTTP response you should `end` it.

This can be done in several ways:

With no arguments, the response is simply ended.

``` js
let response = request.response();
response.write("hello world!");
response.end();
```

It can also be called with a string or buffer in the same way `write` is
called. In this case it’s just the same as calling write with a string
or buffer followed by calling end with no arguments. For example:

``` js
let response = request.response();
response.end("hello world!");
```

### Closing the underlying connection

You can close the underlying TCP connection with `close`.

Non keep-alive connections will be automatically closed by Vert.x when
the response is ended.

Keep-alive connections are not automatically closed by Vert.x by
default. If you want keep-alive connections to be closed after an idle
time, then you configure `setIdleTimeout`.

HTTP/2 connections send a {@literal GOAWAY} frame before closing the
response.

### Setting response headers

HTTP response headers can be added to the response by adding them
directly to the `headers`:

``` js
let response = request.response();
let headers = response.headers();
headers.set("content-type", "text/html");
headers.set("other-header", "wibble");
```

Or you can use `putHeader`

``` js
let response = request.response();
response.putHeader("content-type", "text/html").putHeader("other-header", "wibble");
```

Headers must all be added before any parts of the response body are
written.

### Chunked HTTP responses and trailers

Vert.x supports [HTTP Chunked Transfer
Encoding](http://en.wikipedia.org/wiki/Chunked_transfer_encoding).

This allows the HTTP response body to be written in chunks, and is
normally used when a large response body is being streamed to a client
and the total size is not known in advance.

You put the HTTP response into chunked mode as follows:

``` js
let response = request.response();
response.setChunked(true);
```

Default is non-chunked. When in chunked mode, each call to one of the
`write` methods will result in a new HTTP chunk being written out.

When in chunked mode you can also write HTTP response trailers to the
response. These are actually written in the final chunk of the response.

> **Note**
> 
> chunked response has no effect for an HTTP/2 stream

To add trailers to the response, add them directly to the `trailers`.

``` js
let response = request.response();
response.setChunked(true);
let trailers = response.trailers();
trailers.set("X-wibble", "woobble").set("X-quux", "flooble");
```

Or use `putTrailer`.

``` js
let response = request.response();
response.setChunked(true);
response.putTrailer("X-wibble", "woobble").putTrailer("X-quux", "flooble");
```

### Serving files directly from disk or the classpath

If you were writing a web server, one way to serve a file from disk
would be to open it as an `AsyncFile` and pipe it to the HTTP response.

Or you could load it it one go using `readFile` and write it straight to
the response.

Alternatively, Vert.x provides a method which allows you to serve a file
from disk or the filesystem to an HTTP response in one operation. Where
supported by the underlying operating system this may result in the OS
directly transferring bytes from the file to the socket without being
copied through user-space at all.

This is done by using `sendFile`, and is usually more efficient for
large files, but may be slower for small files.

Here’s a very simple web server that serves files from the file system
using sendFile:

``` js
vertx.createHttpServer().requestHandler((request) => {
  let file = "";
  if (request.path() == "/") {
    file = "index.html";
  } else if (!request.path().contains("..")) {
    file = request.path();
  }
  request.response().sendFile("web/" + file);
}).listen(8080);
```

Sending a file is asynchronous and may not complete until some time
after the call has returned. If you want to be notified when the file
has been writen you can use `sendFile`

Please see the chapter about [serving files from the
classpath](#classpath) for restrictions about the classpath resolution
or disabling it.

> **Note**
> 
> If you use `sendFile` while using HTTPS it will copy through
> user-space, since if the kernel is copying data directly from disk to
> socket it doesn’t give us an opportunity to apply any encryption.

> **Warning**
> 
> If you’re going to write web servers directly using Vert.x be careful
> that users cannot exploit the path to access files outside the
> directory from which you want to serve them or the classpath It may be
> safer instead to use Vert.x Web.

When there is a need to serve just a segment of a file, say starting
from a given byte, you can achieve this by doing:

``` js
vertx.createHttpServer().requestHandler((request) => {
  let offset = 0;
  try {
    offset = Java.type("java.lang.Long").parseLong(request.getParam("start"));
  } catch(err) {
    // error handling...
  }


  let end = Java.type("java.lang.Long").MAX_VALUE;
  try {
    end = Java.type("java.lang.Long").parseLong(request.getParam("end"));
  } catch(err) {
    // error handling...
  }


  request.response().sendFile("web/mybigfile.txt", offset, end);
}).listen(8080);
```

You are not required to supply the length if you want to send a file
starting from an offset until the end, in this case you can just do:

``` js
vertx.createHttpServer().requestHandler((request) => {
  let offset = 0;
  try {
    offset = Java.type("java.lang.Long").parseLong(request.getParam("start"));
  } catch(err) {
    // error handling...
  }


  request.response().sendFile("web/mybigfile.txt", offset);
}).listen(8080);
```

### Piping responses

The server response is a `WriteStream` instance so you can pipe to it
from any `ReadStream`, e.g. `AsyncFile`, `NetSocket`, `WebSocket` or
`HttpServerRequest`.

Here’s an example which echoes the request body back in the response for
any PUT methods. It uses a pipe for the body, so it will work even if
the HTTP request body is much larger than can fit in memory at any one
time:

``` js
vertx.createHttpServer().requestHandler((request) => {
  let response = request.response();
  if (request.method() === HttpMethod.PUT) {
    response.setChunked(true);
    request.pipeTo(response);
  } else {
    response.setStatusCode(400).end();
  }
}).listen(8080);
```

### Writing HTTP/2 frames

HTTP/2 is a framed protocol with various frames for the HTTP
request/response model. The protocol allows other kind of frames to be
sent and received.

To send such frames, you can use the `writeCustomFrame` on the response.
Here’s an example:

``` js
import { Buffer } from "@vertx/core"

let frameType = 40;
let frameStatus = 10;
let payload = Buffer.buffer("some data");

// Sending a frame to the client
response.writeCustomFrame(frameType, frameStatus, payload);
```

These frames are sent immediately and are not subject to flow control -
when such frame is sent there it may be done before other {@literal
DATA} frames.

### Stream reset

HTTP/1.x does not allow a clean reset of a request or a response stream,
for example when a client uploads a resource already present on the
server, the server needs to accept the entire response.

HTTP/2 supports stream reset at any time during the request/response:

``` js
// Reset the stream
request.response().reset();
```

By default the `NO_ERROR` (0) error code is sent, another code can sent
instead:

``` js
// Cancel the stream
request.response().reset(8);
```

The HTTP/2 specification defines the list of [error
codes](http://httpwg.org/specs/rfc7540.html#ErrorCodes) one can use.

The request handler are notified of stream reset events with the
`request handler` and `response handler`:

``` js
request.response().exceptionHandler((err) => {
  if (err instanceof StreamResetException) {
    let reset = err;
    console.log("Stream reset " + reset.getCode());
  }
});
```

### Server push

Server push is a new feature of HTTP/2 that enables sending multiple
responses in parallel for a single client request.

When a server process a request, it can push a request/response to the
client:

``` js
let response = request.response();

// Push main.js to the client
response.push(HttpMethod.GET, "/main.js", (ar) => {

  if (ar.succeeded()) {

    // The server is ready to push the response
    let pushedResponse = ar.result();

    // Send main.js response
    pushedResponse.putHeader("content-type", "application/json").end("alert(\"Push response hello\")");
  } else {
    console.log("Could not push client resource " + ar.cause());
  }
});

// Send the requested resource
response.sendFile("<html><head><script src=\"/main.js\"></script></head><body></body></html>");
```

When the server is ready to push the response, the push response handler
is called and the handler can send the response.

The push response handler may receive a failure, for instance the client
may cancel the push because it already has `main.js` in its cache and
does not want it anymore.

The `push` method must be called before the initiating response ends,
however the pushed response can be written after.

### Handling exceptions

You can set an `exceptionHandler` to receive any exceptions that happens
before the connection is passed to the `requestHandler` or to the
`webSocketHandler`, e.g during the TLS handshake.

## HTTP Compression

Vert.x comes with support for HTTP Compression out of the box.

This means you are able to automatically compress the body of the
responses before they are sent back to the client.

If the client does not support HTTP compression the responses are sent
back without compressing the body.

This allows to handle Client that support HTTP Compression and those
that not support it at the same time.

To enable compression use can configure it with
`setCompressionSupported`.

By default compression is not enabled.

When HTTP compression is enabled the server will check if the client
includes an `Accept-Encoding` header which includes the supported
compressions. Commonly used are deflate and gzip. Both are supported by
Vert.x.

If such a header is found the server will automatically compress the
body of the response with one of the supported compressions and send it
back to the client.

Whenever the response needs to be sent without compression you can set
the header `content-encoding` to `identity`:

``` js
// Disable compression and send an image
request.response().putHeader(Java.type("io.vertx.core.http.HttpHeaders").CONTENT_ENCODING, Java.type("io.vertx.core.http.HttpHeaders").IDENTITY).sendFile("/path/to/image.jpg");
```

Be aware that compression may be able to reduce network traffic but is
more CPU-intensive.

To address this latter issue Vert.x allows you to tune the 'compression
level' parameter that is native of the gzip/deflate compression
algorithms.

Compression level allows to configure gizp/deflate algorithms in terms
of the compression ratio of the resulting data and the computational
cost of the compress/decompress operation.

The compression level is an integer value ranged from '1' to '9', where
'1' means lower compression ratio but fastest algorithm and '9' means
maximum compression ratio available but a slower algorithm.

Using compression levels higher that 1-2 usually allows to save just
some bytes in size - the gain is not linear, and depends on the specific
data to be compressed - but it comports a non-trascurable cost in term
of CPU cycles required to the server while generating the compressed
response data ( Note that at moment Vert.x doesn’t support any form
caching of compressed response data, even for static files, so the
compression is done on-the-fly at every request body generation ) and in
the same way it affects client(s) while decoding (inflating) received
responses, operation that becomes more CPU-intensive the more the level
increases.

By default - if compression is enabled via `setCompressionSupported` -
Vert.x will use '6' as compression level, but the parameter can be
configured to address any case with `setCompressionLevel`.

## Creating an HTTP client

You create an `HttpClient` instance with default options as follows:

``` js
let client = vertx.createHttpClient();
```

If you want to configure options for the client, you create it as
follows:

``` js
let options = new HttpClientOptions()
  .setKeepAlive(false);
let client = vertx.createHttpClient(options);
```

Vert.x supports HTTP/2 over TLS `h2` and over TCP `h2c`.

By default the http client performs HTTP/1.1 requests, to perform HTTP/2
requests the `setProtocolVersion` must be set to `HTTP_2`.

For `h2` requests, TLS must be enabled with *Application-Layer Protocol
Negotiation*:

``` js
let options = new HttpClientOptions()
  .setProtocolVersion("HTTP_2")
  .setSsl(true)
  .setUseAlpn(true)
  .setTrustAll(true);

let client = vertx.createHttpClient(options);
```

For `h2c` requests, TLS must be disabled, the client will do an HTTP/1.1
requests and try an upgrade to HTTP/2:

``` js
let options = new HttpClientOptions()
  .setProtocolVersion("HTTP_2");

let client = vertx.createHttpClient(options);
```

`h2c` connections can also be established directly, i.e connection
started with a prior knowledge, when `setHttp2ClearTextUpgrade` options
is set to false: after the connection is established, the client will
send the HTTP/2 connection preface and expect to receive the same
preface from the server.

The http server may not support HTTP/2, the actual version can be
checked with `version` when the response arrives.

When a clients connects to an HTTP/2 server, it sends to the server its
`initial settings`. The settings define how the server can use the
connection, the default initial settings for a client are the default
values defined by the HTTP/2 RFC.

## Logging network client activity

For debugging purposes, network activity can be logged.

``` js
let options = new HttpClientOptions()
  .setLogActivity(true);
let client = vertx.createHttpClient(options);
```

See the chapter on [logging network activity](#logging_network_activity)
for a detailed explanation.

## Making requests

The http client is very flexible and there are various ways you can make
requests with it.

Often you want to make many requests to the same host/port with an http
client. To avoid you repeating the host/port every time you make a
request you can configure the client with a default host/port:

``` js
// Set the default host
let options = new HttpClientOptions()
  .setDefaultHost("wibble.com");
// Can also set default port if you want...
let client = vertx.createHttpClient(options);
client.getNow("/some-uri", (response) => {
  console.log("Received response with status code " + response.statusCode());
});
```

Alternatively if you find yourself making lots of requests to different
host/ports with the same client you can simply specify the host/port
when doing the request.

``` js
let client = vertx.createHttpClient();

// Specify both port and host name
client.getNow(8080, "myserver.mycompany.com", "/some-uri", (response) => {
  console.log("Received response with status code " + response.statusCode());
});

// This time use the default port 80 but specify the host name
client.getNow("foo.othercompany.com", "/other-uri", (response) => {
  console.log("Received response with status code " + response.statusCode());
});
```

Both methods of specifying host/port are supported for all the different
ways of making requests with the client.

### Simple requests with no request body

Often, you’ll want to make HTTP requests with no request body. This is
usually the case with HTTP GET, OPTIONS and HEAD requests.

The simplest way to do this with the Vert.x http client is using the
methods suffixed with `Now`. For example `getNow`.

These methods create the http request and send it in a single method
call and allow you to provide a handler that will be called with the
http response when it comes back.

``` js
let client = vertx.createHttpClient();

// Send a GET request
client.getNow("/some-uri", (response) => {
  console.log("Received response with status code " + response.statusCode());
});

// Send a GET request
client.headNow("/other-uri", (response) => {
  console.log("Received response with status code " + response.statusCode());
});
```

### Writing general requests

At other times you don’t know the request method you want to send until
run-time. For that use case we provide general purpose request methods
such as `request` which allow you to specify the HTTP method at
run-time:

``` js
let client = vertx.createHttpClient();

client.request(HttpMethod.GET, "some-uri", (response) => {
  console.log("Received response with status code " + response.statusCode());
}).end();

client.request(HttpMethod.POST, "foo-uri", (response) => {
  console.log("Received response with status code " + response.statusCode());
}).end("some-data");
```

### Writing request bodies

Sometimes you’ll want to write requests which have a body, or perhaps
you want to write headers to a request before sending it.

To do this you can call one of the specific request methods such as
`post` or one of the general purpose request methods such as `request`.

These methods don’t send the request immediately, but instead return an
instance of `HttpClientRequest` which can be used to write to the
request body or write headers.

Here are some examples of writing a POST request with a body: m

``` js
let client = vertx.createHttpClient();

let request = client.post("some-uri", (response) => {
  console.log("Received response with status code " + response.statusCode());
});

// Now do stuff with the request
request.putHeader("content-length", "1000");
request.putHeader("content-type", "text/plain");
request.write(body);

// Make sure the request is ended when you're done with it
request.end();

// Or fluently:

client.post("some-uri", (response) => {
  console.log("Received response with status code " + response.statusCode());
}).putHeader("content-length", "1000").putHeader("content-type", "text/plain").write(body).end();

// Or event more simply:

client.post("some-uri", (response) => {
  console.log("Received response with status code " + response.statusCode());
}).putHeader("content-type", "text/plain").end(body);
```

Methods exist to write strings in UTF-8 encoding and in any specific
encoding and to write buffers:

``` js
import { Buffer } from "@vertx/core"

// Write string encoded in UTF-8
request.write("some data");

// Write string encoded in specific encoding
request.write("some other data", "UTF-16");

// Write a buffer
let buffer = Buffer.buffer();
buffer.appendInt(123).appendLong(245);
request.write(buffer);
```

If you are just writing a single string or buffer to the HTTP request
you can write it and end the request in a single call to the `end`
function.

``` js
import { Buffer } from "@vertx/core"

// Write string and end the request (send it) in a single call
request.end("some simple data");

// Write buffer and end the request (send it) in a single call
let buffer = Buffer.buffer().appendDouble(12.34).appendLong(432);
request.end(buffer);
```

When you’re writing to a request, the first call to `write` will result
in the request headers being written out to the wire.

The actual write is asynchronous and might not occur until some time
after the call has returned.

Non-chunked HTTP requests with a request body require a `Content-Length`
header to be provided.

Consequently, if you are not using chunked HTTP then you must set the
`Content-Length` header before writing to the request, as it will be too
late otherwise.

If you are calling one of the `end` methods that take a string or buffer
then Vert.x will automatically calculate and set the `Content-Length`
header before writing the request body.

If you are using HTTP chunking a a `Content-Length` header is not
required, so you do not have to calculate the size up-front.

### Writing request headers

You can write headers to a request using the `headers` multi-map as
follows:

``` js
// Write some headers using the headers() multimap

let headers = request.headers();
headers.set("content-type", "application/json").set("other-header", "foo");
```

The headers are an instance of `MultiMap` which provides operations for
adding, setting and removing entries. Http headers allow more than one
value for a specific key.

You can also write headers using `putHeader`

``` js
// Write some headers using the putHeader method

request.putHeader("content-type", "application/json").putHeader("other-header", "foo");
```

If you wish to write headers to the request you must do so before any
part of the request body is written.

### Non standard HTTP methods

The `OTHER` HTTP method is used for non standard methods, when this
method is used, `setRawMethod` must be used to set the raw method to
send to the server.

### Ending HTTP requests

Once you have finished with the HTTP request you must end it with one of
the `end` operations.

Ending a request causes any headers to be written, if they have not
already been written and the request to be marked as complete.

Requests can be ended in several ways. With no arguments the request is
simply ended:

``` js
request.end();
```

Or a string or buffer can be provided in the call to `end`. This is like
calling `write` with the string or buffer before calling `end` with no
arguments

``` js
import { Buffer } from "@vertx/core"
// End the request with a string
request.end("some-data");

// End it with a buffer
let buffer = Buffer.buffer().appendFloat(12.3).appendInt(321);
request.end(buffer);
```

### Chunked HTTP requests

Vert.x supports [HTTP Chunked Transfer
Encoding](http://en.wikipedia.org/wiki/Chunked_transfer_encoding) for
requests.

This allows the HTTP request body to be written in chunks, and is
normally used when a large request body is being streamed to the server,
whose size is not known in advance.

You put the HTTP request into chunked mode using `setChunked`.

In chunked mode each call to write will cause a new chunk to be written
to the wire. In chunked mode there is no need to set the
`Content-Length` of the request up-front.

``` js
request.setChunked(true);

// Write some chunks
for (let i = 0;i < 10;i++) {
  request.write("this-is-chunk-" + i);
}

request.end();
```

### Request timeouts

You can set a timeout for a specific http request using `setTimeout`.

If the request does not return any data within the timeout period an
exception will be passed to the exception handler (if provided) and the
request will be closed.

### Handling exceptions

You can handle exceptions corresponding to a request by setting an
exception handler on the `HttpClientRequest` instance:

``` js
let request = client.post("some-uri", (response) => {
  console.log("Received response with status code " + response.statusCode());
});
request.exceptionHandler((e) => {
  console.log("Received exception: " + e.getMessage());
  e.printStackTrace();
});
```

This does not handle non *2xx* response that need to be handled in the
`HttpClientResponse` code:

``` js
let request = client.post("some-uri", (response) => {
  if (response.statusCode() === 200) {
    console.log("Everything fine");
    return
  }
  if (response.statusCode() === 500) {
    console.log("Unexpected behavior on the server side");
    return
  }
});
request.end();
```

> **Important**
> 
> `XXXNow` methods cannot receive an exception handler.

### Specifying a handler on the client request

Instead of providing a response handler in the call to create the client
request object, alternatively, you can not provide a handler when the
request is created and set it later on the request object itself, using
`handler`, for example:

``` js
let request = client.post("some-uri");
request.handler((response) => {
  console.log("Received response with status code " + response.statusCode());
});
```

### Using the request as a stream

The `HttpClientRequest` instance is also a `WriteStream` which means you
can pump to it from any `ReadStream` instance.

For, example, you could pump a file on disk to a http request body as
follows:

``` js
import { Pump } from "@vertx/core"

request.setChunked(true);
let pump = Pump.pump(file, request);
file.endHandler((v) => {
  request.end();
});
pump.start();
```

### Writing HTTP/2 frames

HTTP/2 is a framed protocol with various frames for the HTTP
request/response model. The protocol allows other kind of frames to be
sent and received.

To send such frames, you can use the `write` on the request. Here’s an
example:

``` js
import { Buffer } from "@vertx/core"

let frameType = 40;
let frameStatus = 10;
let payload = Buffer.buffer("some data");

// Sending a frame to the server
request.writeCustomFrame(frameType, frameStatus, payload);
```

### Stream reset

HTTP/1.x does not allow a clean reset of a request or a response stream,
for example when a client uploads a resource already present on the
server, the server needs to accept the entire response.

HTTP/2 supports stream reset at any time during the request/response:

``` js
request.reset();
```

By default the NO\_ERROR (0) error code is sent, another code can sent
instead:

``` js
request.reset(8);
```

The HTTP/2 specification defines the list of [error
codes](http://httpwg.org/specs/rfc7540.html#ErrorCodes) one can use.

The request handler are notified of stream reset events with the
`request handler` and `response handler`:

``` js
request.exceptionHandler((err) => {
  if (err instanceof StreamResetException) {
    let reset = err;
    console.log("Stream reset " + reset.getCode());
  }
});
```

## Handling HTTP responses

You receive an instance of `HttpClientResponse` into the handler that
you specify in of the request methods or by setting a handler directly
on the `HttpClientRequest` object.

You can query the status code and the status message of the response
with `statusCode` and `statusMessage`.

``` js
client.getNow("some-uri", (response) => {
  // the status code - e.g. 200 or 404
  console.log("Status code is " + response.statusCode());

  // the status message e.g. "OK" or "Not Found".
  console.log("Status message is " + response.statusMessage());
});
```

### Using the response as a stream

The `HttpClientResponse` instance is also a `ReadStream` which means you
can pipe it to any `WriteStream` instance.

### Response headers and trailers

Http responses can contain headers. Use `headers` to get the headers.

The object returned is a `MultiMap` as HTTP headers can contain multiple
values for single keys.

``` js
let contentType = response.headers().get("content-type");
let contentLength = response.headers().get("content-lengh");
```

Chunked HTTP responses can also contain trailers - these are sent in the
last chunk of the response body.

You use `trailers` to get the trailers. Trailers are also a `MultiMap`.

### Reading the request body

The response handler is called when the headers of the response have
been read from the wire.

If the response has a body this might arrive in several pieces some time
after the headers have been read. We don’t wait for all the body to
arrive before calling the response handler as the response could be very
large and we might be waiting a long time, or run out of memory for
large responses.

As parts of the response body arrive, the `handler` is called with a
`Buffer` representing the piece of the body:

``` js
client.getNow("some-uri", (response) => {

  response.handler((buffer) => {
    console.log("Received a part of the response body: " + buffer);
  });
});
```

If you know the response body is not very large and want to aggregate it
all in memory before handling it, you can either aggregate it yourself:

``` js
import { Buffer } from "@vertx/core"

client.getNow("some-uri", (response) => {

  // Create an empty buffer
  let totalBuffer = Buffer.buffer();

  response.handler((buffer) => {
    console.log("Received a part of the response body: " + buffer.length());

    totalBuffer.appendBuffer(buffer);
  });

  response.endHandler((v) => {
    // Now all the body has been read
    console.log("Total response body length is " + totalBuffer.length());
  });
});
```

Or you can use the convenience `bodyHandler` which is called with the
entire body when the response has been fully read:

``` js
client.getNow("some-uri", (response) => {

  response.bodyHandler((totalBuffer) => {
    // Now all the body has been read
    console.log("Total response body length is " + totalBuffer.length());
  });
});
```

### Response end handler

The response `endHandler` is called when the entire response body has
been read or immediately after the headers have been read and the
response handler has been called if there is no body.

### Reading cookies from the response

You can retrieve the list of cookies from a response using `cookies`.

Alternatively you can just parse the `Set-Cookie` headers yourself in
the response.

### 30x redirection handling

The client can be configured to follow HTTP redirections provided by the
`Location` response header when the client receives:

  - a `301`, `302`, `307` or `308` status code along with a HTTP GET or
    HEAD method

  - a `303` status code, in addition the directed request perform an
    HTTP GET methodn

Here’s an example:

``` js
client.get("some-uri", (response) => {
  console.log("Received response with status code " + response.statusCode());
}).setFollowRedirects(true).end();
```

The maximum redirects is `16` by default and can be changed with
`setMaxRedirects`.

``` js
let client = vertx.createHttpClient(new HttpClientOptions()
  .setMaxRedirects(32));

client.get("some-uri", (response) => {
  console.log("Received response with status code " + response.statusCode());
}).setFollowRedirects(true).end();
```

One size does not fit all and the default redirection policy may not be
adapted to your needs.

The default redirection policy can changed with a custom implementation:

``` js
import { Future } from "@vertx/core"

client.redirectHandler((response) => {

  // Only follow 301 code
  if (response.statusCode() === 301 && (response.getHeader("Location") !== null && response.getHeader("Location") !== undefined)) {

    // Compute the redirect URI
    let absoluteURI = resolveURI(response.request().absoluteURI(), response.getHeader("Location"));

    // Create a new ready to use request that the client will use
    return Future.succeededFuture(client.getAbs(absoluteURI))
  }

  // We don't redirect
  return null
});
```

The policy handles the original `HttpClientResponse` received and
returns either `null` or a `Future<HttpClientRequest>`.

  - when `null` is returned, the original response is processed

  - when a future is returned, the request will be sent on its
    successful completion

  - when a future is returned, the exception handler set on the request
    is called on its failure

The returned request must be unsent so the original request handlers can
be sent and the client can send it after.

Most of the original request settings will be propagated to the new
request:

  - request headers, unless if you have set some headers (including
    `setHost`)

  - request body unless the returned request uses a `GET` method

  - response handler

  - request exception handler

  - request timeout

### 100-Continue handling

According to the [HTTP 1.1
specification](http://www.w3.org/Protocols/rfc2616/rfc2616-sec8.html) a
client can set a header `Expect: 100-Continue` and send the request
header before sending the rest of the request body.

The server can then respond with an interim response status `Status: 100
(Continue)` to signify to the client that it is ok to send the rest of
the body.

The idea here is it allows the server to authorise and accept/reject the
request before large amounts of data are sent. Sending large amounts of
data if the request might not be accepted is a waste of bandwidth and
ties up the server in reading data that it will just discard.

Vert.x allows you to set a `continueHandler` on the client request
object

This will be called if the server sends back a `Status: 100 (Continue)`
response to signify that it is ok to send the rest of the request.

This is used in conjunction with
\`[sendHead](/es4x/@vertx/core/classes/httpclientrequest.html#sendhead)\`to
send the head of the request.

Here’s an example:

``` js
let request = client.put("some-uri", (response) => {
  console.log("Received response with status code " + response.statusCode());
});

request.putHeader("Expect", "100-Continue");

request.continueHandler((v) => {
  // OK to send rest of body
  request.write("Some data");
  request.write("Some more data");
  request.end();
});
```

On the server side a Vert.x http server can be configured to
automatically send back 100 Continue interim responses when it receives
an `Expect: 100-Continue` header.

This is done by setting the option `setHandle100ContinueAutomatically`.

If you’d prefer to decide whether to send back continue responses
manually, then this property should be set to `false` (the default),
then you can inspect the headers and call `writeContinue` to have the
client continue sending the body:

``` js
httpServer.requestHandler((request) => {
  if (request.getHeader("Expect").equalsIgnoreCase("100-Continue")) {

    // Send a 100 continue response
    request.response().writeContinue();

    // The client should send the body when it receives the 100 response
    request.bodyHandler((body) => {
      // Do something with body
    });

    request.endHandler((v) => {
      request.response().end();
    });
  }
});
```

You can also reject the request by sending back a failure status code
directly: in this case the body should either be ignored or the
connection should be closed (100-Continue is a performance hint and
cannot be a logical protocol constraint):

``` js
httpServer.requestHandler((request) => {
  if (request.getHeader("Expect").equalsIgnoreCase("100-Continue")) {

    //
    let rejectAndClose = true;
    if (rejectAndClose) {

      // Reject with a failure code and close the connection
      // this is probably best with persistent connection
      request.response().setStatusCode(405).putHeader("Connection", "close").end();
    } else {

      // Reject with a failure code and ignore the body
      // this may be appropriate if the body is small
      request.response().setStatusCode(405).end();
    }
  }
});
```

### Client push

Server push is a new feature of HTTP/2 that enables sending multiple
responses in parallel for a single client request.

A push handler can be set on a request to receive the request/response
pushed by the server:

``` js
let request = client.get("/index.html", (response) => {
  // Process index.html response
});

// Set a push handler to be aware of any resource pushed by the server
request.pushHandler((pushedRequest) => {

  // A resource is pushed for this request
  console.log("Server pushed " + pushedRequest.path());

  // Set an handler for the response
  pushedRequest.handler((pushedResponse) => {
    console.log("The response for the pushed request");
  });
});

// End the request
request.end();
```

If the client does not want to receive a pushed request, it can reset
the stream:

``` js
request.pushHandler((pushedRequest) => {
  if (pushedRequest.path() == "/main.js") {
    pushedRequest.reset();
  } else {
    // Handle it
  }
});
```

When no handler is set, any stream pushed will be automatically
cancelled by the client with a stream reset (`8` error code).

### Receiving custom HTTP/2 frames

HTTP/2 is a framed protocol with various frames for the HTTP
request/response model. The protocol allows other kind of frames to be
sent and received.

To receive custom frames, you can use the customFrameHandler on the
request, this will get called every time a custom frame arrives. Here’s
an example:

``` js
response.customFrameHandler((frame) => {

  console.log("Received a frame type=" + frame.type() + " payload" + frame.payload().toString());
});
```

## Enabling compression on the client

The http client comes with support for HTTP Compression out of the box.

This means the client can let the remote http server know that it
supports compression, and will be able to handle compressed response
bodies.

An http server is free to either compress with one of the supported
compression algorithms or to send the body back without compressing it
at all. So this is only a hint for the Http server which it may ignore
at will.

To tell the http server which compression is supported by the client it
will include an `Accept-Encoding` header with the supported compression
algorithm as value. Multiple compression algorithms are supported. In
case of Vert.x this will result in the following header added:

Accept-Encoding: gzip, deflate

The server will choose then from one of these. You can detect if a
server ompressed the body by checking for the `Content-Encoding` header
in the response sent back from it.

If the body of the response was compressed via gzip it will include for
example the following header:

Content-Encoding: gzip

To enable compression set `setTryUseCompression` on the options used
when creating the client.

By default compression is disabled.

## HTTP/1.x pooling and keep alive

Http keep alive allows http connections to be used for more than one
request. This can be a more efficient use of connections when you’re
making multiple requests to the same server.

For HTTP/1.x versions, the http client supports pooling of connections,
allowing you to reuse connections between requests.

For pooling to work, keep alive must be true using `setKeepAlive` on the
options used when configuring the client. The default value is true.

When keep alive is enabled. Vert.x will add a `Connection: Keep-Alive`
header to each HTTP/1.0 request sent. When keep alive is disabled.
Vert.x will add a `Connection: Close` header to each HTTP/1.1 request
sent to signal that the connection will be closed after completion of
the response.

The maximum number of connections to pool **for each server** is
configured using `setMaxPoolSize`

When making a request with pooling enabled, Vert.x will create a new
connection if there are less than the maximum number of connections
already created for that server, otherwise it will add the request to a
queue.

Keep alive connections will be closed by the client automatically after
a timeout. The timeout can be specified by the server using the
`keep-alive` header:

    keep-alive: timeout=30

You can set the default timeout using `setKeepAliveTimeout` - any
connections not used within this timeout will be closed. Please note the
timeout value is in seconds not milliseconds.

## HTTP/1.1 pipe-lining

The client also supports pipe-lining of requests on a connection.

Pipe-lining means another request is sent on the same connection before
the response from the preceding one has returned. Pipe-lining is not
appropriate for all requests.

To enable pipe-lining, it must be enabled using `setPipelining`. By
default pipe-lining is disabled.

When pipe-lining is enabled requests will be written to connections
without waiting for previous responses to return.

The number of pipe-lined requests over a single connection is limited by
`setPipeliningLimit`. This option defines the maximum number of http
requests sent to the server awaiting for a response. This limit ensures
the fairness of the distribution of the client requests over the
connections to the same server.

## HTTP/2 multiplexing

HTTP/2 advocates to use a single connection to a server, by default the
http client uses a single connection for each server, all the streams to
the same server are multiplexed over the same connection.

When the clients needs to use more than a single connection and use
pooling, the `setHttp2MaxPoolSize` shall be used.

When it is desirable to limit the number of multiplexed streams per
connection and use a connection pool instead of a single connection,
`setHttp2MultiplexingLimit` can be used.

``` js
let clientOptions = new HttpClientOptions()
  .setHttp2MultiplexingLimit(10)
  .setHttp2MaxPoolSize(3);

// Uses up to 3 connections and up to 10 streams per connection
let client = vertx.createHttpClient(clientOptions);
```

The multiplexing limit for a connection is a setting set on the client
that limits the number of streams of a single connection. The effective
value can be even lower if the server sets a lower limit with the
`SETTINGS_MAX_CONCURRENT_STREAMS` setting.

HTTP/2 connections will not be closed by the client automatically. To
close them you can call `close` or close the client instance.

Alternatively you can set idle timeout using `setIdleTimeout` - any
connections not used within this timeout will be closed. Please note the
idle timeout value is in seconds not milliseconds.

## HTTP connections

The `HttpConnection` offers the API for dealing with HTTP connection
events, lifecycle and settings.

HTTP/2 implements fully the `HttpConnection` API.

HTTP/1.x implements partially the `HttpConnection` API: only the close
operation, the close handler and exception handler are implemented. This
protocol does not provide semantics for the other operations.

### Server connections

The `connection` method returns the request connection on the server:

``` js
let connection = request.connection();
```

A connection handler can be set on the server to be notified of any
incoming connection:

``` js
let server = vertx.createHttpServer(http2Options);

server.connectionHandler((connection) => {
  console.log("A client connected");
});
```

### Client connections

The `connection` method returns the request connection on the client:

``` js
let connection = request.connection();
```

A connection handler can be set on the client to be notified when a
connection has been established happens:

``` js
client.connectionHandler((connection) => {
  console.log("Connected to the server");
});
```

### Connection settings

The configuration of an HTTP/2 is configured by the `Http2Settings` data
object.

Each endpoint must respect the settings sent by the other side of the
connection.

When a connection is established, the client and the server exchange
initial settings. Initial settings are configured by
`setInitialSettings` on the client and `setInitialSettings` on the
server.

The settings can be changed at any time after the connection is
established:

``` js
connection.updateSettings(new Http2Settings()
  .setMaxConcurrentStreams(100));
```

As the remote side should acknowledge on reception of the settings
update, it’s possible to give a callback to be notified of the
acknowledgment:

``` js
connection.updateSettings(new Http2Settings()
  .setMaxConcurrentStreams(100), (ar) => {
  if (ar.succeeded()) {
    console.log("The settings update has been acknowledged ");
  }
});
```

Conversely the `remoteSettingsHandler` is notified when the new remote
settings are received:

``` js
connection.remoteSettingsHandler((settings) => {
  console.log("Received new settings");
});
```

> **Note**
> 
> this only applies to the HTTP/2 protocol

### Connection ping

HTTP/2 connection ping is useful for determining the connection
round-trip time or check the connection validity: `ping` sends a
{@literal PING} frame to the remote endpoint:

``` js
import { Buffer } from "@vertx/core"
let data = Buffer.buffer();
for (let i = 0;i < 8;i++) {
  data.appendByte(i);
}
connection.ping(data, (pong) => {
  console.log("Remote side replied");
});
```

Vert.x will send automatically an acknowledgement when a {@literal PING}
frame is received, an handler can be set to be notified for each ping
received:

``` js
connection.pingHandler((ping) => {
  console.log("Got pinged by remote side");
});
```

The handler is just notified, the acknowledgement is sent whatsoever.
Such feature is aimed for implementing protocols on top of HTTP/2.

> **Note**
> 
> this only applies to the HTTP/2 protocol

### Connection shutdown and go away

Calling `shutdown` will send a {@literal GOAWAY} frame to the remote
side of the connection, asking it to stop creating streams: a client
will stop doing new requests and a server will stop pushing responses.
After the {@literal GOAWAY} frame is sent, the connection waits some
time (30 seconds by default) until all current streams closed and close
the connection:

``` js
connection.shutdown();
```

The `shutdownHandler` notifies when all streams have been closed, the
connection is not yet closed.

It’s possible to just send a {@literal GOAWAY} frame, the main
difference with a shutdown is that it will just tell the remote side of
the connection to stop creating new streams without scheduling a
connection close:

``` js
connection.goAway(0);
```

Conversely, it is also possible to be notified when {@literal GOAWAY}
are received:

``` js
connection.goAwayHandler((goAway) => {
  console.log("Received a go away frame");
});
```

The `shutdownHandler` will be called when all current streams have been
closed and the connection can be closed:

``` js
connection.goAway(0);
connection.shutdownHandler((v) => {

  // All streams are closed, close the connection
  connection.close();
});
```

This applies also when a {@literal GOAWAY} is received.

> **Note**
> 
> this only applies to the HTTP/2 protocol

### Connection close

Connection `close` closes the connection:

  - it closes the socket for HTTP/1.x

  - a shutdown with no delay for HTTP/2, the {@literal GOAWAY} frame
    will still be sent before the connection is closed. \*

The `closeHandler` notifies when a connection is closed.

## HttpClient usage

The HttpClient can be used in a Verticle or embedded.

When used in a Verticle, the Verticle **should use its own client
instance**.

More generally a client should not be shared between different Vert.x
contexts as it can lead to unexpected behavior.

For example a keep-alive connection will call the client handlers on the
context of the request that opened the connection, subsequent requests
will use the same context.

When this happen Vert.x detects it and log a warn:

    Reusing a connection with a different context: an HttpClient is probably shared between different Verticles

The HttpClient can be embedded in a non Vert.x thread like a unit test
or a plain java `main`: the client handlers will be called by different
Vert.x threads and contexts, such contexts are created as needed. For
production this usage is not recommended.

## Server sharing

When several HTTP servers listen on the same port, vert.x orchestrates
the request handling using a round-robin strategy.

Let’s take a verticle creating a HTTP server such as:

**io.vertx.examples.http.sharing.HttpServerVerticle.**

``` js
vertx.createHttpServer().requestHandler((request) => {
  request.response().end("Hello from server " + this);
}).listen(8080);
```

This service is listening on the port 8080. So, when this verticle is
instantiated multiple times as with: `vertx run
io.vertx.examples.http.sharing.HttpServerVerticle -instances 2`, what’s
happening ? If both verticles would bind to the same port, you would
receive a socket exception. Fortunately, vert.x is handling this case
for you. When you deploy another server on the same host and port as an
existing server it doesn’t actually try and create a new server
listening on the same host/port. It binds only once to the socket. When
receiving a request it calls the server handlers following a round robin
strategy.

Let’s now imagine a client such as:

``` js
vertx.setPeriodic(100, (l) => {
  vertx.createHttpClient().getNow(8080, "localhost", "/", (resp) => {
    resp.bodyHandler((body) => {
      console.log(body.toString("ISO-8859-1"));
    });
  });
});
```

Vert.x delegates the requests to one of the server sequentially:

    Hello from i.v.e.h.s.HttpServerVerticle@1
    Hello from i.v.e.h.s.HttpServerVerticle@2
    Hello from i.v.e.h.s.HttpServerVerticle@1
    Hello from i.v.e.h.s.HttpServerVerticle@2
    ...

Consequently the servers can scale over available cores while each
Vert.x verticle instance remains strictly single threaded, and you don’t
have to do any special tricks like writing load-balancers in order to
scale your server on your multi-core machine.

## Using HTTPS with Vert.x

Vert.x http servers and clients can be configured to use HTTPS in
exactly the same way as net servers.

Please see [configuring net servers to use SSL](#ssl) for more
information.

SSL can also be enabled/disabled per request with `RequestOptions` or
when specifying a scheme with `requestAbs` method.

``` js
client.getNow(new RequestOptions()
  .setHost("localhost")
  .setPort(8080)
  .setURI("/")
  .setSsl(true), (response) => {
  console.log("Received response with status code " + response.statusCode());
});
```

The `setSsl` setting acts as the default client setting.

The `setSsl` overrides the default client setting

  - setting the value to `false` will disable SSL/TLS even if the client
    is configured to use SSL/TLS

  - setting the value to `true` will enable SSL/TLS even if the client
    is configured to not use SSL/TLS, the actual client SSL/TLS (such as
    trust, key/certificate, ciphers, ALPN, …​) will be reused

Likewise `requestAbs` scheme also overrides the default client setting.

### Server Name Indication (SNI)

Vert.x http servers can be configured to use SNI in exactly the same way
as {@linkplain io.vertx.core.net net servers}.

Vert.x http client will present the actual hostname as *server name*
during the TLS handshake.

## WebSockets

[WebSockets](http://en.wikipedia.org/wiki/WebSocket) are a web
technology that allows a full duplex socket-like connection between HTTP
servers and HTTP clients (typically browsers).

Vert.x supports WebSockets on both the client and server-side.

### WebSockets on the server

There are two ways of handling WebSockets on the server side.

#### WebSocket handler

The first way involves providing a `webSocketHandler` on the server
instance.

When a WebSocket connection is made to the server, the handler will be
called, passing in an instance of `ServerWebSocket`.

``` js
server.webSocketHandler((webSocket) => {
  console.log("Connected!");
});
```

You can choose to reject the WebSocket by calling `reject`.

``` js
server.webSocketHandler((webSocket) => {
  if (webSocket.path() == "/myapi") {
    webSocket.reject();
  } else {
    // Do something
  }
});
```

You can perform an asynchronous handshake by calling `setHandshake` with
a `Future`:

``` js
import { Promise } from "@vertx/core"
server.webSocketHandler((webSocket) => {
  let promise = Promise.promise();
  webSocket.setHandshake(promise.future());
  authenticate(webSocket.headers(), (ar) => {
    if (ar.succeeded()) {
      // Terminate the handshake with the status code 101 (Switching Protocol)
      // Reject the handshake with 401 (Unauthorized)
      promise.complete(ar.succeeded() ? 101 : 401);
    } else {
      // Will send a 500 error
      promise.fail(ar.cause());
    }
  });
});
```

> **Note**
> 
> the WebSocket will be automatically accepted after the handler is
> called unless the WebSocket’s handshake has been set

#### Upgrading to WebSocket

The second way of handling WebSockets is to handle the HTTP Upgrade
request that was sent from the client, and call `upgrade` on the server
request.

``` js
server.requestHandler((request) => {
  if (request.path() == "/myapi") {

    let webSocket = request.upgrade();
    // Do something

  } else {
    // Reject
    request.response().setStatusCode(400).end();
  }
});
```

#### The server WebSocket

The `ServerWebSocket` instance enables you to retrieve the `headers`,
`path`, `query` and `URI` of the HTTP request of the WebSocket
handshake.

### WebSockets on the client

The Vert.x `HttpClient` supports WebSockets.

You can connect a WebSocket to a server using one of the `webSocket`
operations and providing a handler.

The handler will be called with an instance of `WebSocket` when the
connection has been made:

``` js
client.webSocket("/some-uri", (res) => {
  if (res.succeeded()) {
    let ws = res.result();
    console.log("Connected!");
  }
});
```

### Writing messages to WebSockets

If you wish to write a single WebSocket message to the WebSocket you can
do this with `writeBinaryMessage` or `writeTextMessage` :

``` js
import { Buffer } from "@vertx/core"
// Write a simple binary message
let buffer = Buffer.buffer().appendInt(123).appendFloat(1.23);
webSocket.writeBinaryMessage(buffer);

// Write a simple text message
let message = "hello";
webSocket.writeTextMessage(message);
```

If the WebSocket message is larger than the maximum WebSocket frame size
as configured with `setMaxWebSocketFrameSize` then Vert.x will split it
into multiple WebSocket frames before sending it on the wire.

### Writing frames to WebSockets

A WebSocket message can be composed of multiple frames. In this case the
first frame is either a *binary* or *text* frame followed by zero or
more *continuation* frames.

The last frame in the message is marked as *final*.

To send a message consisting of multiple frames you create frames using
`WebSocketFrame.binaryFrame` , `WebSocketFrame.textFrame` or
`WebSocketFrame.continuationFrame` and write them to the WebSocket using
`writeFrame`.

Here’s an example for binary frames:

``` js
import { WebSocketFrame } from "@vertx/core"

let frame1 = WebSocketFrame.binaryFrame(buffer1, false);
webSocket.writeFrame(frame1);

let frame2 = WebSocketFrame.continuationFrame(buffer2, false);
webSocket.writeFrame(frame2);

// Write the final frame
let frame3 = WebSocketFrame.continuationFrame(buffer2, true);
webSocket.writeFrame(frame3);
```

In many cases you just want to send a WebSocket message that consists of
a single final frame, so we provide a couple of shortcut methods to do
that with `writeFinalBinaryFrame` and `writeFinalTextFrame`.

Here’s an example:

``` js
import { Buffer } from "@vertx/core"

// Send a WebSocket messages consisting of a single final text frame:

webSocket.writeFinalTextFrame("Geronimo!");

// Send a WebSocket messages consisting of a single final binary frame:

let buff = Buffer.buffer().appendInt(12).appendString("foo");

webSocket.writeFinalBinaryFrame(buff);
```

### Reading frames from WebSockets

To read frames from a WebSocket you use the `frameHandler`.

The frame handler will be called with instances of `WebSocketFrame` when
a frame arrives, for example:

``` js
webSocket.frameHandler((frame) => {
  console.log("Received a frame of size!");
});
```

### Closing WebSockets

Use `close` to close the WebSocket connection when you have finished
with it.

### Piping WebSockets

The `WebSocket` instance is also a `ReadStream` and a `WriteStream` so
it can be used with pipes.

When using a WebSocket as a write stream or a read stream it can only be
used with WebSockets connections that are used with binary frames that
are no split over multiple frames.

### Event bus handlers

Every WebSocket automatically registers two handler on the event bus,
and when any data are received in this handler, it writes them to
itself. Those are local subscriptions not routed on the cluster.

This enables you to write data to a WebSocket which is potentially in a
completely different verticle sending data to the address of that
handler.

The addresses of the handlers are given by `binaryHandlerID` and
`textHandlerID`.

## Using a proxy for HTTP/HTTPS connections

The http client supports accessing http/https URLs via a HTTP proxy
(e.g. Squid) or *SOCKS4a* or *SOCKS5* proxy. The CONNECT protocol uses
HTTP/1.x but can connect to HTTP/1.x and HTTP/2 servers.

Connecting to h2c (unencrypted HTTP/2 servers) is likely not supported
by http proxies since they will support HTTP/1.1 only.

The proxy can be configured in the `HttpClientOptions` by setting a
`ProxyOptions` object containing proxy type, hostname, port and
optionally username and password.

Here’s an example of using an HTTP proxy:

``` js
let options = new HttpClientOptions()
  .setProxyOptions(new ProxyOptions()
    .setType("HTTP")
    .setHost("localhost")
    .setPort(3128)
    .setUsername("username")
    .setPassword("secret"));
let client = vertx.createHttpClient(options);
```

When the client connects to an http URL, it connects to the proxy server
and provides the full URL in the HTTP request ("GET
<http://www.somehost.com/path/file.html> HTTP/1.1").

When the client connects to an https URL, it asks the proxy to create a
tunnel to the remote host with the CONNECT method.

For a SOCKS5 proxy:

``` js
let options = new HttpClientOptions()
  .setProxyOptions(new ProxyOptions()
    .setType("SOCKS5")
    .setHost("localhost")
    .setPort(1080)
    .setUsername("username")
    .setPassword("secret"));
let client = vertx.createHttpClient(options);
```

The DNS resolution is always done on the proxy server, to achieve the
functionality of a SOCKS4 client, it is necessary to resolve the DNS
address locally.

### Handling of other protocols

The HTTP proxy implementation supports getting ftp:// urls if the proxy
supports that, which isn’t available in non-proxy getAbs requests.

``` js
let options = new HttpClientOptions()
  .setProxyOptions(new ProxyOptions()
    .setType("HTTP"));
let client = vertx.createHttpClient(options);
client.getAbs("ftp://ftp.gnu.org/gnu/", (response) => {
  console.log("Received response with status code " + response.statusCode());
});
```

Support for other protocols is not available since java.net.URL does not
support them (gopher:// for example).

## Automatic clean-up in verticles

If you’re creating http servers and clients from inside verticles, those
servers and clients will be automatically closed when the verticle is
undeployed.

# Using the SharedData API

As its name suggests, the `SharedData` API allows you to safely share
data between:

  - different parts of your application, or

  - different applications in the same Vert.x instance, or

  - different applications across a cluster of Vert.x instances.

In practice, it provides:

  - synchronous maps (local-only)

  - asynchronous maps

  - asynchronous locks

  - asynchronous counters

> **Important**
> 
> The behavior of the distributed data structure depends on the cluster
> manager you use. Backup (replication) and behavior when a network
> partition is faced are defined by the cluster manager and its
> configuration. Please refer to the cluster manager documentation as
> well as to the underlying framework manual.

## Local maps

`Local maps` allow you to share data safely between different event
loops (e.g. different verticles) in the same Vert.x instance.

They only allow certain data types to be used as keys and values:

  - immutable types (e.g. strings, booleans, …​ etc), or

  - types implementing the `Shareable` interface (buffers, JSON arrays,
    JSON objects, or your own shareable objects).

In the latter case the key/value will be copied before putting it into
the map.

This way we can ensure there is no *shared access to mutable state*
between different threads in your Vert.x application. And you won’t have
to worry about protecting that state by synchronising access to it.

Here’s an example of using a shared local map:

``` js
import { Buffer } from "@vertx/core"
let sharedData = vertx.sharedData();

let map1 = sharedData.getLocalMap("mymap1");

map1.put("foo", "bar");

let map2 = sharedData.getLocalMap("mymap2");

map2.put("eek", Buffer.buffer().appendInt(123));

// Then... in another part of your application:

map1 = sharedData.getLocalMap("mymap1");

let val = map1.get("foo");

map2 = sharedData.getLocalMap("mymap2");

let buff = map2.get("eek");
```

## Asynchronous shared maps

`Asynchronous shared maps` allow data to be put in the map and retrieved
locally or from any other node.

This makes them really useful for things like storing session state in a
farm of servers hosting a Vert.x Web application.

Getting the map is asynchronous and the result is returned to you in the
handler that you specify. Here’s an example:

``` js
let sharedData = vertx.sharedData();

sharedData.getAsyncMap("mymap", (res) => {
  if (res.succeeded()) {
    let map = res.result();
  } else {
    // Something went wrong!
  }
});
```

When Vert.x is clustered, data that you put into the map is accessible
locally as well as on any of the other cluster members.

> **Important**
> 
> In clustered mode, asynchronous shared maps rely on distributed data
> structures provided by the cluster manager. Beware that the latency
> relative to asynchronous shared map operations can be much higher in
> clustered than in local mode.

If your application doesn’t need data to be shared with every other
node, you can retrieve a local-only map:

``` js
let sharedData = vertx.sharedData();

sharedData.getLocalAsyncMap("mymap", (res) => {
  if (res.succeeded()) {
    // Local-only async map
    let map = res.result();
  } else {
    // Something went wrong!
  }
});
```

### Putting data in a map

You put data in a map with `put`.

The actual put is asynchronous and the handler is notified once it is
complete:

``` js
map.put("foo", "bar", (resPut) => {
  if (resPut.succeeded()) {
    // Successfully put the value
  } else {
    // Something went wrong!
  }
});
```

### Getting data from a map

You get data from a map with `get`.

The actual get is asynchronous and the handler is notified with the
result some time later:

``` js
map.get("foo", (resGet) => {
  if (resGet.succeeded()) {
    // Successfully got the value
    let val = resGet.result();
  } else {
    // Something went wrong!
  }
});
```

#### Other map operations

You can also remove entries from an asynchronous map, clear them and get
the size.

See the `API docs` for a detailed list of map operations.

## Asynchronous locks

`Asynchronous locks` allow you to obtain exclusive locks locally or
across the cluster. This is useful when you want to do something or
access a resource on only one node of a cluster at any one time.

Asynchronous locks have an asynchronous API unlike most lock APIs which
block the calling thread until the lock is obtained.

To obtain a lock use `getLock`. This won’t block, but when the lock is
available, the handler will be called with an instance of `Lock`,
signalling that you now own the lock.

While you own the lock, no other caller, locally or on the cluster, will
be able to obtain the lock.

When you’ve finished with the lock, you call `release` to release it, so
another caller can obtain it:

``` js
let sharedData = vertx.sharedData();

sharedData.getLock("mylock", (res) => {
  if (res.succeeded()) {
    // Got the lock!
    let lock = res.result();

    // 5 seconds later we release the lock so someone else can get it

    vertx.setTimer(5000, (tid) => {
      lock.release();
    });

  } else {
    // Something went wrong
  }
});
```

You can also get a lock with a timeout. If it fails to obtain the lock
within the timeout the handler will be called with a failure:

``` js
let sharedData = vertx.sharedData();

sharedData.getLockWithTimeout("mylock", 10000, (res) => {
  if (res.succeeded()) {
    // Got the lock!
    let lock = res.result();

  } else {
    // Failed to get lock
  }
});
```

See the `API docs` for a detailed list of lock operations.

> **Important**
> 
> In clustered mode, asynchronous locks rely on distributed data
> structures provided by the cluster manager. Beware that the latency
> relative to asynchronous shared lock operations can be much higher in
> clustered than in local mode.

If your application doesn’t need the lock to be shared with every other
node, you can retrieve a local-only lock:

``` js
let sharedData = vertx.sharedData();

sharedData.getLocalLock("mylock", (res) => {
  if (res.succeeded()) {
    // Local-only lock
    let lock = res.result();

    // 5 seconds later we release the lock so someone else can get it

    vertx.setTimer(5000, (tid) => {
      lock.release();
    });

  } else {
    // Something went wrong
  }
});
```

## Asynchronous counters

It’s often useful to maintain an atomic counter locally or across the
different nodes of your application.

You can do this with `Counter`.

You obtain an instance with `getCounter`:

``` js
let sharedData = vertx.sharedData();

sharedData.getCounter("mycounter", (res) => {
  if (res.succeeded()) {
    let counter = res.result();
  } else {
    // Something went wrong!
  }
});
```

Once you have an instance you can retrieve the current count, atomically
increment it, decrement and add a value to it using the various methods.

See the `API docs` for a detailed list of counter operations.

> **Important**
> 
> In clustered mode, asynchronous counters rely on distributed data
> structures provided by the cluster manager. Beware that the latency
> relative to asynchronous shared counter operations can be much higher
> in clustered than in local mode.

If your application doesn’t need the counter to be shared with every
other node, you can retrieve a local-only counter:

``` js
let sharedData = vertx.sharedData();

sharedData.getLocalCounter("mycounter", (res) => {
  if (res.succeeded()) {
    // Local-only counter
    let counter = res.result();
  } else {
    // Something went wrong!
  }
});
```

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

# Datagram sockets (UDP)

Using User Datagram Protocol (UDP) with Vert.x is a piece of cake.

UDP is a connection-less transport which basically means you have no
persistent connection to a remote peer.

Instead you can send and receive packages and the remote address is
contained in each of them.

Beside this UDP is not as safe as TCP to use, which means there are no
guarantees that a send Datagram packet will receive it’s endpoint at
all.

The only guarantee is that it will either receive complete or not at
all.

Also you usually can’t send data which is bigger then the MTU size of
your network interface, this is because each packet will be send as one
packet.

But be aware even if the packet size is smaller then the MTU it may
still fail.

At which size it will fail depends on the Operating System etc. So rule
of thumb is to try to send small packets.

Because of the nature of UDP it is best fit for Applications where you
are allowed to drop packets (like for example a monitoring application).

The benefits are that it has a lot less overhead compared to TCP, which
can be handled by the NetServer and NetClient (see above).

## Creating a DatagramSocket

To use UDP you first need t create a `DatagramSocket`. It does not
matter here if you only want to send data or send and receive.

``` js
let socket = vertx.createDatagramSocket(new DatagramSocketOptions());
```

The returned `DatagramSocket` will not be bound to a specific port. This
is not a problem if you only want to send data (like a client), but more
on this in the next section.

## Sending Datagram packets

As mentioned before, User Datagram Protocol (UDP) sends data in packets
to remote peers but is not connected to them in a persistent fashion.

This means each packet can be sent to a different remote peer.

Sending packets is as easy as shown here:

``` js
import { Buffer } from "@vertx/core"
let socket = vertx.createDatagramSocket(new DatagramSocketOptions());
let buffer = Buffer.buffer("content");
// Send a Buffer
socket.send(buffer, 1234, "10.0.0.1", (asyncResult) => {
  console.log("Send succeeded? " + asyncResult.succeeded());
});
// Send a String
socket.send("A string used as content", 1234, "10.0.0.1", (asyncResult) => {
  console.log("Send succeeded? " + asyncResult.succeeded());
});
```

## Receiving Datagram packets

If you want to receive packets you need to bind the `DatagramSocket` by
calling `listen(…​)}` on it.

This way you will be able to receive ``DatagramPacket`s that were sent
to the address and port on
which the `DatagramSocket`` listens.

Beside this you also want to set a `Handler` which will be called for
each received `DatagramPacket`.

The `DatagramPacket` has the following methods:

  - `sender`: The InetSocketAddress which represent the sender of the
    packet

  - `data`: The Buffer which holds the data which was received.

So to listen on a specific address and port you would do something like
shown here:

``` js
let socket = vertx.createDatagramSocket(new DatagramSocketOptions());
socket.listen(1234, "0.0.0.0", (asyncResult) => {
  if (asyncResult.succeeded()) {
    socket.handler((packet) => {
      // Do something with the packet
    });
  } else {
    console.log("Listen failed" + asyncResult.cause());
  }
});
```

Be aware that even if the {code AsyncResult} is successed it only means
it might be written on the network stack, but gives no guarantee that it
ever reached or will reach the remote peer at all.

If you need such a guarantee then you want to use TCP with some
handshaking logic build on top.

## Multicast

### Sending Multicast packets

Multicast allows multiple sockets to receive the same packets. This
works by having the sockets join the same multicast group to which you
can then send packets.

We will look at how you can join a Multicast Group and receive packets
in the next section.

Sending multicast packets is not different than sending normal Datagram
packets. The difference is that you pass in a multicast group address to
the send method.

This is show here:

``` js
import { Buffer } from "@vertx/core"
let socket = vertx.createDatagramSocket(new DatagramSocketOptions());
let buffer = Buffer.buffer("content");
// Send a Buffer to a multicast address
socket.send(buffer, 1234, "230.0.0.1", (asyncResult) => {
  console.log("Send succeeded? " + asyncResult.succeeded());
});
```

All sockets that have joined the multicast group 230.0.0.1 will receive
the packet.

#### Receiving Multicast packets

If you want to receive packets for specific Multicast group you need to
bind the `DatagramSocket` by calling `listen(…​)` on it to join the
Multicast group.

This way you will receive DatagramPackets that were sent to the address
and port on which the `DatagramSocket` listens and also to those sent to
the Multicast group.

Beside this you also want to set a Handler which will be called for each
received DatagramPacket.

The `DatagramPacket` has the following methods:

  - `sender()`: The InetSocketAddress which represent the sender of the
    packet

  - `data()`: The Buffer which holds the data which was received.

So to listen on a specific address and port and also receive packets for
the Multicast group 230.0.0.1 you would do something like shown here:

``` js
let socket = vertx.createDatagramSocket(new DatagramSocketOptions());
socket.listen(1234, "0.0.0.0", (asyncResult) => {
  if (asyncResult.succeeded()) {
    socket.handler((packet) => {
      // Do something with the packet
    });

    // join the multicast group
    socket.listenMulticastGroup("230.0.0.1", (asyncResult2) => {
      console.log("Listen succeeded? " + asyncResult2.succeeded());
    });
  } else {
    console.log("Listen failed" + asyncResult.cause());
  }
});
```

#### Unlisten / leave a Multicast group

There are sometimes situations where you want to receive packets for a
Multicast group for a limited time.

In this situations you can first start to listen for them and then later
unlisten.

This is shown here:

``` js
let socket = vertx.createDatagramSocket(new DatagramSocketOptions());
socket.listen(1234, "0.0.0.0", (asyncResult) => {
  if (asyncResult.succeeded()) {
    socket.handler((packet) => {
      // Do something with the packet
    });

    // join the multicast group
    socket.listenMulticastGroup("230.0.0.1", (asyncResult2) => {
      if (asyncResult2.succeeded()) {
        // will now receive packets for group

        // do some work

        socket.unlistenMulticastGroup("230.0.0.1", (asyncResult3) => {
          console.log("Unlisten succeeded? " + asyncResult3.succeeded());
        });
      } else {
        console.log("Listen failed" + asyncResult2.cause());
      }
    });
  } else {
    console.log("Listen failed" + asyncResult.cause());
  }
});
```

#### Blocking multicast

Beside unlisten a Multicast address it’s also possible to just block
multicast for a specific sender address.

Be aware this only work on some Operating Systems and kernel versions.
So please check the Operating System documentation if it’s supported.

This an expert feature.

To block multicast from a specific address you can call
`blockMulticastGroup(…​)` on the DatagramSocket like shown here:

``` js
let socket = vertx.createDatagramSocket(new DatagramSocketOptions());

// Some code

// This would block packets which are send from 10.0.0.2
socket.blockMulticastGroup("230.0.0.1", "10.0.0.2", (asyncResult) => {
  console.log("block succeeded? " + asyncResult.succeeded());
});
```

### DatagramSocket properties

When creating a `DatagramSocket` there are multiple properties you can
set to change it’s behaviour with the `DatagramSocketOptions` object.
Those are listed here:

  - `setSendBufferSize` Sets the send buffer size in bytes.

  - `setReceiveBufferSize` Sets the TCP receive buffer size in bytes.

  - `setReuseAddress` If true then addresses in TIME\_WAIT state can be
    reused after they have been closed.

  - `setTrafficClass`

  - `setBroadcast` Sets or clears the SO\_BROADCAST socket option. When
    this option is set, Datagram (UDP) packets may be sent to a local
    interface’s broadcast address.

  - `setMulticastNetworkInterface` Sets or clears the
    IP\_MULTICAST\_LOOP socket option. When this option is set,
    multicast packets will also be received on the local interface.

  - `setMulticastTimeToLive` Sets the IP\_MULTICAST\_TTL socket option.
    TTL stands for "Time to Live," but in this context it specifies the
    number of IP hops that a packet is allowed to go through,
    specifically for multicast traffic. Each router or gateway that
    forwards a packet decrements the TTL. If the TTL is decremented to 0
    by a router, it will not be forwarded.

### DatagramSocket Local Address

You can find out the local address of the socket (i.e. the address of
this side of the UDP Socket) by calling `localAddress`. This will only
return an `InetSocketAddress` if you bound the `DatagramSocket` with
`listen(…​)` before, otherwise it will return null.

### Closing a DatagramSocket

You can close a socket by invoking the `close` method. This will close
the socket and release all resources

# DNS client

Often you will find yourself in situations where you need to obtain DNS
informations in an asynchronous fashion. Unfortunally this is not
possible with the API that is shipped with the Java Virtual Machine
itself. Because of this Vert.x offers it’s own API for DNS resolution
which is fully asynchronous.

To obtain a DnsClient instance you will create a new via the Vertx
instance.

``` js
let client = vertx.createDnsClient(53, "10.0.0.1");
```

You can also create the client with options and configure the query
timeout.

``` js
let client = vertx.createDnsClient(new DnsClientOptions()
  .setPort(53)
  .setHost("10.0.0.1")
  .setQueryTimeout(10000));
```

Creating the client with no arguments or omitting the server address
will use the address of the server used internally for non blocking
address resolution.

``` js
let client1 = vertx.createDnsClient();

// Just the same but with a different query timeout
let client2 = vertx.createDnsClient(new DnsClientOptions()
  .setQueryTimeout(10000));
```

## lookup

Try to lookup the A (ipv4) or AAAA (ipv6) record for a given name. The
first which is returned will be used, so it behaves the same way as you
may be used from when using "nslookup" on your operation system.

To lookup the A / AAAA record for "vertx.io" you would typically use it
like:

``` js
let client = vertx.createDnsClient(53, "9.9.9.9");
client.lookup("vertx.io", (ar) => {
  if (ar.succeeded()) {
    console.log(ar.result());
  } else {
    console.log("Failed to resolve entry" + ar.cause());
  }
});
```

## lookup4

Try to lookup the A (ipv4) record for a given name. The first which is
returned will be used, so it behaves the same way as you may be used
from when using "nslookup" on your operation system.

To lookup the A record for "vertx.io" you would typically use it like:

``` js
let client = vertx.createDnsClient(53, "9.9.9.9");
client.lookup4("vertx.io", (ar) => {
  if (ar.succeeded()) {
    console.log(ar.result());
  } else {
    console.log("Failed to resolve entry" + ar.cause());
  }
});
```

## lookup6

Try to lookup the AAAA (ipv6) record for a given name. The first which
is returned will be used, so it behaves the same way as you may be used
from when using "nslookup" on your operation system.

To lookup the A record for "vertx.io" you would typically use it like:

``` js
let client = vertx.createDnsClient(53, "9.9.9.9");
client.lookup6("vertx.io", (ar) => {
  if (ar.succeeded()) {
    console.log(ar.result());
  } else {
    console.log("Failed to resolve entry" + ar.cause());
  }
});
```

## resolveA

Try to resolve all A (ipv4) records for a given name. This is quite
similar to using "dig" on unix like operation systems.

To lookup all the A records for "vertx.io" you would typically do:

``` js
let client = vertx.createDnsClient(53, "9.9.9.9");
client.resolveA("vertx.io", (ar) => {
  if (ar.succeeded()) {
    let records = ar.result();
    records.forEach(record => {
      console.log(record);
    });
  } else {
    console.log("Failed to resolve entry" + ar.cause());
  }
});
```

## resolveAAAA

Try to resolve all AAAA (ipv6) records for a given name. This is quite
similar to using "dig" on unix like operation systems.

To lookup all the AAAAA records for "vertx.io" you would typically do:

``` js
let client = vertx.createDnsClient(53, "9.9.9.9");
client.resolveAAAA("vertx.io", (ar) => {
  if (ar.succeeded()) {
    let records = ar.result();
    records.forEach(record => {
      console.log(record);
    });
  } else {
    console.log("Failed to resolve entry" + ar.cause());
  }
});
```

## resolveCNAME

Try to resolve all CNAME records for a given name. This is quite similar
to using "dig" on unix like operation systems.

To lookup all the CNAME records for "vertx.io" you would typically do:

``` js
let client = vertx.createDnsClient(53, "9.9.9.9");
client.resolveCNAME("vertx.io", (ar) => {
  if (ar.succeeded()) {
    let records = ar.result();
    records.forEach(record => {
      console.log(record);
    });
  } else {
    console.log("Failed to resolve entry" + ar.cause());
  }
});
```

## resolveMX

Try to resolve all MX records for a given name. The MX records are used
to define which Mail-Server accepts emails for a given domain.

To lookup all the MX records for "vertx.io" you would typically do:

``` js
let client = vertx.createDnsClient(53, "9.9.9.9");
client.resolveMX("vertx.io", (ar) => {
  if (ar.succeeded()) {
    let records = ar.result();
    records.forEach(record => {
      console.log(record);
    });
  } else {
    console.log("Failed to resolve entry" + ar.cause());
  }
});
```

Be aware that the List will contain the `MxRecord` sorted by the
priority of them, which means MX records with smaller priority coming
first in the List.

The `MxRecord` allows you to access the priority and the name of the MX
record by offer methods for it like:

``` js
record.priority();
record.name();
```

## resolveTXT

Try to resolve all TXT records for a given name. TXT records are often
used to define extra informations for a domain.

To resolve all the TXT records for "vertx.io" you could use something
along these lines:

``` js
let client = vertx.createDnsClient(53, "9.9.9.9");
client.resolveTXT("vertx.io", (ar) => {
  if (ar.succeeded()) {
    let records = ar.result();
    records.forEach(record => {
      console.log(record);
    });
  } else {
    console.log("Failed to resolve entry" + ar.cause());
  }
});
```

## resolveNS

Try to resolve all NS records for a given name. The NS records specify
which DNS Server hosts the DNS informations for a given domain.

To resolve all the NS records for "vertx.io" you could use something
along these lines:

``` js
let client = vertx.createDnsClient(53, "9.9.9.9");
client.resolveNS("vertx.io", (ar) => {
  if (ar.succeeded()) {
    let records = ar.result();
    records.forEach(record => {
      console.log(record);
    });
  } else {
    console.log("Failed to resolve entry" + ar.cause());
  }
});
```

## resolveSRV

Try to resolve all SRV records for a given name. The SRV records are
used to define extra informations like port and hostname of services.
Some protocols need this extra informations.

To lookup all the SRV records for "vertx.io" you would typically do:

``` js
let client = vertx.createDnsClient(53, "9.9.9.9");
client.resolveSRV("vertx.io", (ar) => {
  if (ar.succeeded()) {
    let records = ar.result();
    records.forEach(record => {
      console.log(record);
    });
  } else {
    console.log("Failed to resolve entry" + ar.cause());
  }
});
```

Be aware that the List will contain the SrvRecords sorted by the
priority of them, which means SrvRecords with smaller priority coming
first in the List.

The `SrvRecord` allows you to access all informations contained in the
SRV record itself:

``` js
record.priority();
record.name();
record.weight();
record.port();
record.protocol();
record.service();
record.target();
```

Please refer to the API docs for the exact details.

## resolvePTR

Try to resolve the PTR record for a given name. The PTR record maps an
ipaddress to a name.

To resolve the PTR record for the ipaddress 10.0.0.1 you would use the
PTR notion of "1.0.0.10.in-addr.arpa"

``` js
let client = vertx.createDnsClient(53, "9.9.9.9");
client.resolvePTR("1.0.0.10.in-addr.arpa", (ar) => {
  if (ar.succeeded()) {
    let record = ar.result();
    console.log(record);
  } else {
    console.log("Failed to resolve entry" + ar.cause());
  }
});
```

## reverseLookup

Try to do a reverse lookup for an ipaddress. This is basically the same
as resolve a PTR record, but allows you to just pass in the ipaddress
and not a valid PTR query string.

To do a reverse lookup for the ipaddress 10.0.0.1 do something similar
like this:

``` js
let client = vertx.createDnsClient(53, "9.9.9.9");
client.reverseLookup("10.0.0.1", (ar) => {
  if (ar.succeeded()) {
    let record = ar.result();
    console.log(record);
  } else {
    console.log("Failed to resolve entry" + ar.cause());
  }
});
```

## Error handling

As you saw in previous sections the DnsClient allows you to pass in a
Handler which will be notified with an AsyncResult once the query was
complete. In case of an error it will be notified with a DnsException
which will hole a {@link io.vertx.core.dns.DnsResponseCode} that
indicate why the resolution failed. This DnsResponseCode can be used to
inspect the cause in more detail.

Possible DnsResponseCodes are:

  - {@link io.vertx.core.dns.DnsResponseCode\#NOERROR} No record was
    found for a given query

  - {@link io.vertx.core.dns.DnsResponseCode\#FORMERROR} Format error

  - {@link io.vertx.core.dns.DnsResponseCode\#SERVFAIL} Server failure

  - {@link io.vertx.core.dns.DnsResponseCode\#NXDOMAIN} Name error

  - {@link io.vertx.core.dns.DnsResponseCode\#NOTIMPL} Not implemented
    by DNS Server

  - {@link io.vertx.core.dns.DnsResponseCode\#REFUSED} DNS Server
    refused the query

  - {@link io.vertx.core.dns.DnsResponseCode\#YXDOMAIN} Domain name
    should not exist

  - {@link io.vertx.core.dns.DnsResponseCode\#YXRRSET} Resource record
    should not exist

  - {@link io.vertx.core.dns.DnsResponseCode\#NXRRSET} RRSET does not
    exist

  - {@link io.vertx.core.dns.DnsResponseCode\#NOTZONE} Name not in zone

  - {@link io.vertx.core.dns.DnsResponseCode\#BADVERS} Bad extension
    mechanism for version

  - {@link io.vertx.core.dns.DnsResponseCode\#BADSIG} Bad signature

  - {@link io.vertx.core.dns.DnsResponseCode\#BADKEY} Bad key

  - {@link io.vertx.core.dns.DnsResponseCode\#BADTIME} Bad timestamp

All of those errors are "generated" by the DNS Server itself.

You can obtain the DnsResponseCode from the DnsException like:

``` java
{@link docoverride.dns.Examples#example16}
```

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

# Record Parser

The record parser allows you to easily parse protocols which are
delimited by a sequence of bytes, or fixed size records. It transforms a
sequence of input buffer to a sequence of buffer structured as
configured (either fixed size or separated records).

For example, if you have a simple ASCII text protocol delimited by '\\n'
and the input is the following:

    buffer1:HELLO\nHOW ARE Y
    buffer2:OU?\nI AM
    buffer3: DOING OK
    buffer4:\n

The record parser would produce

    buffer1:HELLO
    buffer2:HOW ARE YOU?
    buffer3:I AM DOING OK

Let’s see the associated code:

``` js
import { RecordParser } from "@vertx/core"
import { Buffer } from "@vertx/core"
let parser = RecordParser.newDelimited("\n", (h) => {
  console.log(h.toString());
});

parser.handle(Buffer.buffer("HELLO\nHOW ARE Y"));
parser.handle(Buffer.buffer("OU?\nI AM"));
parser.handle(Buffer.buffer("DOING OK"));
parser.handle(Buffer.buffer("\n"));
```

You can also produce fixed sized chunks as follows:

``` js
import { RecordParser } from "@vertx/core"
RecordParser.newFixed(4, (h) => {
  console.log(h.toString());
});
```

For more details, check out the `RecordParser` class.

# Json Parser

You can easily parse JSON structures but that requires to provide the
JSON content at once, but it may not be convenient when you need to
parse very large structures.

The non-blocking JSON parser is an event driven parser able to deal with
very large structures. It transforms a sequence of input buffer to a
sequence of JSON parse events.

``` js
Code not translatable
```

The parser is non-blocking and emitted events are driven by the input
buffers.

``` js
import { JsonParser } from "@vertx/core"
import { Buffer } from "@vertx/core"

let parser = JsonParser.newParser();

// start array event
// start object event
// "firstName":"Bob" event
parser.handle(Buffer.buffer("[{\"firstName\":\"Bob\","));

// "lastName":"Morane" event
// end object event
parser.handle(Buffer.buffer("\"lastName\":\"Morane\"},"));

// start object event
// "firstName":"Luke" event
// "lastName":"Lucky" event
// end object event
parser.handle(Buffer.buffer("{\"firstName\":\"Luke\",\"lastName\":\"Lucky\"}"));

// end array event
parser.handle(Buffer.buffer("]"));

// Always call end
parser.end();
```

Event driven parsing provides more control but comes at the price of
dealing with fine grained events, which can be inconvenient sometimes.
The JSON parser allows you to handle JSON structures as values when it
is desired:

``` js
Code not translatable
```

The value mode can be set and unset during the parsing allowing you to
switch between fine grained events or JSON object value events.

``` js
Code not translatable
```

You can do the same with arrays as well

``` js
Code not translatable
```

You can also decode POJOs

``` js
parser.handler((event) => {
  // Handle each object
  // Get the field in which this object was parsed
  let id = event.fieldName();
  let user = event.mapTo(Java.type("examples.ParseToolsExamples.User").class);
  console.log("User with id " + id + " : " + user.firstName + " " + user.lastName);
});
```

Whenever the parser fails to process a buffer, an exception will be
thrown unless you set an exception handler:

``` js
import { JsonParser } from "@vertx/core"

let parser = JsonParser.newParser();

parser.exceptionHandler((err) => {
  // Catch any parsing or decoding error
});
```

The parser also parses json streams:

  - concatenated json streams: `{"temperature":30}{"temperature":50}`

  - line delimited json streams: `{"an":"object"}\r\n3\r\n"a
    string"\r\nnull`

For more details, check out the `JsonParser` class.

# Thread safety

Most Vert.x objects are safe to access from different threads. *However*
performance is optimised when they are accessed from the same context
they were created from.

For example if you have deployed a verticle which creates a `NetServer`
which provides `NetSocket` instances in it’s handler, then it’s best to
always access that socket instance from the event loop of the verticle.

If you stick to the standard Vert.x verticle deployment model and avoid
sharing objects between verticles then this should be the case without
you having to think about it.

# Metrics SPI

By default Vert.x does not record any metrics. Instead it provides an
SPI for others to implement which can be added to the classpath. The
metrics SPI is an advanced feature which allows implementers to capture
events from Vert.x in order to gather metrics. For more information on
this, please consult the `API Documentation`.

You can also specify a metrics factory programmatically if embedding
Vert.x using `setFactory`.

# OSGi

Vert.x Core is packaged as an OSGi bundle, so can be used in any OSGi
R4.2+ environment such as Apache Felix or Eclipse Equinox. The bundle
exports `io.vertx.core*`.

However, the bundle has some dependencies on Jackson and Netty. To get
the vert.x core bundle resolved deploy:

  - Jackson Annotation \[2.6.0,3)

  - Jackson Core \[2.6.2,3)

  - Jackson Databind \[2.6.2,3)

  - Netty Buffer \[4.0.31,5)

  - Netty Codec \[4.0.31,5)

  - Netty Codec/Socks \[4.0.31,5)

  - Netty Codec/Common \[4.0.31,5)

  - Netty Codec/Handler \[4.0.31,5)

  - Netty Codec/Transport \[4.0.31,5)

Here is a working deployment on Apache Felix 5.2.0:

    14|Active     |    1|Jackson-annotations (2.6.0)
    15|Active     |    1|Jackson-core (2.6.2)
    16|Active     |    1|jackson-databind (2.6.2)
    18|Active     |    1|Netty/Buffer (4.0.31.Final)
    19|Active     |    1|Netty/Codec (4.0.31.Final)
    20|Active     |    1|Netty/Codec/HTTP (4.0.31.Final)
    21|Active     |    1|Netty/Codec/Socks (4.0.31.Final)
    22|Active     |    1|Netty/Common (4.0.31.Final)
    23|Active     |    1|Netty/Handler (4.0.31.Final)
    24|Active     |    1|Netty/Transport (4.0.31.Final)
    25|Active     |    1|Netty/Transport/SCTP (4.0.31.Final)
    26|Active     |    1|Vert.x Core (3.1.0)

On Equinox, you may want to disable the `ContextFinder` with the
following framework property: `eclipse.bundle.setTCCL=false`

# The 'vertx' command line

The `vertx` command is used to interact with Vert.x from the command
line. It’s main use is to run Vert.x verticles. To do this you need to
download and install a Vert.x distribution, and add the `bin` directory
of the installation to your `PATH` environment variable. Also make sure
you have a Java 8 JDK on your `PATH`.

> **Note**
> 
> The JDK is required to support on the fly compilation of Java code.

## Run verticles

You can run raw Vert.x verticles directly from the command line using
`vertx run`. Here is a couple of examples of the `run` *command*:

    vertx run my-verticle.js                                 (1)
    vertx run my-verticle.groovy                             (2)
    vertx run my-verticle.rb                                 (3)
    
    vertx run io.vertx.example.MyVerticle                    (4)
    vertx run io.vertx.example.MVerticle -cp my-verticle.jar (5)
    
    vertx run MyVerticle.java                                (6)

1.  Deploys a JavaScript verticle

2.  Deploys a Groovy verticle

3.  Deploys a Ruby verticle

4.  Deploys an already compiled Java verticle. Classpath root is the
    current directory

5.  Deploys a verticle packaged in a Jar, the jar need to be in the
    classpath

6.  Compiles the Java source and deploys it

As you can see in the case of Java, the name can either be the fully
qualified class name of the verticle, or you can specify the Java Source
file directly and Vert.x compiles it for you.

You can also prefix the verticle with the name of the language
implementation to use. For example if the verticle is a compiled Groovy
class, you prefix it with `groovy:` so that Vert.x knows it’s a Groovy
class not a Java class.

    vertx run groovy:io.vertx.example.MyGroovyVerticle

The `vertx run` command can take a few optional parameters, they are:

  - `-options <options>` - Provides the Vert.x options. `options` is the
    name of a JSON file that represents the Vert.x options, or a JSON
    string. This is optional.

  - `-conf <config>` - Provides some configuration to the verticle.
    `config` is the name of a JSON file that represents the
    configuration for the verticle, or a JSON string. This is optional.

  - `-cp <path>` - The path on which to search for the verticle and any
    other resources used by the verticle. This defaults to `.` (current
    directory). If your verticle references other scripts, classes or
    other resources (e.g. jar files) then make sure these are on this
    path. The path can contain multiple path entries separated by `:`
    (colon) or `;` (semi-colon) depending on the operating system. Each
    path entry can be an absolute or relative path to a directory
    containing scripts, or absolute or relative filenames for jar or zip
    files. An example path might be `-cp
    classes:lib/otherscripts:jars/myjar.jar:jars/otherjar.jar`. Always
    use the path to reference any resources that your verticle requires.
    Do **not** put them on the system classpath as this can cause
    isolation issues between deployed verticles.

  - `-instances <instances>` - The number of instances of the verticle
    to instantiate. Each verticle instance is strictly single threaded
    so to scale your application across available cores you might want
    to deploy more than one instance. If omitted a single instance will
    be deployed.

  - `-worker` - This option determines whether the verticle is a worker
    verticle or not.

  - `-cluster` - This option determines whether the Vert.x instance will
    attempt to form a cluster with other Vert.x instances on the
    network. Clustering Vert.x instances allows Vert.x to form a
    distributed event bus with other nodes. Default is `false` (not
    clustered).

  - `-cluster-port` - If the `cluster` option has also been specified
    then this determines which port will be bound for cluster
    communication with other Vert.x instances. Default is `0` - which
    means '*choose a free random port*'. You don’t usually need to
    specify this parameter unless you really need to bind to a specific
    port.

  - `-cluster-host` - If the `cluster` option has also been specified
    then this determines which host address will be bound for cluster
    communication with other Vert.x instances. By default it will try
    and pick one from the available interfaces. If you have more than
    one interface and you want to use a specific one, specify it here.

  - `-cluster-public-port` - If the `cluster` option has also been
    specified then this determines which port will be advertised for
    cluster communication with other Vert.x instances. Default is `-1`,
    which means same as `cluster-port`.

  - `-cluster-public-host` - If the `cluster` option has also been
    specified then this determines which host address will be advertised
    for cluster communication with other Vert.x instances. If not
    specified, Vert.x uses the value of `cluster-host`.

  - `-ha` - if specified the verticle will be deployed as high
    availability (HA) deployment. See related section for more details

  - `-quorum` - used in conjunction with `-ha`. It specifies the minimum
    number of nodes in the cluster for any *HA deploymentIDs* to be
    active. Defaults to 0.

  - `-hagroup` - used in conjunction with `-ha`. It specifies the HA
    group this node will join. There can be multiple HA groups in a
    cluster. Nodes will only failover to other nodes in the same group.
    The default value is \` \_\_DEFAULT\_\_\`

You can also set system properties using: `-Dkey=value`.

Here are some more examples:

Run a JavaScript verticle server.js with default settings

    vertx run server.js

Run 10 instances of a pre-compiled Java verticle specifying classpath

    vertx run com.acme.MyVerticle -cp "classes:lib/myjar.jar" -instances 10

Run 10 instances of a Java verticle by source *file*

    vertx run MyVerticle.java -instances 10

Run 20 instances of a ruby worker verticle

    vertx run order_worker.rb -instances 20 -worker

Run two JavaScript verticles on the same machine and let them cluster
together with each other and any other servers on the network

    vertx run handler.js -cluster
    vertx run sender.js -cluster

Run a Ruby verticle passing it some config

    vertx run my_verticle.rb -conf my_verticle.conf

Where `my_verticle.conf` might contain something like:

``` json
{
"name": "foo",
"num_widgets": 46
}
```

The config will be available inside the verticle via the core API.

When using the high-availability feature of vert.x you may want to
create a *bare* instance of vert.x. This instance does not deploy any
verticles when launched, but will receive a verticle if another node of
the cluster dies. To create a *bare* instance, launch:

    vertx bare

Depending on your cluster configuration, you may have to append the
`cluster-host` and `cluster-port` parameters.

## Executing a Vert.x application packaged as a fat jar

A *fat jar* is an executable jar embedding its dependencies. This means
you don’t have to have Vert.x pre-installed on the machine on which you
execute the jar. Like any executable Java jar it can be executed with.

    java -jar my-application-fat.jar

There is nothing really Vert.x specific about this, you could do this
with any Java application

You can either create your own main class and specify that in the
manifest, but it’s recommended that you write your code as verticles and
use the Vert.x `Launcher` class (`io.vertx.core.Launcher`) as your main
class. This is the same main class used when running Vert.x at the
command line and therefore allows you to specify command line arguments,
such as `-instances` in order to scale your application more easily.

To deploy your verticle in a *fatjar* like this you must have a
*manifest* with:

  - `Main-Class` set to `io.vertx.core.Launcher`

  - `Main-Verticle` specifying the main verticle (fully qualified class
    name or script file name)

You can also provide the usual command line arguments that you would
pass to `vertx run`:

    java -jar my-verticle-fat.jar -cluster -conf myconf.json
    java -jar my-verticle-fat.jar -cluster -conf myconf.json -cp path/to/dir/conf/cluster_xml

> **Note**
> 
> Please consult the Maven/Gradle simplest and Maven/Gradle verticle
> examples in the examples repository for examples of building
> applications as fatjars.

A fat jar executes the `run` command, by default.

## Displaying version of Vert.x

To display the vert.x version, just launch:

    vertx version

## Other commands

The `vertx` command line and the `Launcher` also provide other
*commands* in addition to `run` and `version`:

You can create a `bare` instance using:

    vertx bare
    # or
    java -jar my-verticle-fat.jar bare

You can also start an application in background using:

    java -jar my-verticle-fat.jar start --vertx-id=my-app-name

If `my-app-name` is not set, a random id will be generated, and printed
on the command prompt. You can pass `run` options to the `start`
command:

    java -jar my-verticle-fat.jar start —-vertx-id=my-app-name -cluster

Once launched in background, you can stop it with the `stop` command:

    java -jar my-verticle-fat.jar stop my-app-name

You can also list the vert.x application launched in background using:

    java -jar my-verticle-fat.jar list

The `start`, `stop` and `list` command are also available from the
`vertx` tool. The start\` command supports a couple of options:

  - `vertx-id` : the application id, uses a random UUID if not set

  - `java-opts` : the Java Virtual Machine options, uses the `JAVA_OPTS`
    environment variable if not set.

  - `redirect-output` : redirect the spawned process output and error
    streams to the parent process streams.

If option values contain spaces, don’t forget to wrap the value between
`""` (double-quotes).

As the `start` command spawns a new process, the java options passed to
the JVM are not propagated, so you **must** use `java-opts` to configure
the JVM (`-X`, `-D`…​). If you use the `CLASSPATH` environment variable,
be sure it contains all the required jars (vertx-core, your jars and all
the dependencies).

The set of commands is extensible, refer to the [Extending the vert.x
Launcher](#_extending_the_vert_x_launcher) section.

## Live Redeploy

When developing it may be convenient to automatically redeploy your
application upon file changes. The `vertx` command line tool and more
generally the `Launcher` class offers this feature. Here are some
examples:

    vertx run MyVerticle.groovy --redeploy="**&#47;*.groovy" --launcher-class=io.vertx.core.Launcher
    vertx run MyVerticle.groovy --redeploy="**&#47;*.groovy,**&#47;*.rb"  --launcher-class=io.vertx.core.Launcher
    java io.vertx.core.Launcher run org.acme.MyVerticle --redeploy="**&#47;*.class"  --launcher-class=io.vertx.core
    .Launcher -cp ...

The redeployment process is implemented as follows. First your
application is launched as a background application (with the `start`
command). On matching file changes, the process is stopped and the
application is restarted. This avoids leaks, as the process is
restarted.

To enable the live redeploy, pass the `--redeploy` option to the `run`
command. The `--redeploy` indicates the set of file to *watch*. This set
can use Ant-style patterns (with `**`, `*` and `?`). You can specify
several sets by separating them using a comma (`,`). Patterns are
relative to the current working directory.

Parameters passed to the `run` command are passed to the application.
Java Virtual Machine options can be configured using `--java-opts`. For
instance, to pass the the `conf` parameter or a system property, you
need to use: `--java-opts="-conf=my-conf.json -Dkey=value"`

The `--launcher-class` option determine with with *main* class the
application is launcher. It’s generally `Launcher`, but you have use you
own *main*.

The redeploy feature can be used in your IDE:

  - Eclipse - create a *Run* configuration, using the
    `io.vertx.core.Launcher` class a *main class*. In the *Program
    arguments* area (in the *Arguments* tab), write `run
    your-verticle-fully-qualified-name --redeploy=**/*.java
    --launcher-class=io.vertx.core.Launcher`. You can also add other
    parameters. The redeployment works smoothly as Eclipse incrementally
    compiles your files on save.

  - IntelliJ - create a *Run* configuration (*Application*), set the
    *Main class* to `io.vertx.core.Launcher`. In the Program arguments
    write: `run your-verticle-fully-qualified-name --redeploy=**/*.class
    --launcher-class=io.vertx.core.Launcher`. To trigger the
    redeployment, you need to *make* the project or the module
    explicitly (*Build* menu → *Make project*).

To debug your application, create your run configuration as a remote
application and configure the debugger using `--java-opts`. However,
don’t forget to re-plug the debugger after every redeployment as a new
process is created every time.

You can also hook your build process in the redeploy cycle:

    java -jar target/my-fat-jar.jar --redeploy="**&#47;*.java" --on-redeploy="mvn package"
    java -jar build/libs/my-fat-jar.jar --redeploy="src&#47;**&#47;*.java" --on-redeploy='./gradlew shadowJar'

The "on-redeploy" option specifies a command invoked after the shutdown
of the application and before the restart. So you can hook your build
tool if it updates some runtime artifacts. For instance, you can launch
`gulp` or `grunt` to update your resources. Don’t forget that passing
parameters to your application requires the `--java-opts` param:

    java -jar target/my-fat-jar.jar --redeploy="**&#47;*.java" --on-redeploy="mvn package" --java-opts="-Dkey=val"
    java -jar build/libs/my-fat-jar.jar --redeploy="src&#47;**&#47;*.java" --on-redeploy='./gradlew shadowJar' --java-opts="-Dkey=val"

The redeploy feature also supports the following settings:

  - `redeploy-scan-period` : the file system check period (in
    milliseconds), 250ms by default

  - `redeploy-grace-period` : the amount of time (in milliseconds) to
    wait between 2 re-deployments, 1000ms by default

  - `redeploy-termination-period` : the amount of time to wait after
    having stopped the application (before launching user command). This
    is useful on Windows, where the process is not killed immediately.
    The time is given in milliseconds. 0 ms by default.

# Cluster Managers

In Vert.x a cluster manager is used for various functions including:

  - Discovery and group membership of Vert.x nodes in a cluster

  - Maintaining cluster wide topic subscriber lists (so we know which
    nodes are interested in which event bus addresses)

  - Distributed Map support

  - Distributed Locks

  - Distributed Counters

Cluster managers *do not* handle the event bus inter-node transport,
this is done directly by Vert.x with TCP connections.

The default cluster manager used in the Vert.x distributions is one that
uses [Hazelcast](http://hazelcast.com) but this can be easily replaced
by a different implementation as Vert.x cluster managers are pluggable.

A cluster manager must implement the interface `ClusterManager`. Vert.x
locates cluster managers at run-time by using the Java [Service
Loader](https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html)
functionality to locate instances of `ClusterManager` on the classpath.

If you are using Vert.x at the command line and you want to use
clustering you should make sure the `lib` directory of the Vert.x
installation contains your cluster manager jar.

If you are using Vert.x from a Maven or Gradle project just add the
cluster manager jar as a dependency of your project.

You can also specify cluster managers programmatically if embedding
Vert.x using `setClusterManager`.

# Logging

Vert.x logs using it’s in-built logging API. The default implementation
uses the JDK (JUL) logging so no extra logging dependencies are needed.

## Configuring JUL logging

A JUL logging configuration file can be specified in the normal JUL way
by providing a system property called: `java.util.logging.config.file`
with the value being your configuration file. For more information on
this and the structure of a JUL config file please consult the JUL
logging documentation.

Vert.x also provides a slightly more convenient way to specify a
configuration file without having to set a system property. Just provide
a JUL config file with the name `vertx-default-jul-logging.properties`
on your classpath (e.g. inside your fatjar) and Vert.x will use that to
configure JUL.

## Using another logging framework

If you don’t want Vert.x to use JUL for it’s own logging you can
configure it to use another logging framework, e.g. Log4J or SLF4J.

To do this you should set a system property called
`vertx.logger-delegate-factory-class-name` with the name of a Java class
which implements the interface `LogDelegateFactory`. We provide
pre-built implementations for Log4J (version 1), Log4J 2 and SLF4J with
the class names `io.vertx.core.logging.Log4jLogDelegateFactory`,
`io.vertx.core.logging.Log4j2LogDelegateFactory` and
`io.vertx.core.logging.SLF4JLogDelegateFactory` respectively. If you
want to use these implementations you should also make sure the relevant
Log4J or SLF4J jars are on your classpath.

Notice that, the provided delegate for Log4J 1 does not support
parameterized message. The delegate for Log4J 2 uses the `{}` syntax
like the SLF4J delegate. JUL delegate uses the `{x}` syntax.

## Netty logging

When configuring logging, you should care about configuring Netty
logging as well.

Netty does not rely on external logging configuration (e.g system
properties) and instead implements a logging configuration based on the
logging libraries visible from the Netty classes:

  - use `SLF4J` library if it is visible

  - otherwise use `Log4j` if it is visible

  - otherwise fallback `java.util.logging`

The logger implementation can be forced to a specific implementation by
setting Netty’s internal logger implementation directly on
`io.netty.util.internal.logging.InternalLoggerFactory`:

``` java
// Force logging to Log4j
InternalLoggerFactory.setDefaultFactory(Log4JLoggerFactory.INSTANCE);
```

## Troubleshooting

### SLF4J warning at startup

If, when you start your application, you see the following message:

    SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
    SLF4J: Defaulting to no-operation (NOP) logger implementation
    SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.

It means that you have SLF4J-API in your classpath but no actual
binding. Messages logged with SLF4J will be dropped. You should add a
binding to your classpath. Check
<https://www.slf4j.org/manual.html#swapping> to pick a binding and
configure it.

Be aware that Netty looks for the SLF4-API jar and uses it by default.

### Connection reset by peer

If your logs show a bunch of:

    io.vertx.core.net.impl.ConnectionBase
    SEVERE: java.io.IOException: Connection reset by peer

It means that the client is resetting the HTTP connection instead of
closing it. This message also indicates that you may have not consumed
the complete payload (the connection was cut before you were able to).

# Host name resolution

Vert.x uses an an address resolver for resolving host name into IP
addresses instead of the JVM built-in blocking resolver.

An host name resolves to an IP address using:

  - the *hosts* file of the operating system

  - otherwise DNS queries against a list of servers

By default it will use the list of the system DNS server addresses from
the environment, if that list cannot be retrieved it will use Google’s
public DNS servers `"8.8.8.8"` and `"8.8.4.4"`.

DNS servers can be also configured when creating a {@link
io.vertx.core.Vertx} instance:

``` $lang
{@link examples.CoreExamples#configureDNSServers}
```

The default port of a DNS server is `53`, when a server uses a different
port, this port can be set using a colon delimiter: `192.168.0.2:40000`.

> **Note**
> 
> sometimes it can be desirable to use the JVM built-in resolver, the
> JVM system property *-Dvertx.disableDnsResolver=true* activates this
> behavior

## Failover

When a server does not reply in a timely manner, the resolver will try
the next one from the list, the search is limited by {@link
io.vertx.core.dns.AddressResolverOptions\#setMaxQueries(int)} (the
default value is `4` queries).

A DNS query is considered as failed when the resolver has not received a
correct answer within {@link
io.vertx.core.dns.AddressResolverOptions\#getQueryTimeout()}
milliseconds (the default value is `5` seconds).

## Server list rotation

By default the dns server selection uses the first one, the remaining
servers are used for failover.

You can configure {@link
io.vertx.core.dns.AddressResolverOptions\#setRotateServers(boolean)} to
`true` to let the resolver perform a round-robin selection instead. It
spreads the query load among the servers and avoids all lookup to hit
the first server of the list.

Failover still applies and will use the next server in the list.

## Hosts mapping

The *hosts* file of the operating system is used to perform an hostname
lookup for an ipaddress.

An alternative *hosts* file can be used instead:

``` $lang
{@link examples.CoreExamples#configureHosts}
```

## Search domains

By default the resolver will use the system DNS search domains from the
environment. Alternatively an explicit search domain list can be
provided:

``` $lang
{@link examples.CoreExamples#configureSearchDomains()}
```

When a search domain list is used, the threshold for the number of dots
is `1` or loaded from `/etc/resolv.conf` on Linux, it can be configured
to a specific value with {@link
io.vertx.core.dns.AddressResolverOptions\#setNdots(int)}.

# High Availability and Fail-Over

Vert.x allows you to run your verticles with high availability (HA)
support. In that case, when a vert.x instance running a verticle dies
abruptly, the verticle is migrated to another vertx instance. The vert.x
instances must be in the same cluster.

## Automatic failover

When vert.x runs with *HA* enabled, if a vert.x instance where a
verticle runs fails or dies, the verticle is redeployed automatically on
another vert.x instance of the cluster. We call this *verticle
fail-over*.

To run vert.x with the *HA* enabled, just add the `-ha` flag to the
command line:

    vertx run my-verticle.js -ha

Now for HA to work, you need more than one Vert.x instances in the
cluster, so let’s say you have another Vert.x instance that you have
already started, for example:

    vertx run my-other-verticle.js -ha

If the Vert.x instance that is running `my-verticle.js` now dies (you
can test this by killing the process with `kill -9`), the Vert.x
instance that is running `my-other-verticle.js` will automatic deploy
`my-verticle
.js` so now that Vert.x instance is running both verticles.

> **Note**
> 
> the migration is only possible if the second vert.x instance has
> access to the verticle file (here `my-verticle.js`).

> **Important**
> 
> Please note that cleanly closing a Vert.x instance will not cause
> failover to occur, e.g. `CTRL-C` or `kill -SIGINT`

You can also start *bare* Vert.x instances - i.e. instances that are not
initially running any verticles, they will also failover for nodes in
the cluster. To start a bare instance you simply do:

    vertx run -ha

When using the `-ha` switch you do not need to provide the `-cluster`
switch, as a cluster is assumed if you want HA.

> **Note**
> 
> depending on your cluster configuration, you may need to customize the
> cluster manager configuration (Hazelcast by default), and/or add the
> `cluster-host` and `cluster-port` parameters.

## HA groups

When running a Vert.x instance with HA you can also optional specify a
*HA group*. A HA group denotes a logical group of nodes in the cluster.
Only nodes with the same HA group will failover onto one another. If you
don’t specify a HA group the default group `__DEFAULT__` is used.

To specify an HA group you use the `-hagroup` switch when running the
verticle, e.g.

    vertx run my-verticle.js -ha -hagroup my-group

Let’s look at an example:

In a first terminal:

    vertx run my-verticle.js -ha -hagroup g1

In a second terminal, let’s run another verticle using the same group:

    vertx run my-other-verticle.js -ha -hagroup g1

Finally, in a third terminal, launch another verticle using a different
group:

    vertx run yet-another-verticle.js -ha -hagroup g2

If we kill the instance in terminal 1, it will fail over to the instance
in terminal 2, not the instance in terminal 3 as that has a different
group.

If we kill the instance in terminal 3, it won’t get failed over as there
is no other vert.x instance in that group.

## Dealing with network partitions - Quora

The HA implementation also supports quora. A quorum is the minimum
number of votes that a distributed transaction has to obtain in order to
be allowed to perform an operation in a distributed system.

When starting a Vert.x instance you can instruct it that it requires a
`quorum` before any HA deployments will be deployed. In this context, a
quorum is a minimum number of nodes for a particular group in the
cluster. Typically you chose your quorum size to `Q = 1 + N/2` where `N`
is the number of nodes in the group. If there are less than `Q` nodes in
the cluster the HA deployments will undeploy. They will redeploy again
if/when a quorum is re-attained. By doing this you can prevent against
network partitions, a.k.a. *split brain*.

There is more information on quora
[here](http://en.wikipedia.org/wiki/Quorum_\(distributed_computing\)).

To run vert.x instances with a quorum you specify `-quorum` on the
command line, e.g.

In a first terminal:

    vertx run my-verticle.js -ha -quorum 3

At this point the Vert.x instance will start but not deploy the module
(yet) because there is only one node in the cluster, not 3.

In a second terminal:

    vertx run my-other-verticle.js -ha -quorum 3

At this point the Vert.x instance will start but not deploy the module
(yet) because there are only two nodes in the cluster, not 3.

In a third console, you can start another instance of vert.x:

    vertx run yet-another-verticle.js -ha -quorum 3

Yay\! - we have three nodes, that’s a quorum. At this point the modules
will automatically deploy on all instances.

If we now close or kill one of the nodes the modules will automatically
undeploy on the other nodes, as there is no longer a quorum.

Quora can also be used in conjunction with ha groups. In that case,
quora are resolved for each particular group.

# Native transports

Vert.x can run with [native
transports](http://netty.io/wiki/native-transports.html) (when
available) on BSD (OSX) and Linux:

``` $lang
{@link examples.CoreExamples#configureNative()}
```

> **Note**
> 
> preferring native transport will not prevent the application to
> execute (for example if a JAR is missing). If your application
> requires native transport, you need to check {@link
> io.vertx.core.Vertx\#isNativeTransportEnabled()}.

## Native Linux Transport

You need to add the following dependency in your classpath:

``` xml
<dependency>
 <groupId>io.netty</groupId>
 <artifactId>netty-transport-native-epoll</artifactId>
 <version>4.1.15.Final</version>
 <classifier>linux-x86_64</classifier>
</dependency>
```

Native on Linux gives you extra networking options:

  - `SO_REUSEPORT`

  - `TCP_QUICKACK`

  - `TCP_CORK`

  - `TCP_FASTOPEN`

<!-- end list -->

``` js
// Available on Linux
vertx.createHttpServer(new HttpServerOptions()
  .setTcpFastOpen(fastOpen)
  .setTcpCork(cork)
  .setTcpQuickAck(quickAck)
  .setReusePort(reusePort));
```

## Native BSD Transport

You need to add the following dependency in your classpath:

``` xml
<dependency>
 <groupId>io.netty</groupId>
 <artifactId>netty-transport-native-kqueue</artifactId>
 <version>4.1.15.Final</version>
 <classifier>osx-x86_64</classifier>
</dependency>
```

MacOS Sierra and above are supported.

Native on BSD gives you extra networking options:

  - `SO_REUSEPORT`

<!-- end list -->

``` js
// Available on BSD
vertx.createHttpServer(new HttpServerOptions()
  .setReusePort(reusePort));
```

## Domain sockets

Natives provide domain sockets support for servers:

``` js
import { SocketAddress } from "@vertx/core"
// Only available on BSD and Linux
vertx.createNetServer().connectHandler((so) => {
  // Handle application
}).listen(SocketAddress.domainSocketAddress("/var/tmp/myservice.sock"));
```

or for http:

``` js
import { SocketAddress } from "@vertx/core"
vertx.createHttpServer().requestHandler((req) => {
  // Handle application
}).listen(SocketAddress.domainSocketAddress("/var/tmp/myservice.sock"), (ar) => {
  if (ar.succeeded()) {
    // Bound to socket
  } else {
    ar.cause().printStackTrace();
  }
});
```

As well as clients:

``` js
import { SocketAddress } from "@vertx/core"
let netClient = vertx.createNetClient();

// Only available on BSD and Linux
let addr = SocketAddress.domainSocketAddress("/var/tmp/myservice.sock");

// Connect to the server
netClient.connect(addr, (ar) => {
  if (ar.succeeded()) {
    // Connected
  } else {
    ar.cause().printStackTrace();
  }
});
```

or for http:

``` js
import { SocketAddress } from "@vertx/core"
let httpClient = vertx.createHttpClient();

// Only available on BSD and Linux
let addr = SocketAddress.domainSocketAddress("/var/tmp/myservice.sock");

// Send request to the server
httpClient.request(HttpMethod.GET, addr, 8080, "localhost", "/", (resp) => {
  // Process response
}).end();
```

# Security notes

Vert.x is a toolkit, not an opinionated framework where we force you to
do things in a certain way. This gives you great power as a developer
but with that comes great responsibility.

As with any toolkit, it’s possible to write insecure applications, so
you should always be careful when developing your application especially
if it’s exposed to the public (e.g. over the internet).

## Web applications

If writing a web application it’s highly recommended that you use
Vert.x-Web instead of Vert.x core directly for serving resources and
handling file uploads.

Vert.x-Web normalises the path in requests to prevent malicious clients
from crafting URLs to access resources outside of the web root.

Similarly for file uploads Vert.x-Web provides functionality for
uploading to a known place on disk and does not rely on the filename
provided by the client in the upload which could be crafted to upload to
a different place on disk.

Vert.x core itself does not provide such checks so it would be up to you
as a developer to implement them yourself.

## Clustered event bus traffic

When clustering the event bus between different Vert.x nodes on a
network, the traffic is sent un-encrypted across the wire, so do not use
this if you have confidential data to send and your Vert.x nodes are not
on a trusted network.

## Standard security best practices

Any service can have potentially vulnerabilities whether it’s written
using Vert.x or any other toolkit so always follow security best
practice, especially if your service is public facing.

For example you should always run them in a DMZ and with an user account
that has limited rights in order to limit the extent of damage in case
the service was compromised.

# Vert.x Command Line Interface API

Vert.x Core provides an API for parsing command line arguments passed to
programs. It’s also able to print help messages detailing the options
available for a command line tool. Even if such features are far from
the Vert.x core topics, this API is used in the `Launcher` class that
you can use in *fat-jar* and in the `vertx` command line tools. In
addition, it’s polyglot (can be used from any supported language) and is
used in Vert.x Shell.

Vert.x CLI provides a model to describe your command line interface, but
also a parser. This parser supports different types of syntax:

  - POSIX like options (ie. `tar -zxvf foo.tar.gz`)

  - GNU like long options (ie. `du --human-readable --max-depth=1`)

  - Java like properties (ie. `java -Djava.awt.headless=true
    -Djava.net.useSystemProxies=true Foo`)

  - Short options with value attached (ie. `gcc -O2 foo.c`)

  - Long options with single hyphen (ie. `ant -projecthelp`)

Using the CLI api is a 3-steps process:

1.  The definition of the command line interface

2.  The parsing of the user command line

3.  The query / interrogation

## Definition Stage

Each command line interface must define the set of options and arguments
that will be used. It also requires a name. The CLI API uses the
`Option` and `Argument` classes to describe options and arguments:

``` js
import { CLI } from "@vertx/core"
let cli = CLI.create("copy").setSummary("A command line interface to copy files.").addOption(new Option()
  .setLongName("directory")
  .setShortName("R")
  .setDescription("enables directory support")
  .setFlag(true)).addArgument(new Argument()
  .setIndex(0)
  .setDescription("The source")
  .setArgName("source")).addArgument(new Argument()
  .setIndex(1)
  .setDescription("The destination")
  .setArgName("target"));
```

As you can see, you can create a new `CLI` using `CLI.create`. The
passed string is the name of the CLI. Once created you can set the
summary and description. The summary is intended to be short (one line),
while the description can contain more details. Each option and argument
are also added on the `CLI` object using the `addArgument` and
`addOption` methods.

### Options

An `Option` is a command line parameter identified by a *key* present in
the user command line. Options must have at least a long name or a short
name. Long name are generally used using a `--` prefix, while short
names are used with a single `-`. Options can get a description
displayed in the usage (see below). Options can receive 0, 1 or several
values. An option receiving 0 values is a `flag`, and must be declared
using `setFlag`. By default, options receive a single value, however,
you can configure the option to receive several values using
`setMultiValued`:

``` js
import { CLI } from "@vertx/core"
let cli = CLI.create("some-name").setSummary("A command line interface illustrating the options valuation.").addOption(new Option()
  .setLongName("flag")
  .setShortName("f")
  .setFlag(true)
  .setDescription("a flag")).addOption(new Option()
  .setLongName("single")
  .setShortName("s")
  .setDescription("a single-valued option")).addOption(new Option()
  .setLongName("multiple")
  .setShortName("m")
  .setMultiValued(true)
  .setDescription("a multi-valued option"));
```

Options can be marked as mandatory. A mandatory option not set in the
user command line throws an exception during the parsing:

``` js
import { CLI } from "@vertx/core"
let cli = CLI.create("some-name").addOption(new Option()
  .setLongName("mandatory")
  .setRequired(true)
  .setDescription("a mandatory option"));
```

Non-mandatory options can have a *default value*. This value would be
used if the user does not set the option in the command line:

``` js
import { CLI } from "@vertx/core"
let cli = CLI.create("some-name").addOption(new Option()
  .setLongName("optional")
  .setDefaultValue("hello")
  .setDescription("an optional option with a default value"));
```

An option can be *hidden* using the `setHidden` method. Hidden option
are not listed in the usage, but can still be used in the user command
line (for power-users).

If the option value is contrained to a fixed set, you can set the
different acceptable choices:

``` js
import { CLI } from "@vertx/core"
let cli = CLI.create("some-name").addOption(new Option()
  .setLongName("color")
  .setDefaultValue("green")
  .setChoices(["blue", "red", "green"])
  .setDescription("a color"));
```

Options can also be instantiated from their JSON form.

### Arguments

Unlike options, arguments do not have a *key* and are identified by
their *index*. For example, in `java com.acme.Foo`, `com.acme.Foo` is an
argument.

Arguments do not have a name, there are identified using a 0-based
index. The first parameter has the index `0`:

``` js
import { CLI } from "@vertx/core"
let cli = CLI.create("some-name").addArgument(new Argument()
  .setIndex(0)
  .setDescription("the first argument")
  .setArgName("arg1")).addArgument(new Argument()
  .setIndex(1)
  .setDescription("the second argument")
  .setArgName("arg2"));
```

If you don’t set the argument indexes, it computes it automatically by
using the declaration order.

``` js
import { CLI } from "@vertx/core"
let cli = CLI.create("some-name").addArgument(new Argument()
  .setDescription("the first argument")
  .setArgName("arg1")).addArgument(new Argument()
  .setDescription("the second argument")
  .setArgName("arg2"));
```

The `argName` is optional and used in the usage message.

As options, `Argument` can:

  - be hidden using `setHidden`

  - be mandatory using `setRequired`

  - have a default value using `setDefaultValue`

  - receive several values using `setMultiValued` - only the last
    argument can be multi-valued.

Arguments can also be instantiated from their JSON form.

### Usage generation

Once your `CLI` instance is configured, you can generate the *usage*
message:

``` js
import { CLI } from "@vertx/core"
let cli = CLI.create("copy").setSummary("A command line interface to copy files.").addOption(new Option()
  .setLongName("directory")
  .setShortName("R")
  .setDescription("enables directory support")
  .setFlag(true)).addArgument(new Argument()
  .setIndex(0)
  .setDescription("The source")
  .setArgName("source")).addArgument(new Argument()
  .setIndex(0)
  .setDescription("The destination")
  .setArgName("target"));

let builder = new (Java.type("java.lang.StringBuilder"))();
cli.usage(builder);
```

It generates an usage message like this one:

    Usage: copy [-R] source target
    
    A command line interface to copy files.
    
     -R,--directory   enables directory support

If you need to tune the usage message, check the `UsageMessageFormatter`
class.

## Parsing Stage

Once your `CLI` instance is configured, you can parse the user command
line to evaluate each option and argument:

``` js
let commandLine = cli.parse(userCommandLineArguments);
```

The `parse` method returns a `CommandLine` object containing the values.
By default, it validates the user command line and checks that each
mandatory options and arguments have been set as well as the number of
values received by each option. You can disable the validation by
passing `false` as second parameter of `parse`. This is useful if you
want to check an argument or option is present even if the parsed
command line is invalid.

You can check whether or not the `CommandLine` is valid using `isValid`.

## Query / Interrogation Stage

Once parsed, you can retrieve the values of the options and arguments
from the `CommandLine` object returned by the `parse` method:

``` js
let commandLine = cli.parse(userCommandLineArguments);
let opt = commandLine.getOptionValue("my-option");
let flag = commandLine.isFlagEnabled("my-flag");
let arg0 = commandLine.getArgumentValue(0);
```

One of your option can have been marked as "help". If a user command
line enabled a "help" option, the validation won’t failed, but give you
the opportunity to check if the user asks for help:

``` js
import { CLI } from "@vertx/core"
let cli = CLI.create("test").addOption(new Option()
  .setLongName("help")
  .setShortName("h")
  .setFlag(true)
  .setHelp(true)).addOption(new Option()
  .setLongName("mandatory")
  .setRequired(true));

let line = cli.parse(Java.type("java.util.Collections").singletonList("-h"));

// The parsing does not fail and let you do:
if (!line.isValid() && line.isAskingForHelp()) {
  let builder = new (Java.type("java.lang.StringBuilder"))();
  cli.usage(builder);
  stream.print(builder.toString());
}
```

# The vert.x Launcher

The vert.x `Launcher` is used in *fat jar* as main class, and by the
`vertx` command line utility. It executes a set of *commands* such as
*run*, *bare*, *start*…​

## Extending the vert.x Launcher

You can extend the set of command by implementing your own `Command` (in
Java only):

``` java
&#64;Name("my-command")
&#64;Summary("A simple hello command.")
public class MyCommand extends DefaultCommand {

 private String name;

 &#64;Option(longName = "name", required = true)
 public void setName(String n) {
   this.name = n;
 }

 &#64;Override
 public void run() throws CLIException {
   System.out.println("Hello " + name);
 }
}
```

You also need an implementation of `CommandFactory`:

``` java
public class HelloCommandFactory extends DefaultCommandFactory<HelloCommand> {
 public HelloCommandFactory() {
  super(HelloCommand.class);
 }
}
```

Then, create the
`src/main/resources/META-INF/services/io.vertx.core.spi.launcher.CommandFactory`
and add a line indicating the fully qualified name of the factory:

    io.vertx.core.launcher.example.HelloCommandFactory

Builds the jar containing the command. Be sure to includes the SPI file
(`META-INF/services/io.vertx.core.spi.launcher.CommandFactory`).

Then, place the jar containing the command into the classpath of your
fat-jar (or include it inside) or in the `lib` directory of your vert.x
distribution, and you would be able to execute:

    vertx hello vert.x
    java -jar my-fat-jar.jar hello vert.x

## Using the Launcher in fat jars

To use the `Launcher` class in a *fat-jar* just set the `Main-Class` of
the *MANIFEST* to `io.vertx.core.Launcher`. In addition, set the
`Main-Verticle` *MANIFEST* entry to the name of your main verticle.

By default, it executed the `run` command. However, you can configure
the default command by setting the `Main-Command` *MANIFEST* entry. The
default command is used if the *fat jar* is launched without a command.

## Sub-classing the Launcher

You can also create a sub-class of `Launcher` to start your application.
The class has been designed to be easily extensible.

A `Launcher` sub-class can:

  - customize the vert.x configuration in `beforeStartingVertx`

  - retrieve the vert.x instance created by the "run" or "bare" command
    by overriding `afterStartingVertx`

  - configure the default verticle and command with `getMainVerticle`
    and `getDefaultCommand`

  - add / remove commands using `register` and `unregister`

## Launcher and exit code

When you use the `Launcher` class as main class, it uses the following
exit code:

  - `0` if the process ends smoothly, or if an uncaught error is thrown

  - `1` for general purpose error

  - `11` if Vert.x cannot be initialized

  - `12` if a spawn process cannot be started, found or stopped. This
    error code is used by the `start` and `stop` command

  - `14` if the system configuration is not meeting the system
    requirement (shc as java not found)

  - `15` if the main verticle cannot be deployed

# Configuring Vert.x cache

When Vert.x needs to read a file from the classpath (embedded in a fat
jar, in a jar form the classpath or a file that is on the classpath), it
copies it to a cache directory. The reason behind this is simple:
reading a file from a jar or from an input stream is blocking. So to
avoid to pay the price every time, Vert.x copies the file to its cache
directory and reads it from there every subsequent read. This behavior
can be configured.

First, by default, Vert.x uses `$CWD/.vertx` as cache directory. It
creates a unique directory inside this one to avoid conflicts. This
location can be configured by using the `vertx.cacheDirBase` system
property. For instance if the current working directory is not writable
(such as in an immutable container context), launch your application
with:

    vertx run my.Verticle -Dvertx.cacheDirBase=/tmp/vertx-cache
    # or
    java -jar my-fat.jar vertx.cacheDirBase=/tmp/vertx-cache

> **Important**
> 
> the directory must be **writable**.

When you are editing resources such as HTML, CSS or JavaScript, this
cache mechanism can be annoying as it serves only the first version of
the file (and so you won’t see your edits if you reload your page). To
avoid this behavior, launch your application with
`-Dvertx.disableFileCaching=true`. With this setting, Vert.x still uses
the cache, but always refresh the version stored in the cache with the
original source. So if you edit a file served from the classpath and
refresh your browser, Vert.x reads it from the classpath, copies it to
the cache directory and serves it from there. Do not use this setting in
production, it can kill your performances.

Finally, you can disable completely the cache by using
`-Dvertx.disableFileCPResolving=true`. This setting is not without
consequences. Vert.x would be unable to read any files from the
classpath (only from the file system). Be very careful when using this
settings.
