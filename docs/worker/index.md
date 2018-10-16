# ES4X Worker API

[MDN](https://developer.mozilla.org/en-US/docs/Web/API/Web_Workers_API/Using_web_workers) defines Web Workers as:

> Web Workers is a simple means for web content to run scripts in background threads.
> The worker thread can perform tasks without interfering with the user interface.

ES4X is not a Browser and does not concern about user interface, however you can also run long running jobs on the
server side. In Vert.x everything is non blocking so even creating workers should follow the same semantics, for this
reason we cannot fully strict follow the worker interface but we replace the constructor with a factory function.

Imagine that in your code you need to run a CPU intensive task, you shouldn't block the event loop, so the logic step
is to use vert.x worker verticles. Worker API can be mapped to Vert.x API with some small nuances.

## Example Worker

Imagine the following worker code:

```js
// Get a reference to the Thread class to cause some blocking...
const Thread = Java.type('java.lang.Thread');

// The worker context is referenced by the variable `self` like on the MDN docs
self.onmessage = function(e) {
  console.log('Message received from main script, will sleep 5 seconds...');
  // Cause some havok in the event loop
  Thread.sleep(5 * 1000);
  var workerResult = 'Result: ' + (e.data[0] * e.data[1]);
  console.log('Posting message back to main script');
  // return data back to the main verticle
  postMessage(workerResult);
};
```

## What needs to be known

Workers are loaded in a seperate context so you can't share functions from the main verticle and worker, all
communication works with message passing (eventbus) using:

* `postMessage()` sends a message to the other side
* `onmessage` receives a message from the other side

### Verticle side

The verticle side of the API allows you to receive errors and `terminate()` workers, while the worker it self can't.

## Example Verticle

```js
Worker.create('workers/worker.js', function (create) {
  if (create.succeeded()) {
    var worker = create.result();
    
    worker.onmessage = function (msg) {
      console.log('onmessage: ' + msg);
    };
    
    worker.onerror = function (err) {
      console.err(err);
      // terminate the worker
      worker.terminate();
    };

    console.log('posting...');
    worker.postMessage({data: [2, 3]});
  }
});
```

So the code that would be not allowed to run on the event loop `Thread.sleep(5000)` is now running on a worker thread
leaving the event loop thread free for all the other IO tasks.

## Polyglot Workers

It is still possible to write workers that are not JavaScript workers. Workers must follow a very small list of rules:

* Workers must register the address: `{deploymentId}.out` to receive messages from the main script.
* Workers should send messages to: `{deploymentId}.in` to send messages to the main script.
* Message payloads are expected to be `JSON.stringify(message)` to avoid any issues between languages
* Workers are expected to be local, if want to connect to a worker anywhere in the cluster, then you need to use the
  constructor with an extra argument `true`, e.g.: `new Worker('deploymentId', true)`.
