This component provides an infrastructure to publish and discover
various resources, such as service proxies, HTTP endpoints, data
sources…​ These resources are called `services`. A `service` is a
discoverable functionality. It can be qualified by its type, metadata,
and location. So a `service` can be a database, a service proxy, a HTTP
endpoint and any other resource you can imagine as soon as you can
describe it, discover it and interact with it. It does not have to be a
vert.x entity, but can be anything. Each service is described by a
`Record`.

The service discovery implements the interactions defined in
service-oriented computing. And to some extent, also provides the
dynamic service-oriented computing interactions. So, applications can
react to arrival and departure of services.

A service provider can:

  - publish a service record

  - un-publish a published record

  - update the status of a published service (down, out of service…​)

A service consumer can:

  - lookup services

  - bind to a selected service (it gets a `ServiceReference`) and use it

  - release the service once the consumer is done with it

  - listen for arrival, departure and modification of services.

Consumer would 1) lookup a service record matching their need, 2)
retrieve the `ServiceReference` that give access to the service, 3) get
a service object to access the service, 4) release the service object
once done.

The process can be simplified using *service type* where you can
directly retrieve the service object if you know from which type it is
(JDBC client, Http client…​).

As stated above, the central piece of information shared by the
providers and consumers are `records`.

Providers and consumers must create their own `ServiceDiscovery`
instance. These instances are collaborating in the background
(distributed structure) to keep the set of services in sync.

The service discovery supports bridges to import and export services
from / to other discovery technologies.

# Using the service discovery

To use the Vert.x service discovery, add the following dependency to the
*dependencies* section of your build descriptor:

  - Maven (in your `pom.xml`):

<!-- end list -->

``` xml
<dependency>
<groupId>io.vertx</groupId>
<artifactId>vertx-service-discovery</artifactId>
<version>${maven.version}</version>
</dependency>
```

  - Gradle (in your `build.gradle` file):

<!-- end list -->

``` groovy
compile 'io.vertx:vertx-service-discovery:${maven.version}'
```

# Overall concepts

The discovery mechanism is based on a few concepts explained in this
section.

## Service records

A service `Record` is an object that describes a service published by a
service provider. It contains a name, some metadata, a location object
(describing where is the service). This record is the only object shared
by the provider (having published it) and the consumer (retrieve it when
doing a lookup).

The metadata and even the location format depend on the `service type`
(see below).

A record is published when the provider is ready to be used, and
withdrawn when the service provider is stopping.

## Service Provider and publisher

A service provider is an entity providing a *service*. The publisher is
responsible for publishing a record describing the provider. It may be a
single entity (a provider publishing itself) or a different entity.

## Service Consumer

Service consumers search for services in the service discovery. Each
lookup retrieves `0..n` `Record`. From these records, a consumer can
retrieve a `ServiceReference`, representing the binding between the
consumer and the provider. This reference allows the consumer to
retrieve the *service object* (to use the service), and release the
service.

It is important to release service references to cleanup the objects and
update the service usages.

## Service object

The service object is the object that gives access to a service. It can
come in various forms, such as a proxy, a client, and may even be
non-existent for some service types. The nature of the service object
depends on the service type.

Notice that because of the polyglot nature of Vert.x, the service object
can differ if you retrieve it from Java, Groovy or another language.

## Service types

Services are just resources, and there are a lot of different kinds of
services. They can be functional services, databases, REST APIs, and so
on. The Vert.x service discovery has the concept of service types to
handle this heterogeneity. Each type defines:

  - how the service is located (URI, event bus address, IP / DNS…​) -
    *location*

  - the nature of the service object (service proxy, HTTP client,
    message consumer…​) - *client*

Some service types are implemented and provided by the service discovery
component, but you can add your own.

## Service events

Every time a service provider is published or withdrawn, an event is
fired on the event bus. This event contains the record that has been
modified.

In addition, in order to track who is using who, every time a reference
is retrieved with `getReference` or released with `release`, events are
emitted on the event bus to track the service usages.

More details on these events below.

## Backend

