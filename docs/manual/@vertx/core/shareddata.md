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

sharedData.getAsyncMap("mymap", (res, res_err) => {
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

sharedData.getLocalAsyncMap("mymap", (res, res_err) => {
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
map.put("foo", "bar", (resPut, resPut_err) => {
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
map.get("foo", (resGet, resGet_err) => {
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

sharedData.getLock("mylock", (res, res_err) => {
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

sharedData.getLockWithTimeout("mylock", 10000, (res, res_err) => {
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

sharedData.getLocalLock("mylock", (res, res_err) => {
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

sharedData.getCounter("mycounter", (res, res_err) => {
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

sharedData.getLocalCounter("mycounter", (res, res_err) => {
  if (res.succeeded()) {
    // Local-only counter
    let counter = res.result();
  } else {
    // Something went wrong!
  }
});
```
