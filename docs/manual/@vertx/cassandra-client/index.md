A Vert.x client allowing applications to interact with an [Apache
Cassandra](http://cassandra.apache.org/) service.

> **Warning**
> 
> This module has *Tech Preview* status, this means the API can change
> between versions.

# Getting started

To use this module, add the following to the *dependencies* section of
your Maven POM file:

``` xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-cassandra-client</artifactId>
 <version>${maven.version}</version>
</dependency>
```

Or, if you use Gradle:

``` groovy
compile 'io.vertx:vertx-cassandra-client:${maven.version}'
```

> **Warning**
> 
> The Cassandra client is not compatible with the Vert.x Dropwizard
> Metrics library. Both are using a different major version of the
> Dropwizard Metrics library and the Datastax Java driver [won’t
> upgrade](https://github.com/datastax/java-driver/pull/943) to the most
> recent version due to the drop of Java 7. The next major version (4.x)
> of the driver will use a more recent Dropwizard Metrics library
> version.

# Creating a client

## Client options

Cassandra is a distributed system, and it can have many nodes. To
connect to Cassandra you need to specify the addresses of some cluster
nodes when creating a `CassandraClientOptions` object:

``` js
import { CassandraClient } from "@vertx/cassandra"
let options = new CassandraClientOptions()
  .setContactPoints(["node1.address", "node2.address", "node3.address"]);
let client = CassandraClient.create(vertx, options);
```

By default, the Cassandra client for Vert.x connects to the local
machine’s port `9042` and is not tied to any specific keyspace. But you
can set either or both of these options:

``` js
import { CassandraClient } from "@vertx/cassandra"
let options = new CassandraClientOptions()
  .setPort(9142)
  .setKeyspace("my_keyspace");
let client = CassandraClient.create(vertx, options);
```

> **Tip**
> 
> For fine tuning purposes, `CassandraClientOptions` exposes a
> `com.datastax.driver.core.Cluster.Builder` instance.

## Shared clients

If you deploy multiple instances of your verticle or have different
verticles interacting with the same database, it is recommended to
create a shared client:

``` js
import { CassandraClient } from "@vertx/cassandra"
let options = new CassandraClientOptions()
  .setContactPoints(["node1.address", "node2.address", "node3.address"])
  .setKeyspace("my_keyspace");
let client = CassandraClient.createShared(vertx, "sharedClientName", options);
```

Shared clients with the same name will use a single underlying
`com.datastax.driver.core.Session`.

## Client lifecycle

After the client is created, it is not connected until the first query
is executed.

> **Tip**
> 
> A shared client can be connected after creation if another client with
> the same name has already executed a query.

Clients created inside a verticle are automatically stopped when the
verticle is undeployed. In other words, you do not need to invoke
`close` in the verticle `stop` method.

In all other cases, you must manually close the client.

> **Note**
> 
> When a shared client is closed, the driver dession is not closed if
> other clients with the same name are still running.

# Using the API

The client API is represented by `CassandraClient`.

## Querying

You can get query results using three different ways.

### Streaming

The streaming API is most appropriate when you need to consume results
iteratively, e.g you want to process each item. This is very efficient
specially for large amount of rows.

In order to give you some inspiration and ideas on how you can use the
API, we’d like to you to consider this example:

``` js
cassandraClient.queryStream("SELECT my_string_col FROM my_keyspace.my_table where my_key = 'my_value'", (queryStream) => {
  if (queryStream.succeeded()) {
    let stream = queryStream.result();

    // resume stream when queue is ready to accept buffers again
    response.drainHandler((v) => {
      stream.resume();
    });

    stream.handler((row) => {
      let value = row.getString("my_string_col");
      response.write(value);

      // pause row stream when we buffer queue is full
      if (response.writeQueueFull()) {
        stream.pause();
      }
    });

    // end request when we reached end of the stream
    stream.endHandler((end) => {
      response.end();
    });

  } else {
    queryStream.cause().printStackTrace();
    // response with internal server error if we are not able to execute given query
    response.setStatusCode(500).end("Unable to execute the query");
  }
});
```

In the example, we are executing a query, and stream results via HTTP.

### Bulk fetching

This API should be used when you need to process all the rows at the
same time.

``` js
cassandraClient.executeWithFullFetch("SELECT * FROM my_keyspace.my_table where my_key = 'my_value'", (executeWithFullFetch) => {
  if (executeWithFullFetch.succeeded()) {
    let rows = executeWithFullFetch.result();
    rows.forEach(row => {
      // handle each row here
    });
  } else {
    console.log("Unable to execute the query");
    executeWithFullFetch.cause().printStackTrace();
  }
});
```

> **Caution**
> 
> Use bulk fetching only if you can afford to load the full result set
> in memory.

## Collector queries

You can use Java collectors with the query API:

``` js
Code not translatable
```

### Low level fetch

This API provides greater control over loading at the expense of being a
bit lower-level than the streaming and bulk fetching APIs.

``` js
cassandraClient.execute("SELECT * FROM my_keyspace.my_table where my_key = 'my_value'", (execute) => {
  if (execute.succeeded()) {
    let resultSet = execute.result();

    resultSet.one((one) => {
      if (one.succeeded()) {
        let row = one.result();
        console.log("One row successfully fetched");
      } else {
        console.log("Unable to fetch a row");
        one.cause().printStackTrace();
      }
    });

    resultSet.fetchMoreResults((fetchMoreResults) => {
      if (fetchMoreResults.succeeded()) {
        let availableWithoutFetching = resultSet.getAvailableWithoutFetching();
        console.log("Now we have " + availableWithoutFetching + " rows fetched, but not consumed!");
        if (resultSet.isFullyFetched()) {
          console.log("The result is fully fetched, we don't need to call this method for one more time!");
        } else {
          console.log("The result still does not fully fetched");
        }
      } else {
        console.log("Unable to fetch more results");
        fetchMoreResults.cause().printStackTrace();
      }
    });

  } else {
    console.log("Unable to execute the query");
    execute.cause().printStackTrace();
  }
});
```

## Prepared queries

For security and efficiency reasons, it is a good idea to use prepared
statements for all the queries you are using more than once.

You can prepare a query:

``` js
cassandraClient.prepare("SELECT * FROM my_keyspace.my_table where my_key = ? ", (preparedStatementResult) => {
  if (preparedStatementResult.succeeded()) {
    console.log("The query has successfully been prepared");
    let preparedStatement = preparedStatementResult.result();
    // now you can use this PreparedStatement object for the next queries
  } else {
    console.log("Unable to prepare the query");
    preparedStatementResult.cause().printStackTrace();
  }
});
```

And then use the
[`PreparedStatement`](https://docs.datastax.com/en/drivers/java/${datastax.driver.minor.version}/com/datastax/driver/core/PreparedStatement.html)
for all the next queries:

``` js
// You can execute you prepared statement using any way to execute queries.

// Low level fetch API
cassandraClient.execute(preparedStatement.bind("my_value"), (done) => {
  let results = done.result();
  // handle results here
});

// Bulk fetching API
cassandraClient.executeWithFullFetch(preparedStatement.bind("my_value"), (done) => {
  let results = done.result();
  // handle results here
});

// Streaming API
cassandraClient.queryStream(preparedStatement.bind("my_value"), (done) => {
  let results = done.result();
  // handle results here
});
```

## Batching

In case you’d like to execute several queries at once, you can use
[`BatchStatement`](https://docs.datastax.com/en/drivers/java/${datastax.driver.minor.version}/com/datastax/driver/core/BatchStatement.html)
for that:

``` js
let batchStatement = new (Java.type("com.datastax.driver.core.BatchStatement"))().add(new (Java.type("com.datastax.driver.core.SimpleStatement"))("INSERT INTO NAMES (name) VALUES ('Pavel')")).add(new (Java.type("com.datastax.driver.core.SimpleStatement"))("INSERT INTO NAMES (name) VALUES ('Thomas')")).add(new (Java.type("com.datastax.driver.core.SimpleStatement"))("INSERT INTO NAMES (name) VALUES ('Julien')"));

cassandraClient.execute(batchStatement, (result) => {
  if (result.succeeded()) {
    console.log("The given batch executed successfully");
  } else {
    console.log("Unable to execute the batch");
    result.cause().printStackTrace();
  }
});
```

## Object Mapper

You can use the object `Mapper` to map between domain classes and
queries.

First, add the following to the *dependencies* section of your Maven POM
file:

``` xml
<dependency>
 <groupId>com.datastax.cassandra</groupId>
 <artifactId>cassandra-driver-mapping</artifactId>
 <version>${datastax.driver.version}</version>
</dependency>
```

Or, if you use Gradle:

``` groovy
compile 'com.datastax.cassandra:cassandra-driver-mapping:${datastax.driver.version}'
```

Consider the following entity:

``` java
@Table(keyspace = "test", name = "names")
public class MappedClass {
 @PartitionKey
 private String name;

 public MappedClass(String name) {
   this.name = name;
 }

 MappedClass() {
   // Required for mapping
 }

 // getters / setters
}
```

Create a mapper for it and you may save, retrieve or delete data from
the corresponding table:

``` js
import { MappingManager } from "@vertx/cassandra"
let mappingManager = MappingManager.create(cassandraClient);
let mapper = mappingManager.mapper(Java.type("examples.CassandraClientExamples.MappedClass").class);

let value = new (Java.type("examples.CassandraClientExamples.MappedClass"))("foo");

mapper.save(value, (handler) => {
  // Entity saved
});

mapper.get(Java.type("java.util.Collections").singletonList("foo"), (handler) => {
  // Entity loaded
});

mapper.delete(Java.type("java.util.Collections").singletonList("foo"), (handler) => {
  // Entity deleted
});
```

> **Tip**
> 
> It is safe to reuse mapping manager and mapper instances for a given
> Cassandra client.