The service discovery uses a Vert.x distributed data structure to store
the records. So, all members of the cluster have access to all the
records. This is the default backend implementation. You can implement
your own by implementing the `ServiceDiscoveryBackend` SPI. For
instance, we provide an implementation based on Redis.

Notice that the discovery does not require Vert.x clustering. In
single-node mode, the structure is local. It can be populated with
``ServiceImporter`s. Since 3.5.0, you can use a local
structure even in clustered mode by setting the system property
`vertx-service-discovery-backend-local`` to `true` (or the environment
variable `VERTX-SERVICE-DISCOVERY-BACKEND-LOCAL` to `true`).

# Creating a service discovery instance

Publishers and consumers must create their own `ServiceDiscovery`
instance to use the discovery infrastructure:

``` js
import { ServiceDiscovery } from "@vertx/service-discovery"
// Use default configuration
let discovery = ServiceDiscovery.create(vertx);

// Customize the configuration
discovery = ServiceDiscovery.create(vertx, new ServiceDiscoveryOptions()
  .setAnnounceAddress("service-announce")
  .setName("my-name"));

// Do something...

discovery.close();
```

By default, the announce address (the event bus address on which service
events are sent is: `vertx.discovery
.announce`. You can also configure a name used for the service usage
(see section about service usage).

When you don’t need the service discovery object anymore, don’t forget
to close it. It closes the different discovery importers and exporters
you have configured and releases the service references.

You should avoid sharing the service discovery instance, so service
usage would represent the right "usages".

# Publishing services

Once you have a service discovery instance, you can publish services.
The process is the following:

1.  create a record for a specific service provider

2.  publish this record

3.  keep the published record that is used to un-publish a service or
    modify it.

To create records, you can either use the `Record` class, or use
convenient methods from the service types.

``` js
import { HttpEndpoint } from "@vertx/service-discovery"
// Manual record creation
let record = new Record()
  .setType("eventbus-service-proxy")
  .setLocation({
    "endpoint" : "the-service-address"
  })
  .setName("my-service")
  .setMetadata({
    "some-label" : "some-value"
  });

discovery.publish(record, (ar) => {
  if (ar.succeeded()) {
    // publication succeeded
    let publishedRecord = ar.result();
  } else {
    // publication failed
  }
});

// Record creation from a type
record = HttpEndpoint.createRecord("some-rest-api", "localhost", 8080, "/api");
discovery.publish(record, (ar) => {
  if (ar.succeeded()) {
    // publication succeeded
    let publishedRecord = ar.result();
  } else {
    // publication failed
  }
});
```

It is important to keep a reference on the returned records, as this
record has been extended by a `registration id`.

# Withdrawing services

To withdraw (un-publish) a record, use:

``` js
discovery.unpublish(record.registration, (ar) => {
  if (ar.succeeded()) {
    // Ok
  } else {
    // cannot un-publish the service, may have already been removed, or the record is not published
  }
});
```

# Looking for services

*This section explains the low-level process to retrieve services, each
service type provide convenient method to aggregates the different
steps.*

On the consumer side, the first thing to do is to lookup for records.
You can search for a single record or all the matching ones. In the
first case, the first matching record is returned.

Consumer can pass a filter to select the service. There are two ways to
describe the filter:

1.  A function taking a `Record` as parameter and returning a boolean
    (it’s a predicate)

2.  This filter is a JSON object. Each entry of the given filter is
    checked against the record. All entries must exactly match the
    record. The entry can use the special `*` value to denote a
    requirement on the key, but not on the value.

Let’s see an example of a JSON filter:

    { "name" = "a" } => matches records with name set to "a"
    { "color" = "*" } => matches records with "color" set
    { "color" = "red" } => only matches records with "color" set to "red"
    { "color" = "red", "name" = "a"} => only matches records with name set to "a", and color set to "red"

If the JSON filter is not set (`null` or empty), it accepts all records.
When using functions, to accept all records, you must return *true*
regardless the record.

Here are some examples:

``` js
// Get any record
discovery.getRecord((r) => {
  true;
}, (ar) => {
  if (ar.succeeded()) {
    if ((ar.result() !== null && ar.result() !== undefined)) {
      // we have a record
    } else {
      // the lookup succeeded, but no matching service
    }
  } else {
    // lookup failed
  }
});

discovery.getRecord(null, (ar) => {
  if (ar.succeeded()) {
    if ((ar.result() !== null && ar.result() !== undefined)) {
      // we have a record
    } else {
      // the lookup succeeded, but no matching service
    }
  } else {
    // lookup failed
  }
});


// Get a record by name
discovery.getRecord((r) => {
  r.name == "some-name";
}, (ar) => {
  if (ar.succeeded()) {
    if ((ar.result() !== null && ar.result() !== undefined)) {
      // we have a record
    } else {
      // the lookup succeeded, but no matching service
    }
  } else {
    // lookup failed
  }
});

discovery.getRecord({
  "name" : "some-service"
}, (ar) => {
  if (ar.succeeded()) {
    if ((ar.result() !== null && ar.result() !== undefined)) {
      // we have a record
    } else {
      // the lookup succeeded, but no matching service
    }
  } else {
    // lookup failed
  }
});

// Get all records matching the filter
discovery.getRecords((r) => {
  "some-value" == r.metadata.some-label;
}, (ar) => {
  if (ar.succeeded()) {
    let results = ar.result();
    // If the list is not empty, we have matching record
    // Else, the lookup succeeded, but no matching service
  } else {
    // lookup failed
  }
});


discovery.getRecords({
  "some-label" : "some-value"
}, (ar) => {
  if (ar.succeeded()) {
    let results = ar.result();
    // If the list is not empty, we have matching record
    // Else, the lookup succeeded, but no matching service
  } else {
    // lookup failed
  }
});
```

You can retrieve a single record or all matching records with
`getRecords`. By default, record lookup does include only records with a
`status` set to `UP`. This can be overridden:

  - when using JSON filter, just set `status` to the value you want (or
    `*` to accept all status)

  - when using function, set the `includeOutOfService` parameter to
    `true` in `getRecords` .

# Retrieving a service reference

Once you have chosen the `Record`, you can retrieve a `ServiceReference`
and then the service object:

``` js
import { HttpClient } from "@vertx/core"
import { MessageConsumer } from "@vertx/core"
let reference1 = discovery.getReference(record1);
let reference2 = discovery.getReference(record2);

// Then, gets the service object, the returned type depends on the service type:
// For http endpoint:
let client = reference1.getAs(HttpClient.class);
// For message source
let consumer = reference2.getAs(MessageConsumer.class);

// When done with the service
reference1.release();
reference2.release();
```

Don’t forget to release the reference once done.

The service reference represents a binding with the service provider.

When retrieving a service reference you can pass a `JsonObject` used to
configure the service object. It can contain various data about the
service object. Some service types do not need additional configuration,
some require configuration (as data sources):

``` js
import { JDBCClient } from "@vertx/jdbc-client"
let reference = discovery.getReferenceWithConfiguration(record, conf);

// Then, gets the service object, the returned type depends on the service type:
// For http endpoint:
let client = reference.getAs(JDBCClient.class);

// Do something with the client...

// When done with the service
reference.release();
```

In the previous examples, the code uses `getAs`. The parameter is the
type of object you expect to get. If you are using Java, you can use
`get`. However in the other language you must pass the expected type.

# Types of services

A said above, the service discovery has the service type concept to
manage the heterogeneity of the different services.

These types are provided by default:

  - `HttpEndpoint` - for REST API’s, the service object is a
    `HttpClient` configured on the host and port (the location is the
    url).

  - `EventBusService` - for service proxies, the service object is a
    proxy. Its type is the proxies interface (the location is the
    address).

  - `MessageSource` - for message sources (publisher), the service
    object is a `MessageConsumer` (the location is the address).

  - `JDBCDataSource` - for JDBC data sources, the service object is a
    `JDBCClient` (the configuration of the client is computed from the
    location, metadata and consumer configuration).

  - `RedisDataSource` - for Redis data sources, the service object is a
    `RedisClient` (the configuration of the client is computed from the
    location, metadata and consumer configuration).

  - `MongoDataSource` - for Mongo data sources, the service object is a
    `MongoClient` (the configuration of the client is computed from the
    location, metadata and consumer configuration).

This section gives details about service types in general and describes
how to use the default service types.

## Services with no type

Some records may have no type (`ServiceType.UNKNOWN`). It is not
possible to retrieve a reference for these records, but you can build
the connection details from the `location` and `metadata` of the
`Record`.

Using these services does not fire service usage events.

## HTTP endpoints

A HTTP endpoint represents a REST API or a service accessible using HTTP
requests. The HTTP endpoint service objects are `HttpClient` configured
with the host, port and ssl.

### Publishing a HTTP endpoint

To publish a HTTP endpoint, you need a `Record`. You can create the
record using `HttpEndpoint.createRecord`.

The next snippet illustrates hot to create a `Record` from
`HttpEndpoint`:

``` js
import { HttpEndpoint } from "@vertx/service-discovery"
let record1 = HttpEndpoint.createRecord("some-http-service", "localhost", 8433, "/api");

discovery.publish(record1, (ar) => {
  // ...
});

let record2 = HttpEndpoint.createRecord("some-other-name", true, "localhost", 8433, "/api", {
  "some-metadata" : "some value"
});
```

When you run your service in a container or on the cloud, it may not
know its public IP and public port, so the publication must be done by
another entity having this info. Generally it’s a bridge.

### Consuming a HTTP endpoint

Once a HTTP endpoint is published, a consumer can retrieve it. The
service object is a `HttpClient` with a port and host configured:

``` js
import { HttpClient } from "@vertx/core"
// Get the record
discovery.getRecord({
  "name" : "some-http-service"
}, (ar) => {
  if (ar.succeeded() && (ar.result() !== null && ar.result() !== undefined)) {
    // Retrieve the service reference
    let reference = discovery.getReference(ar.result());
    // Retrieve the service object
    let client = reference.getAs(HttpClient.class);

    // You need to path the complete path
    client.getNow("/api/persons", (response) => {

      // ...

      // Dont' forget to release the service
      reference.release();

    });
  }
});
```

You can also use the `HttpEndpoint.getClient` method to combine lookup
and service retrieval in one call:

``` js
import { ServiceDiscovery } from "@vertx/service-discovery"
import { HttpEndpoint } from "@vertx/service-discovery"
HttpEndpoint.getClient(discovery, {
  "name" : "some-http-service"
}, (ar) => {
  if (ar.succeeded()) {
    let client = ar.result();

    // You need to path the complete path
    client.getNow("/api/persons", (response) => {

      // ...

      // Dont' forget to release the service
      ServiceDiscovery.releaseServiceObject(discovery, client);

    });
  }
});
```

In this second version, the service object is released using
`ServiceDiscovery.releaseServiceObject`, so you don’t need to keep the
service reference.

Since Vert.x 3.4.0, another client has been provided. This higher-level
client, named `WebClient` tends to be easier to use. You can retrieve a
`WebClient` instances using:

``` js
import { WebClient } from "@vertx/web-client"
// Get the record
discovery.getRecord({
  "name" : "some-http-service"
}, (ar) => {
  if (ar.succeeded() && (ar.result() !== null && ar.result() !== undefined)) {
    // Retrieve the service reference
    let reference = discovery.getReference(ar.result());
    // Retrieve the service object
    let client = reference.getAs(WebClient.class);

    // You need to path the complete path
    client.get("/api/persons").send((response) => {

      // ...

      // Dont' forget to release the service
      reference.release();

    });
  }
});
```

And, if you prefer the approach using the service type:

``` js
import { ServiceDiscovery } from "@vertx/service-discovery"
import { HttpEndpoint } from "@vertx/service-discovery"
HttpEndpoint.getWebClient(discovery, {
  "name" : "some-http-service"
}, (ar) => {
  if (ar.succeeded()) {
    let client = ar.result();

    // You need to path the complete path
    client.get("/api/persons").send((response) => {

      // ...

      // Dont' forget to release the service
      ServiceDiscovery.releaseServiceObject(discovery, client);

    });
  }
});
```

## Event bus services

Event bus services are service proxies. They implement async-RPC
services on top of the event bus. When retrieving a service object from
an event bus service, you get a service proxy of the right type. You can
access helper methods from `EventBusService`.

Notice that service proxies (service implementations and service
interfaces) are developed in Java.

### Publishing an event bus service

To publish an event bus service, you need to create a `Record`:

``` js
import { EventBusService } from "@vertx/service-discovery"
let record = EventBusService.createRecord("some-eventbus-service", "address", "examples.MyService", {
  "some-metadata" : "some value"
});

discovery.publish(record, (ar) => {
  // ...
});
```

### Consuming an event bus service

To consume an event bus service you can either retrieve the record and
then get the reference, or use the `EventBusService` interface that
combines the two operations in one call.

However, as the service is searched by (Java) interface, you need to
specify the type of client you expect.

``` js
var ServiceDiscovery = require("vertx-service-discovery-js/service_discovery");
var EventBusService = require("vertx-service-discovery-js/event_bus_service");
var MyService = require("org-acme-js/MyService");
EventBusService.getServiceProxyWithJsonFilter(discovery,
{ "service.interface" : "org.acme.MyService"},
MyService,
function (ar, ar_err) {
if (ar_err == null) {
var service = ar;

// Dont' forget to release the service
ServiceDiscovery.releaseServiceObject(discovery, service);
}
});
```

## Message source

A message source is a component sending messages on the event bus on a
specific address. Message source clients are `MessageConsumer`.

The *location* or a message source service is the event bus address on
which messages are sent.

### Publishing a message source

As for the other service types, publishing a message source is a 2-step
process:

1.  create a record, using `MessageSource`

2.  publish the record

<!-- end list -->

``` js
import { MessageSource } from "@vertx/service-discovery"
let record = MessageSource.createRecord("some-message-source-service", "some-address");

discovery.publish(record, (ar) => {
  // ...
});

record = MessageSource.createRecord("some-other-message-source-service", "some-address", "examples.MyData");
```

In the second record, the type of payload is also indicated. This
information is optional.

### Consuming a message source

On the consumer side, you can retrieve the record and the reference, or
use the `MessageSource` class to retrieve the service is one call.

With the first approach, the code is the following:

``` js
import { MessageConsumer } from "@vertx/core"
// Get the record
discovery.getRecord({
  "name" : "some-message-source-service"
}, (ar) => {
  if (ar.succeeded() && (ar.result() !== null && ar.result() !== undefined)) {
    // Retrieve the service reference
    let reference = discovery.getReference(ar.result());
    // Retrieve the service object
    let consumer = reference.getAs(MessageConsumer.class);

    // Attach a message handler on it
    consumer.handler((message) => {
      // message handler
      let payload = message.body();
    });
  }
});
```

When, using `MessageSource`, it becomes:

``` js
import { MessageSource } from "@vertx/service-discovery"
MessageSource.getConsumer(discovery, {
  "name" : "some-message-source-service"
}, (ar) => {
  if (ar.succeeded()) {
    let consumer = ar.result();

    // Attach a message handler on it
    consumer.handler((message) => {
      // message handler
      let payload = message.body();
    });
    // ...
  }
});
```

## JDBC Data source

Data sources represents databases or data stores. JDBC data sources are
a specialization for databases accessible using a JDBC driver. The
client of a JDBC data source service is a `JDBCClient`.

### Publishing a JDBC service

As for the other service types, publishing a JDBC data source is a
2-step process:

1.  create a record, using `JDBCDataSource`

2.  publish the record

<!-- end list -->

``` js
import { JDBCDataSource } from "@vertx/service-discovery"
let record = JDBCDataSource.createRecord("some-data-source-service", {
  "url" : "some jdbc url"
}, {
  "some-metadata" : "some-value"
});

discovery.publish(record, (ar) => {
  // ...
});
```

As JDBC data sources can represent a high variety of databases, and
their access is often different, the record is rather unstructured. The
`location` is a simple JSON object that should provide the fields to
access the data source (JDBC url, username…​). The set of fields may
depend on the database but also on the connection pool used in front.

### Consuming a JDBC service

As stated in the previous section, how to access a data source depends
on the data source itself. To build the `JDBCClient`, you can merge
configuration: the record location, the metadata and a json object
provided by the consumer:

``` js
import { JDBCClient } from "@vertx/jdbc-client"
// Get the record
discovery.getRecord({
  "name" : "some-data-source-service"
}, (ar) => {
  if (ar.succeeded() && (ar.result() !== null && ar.result() !== undefined)) {
    // Retrieve the service reference
    let reference = discovery.getReferenceWithConfiguration(ar.result(), {
      "username" : "clement",
      "password" : "*****"
    });

    // Retrieve the service object
    let client = reference.getAs(JDBCClient.class);

    // ...

    // when done
    reference.release();
  }
});
```

You can also use the `JDBCClient` class to the lookup and retrieval in
one call:

``` js
import { ServiceDiscovery } from "@vertx/service-discovery"
import { JDBCDataSource } from "@vertx/service-discovery"
JDBCDataSource.getJDBCClient(discovery, {
  "name" : "some-data-source-service"
}, {
  "username" : "clement",
  "password" : "*****"
}, (ar) => {
  if (ar.succeeded()) {
    let client = ar.result();

    // ...

    // Dont' forget to release the service
    ServiceDiscovery.releaseServiceObject(discovery, client);

  }
});
```

## Redis Data source

Redis data sources are a specialization for Redis persistence databases.
The client of a Redis data source service is a `RedisClient`.

### Publishing a Redis service

Publishing a Redis data source is a 2-step process:

1.  create a record, using `RedisDataSource`

2.  publish the record

<!-- end list -->

``` js
import { RedisDataSource } from "@vertx/service-discovery"
let record = RedisDataSource.createRecord("some-redis-data-source-service", {
  "url" : "localhost"
}, {
  "some-metadata" : "some-value"
});

discovery.publish(record, (ar) => {
  // ...
});
```

The `location` is a simple JSON object that should provide the fields to
access the Redis data source (url, port…​).

### Consuming a Redis service

As stated in the previous section, how to access a data source depends
on the data source itself. To build the `RedisClient`, you can merge
configuration: the record location, the metadata and a json object
provided by the consumer:

``` js
import { RedisClient } from "@vertx/redis-client"
// Get the record
discovery.getRecord({
  "name" : "some-redis-data-source-service"
}, (ar) => {
  if (ar.succeeded() && (ar.result() !== null && ar.result() !== undefined)) {
    // Retrieve the service reference
    let reference = discovery.getReference(ar.result());

    // Retrieve the service instance
    let client = reference.getAs(RedisClient.class);

    // ...

    // when done
    reference.release();
  }
});
```

You can also use the `RedisDataSource` class to the lookup and retrieval
in one call:

``` js
import { ServiceDiscovery } from "@vertx/service-discovery"
import { RedisDataSource } from "@vertx/service-discovery"
RedisDataSource.getRedisClient(discovery, {
  "name" : "some-redis-data-source-service"
}, (ar) => {
  if (ar.succeeded()) {
    let client = ar.result();

    // ...

    // Dont' forget to release the service
    ServiceDiscovery.releaseServiceObject(discovery, client);

  }
});
```

## Mongo Data source

Mongo data sources are a specialization for MongoDB databases. The
client of a Mongo data source service is a `MongoClient`.

### Publishing a Mongo service

Publishing a Mongo data source is a 2-step process:

1.  create a record, using `MongoDataSource`

2.  publish the record

<!-- end list -->

``` js
import { MongoDataSource } from "@vertx/service-discovery"
let record = MongoDataSource.createRecord("some-data-source-service", {
  "connection_string" : "some mongo connection"
}, {
  "some-metadata" : "some-value"
});

discovery.publish(record, (ar) => {
  // ...
});
```

The `location` is a simple JSON object that should provide the fields to
access the Redis data source (url, port…​).

### Consuming a Mongo service

As stated in the previous section, how to access a data source depends
on the data source itself. To build the `MongoClient`, you can merge
configuration: the record location, the metadata and a json object
provided by the consumer:

``` js
// Get the record
discovery.getRecord({
  "name" : "some-data-source-service"
}, (ar) => {
  if (ar.succeeded() && (ar.result() !== null && ar.result() !== undefined)) {
    // Retrieve the service reference
    let reference = discovery.getReferenceWithConfiguration(ar.result(), {
      "username" : "clement",
      "password" : "*****"
    });

    // Retrieve the service object
    let client = reference.get();

    // ...

    // when done
    reference.release();
  }
});
```

You can also use the `MongoDataSource` class to the lookup and retrieval
in one call:

``` js
import { ServiceDiscovery } from "@vertx/service-discovery"
import { MongoDataSource } from "@vertx/service-discovery"
MongoDataSource.getMongoClient(discovery, {
  "name" : "some-data-source-service"
}, {
  "username" : "clement",
  "password" : "*****"
}, (ar) => {
  if (ar.succeeded()) {
    let client = ar.result();

    // ...

    // Dont' forget to release the service
    ServiceDiscovery.releaseServiceObject(discovery, client);

  }
});
```

# Listening for service arrivals and departures

Every time a provider is published or removed, an event is published on
the *vertx.discovery.announce* address. This address is configurable
from the `ServiceDiscoveryOptions`.

The received record has a `status` field indicating the new state of the
record:

  - `UP` : the service is available, you can start using it

  - `DOWN` : the service is not available anymore, you should not use it
    anymore

  - `OUT_OF_SERVICE` : the service is not running, you should not use it
    anymore, but it may come back later.

# Listening for service usage

Every time a service reference is retrieved (`bind`) or released
(`release`), an event is published on the *vertx .discovery.usage*
address. This address is configurable from the
`ServiceDiscoveryOptions`.

It lets you listen for service usage and map the service bindings.

The received message is a `JsonObject` containing:

  - the record in the `record` field

  - the type of event in the `type` field. It’s either `bind` or
    `release`

  - the id of the service discovery (either its name or the node id) in
    the `id` field

This `id` is configurable from the `ServiceDiscoveryOptions`. By default
it’s "localhost" on single node configuration and the id of the node in
clustered mode.

You can disable the service usage support by setting the usage address
to `null` with `setUsageAddress`.

# Service discovery bridges

Bridges let you import and export services from / to other discovery
mechanism such as Docker, Kubernetes, Consul…​ Each bridge decides how
the services are imported and exported. It does not have to be
bi-directional.

You can provide your own bridge by implementing the `ServiceImporter`
interface and register it using `registerServiceImporter`.

The second parameter can provide an optional configuration for the
bridge.

When the bridge is registered the `start` method is called. It lets you
configure the bridge. When the bridge is configured, ready and has
imported / exported the initial services, it must complete the given
`Future`. If the bridge starts method is blocking, it must use an
`executeBlocking` construct, and complete the given future object.

When the service discovery is stopped, the bridge is stopped. The
`close` method is called that provides the opportunity to cleanup
resources, removed imported / exported services…​ This method must
complete the given `Future` to notify the caller of the completion.

Notice than in a cluster, only one member needs to register the bridge
as the records are accessible by all members.

# Additional bridges

In addition of the bridges supported by this library, Vert.x Service
Discovery provides additional bridges you can use in your application.

Unresolved directive in index.adoc - include::consul-bridge.adoc\[\]

Unresolved directive in index.adoc - include::kubernetes-bridge.adoc\[\]

Unresolved directive in index.adoc - include::zookeeper-bridge.adoc\[\]

Unresolved directive in index.adoc -
include::docker-links-bridge.adoc\[\]

# Additional backends

In addition of the backend supported by this library, Vert.x Service
Discovery provides additional backends you can use in your application.

Unresolved directive in index.adoc - include::redis-backend.adoc\[\]
