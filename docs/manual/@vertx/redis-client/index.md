Vert.x-redis is redis client to be used with Vert.x.

This module allows data to be saved, retrieved, searched for, and
deleted in a Redis. Redis is an open source, advanced key-value store.
It is often referred to as a data structure server since keys can
contain strings, hashes, lists, sets and sorted sets. To use this module
you must have a Redis server instance running on your network.

Redis has a rich API and it can be organized in the following groups:

  - Cluster - Commands related to cluster management, note that using
    most of these commands you will need a redis server with version
    \>=3.0.0

  - Connection - Commands that allow you to switch DBs, connect,
    disconnect and authenticate to a server.

  - Hashes - Commands that allow operations on hashes.

  - HyperLogLog - Commands to approximating the number of distinct
    elements in a multiset, a HyperLogLog.

  - Keys - Commands to work with Keys.

  - List - Commands to work with Lists.

  - Pub/Sub - Commands to create queues and pub/sub clients.

  - Scripting - Commands to run Lua Scripts in redis.

  - Server - Commands to manage and get server configurations.

  - Sets - Commands to work with un ordered sets.

  - Sorted Sets - Commands to work with sorted sets.

  - Strings - Commands to work with Strings.

  - Transactions - Commands to handle transaction lifecycle.

  - Streams - Commands to handle streaming.

# Using Vert.x-Redis

To use the Vert.x Redis client, add the following dependency to the
*dependencies* section of your build descriptor:

  - Maven (in your `pom.xml`):

<!-- end list -->

``` xml
<dependency>
<groupId>io.vertx</groupId>
<artifactId>vertx-redis-client</artifactId>
<version>${maven.version}</version>
</dependency>
```

  - Gradle (in your `build.gradle` file):

<!-- end list -->

``` groovy
compile 'io.vertx:vertx-redis-client:${maven.version}'
```

# Connecting to Redis

The Redis client can operate in 3 distinct modes:

  - Simple client (probably what most users need).

  - Sentinel (when working with Redis in High Availability mode).

  - Cluster (when working with Redis in Clustered mode).

The connection mode is selected by the factory method on the Redis
interface. Regardless of the mode the client can be configured using a
`RedisOptions` data object. By default some configuration values are
initialized with the following values:

  - `netClientOptions`: default is `TcpKeepAlive: true`, `TcpNoDelay:
    true`

  - `endpoint`: default is `redis://localhost:6379`

  - `masterName`: default is `mymaster`

  - `role` default is `MASTER`

  - `slaves` default is `NEVER`

In order to obtain a connection use the following code:

``` js
import { Redis } from "@vertx/redis-client"
Redis.createClient(vertx, new RedisOptions()).connect((onConnect) => {
  if (onConnect.succeeded()) {
    let client = onConnect.result();
  }
});
```

In the configuration contains a `password` and/or a `select` database,
these 2 commands will be executed automatically once a successful
connection is established to the server.

``` js
import { Redis } from "@vertx/redis-client"
Redis.createClient(vertx, "redis://:abracadabra@localhost:6379/1").connect((onConnect) => {
  if (onConnect.succeeded()) {
    let client = onConnect.result();
  }
});
```

# Connection String

The client will recognize addresses that follow the expression:

    redis://[:password@]host[:port][/db-number]

Or

    unix://[:password@]/domain/docker.sock[?select=db-number]

When specifying a password or a database those commands are always
executed on connection start.

# Running commands

Given that the redis client is connected to the server, all commands are
now possible to execute using this module. The module offers a clean API
for executing commands without the need to hand write the command
itself, for example if one wants to get a value of a key it can be done
as:

``` js
import { RedisAPI } from "@vertx/redis-client"
let redis = RedisAPI.api(client);

redis.get("mykey", (res) => {
  if (res.succeeded()) {
    // so something...
  }
});
```

The response object is a generic type that allow converting from the
basic redis types to your language types. For example, if your response
is of type `INTEGER` then you can get the value as any numeric primitive
type `int`, `long`, etc…​

Or you can perform more complex tasks such as handling responses as
iterators:

``` js
// this is a multi redis response (think of it as an array
if (response.type() === ResponseType.MULTI) {
  response.forEach(item => {
    // do something with item...
  });
}
```

# High Availability mode

To work with high availability mode the connection creation is quite
similar:

``` js
import { Command } from "@vertx/redis-client"
import { Request } from "@vertx/redis-client"
import { Redis } from "@vertx/redis-client"
Redis.createClient(vertx, new RedisOptions()
  .setType("SENTINEL")
  .setConnectionStrings(["redis://127.0.0.1:5000", "redis://127.0.0.1:5001", "redis://127.0.0.1:5002"])
  .setMasterName("sentinel7000")
  .setRole("MASTER")).connect((onConnect) => {
  // assuming we got a connection to the master node
  // query the info for the node
  onConnect.result().send(Request.cmd(Command.INFO), (info) => {
    // do something...
  });
});
```

What is important to notice is that in this mode, an extra connection is
established to the server(s) and behind the scenes the client will
listen for events from the sentinel. When the sentinel notifies that we
switched masters, then an exception is send to the client and you can
decide what to do next.

# Cluster mode

To work with cluster the connection creation is quite similar:

