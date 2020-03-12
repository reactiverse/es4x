[Consul](https://www.consul.io) is a tool for discovering and
configuring services in your infrastructure. A Vert.x client allowing
applications to interact with a Consul system via blocking and
non-blocking HTTP API.

# Using Vert.x Consul Client

To use this project, add the following dependency to the *dependencies*
section of your build descriptor:

  - Maven (in your `pom.xml`):

<!-- end list -->

``` xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-consul-client</artifactId>
 <version>${maven.version}</version>
</dependency>
```

  - Gradle (in your `build.gradle` file):

<!-- end list -->

``` groovy
compile 'io.vertx:vertx-consul-client:${maven.version}'
```

# Creating a client

Just use factory method:

``` js
import { ConsulClient } from "@vertx/consul"

let client = ConsulClient.create(vertx);
```

Also the client can be configured with an options.

``` js
import { ConsulClient } from "@vertx/consul"

let options = new ConsulClientOptions()
  .setHost("consul.example.com");

let client = ConsulClient.create(vertx, options);
```

The following configuration is supported by the consul client:

  - `host`
    Consul host. Defaults to `localhost`

  - `port`
    Consul HTTP API port. Defaults to `8500`

  - `timeout`
    Sets the amount of time (in milliseconds) after which if the request
    does not return any data within the timeout period an failure will
    be passed to the handler and the request will be closed.

  - `aclToken`
    The ACL token. When provided, the client will use this token when
    making requests to the Consul by providing the "?token" query
    parameter. When not provided, the empty token, which maps to the
    'anonymous' ACL policy, is used.

  - `dc`
    The datacenter name. When provided, the client will use it when
    making requests to the Consul by providing the "?dc" query
    parameter. When not provided, the datacenter of the consul agent is
    queried.

ConsulClient options extends WebClientOptions from `vertx-web-client`
module, therefore a lot of settings are available. Please see the
documentation.

# Using the API

The client API is represented by `ConsulClient`. The API is very similar
to Consul’s HTTP API that described in [Consul API
docs](https://www.consul.io/docs/agent/http.html)

## Blocking queries

Certain endpoints support a feature called a "blocking query". A
blocking query is used to wait for a potential change using long
polling. Any endpoint that supports blocking also provide a unique
identifier (index) representing the current state of the requested
resource. The following configuration is used to perform blocking
queries:

  - `index`
    value indicating that the client wishes to wait for any changes
    subsequent to that index.

  - `wait`
    parameter specifying a maximum duration for the blocking request.
    This is limited to 10 minutes.

<!-- end list -->

``` js
let opts = new BlockingQueryOptions()
  .setIndex(lastIndex)
  .setWait("1m");
```

A critical note is that the return of a blocking request is **no
guarantee** of a change. It is possible that the timeout was reached or
that there was an idempotent write that does not affect the result of
the query.

# Key/Value Store

The KV endpoints are used to access Consul’s simple key/value store,
useful for storing service configuration or other metadata. The
following endpoints are supported:

  - To manage updates of individual keys, deletes of individual keys or
    key prefixes, and fetches of individual keys or key prefixes

  - To manage updates or fetches of multiple keys inside a single,
    atomic transaction

## Get key-value pair from store

Consul client can return the value for certain key

``` js
consulClient.getValue("key", (res) => {
  if (res.succeeded()) {
    console.log("retrieved value: " + res.result().value);
    console.log("modify index: " + res.result().modifyIndex);
  } else {
    res.cause().printStackTrace();
  }
});
```

…​or it can return all key-value pairs with the given prefix

``` js
consulClient.getValues("prefix", (res) => {
  if (res.succeeded()) {
    console.log("modify index: " + res.result().index);
    res.result().list.forEach(kv => {
      console.log("retrieved value: " + kv.value);
    });
  } else {
    res.cause().printStackTrace();
  }
});
```

The returned key-value object contains these fields (see
[documentation](https://www.consul.io/docs/agent/http/kv.html#single)):

  - `createIndex`
    the internal index value that represents when the entry was created.

  - `modifyIndex`
    the last index that modified this key

  - `lockIndex`
    the number of times this key has successfully been acquired in a
    lock

  - `key`
    the key

  - `flags`
    the flags attached to this entry. Clients can choose to use this
    however makes sense for their application

  - `value`
    the value

  - `session`
    the session that owns the lock

The modify index can be used for blocking queries:

``` js
let opts = new BlockingQueryOptions()
  .setIndex(modifyIndex)
  .setWait("1m");

consulClient.getValueWithOptions("key", opts, (res) => {
  if (res.succeeded()) {
    console.log("retrieved value: " + res.result().value);
    console.log("new modify index: " + res.result().modifyIndex);
  } else {
    res.cause().printStackTrace();
  }
});
```

## Put key-value pair to store

``` js
consulClient.putValue("key", "value", (res) => {
  if (res.succeeded()) {
    let opResult = res.result() ? "success" : "fail";
    console.log("result of the operation: " + opResult);
  } else {
    res.cause().printStackTrace();
  }
});
```

Put request with options also accepted

``` js
let opts = new KeyValueOptions()
  .setFlags(42)
  .setCasIndex(modifyIndex)
  .setAcquireSession("acquireSessionID")
  .setReleaseSession("releaseSessionID");

consulClient.putValueWithOptions("key", "value", opts, (res) => {
  if (res.succeeded()) {
    let opResult = res.result() ? "success" : "fail";
    console.log("result of the operation: " + opResult);
  } else {
    res.cause().printStackTrace();
  }
});
```

The list of the query options that can be used with a `PUT` request:

  - `flags`
    This can be used to specify an unsigned value between `0` and
    `264-1`. Clients can choose to use this however makes sense for
    their application.

  - `casIndex`
    This flag is used to turn the PUT into a Check-And-Set operation.
    This is very useful as a building block for more complex
    synchronization primitives. If the index is `0`, Consul will only
    put the key if it does not already exist. If the index is non-zero,
    the key is only set if the index matches the ModifyIndex of that
    key.

  - `acquireSession`
    This flag is used to turn the PUT into a lock acquisition operation.
    This is useful as it allows leader election to be built on top of
    Consul. If the lock is not held and the session is valid, this
    increments the LockIndex and sets the Session value of the key in
    addition to updating the key contents. A key does not need to exist
    to be acquired. If the lock is already held by the given session,
    then the LockIndex is not incremented but the key contents are
    updated. This lets the current lock holder update the key contents
    without having to give up the lock and reacquire it.

  - `releaseSession`
    This flag is used to turn the PUT into a lock release operation.
    This is useful when paired with `acquireSession` as it allows
    clients to yield a lock. This will leave the LockIndex unmodified
    but will clear the associated Session of the key. The key must be
    held by this session to be unlocked.

## Transactions

When connected to Consul 0.7 and later, client allows to manage updates
or fetches of multiple keys inside a single, atomic transaction. KV is
the only available operation type, though other types of operations may
be added in future versions of Consul to be mixed with key/value
operations (see
[documentation](https://www.consul.io/docs/agent/http/kv.html#txn)).

``` js
let request = new TxnRequest()
  .setOperations([new TxnKVOperation()
    .setKey("key1")
    .setValue("value1")
    .setType("SET"), new TxnKVOperation()
    .setKey("key2")
    .setValue("value2")
    .setType("SET")]);

consulClient.transaction(request, (res) => {
  if (res.succeeded()) {
    console.log("succeeded results: " + res.result().results.length);
    console.log("errors: " + res.result().errors.length);
  } else {
    res.cause().printStackTrace();
  }
});
```

## Delete key-value pair

At last, Consul client allows to delete key-value pair from store:

``` js
consulClient.deleteValue("key", (res) => {
  if (res.succeeded()) {
    console.log("complete");
  } else {
    res.cause().printStackTrace();
  }
});
```

…​or all key-value pairs with corresponding key prefix

``` js
consulClient.deleteValues("prefix", (res) => {
  if (res.succeeded()) {
    console.log("complete");
  } else {
    res.cause().printStackTrace();
  }
});
```

# Services

One of the main goals of service discovery is to provide a catalog of
available services. To that end, the agent provides a simple service
definition format to declare the availability of a service and to
potentially associate it with a health check.

## Service registering

A service definition must include a `name` and may optionally provide an
`id`, `tags`, `address`, `port`, and `checks`.

``` js
let opts = new ServiceOptions()
  .setName("serviceName")
  .setId("serviceId")
  .setTags(["tag1", "tag2"])
  .setCheckOptions(new CheckOptions()
    .setTtl("10s"))
  .setAddress("10.0.0.1")
  .setPort(8048);
```

  - `name`
    the name of service

  - `id`
    the `id` is set to the `name` if not provided. It is required that
    all services have a unique ID per node, so if names might conflict
    then unique IDs should be provided.

  - `tags`
    list of values that are opaque to Consul but can be used to
    distinguish between primary or secondary nodes, different versions,
    or any other service level labels.

  - `address`
    used to specify a service-specific IP address. By default, the IP
    address of the agent is used, and this does not need to be provided.

  - `port`
    used as well to make a service-oriented architecture simpler to
    configure; this way, the address and port of a service can be
    discovered.

  - `checks`
    associated health checks

These options used to register service in catalog:

``` js
consulClient.registerService(opts, (res) => {
  if (res.succeeded()) {
    console.log("Service successfully registered");
  } else {
    res.cause().printStackTrace();
  }

});
```

## Service discovery

Consul client allows to obtain actual list of the nodes providing a
service

``` js
consulClient.catalogServiceNodes("serviceName", (res) => {
  if (res.succeeded()) {
    console.log("found " + res.result().list.length + " services");
    console.log("consul state index: " + res.result().index);
    res.result().list.forEach(service => {
      console.log("Service node: " + service.node);
      console.log("Service address: " + service.address);
      console.log("Service port: " + service.port);
    });
  } else {
    res.cause().printStackTrace();
  }
});
```

It is possible to obtain this list with the statuses of the associated
health checks. The result can be filtered by check status.

``` js
consulClient.healthServiceNodes("serviceName", passingOnly, (res) => {
  if (res.succeeded()) {
    console.log("found " + res.result().list.length + " services");
    console.log("consul state index: " + res.result().index);
    res.result().list.forEach(entry => {
      console.log("Service node: " + entry.node);
      console.log("Service address: " + entry.service.address);
      console.log("Service port: " + entry.service.port);
    });
  } else {
    res.cause().printStackTrace();
  }
});
```

There are additional parameters for services queries

``` js
let queryOpts = new ServiceQueryOptions()
  .setTag("tag1")
  .setNear("_agent")
  .setBlockingOptions(new BlockingQueryOptions()
    .setIndex(lastIndex));
```

  - `tag`
    by default, all nodes matching the service are returned. The list
    can be filtered by tag using the `tag` query parameter

  - `near`
    adding the optional `near` parameter with a node name will sort the
    node list in ascending order based on the estimated round trip time
    from that node. Passing `near`=`_agent` will use the agent’s node
    for the sort.

  - `blockingOptions`
    the blocking qyery options

Then the request should look like

``` js
consulClient.healthServiceNodesWithOptions("serviceName", passingOnly, queryOpts, (res) => {
  if (res.succeeded()) {
    console.log("found " + res.result().list.length + " services");
  } else {
    res.cause().printStackTrace();
  }

});
```

## Deregister service

Service can be deregistered by its ID:

``` js
consulClient.deregisterService("serviceId", (res) => {
  if (res.succeeded()) {
    console.log("Service successfully deregistered");
  } else {
    res.cause().printStackTrace();
  }
});
```

# Health Checks

One of the primary roles of the agent is management of system-level and
application-level health checks. A health check is considered to be
application-level if it is associated with a service. If not associated
with a service, the check monitors the health of the entire node.

``` js
let opts = new CheckOptions()
  .setTcp("localhost:4848")
  .setInterval("1s");
```

The list of check options that supported by Consul client is:

  - `id`
    the check ID

  - `name`
    check name

  - `script`
    local path to checking script. Also you should set checking interval

  - `http`
    HTTP address to check. Also you should set checking interval

  - `ttl`
    Time to Live of check

  - `tcp`
    TCP address to check. Also you should set checking interval

  - `interval`
    checking interval in Go’s time format which is sequence of decimal
    numbers, each with optional fraction and a unit suffix, such as
    "300ms", "-1.5h" or "2h45m". Valid time units are "ns", "us" (or
    "µs"), "ms", "s", "m", "h"

  - `notes`
    the check notes

  - `serviceId`
    the service ID to associate the registered check with an existing
    service provided by the agent.

  - `deregisterAfter`
    deregister timeout. This is optional field, which is a timeout in
    the same time format as Interval and TTL. If a check is associated
    with a service and has the critical state for more than this
    configured value, then its associated service (and all of its
    associated checks) will automatically be deregistered. The minimum
    timeout is 1 minute, and the process that reaps critical services
    runs every 30 seconds, so it may take slightly longer than the
    configured timeout to trigger the deregistration. This should
    generally be configured with a timeout that’s much, much longer than
    any expected recoverable outage for the given service.

  - `status`
    the check status to specify the initial state of the health check

The `Name` field is mandatory, as is one of `Script`, `HTTP`, `TCP` or
`TTL`. `Script`, `TCP` and `HTTP` also require that `Interval` be set.
If an `ID` is not provided, it is set to `Name`. You cannot have
duplicate ID entries per agent, so it may be necessary to provide an ID.

``` js
consulClient.registerCheck(opts, (res) => {
  if (res.succeeded()) {
    console.log("check successfully registered");
  } else {
    res.cause().printStackTrace();
  }
});
```

# Events

The Consul provides a mechanism to fire a custom user event to an entire
datacenter. These events are opaque to Consul, but they can be used to
build scripting infrastructure to do automated deploys, restart
services, or perform any other orchestration action.

To send user event only its name is required

``` js
consulClient.fireEvent("eventName", (res) => {
  if (res.succeeded()) {
    console.log("Event sent");
    console.log("id: " + res.result().id);
  } else {
    res.cause().printStackTrace();
  }
});
```

Also additional options can be specified.

  - `node`
    regular expression to filter recipients by node name

  - `service`
    regular expression to filter recipients by service

  - `tag`
    regular expression to filter recipients by tag

  - `payload`
    an optional body of the event. The body contents are opaque to
    Consul and become the "payload" of the event

<!-- end list -->

``` js
let opts = new EventOptions()
  .setTag("tag")
  .setPayload("message");

consulClient.fireEventWithOptions("eventName", opts, (res) => {
  if (res.succeeded()) {
    console.log("Event sent");
    console.log("id: " + res.result().id);
  } else {
    res.cause().printStackTrace();
  }
});
```

The Consul Client supports queries for obtain the most recent events
known by the agent. Events are broadcast using the gossip protocol, so
they have no global ordering nor do they make a promise of delivery.
Agents only buffer the most recent entries. The current buffer size is
256, but this value could change in the future.

``` js
consulClient.listEvents((res) => {
  if (res.succeeded()) {
    console.log("Consul index: " + res.result().index);
    res.result().list.forEach(event => {
      console.log("Event id: " + event.id);
      console.log("Event name: " + event.name);
      console.log("Event payload: " + event.payload);
    });
  } else {
    res.cause().printStackTrace();
  }
});
```

The Consul Index can be used to prepare blocking requests:

``` js
let opts = new EventListOptions()
  .setName("eventName")
  .setBlockingOptions(new BlockingQueryOptions()
    .setIndex(lastIndex));

consulClient.listEventsWithOptions(opts, (res) => {
  if (res.succeeded()) {
    console.log("Consul index: " + res.result().index);
    res.result().list.forEach(event => {
      console.log("Event id: " + event.id);
    });
  } else {
    res.cause().printStackTrace();
  }
});
```

# Sessions

Consul provides a session mechanism which can be used to build
distributed locks. Sessions act as a binding layer between nodes, health
checks, and key/value data. When a session is constructed, a node name,
a list of health checks, a behavior, a TTL, and a lock-delay may be
provided.

``` js
let opts = new SessionOptions()
  .setNode("nodeId")
  .setBehavior("RELEASE");
```

  - `lockDelay`
    can be specified as a duration string using an 's' suffix for
    seconds. The default is '15s'.

  - `name`
    can be used to provide a human-readable name for the Session.

  - `node`
    must refer to a node that is already registered, if specified. By
    default, the agent’s own node name is used.

  - `checks`
    is used to provide a list of associated health checks. It is highly
    recommended that, if you override this list, you include the default
    `serfHealth`.

  - `behavior`
    can be set to either `release` or `delete`. This controls the
    behavior when a session is invalidated. By default, this is
    `release`, causing any locks that are held to be released. Changing
    this to `delete` causes any locks that are held to be deleted.
    `delete` is useful for creating ephemeral key/value entries.

  - `ttl`
    is a duration string, and like `LockDelay` it can use s as a suffix
    for seconds. If specified, it must be between 10s and 86400s
    currently. When provided, the session is invalidated if it is not
    renewed before the TTL expires.

For full info see [Consul Sessions
internals](https://www.consul.io/docs/internals/sessions.html)

The newly constructed session is provided with a named ID that can be
used to identify it. This ID can be used with the KV store to acquire
locks: advisory mechanisms for mutual exclusion.

``` js
consulClient.createSessionWithOptions(opts, (res) => {
  if (res.succeeded()) {
    console.log("Session successfully created");
    console.log("id: " + res.result());
  } else {
    res.cause().printStackTrace();
  }
});
```

And also to destroy it

``` js
consulClient.destroySession(sessionId, (res) => {
  if (res.succeeded()) {
    console.log("Session successfully destroyed");
  } else {
    res.cause().printStackTrace();
  }
});
```

Lists sessions belonging to a node

``` js
consulClient.listNodeSessions("nodeId", (res) => {
  if (res.succeeded()) {
    res.result().list.forEach(session => {
      console.log("Session id: " + session.id);
      console.log("Session node: " + session.node);
      console.log("Session create index: " + session.createIndex);
    });
  } else {
    res.cause().printStackTrace();
  }
});
```

All of the read session endpoints support blocking queries and all
consistency modes.

``` js
let blockingOpts = new BlockingQueryOptions()
  .setIndex(lastIndex);

consulClient.listSessionsWithOptions(blockingOpts, (res) => {
  if (res.succeeded()) {
    console.log("Found " + res.result().list.length + " sessions");
  } else {
    res.cause().printStackTrace();
  }
});
```

# Nodes in datacenter

``` js
consulClient.catalogNodes((res) => {
  if (res.succeeded()) {
    console.log("found " + res.result().list.length + " nodes");
    console.log("consul state index " + res.result().index);
  } else {
    res.cause().printStackTrace();
  }
});
```

This endpoint supports blocking queries and sorting by distance from
specified node

``` js
let opts = new NodeQueryOptions()
  .setNear("_agent")
  .setBlockingOptions(new BlockingQueryOptions()
    .setIndex(lastIndex));

consulClient.catalogNodesWithOptions(opts, (res) => {
  if (res.succeeded()) {
    console.log("found " + res.result().list.length + " nodes");
  } else {
    res.cause().printStackTrace();
  }
});
```

# Prepared Queries

This endpoint creates, updates, destroys, and executes prepared queries.
Prepared queries allow you to register a complex service query and then
execute it later via its ID or name to get a set of healthy nodes that
provide a given service. This is particularly useful in combination with
Consul’s DNS Interface as it allows for much richer queries than would
be possible given the limited entry points exposed by DNS.

There are many parameters to creating a prepared query. For full details
please [see docs](https://www.consul.io/api/query.html)

  - `dc`
    Specifies the datacenter to query. This will default to the
    datacenter of the agent being queried. This is specified as part of
    the URL as a query parameter.

  - `name`
    Specifies an optional friendly name that can be used to execute a
    query instead of using its ID.

  - `session`
    Specifies the ID of an existing session. This provides a way to
    automatically remove a prepared query when the given session is
    invalidated. If not given the prepared query must be manually
    removed when no longer needed.

  - `token`
    Specifies the ACL token to use each time the query is executed. This
    allows queries to be executed by clients with lesser or even no ACL
    Token, so this should be used with care. The token itself can only
    be seen by clients with a management token. If the Token field is
    left blank or omitted, the client’s ACL Token will be used to
    determine if they have access to the service being queried. If the
    client does not supply an ACL Token, the anonymous token will be
    used.

  - `service`
    Specifies the name of the service to query. This is required field.

  - `failover`
    contains two fields, both of which are optional, and determine what
    happens if no healthy nodes are available in the local datacenter
    when the query is executed. It allows the use of nodes in other
    datacenters with very little configuration.

  - `nearestN`
    Specifies that the query will be forwarded to up to NearestN other
    datacenters based on their estimated network round trip time using
    Network Coordinates from the WAN gossip pool. The median round trip
    time from the server handling the query to the servers in the remote
    datacenter is used to determine the priority.

  - `datacenters`
    Specifies a fixed list of remote datacenters to forward the query to
    if there are no healthy nodes in the local datacenter. Datacenters
    are queried in the order given in the list. If this option is
    combined with NearestN, then the NearestN queries will be performed
    first, followed by the list given by Datacenters. A given datacenter
    will only be queried one time during a failover, even if it is
    selected by both NearestN and is listed in Datacenters.

  - `onlyPassing`
    Specifies the behavior of the query’s health check filtering. If
    this is set to false, the results will include nodes with checks in
    the passing as well as the warning states. If this is set to true,
    only nodes with checks in the passing state will be returned.

  - `tags`
    Specifies a list of service tags to filter the query results. For a
    service to pass the tag filter it must have all of the required
    tags, and none of the excluded tags (prefixed with \!).

  - `nodeMeta`
    Specifies a list of user-defined key/value pairs that will be used
    for filtering the query results to nodes with the given metadata
    values present.

  - `dnsTtl`
    Specifies the TTL duration when query results are served over DNS.
    If this is specified, it will take precedence over any Consul
    agent-specific configuration.

  - `templateType`
    is the query type, which must be `name_prefix_match`. This means
    that the template will apply to any query lookup with a name whose
    prefix matches the Name field of the template. In this example, any
    query for geo-db will match this query. Query templates are resolved
    using a longest prefix match, so it’s possible to have high-level
    templates that are overridden for specific services. Static queries
    are always resolved first, so they can also override templates.

  - `templateRegexp`
    is an optional regular expression which is used to extract fields
    from the entire name, once this template is selected. In this
    example, the regular expression takes the first item after the "-"
    as the database name and everything else after as a tag. See the RE2
    reference for syntax of this regular expression.

<!-- end list -->

``` js
let def = new PreparedQueryDefinition()
  .setName("Query name")
  .setService("service-${match(1)}-${match(2)}")
  .setDcs(["dc1", "dc42"])
  .setTemplateType("name_prefix_match")
  .setTemplateRegexp("^find_(.+?)_(.+?)$");
```

If the query is successfully created, its ID will be provided

``` js
consulClient.createPreparedQuery(def, (res) => {
  if (res.succeeded()) {
    let queryId = res.result();
    console.log("Query created: " + queryId);
  } else {
    res.cause().printStackTrace();
  }
});
```

The prepared query can be executed by its id

``` js
consulClient.executePreparedQuery(id, (res) => {
  if (res.succeeded()) {
    let response = res.result();
    console.log("Found " + response.nodes.length + " nodes");
  } else {
    res.cause().printStackTrace();
  }
});
```

or by query string that must match template regexp

``` js
consulClient.executePreparedQuery("find_1_2", (res) => {
  // matches template regexp "^find_(.+?)_(.+?)$"
  if (res.succeeded()) {
    let response = res.result();
    console.log("Found " + response.nodes.length + " nodes");
  } else {
    res.cause().printStackTrace();
  }
});
```

Finally, `ConsulClient` allows you to modify, get or delete prepared
queries

``` js
consulClient.deletePreparedQuery(query, (res) => {
  if (res.succeeded()) {
    console.log("Query deleted");
  } else {
    res.cause().printStackTrace();
  }
});
```

# Watches

Watches are a way of specifying a view of data (e.g. list of nodes, KV
pairs, health checks) which is monitored for updates. When an update is
detected, an `Handler` with `WatchResult` is invoked. As an example, you
could watch the status of health checks and notify when a check is
critical.

``` js
import { Watch } from "@vertx/consul"
Watch.key("foo/bar", vertx).setHandler((res) => {
  if (res.succeeded()) {
    console.log("value: " + res.nextResult().value);
  } else {
    res.cause().printStackTrace();
  }
}).start();
```