``` js
let options = new RedisOptions()
  .setConnectionStrings(["redis://127.0.0.1:7000", "redis://127.0.0.1:7001", "redis://127.0.0.1:7002", "redis://127.0.0.1:7003", "redis://127.0.0.1:7004", "redis://127.0.0.1:7005"]);
```

In this case the configuration requires one of more members of the
cluster to be known. This list will be used to ask the cluster for the
current configuration, which means if any of the listed members is not
available it will be skipped.

In cluster mode a connection is established to each node and special
care is needed when executing commands. It is recommended to read redis
manual in order to understand how clustering works. The client operating
in this mode will do a best effort to identify which slot is used by the
executed command in order to execute it on the right node. There could
be cases where this isn’t possible to identify and in that case as a
best effort the command will be run on a random node.

# Pub/Sub mode

Redis supports queues and pub/sub mode, when operated in this mode once
a connection invokes a subscriber mode then it cannot be used for
running other commands than the command to leave that mode.

To start a subscriber one would do:

``` js
import { Redis } from "@vertx/redis-client"

Redis.createClient(vertx, new RedisOptions()).connect((onConnect) => {
  if (onConnect.succeeded()) {
    let client = onConnect.result();

    client.handler((message) => {
      // do whatever you need to do with your message
    });
  }
});
```

And from another place in the code publish messages to the queue:

``` js
import { Command } from "@vertx/redis-client"
import { Request } from "@vertx/redis-client"

redis.send(Request.cmd(Command.PUBLISH).arg("channel1").arg("Hello World!"), (res) => {
  if (res.succeeded()) {
    // published!
  }
});
```

> **Note**
> 
> It is important to remember that the commands `SUBSCRIBE`,
> `UNSUBSCRIBE`, `PSUBSCRIBE` and `PUNSUBSCRIBE` are `void`. This means
> that the result in case of success is `null` not a instance of
> response. All messages are then routed through the handler on the
> client.

# Domain Sockets

Most of the examples shown connecting to a TCP sockets, however it is
also possible to use Redis connecting to a UNIX domain docket:

``` js
import { Redis } from "@vertx/redis-client"

Redis.createClient(vertx, "unix:///tmp/redis.sock").connect((onConnect) => {
  if (onConnect.succeeded()) {
    let client = onConnect.result();
  }
});
```

Be aware that HA and cluster modes report server addresses always on TCP
addresses not domain sockets. So the combination is not possible. Not
because of this client but how Redis works.

# Connection Pooling

All client variations are backed by a connection pool. By default the
configuration sets the pool size to 1, which means that it operates just
like a single connection. There are 4 tunnables for the pool:

  - `maxPoolSize` the max number of connections on the pool (default
    `6`)

  - `maxPoolWaiting` the max waiting handlers to get a connection on a
    queue (default `24`)

  - `poolCleanerInterval` the interval when connections will be clean
    default is `-1` (disabled)

  - `poolRecycleTimeout` the timeout to keep an open connection on the
    pool waiting and then close (default `15_000`)

Pooling is quite useful to avoid custom connection management, for
example you can just use as:

``` js
import { Command } from "@vertx/redis-client"
import { Request } from "@vertx/redis-client"
import { Redis } from "@vertx/redis-client"
Redis.createClient(vertx, "redis://localhost:7006").send(Request.cmd(Command.PING), (send) => {
  if (send.succeeded()) {
    // Should have received a pong...
  }
});
```

It is important to observe that no connection was acquired or returned,
it’s all handled by the pool. However there might be some scalability
issues when more than 1 concurrent request attempts to get a connection
from the pool, in order to overcome this we need to tune the pool. A
common configuration is to set the maximum size of the pool to the
number of available CPU cores and allow requests to get a connection
from the pool to queue:

``` js
import { Command } from "@vertx/redis-client"
import { Request } from "@vertx/redis-client"
import { Redis } from "@vertx/redis-client"
Redis.createClient(vertx, new RedisOptions()
  .setConnectionString("redis://localhost:7006")
  .setMaxPoolSize(8)
  .setMaxWaitingHandlers(32)).send(Request.cmd(Command.PING), (send) => {
  if (send.succeeded()) {
    // Should have received a pong...
  }
});
```

> **Note**
> 
> Pooling is not compatible with `SUBSCRIBE`, `UNSUBSCRIBE`,
> `PSUBSCRIBE` or `PUNSUBSCRIBE` because these commands will modify the
> way the connection operates and the connection cannot be reused.

# Implementing Reconnect on Error

While the connection pool is quite useful, for performance, a connection
should not be auto managed but controlled by you. In this case you will
need to handle connection recovery, error handling and reconnect.

A typical scenario is that a user will want to reconnect to the server
whenever an error occurs. The automatic reconnect is not part of the
redis client as it will force a behaviour that might not match the user
expectations, for example:

1.  What should happen to current in-flight requests?

2.  Should the exception handler be invoked or not?

3.  What if the retry will also fail?

4.  Should the previous state (db, authentication, subscriptions) be
    restored?

5.  Etc…​

In order to give the user full flexibility, this decision should not be
performed by the client. However a simple reconnect with backoff timeout
could be implemented as follows:

``` js
Code not translatable
```

In this example the client object will be replaced on reconnect and the
application will retry up to 16 times with a backoff up to 1280ms. By
discarding the client we ensure that all old inflight responses are lost
and all new ones will be on the new connection.
