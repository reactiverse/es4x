A Vert.x client allowing applications to interact with a MongoDB
instance, whether that’s saving, retrieving, searching, or deleting
documents. Mongo is a great match for persisting data in a Vert.x
application as it natively handles JSON (BSON) documents.

**Features**

  - Completely non-blocking

  - Custom codec to support fast serialization to/from Vert.x JSON

  - Supports a majority of the configuration options from the MongoDB
    Java Driver

This client is based on the [MongoDB Async
Driver](http://mongodb.github.io/mongo-java-driver/3.2/driver-async/getting-started).

# Using Vert.x MongoDB Client

To use this project, add the following dependency to the *dependencies*
section of your build descriptor:

  - Maven (in your `pom.xml`):

<!-- end list -->

``` xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-mongo-client</artifactId>
 <version>${maven.version}</version>
</dependency>
```

  - Gradle (in your `build.gradle` file):

<!-- end list -->

``` groovy
compile 'io.vertx:vertx-mongo-client:${maven.version}'
```

# Creating a client

You can create a client in several ways:

## Using the default shared pool

In most cases you will want to share a pool between different client
instances.

E.g. you scale your application by deploying multiple instances of your
verticle and you want each verticle instance to share the same pool so
you don’t end up with multiple pools

The simplest way to do this is as follows:

``` js
import { MongoClient } from "@vertx/mongo-client"
let client = MongoClient.createShared(vertx, config);
```

The first call to `MongoClient.createShared` will actually create the
pool, and the specified config will be used.

Subsequent calls will return a new client instance that uses the same
pool, so the configuration won’t be used.

## Specifying a pool source name

You can create a client specifying a pool source name as follows

``` js
import { MongoClient } from "@vertx/mongo-client"
let client = MongoClient.createShared(vertx, config, "MyPoolName");
```

If different clients are created using the same Vert.x instance and
specifying the same pool name, they will share the same pool.

The first call to `MongoClient.createShared` will actually create the
pool, and the specified config will be used.

Subsequent calls will return a new client instance that uses the same
pool, so the configuration won’t be used.

Use this way of creating if you wish different groups of clients to have
different pools, e.g. they’re interacting with different databases.

## Creating a client with a non shared data pool

In most cases you will want to share a pool between different client
instances. However, it’s possible you want to create a client instance
that doesn’t share its pool with any other client.

In that case you can use `MongoClient.create`.

``` js
import { MongoClient } from "@vertx/mongo-client"
let client = MongoClient.create(vertx, config);
```

This is equivalent to calling `MongoClient.createShared` with a unique
pool name each time.

# Using the API

The client API is represented by `MongoClient`.

## Saving documents

To save a document you use `save`.

If the document has no `\_id` field, it is inserted, otherwise, it is
*upserted*. Upserted means it is inserted if it doesn’t already exist,
otherwise it is updated.

If the document is inserted and has no id, then the id field generated
will be returned to the result handler.

Here’s an example of saving a document and getting the id back

``` js
// Document has no id
let document = {
  "title" : "The Hobbit"
};
mongoClient.save("books", document, (res) => {
  if (res.succeeded()) {
    let id = res.result();
    console.log("Saved book with id " + id);
  } else {
    res.cause().printStackTrace();
  }
});
```

And here’s an example of saving a document which already has an id.

``` js
// Document has an id already
let document = {
  "title" : "The Hobbit",
  "_id" : "123244"
};
mongoClient.save("books", document, (res) => {
  if (res.succeeded()) {
    // ...
  } else {
    res.cause().printStackTrace();
  }
});
```

## Inserting documents

To insert a document you use `insert`.

If the document is inserted and has no id, then the id field generated
will be returned to the result handler.

``` js
// Document has an id already
let document = {
  "title" : "The Hobbit"
};
mongoClient.insert("books", document, (res) => {
  if (res.succeeded()) {
    let id = res.result();
    console.log("Inserted book with id " + id);
  } else {
    res.cause().printStackTrace();
  }
});
```

If a document is inserted with an id, and a document with that id
already exists, the insert will fail:

``` js
// Document has an id already
let document = {
  "title" : "The Hobbit",
  "_id" : "123244"
};
mongoClient.insert("books", document, (res) => {
  if (res.succeeded()) {
    //...
  } else {
    // Will fail if the book with that id already exists.
  }
});
```

## Updating documents

To update a documents you use `updateCollection`.

This updates one or multiple documents in a collection. The json object
that is passed in the `updateCollection` parameter must contain [Update
Operators](http://docs.mongodb.org/manual/reference/operator/update-field/)
and determines how the object is updated.

The json object specified in the query parameter determines which
documents in the collection will be updated.

Here’s an example of updating a document in the books collection:

``` js
// Match any documents with title=The Hobbit
let query = {
  "title" : "The Hobbit"
};
// Set the author field
let update = {
  "$set" : {
    "author" : "J. R. R. Tolkien"
  }
};
mongoClient.updateCollection("books", query, update, (res) => {
  if (res.succeeded()) {
    console.log("Book updated !");
  } else {
    res.cause().printStackTrace();
  }
});
```

To specify if the update should upsert or update multiple documents, use
`updateCollectionWithOptions` and pass in an instance of
`UpdateOptions`.

This has the following fields:

  - `multi`
    set to true to update multiple documents

  - `upsert`
    set to true to insert the document if the query doesn’t match

  - `writeConcern`
    the write concern for this operation

<!-- end list -->

``` js
// Match any documents with title=The Hobbit
let query = {
  "title" : "The Hobbit"
};
// Set the author field
let update = {
  "$set" : {
    "author" : "J. R. R. Tolkien"
  }
};
let options = new UpdateOptions()
  .setMulti(true);
mongoClient.updateCollectionWithOptions("books", query, update, options, (res) => {
  if (res.succeeded()) {
    console.log("Book updated !");
  } else {
    res.cause().printStackTrace();
  }
});
```

## Replacing documents

To replace documents you use `replaceDocuments`.

This is similar to the update operation, however it does not take any
operator. Instead it replaces the entire document with the one provided.

Here’s an example of replacing a document in the books collection

``` js
let query = {
  "title" : "The Hobbit"
};
let replace = {
  "title" : "The Lord of the Rings",
  "author" : "J. R. R. Tolkien"
};
mongoClient.replaceDocuments("books", query, replace, (res) => {
  if (res.succeeded()) {
    console.log("Book replaced !");
  } else {
    res.cause().printStackTrace();
  }
});
```

## Bulk operations

To execute multiple insert, update, replace, or delete operations at
once, use `bulkWrite`.

You can pass a list of `BulkOperations`, with each working similar to
the matching single operation. You can pass as many operations, even of
the same type, as you wish.

To specify if the bulk operation should be executed in order, and with
what write option, use `bulkWriteWithOptions` and pass an instance of
`BulkWriteOptions`. For more explanation what ordered means, see
[Execution of
Operations](https://docs.mongodb.com/manual/reference/method/db.collection.bulkWrite/#execution-of-operations).

## Finding documents

To find documents you use `find`.

The `query` parameter is used to match the documents in the collection.

Here’s a simple example with an empty query that will match all books:

``` js
// empty query = match any
let query = {
};
mongoClient.find("books", query, (res) => {
  if (res.succeeded()) {
    res.result().forEach(json => {
      console.log(JSON.stringify(json));
    });
  } else {
    res.cause().printStackTrace();
  }
});
```

Here’s another example that will match all books by Tolkien:

``` js
// will match all Tolkien books
let query = {
  "author" : "J. R. R. Tolkien"
};
mongoClient.find("books", query, (res) => {
  if (res.succeeded()) {
    res.result().forEach(json => {
      console.log(JSON.stringify(json));
    });
  } else {
    res.cause().printStackTrace();
  }
});
```

The matching documents are returned as a list of json objects in the
result handler.

To specify things like what fields to return, how many results to
return, etc use `findWithOptions` and pass in the an instance of
`FindOptions`.

This has the following fields:

  - `fields`
    The fields to return in the results. Defaults to `null`, meaning all
    fields will be returned

  - `sort`
    The fields to sort by. Defaults to `null`.

  - `limit`
    The limit of the number of results to return. Default to `-1`,
    meaning all results will be returned.

  - `skip`
    The number of documents to skip before returning the results.
    Defaults to `0`.

## Finding documents in batches

When dealing with large data sets, it is not advised to use the `find`
and `findWithOptions` methods. In order to avoid inflating the whole
response into memory, use `findBatch`:

``` js
// will match all Tolkien books
let query = {
  "author" : "J. R. R. Tolkien"
};
mongoClient.findBatch("book", query).exceptionHandler((throwable) => {
  throwable.printStackTrace();
}).endHandler((v) => {
  console.log("End of research");
}).handler((doc) => {
  console.log("Found doc: " + JSON.stringify(doc));
});
```

The matching documents are emitted one by one by the `ReadStream`
handler.

`FindOptions` has an extra parameter `batchSize` which you can use to
set the number of documents to load at once:

``` js
// will match all Tolkien books
let query = {
  "author" : "J. R. R. Tolkien"
};
let options = new FindOptions()
  .setBatchSize(100);
mongoClient.findBatchWithOptions("book", query, options).exceptionHandler((throwable) => {
  throwable.printStackTrace();
}).endHandler((v) => {
  console.log("End of research");
}).handler((doc) => {
  console.log("Found doc: " + JSON.stringify(doc));
});
```

By default, `batchSize` is set to 20.

## Finding a single document

To find a single document you use `findOne`.

This works just like `find` but it returns just the first matching
document.

## Removing documents

To remove documents use `removeDocuments`.

The `query` parameter is used to match the documents in the collection
to determine which ones to remove.

Here’s an example of removing all Tolkien books:

``` js
let query = {
  "author" : "J. R. R. Tolkien"
};
mongoClient.removeDocuments("books", query, (res) => {
  if (res.succeeded()) {
    console.log("Never much liked Tolkien stuff!");
  } else {
    res.cause().printStackTrace();
  }
});
```

## Removing a single document

To remove a single document you use `removeDocument`.

This works just like `removeDocuments` but it removes just the first
matching document.

## Counting documents

To count documents use `count`.

Here’s an example that counts the number of Tolkien books. The number is
passed to the result handler.

``` js
let query = {
  "author" : "J. R. R. Tolkien"
};
mongoClient.count("books", query, (res) => {
  if (res.succeeded()) {
    let num = res.result();
  } else {
    res.cause().printStackTrace();
  }
});
```

## Managing MongoDB collections

All MongoDB documents are stored in collections.

To get a list of all collections you can use `getCollections`

``` js
mongoClient.getCollections((res) => {
  if (res.succeeded()) {
    let collections = res.result();
  } else {
    res.cause().printStackTrace();
  }
});
```

To create a new collection you can use `createCollection`

``` js
mongoClient.createCollection("mynewcollectionr", (res) => {
  if (res.succeeded()) {
    // Created ok!
  } else {
    res.cause().printStackTrace();
  }
});
```

To drop a collection you can use `dropCollection`

> **Note**
>
> Dropping a collection will delete all documents within it\!

``` js
mongoClient.dropCollection("mynewcollectionr", (res) => {
  if (res.succeeded()) {
    // Dropped ok!
  } else {
    res.cause().printStackTrace();
  }
});
```

## Running other MongoDB commands

You can run arbitrary MongoDB commands with `runCommand`.

Commands can be used to run more advanced MongoDB features, such as
using MapReduce. For more information see the mongo docs for supported
[Commands](http://docs.mongodb.org/manual/reference/command).

Here’s an example of running an aggregate command. Note that the command
name must be specified as a parameter and also be contained in the JSON
that represents the command. This is because JSON is not ordered but
BSON is ordered and MongoDB expects the first BSON entry to be the name
of the command. In order for us to know which of the entries in the JSON
is the command name it must be specified as a parameter.

``` js
let command = {
  "aggregate" : "collection_name",
  "pipeline" : [
  ]
};
mongoClient.runCommand("aggregate", command, (res) => {
  if (res.succeeded()) {
    let resArr = res.result().result;
    // etc
  } else {
    res.cause().printStackTrace();
  }
});
```

## MongoDB Extended JSON support

For now, only `date`, `oid` and `binary` types are supported (see
[MongoDB Extended
JSON](http://docs.mongodb.org/manual/reference/mongodb-extended-json)).

Here’s an example of inserting a document with a `date` field:

``` js
let document = {
  "title" : "The Hobbit",
  "publicationDate" : {
    "$date" : "1937-09-21T00:00:00+00:00"
  }
};
mongoService.save("publishedBooks", document, (res) => {
  if (res.succeeded()) {
    let id = res.result();
    mongoService.findOne("publishedBooks", {
      "_id" : id
    }, null, (res2) => {
      if (res2.succeeded()) {
        console.log("To retrieve ISO-8601 date : " + res2.result().publicationDate.$date);
      } else {
        res2.cause().printStackTrace();
      }
    });
  } else {
    res.cause().printStackTrace();
  }
});
```

Here’s an example (in Java) of inserting a document with a binary field
and reading it back

``` js
byte[] binaryObject = new byte[40];
JsonObject document = new JsonObject()
  .put("name", "Alan Turing")
  .put("binaryStuff", new JsonObject().put("$binary", binaryObject));
mongoService.save("smartPeople", document, res -> {
  if (res.succeeded()) {
    String id = res.result();
    mongoService.findOne("smartPeople", new JsonObject().put("_id", id), null, res2 -> {
      if (res2.succeeded()) {
        byte[] reconstitutedBinaryObject = res2.result().getJsonObject("binaryStuff").getBinary("$binary");
        //This could now be de-serialized into an object in real life
      } else {
        res2.cause().printStackTrace();
      }
    });
  } else {
    res.cause().printStackTrace();
  }
});
```

Here’s an example of inserting a base 64 encoded string, typing it as
binary a binary field, and reading it back

``` js
//This could be a the byte contents of a pdf file, etc converted to base 64
let base64EncodedString = "a2FpbHVhIGlzIHRoZSAjMSBiZWFjaCBpbiB0aGUgd29ybGQ=";
let document = {
  "name" : "Alan Turing",
  "binaryStuff" : {
    "$binary" : base64EncodedString
  }
};
mongoService.save("smartPeople", document, (res) => {
  if (res.succeeded()) {
    let id = res.result();
    mongoService.findOne("smartPeople", {
      "_id" : id
    }, null, (res2) => {
      if (res2.succeeded()) {
        let reconstitutedBase64EncodedString = res2.result().binaryStuff.$binary;
        //This could now converted back to bytes from the base 64 string
      } else {
        res2.cause().printStackTrace();
      }
    });
  } else {
    res.cause().printStackTrace();
  }
});
```

Here’s an example of inserting an object ID and reading it back

``` js
let individualId = new (Java.type("org.bson.types.ObjectId"))().toHexString();
let document = {
  "name" : "Stephen Hawking",
  "individualId" : {
    "$oid" : individualId
  }
};
mongoService.save("smartPeople", document, (res) => {
  if (res.succeeded()) {
    let id = res.result();
    let query = {
      "_id" : id
    };
    mongoService.findOne("smartPeople", query, null, (res2) => {
      if (res2.succeeded()) {
        let reconstitutedIndividualId = res2.result().individualId.$oid;
      } else {
        res2.cause().printStackTrace();
      }
    });
  } else {
    res.cause().printStackTrace();
  }
});
```

## Getting distinct values

Here’s an example of getting distinct value

``` js
let document = {
  "title" : "The Hobbit"
};
mongoClient.save("books", document, (res) => {
  if (res.succeeded()) {
    mongoClient.distinct("books", "title", Java.type("java.lang.String").class.getName(), (res2) => {
      console.log("Title is : " + res2.result()[0]);
    });
  } else {
    res.cause().printStackTrace();
  }
});
```

Here’s an example of getting distinct value in batch mode

``` js
let document = {
  "title" : "The Hobbit"
};
mongoClient.save("books", document, (res) => {
  if (res.succeeded()) {
    mongoClient.distinctBatch("books", "title", Java.type("java.lang.String").class.getName()).handler((book) => {
      console.log("Title is : " + book.title);
    });
  } else {
    res.cause().printStackTrace();
  }
});
```

  - Here’s an example of getting distinct value with query

<!-- end list -->

``` js
let document = {
  "title" : "The Hobbit",
  "publicationDate" : {
    "$date" : "1937-09-21T00:00:00+00:00"
  }
};
let query = {
  "publicationDate" : {
    "$gte" : {
      "$date" : "1937-09-21T00:00:00+00:00"
    }
  }
};
mongoClient.save("books", document, (res) => {
  if (res.succeeded()) {
    mongoClient.distinctWithQuery("books", "title", Java.type("java.lang.String").class.getName(), query, (res2) => {
      console.log("Title is : " + res2.result()[0]);
    });
  }
});
```

Here’s an example of getting distinct value in batch mode with query

``` js
let document = {
  "title" : "The Hobbit",
  "publicationDate" : {
    "$date" : "1937-09-21T00:00:00+00:00"
  }
};
let query = {
  "publicationDate" : {
    "$gte" : {
      "$date" : "1937-09-21T00:00:00+00:00"
    }
  }
};
mongoClient.save("books", document, (res) => {
  if (res.succeeded()) {
    mongoClient.distinctBatchWithQuery("books", "title", Java.type("java.lang.String").class.getName(), query).handler((book) => {
      console.log("Title is : " + book.title);
    });
  }
});
```

# Configuring the client

The client is configured with a json object.

The following configuration is supported by the mongo client:

  - `db_name`
    Name of the database in the MongoDB instance to use. Defaults to
    `default_db`

  - `useObjectId`
    Toggle this option to support persisting and retrieving ObjectId’s
    as strings. If `true`, hex-strings will be saved as native Mongodb
    ObjectId types in the document collection. This will allow the
    sorting of documents based on creation time. You can also derive the
    creation time from the hex-string using ObjectId::getDate(). Set to
    `false` for other types of your choosing. If set to false, or left
    to default, hex strings will be generated as the document \_id if
    the \_id is omitted from the document. Defaults to `false`.

The mongo client tries to support most options that are allowed by the
driver. There are two ways to configure mongo for use by the driver,
either by a connection string or by separate configuration options.

> **Note**
>
> If the connection string is used the mongo client will ignore any
> driver configuration options.

  - `connection_string`
    The connection string the driver uses to create the client. E.g.
    `mongodb://localhost:27017`. For more information on the format of
    the connection string please consult the driver documentation.

**Specific driver configuration options**

``` js
{
 // Single Cluster Settings
 "host" : "127.0.0.1", // string
 "port" : 27017,      // int

 // Multiple Cluster Settings
 "hosts" : [
   {
     "host" : "cluster1", // string
     "port" : 27000       // int
   },
   {
     "host" : "cluster2", // string
     "port" : 28000       // int
   },
   ...
 ],
 "replicaSet" :  "foo",    // string
 "serverSelectionTimeoutMS" : 30000, // long

 // Connection Pool Settings
 "maxPoolSize" : 50,                // int
 "minPoolSize" : 25,                // int
 "maxIdleTimeMS" : 300000,          // long
 "maxLifeTimeMS" : 3600000,         // long
 "waitQueueMultiple"  : 10,         // int
 "waitQueueTimeoutMS" : 10000,      // long
 "maintenanceFrequencyMS" : 2000,   // long
 "maintenanceInitialDelayMS" : 500, // long

 // Credentials / Auth
 "username"   : "john",     // string
 "password"   : "passw0rd", // string
 "authSource" : "some.db"   // string
 // Auth mechanism
 "authMechanism"     : "GSSAPI",        // string
 "gssapiServiceName" : "myservicename", // string

 // Socket Settings
 "connectTimeoutMS" : 300000, // int
 "socketTimeoutMS"  : 100000, // int
 "sendBufferSize"    : 8192,  // int
 "receiveBufferSize" : 8192,  // int
 "keepAlive" : true           // boolean

 // Heartbeat socket settings
 "heartbeat.socket" : {
 "connectTimeoutMS" : 300000, // int
 "socketTimeoutMS"  : 100000, // int
 "sendBufferSize"    : 8192,  // int
 "receiveBufferSize" : 8192,  // int
 "keepAlive" : true           // boolean
 }

 // Server Settings
 "heartbeatFrequencyMS" :    1000 // long
 "minHeartbeatFrequencyMS" : 500 // long
}
```

**Driver option descriptions**

  - `host`
    The host the MongoDB instance is running. Defaults to `127.0.0.1`.
    This is ignored if `hosts` is specified

  - `port`
    The port the MongoDB instance is listening on. Defaults to `27017`.
    This is ignored if `hosts` is specified

  - `hosts`
    An array representing the hosts and ports to support a MongoDB
    cluster (sharding / replication)

  - `host`
    A host in the cluster

  - `port`
    The port a host in the cluster is listening on

  - `replicaSet`
    The name of the replica set, if the MongoDB instance is a member of
    a replica set

  - `serverSelectionTimeoutMS`
    The time in milliseconds that the mongo driver will wait to select a
    server for an operation before raising an error.

  - `maxPoolSize`
    The maximum number of connections in the connection pool. The
    default value is `100`

  - `minPoolSize`
    The minimum number of connections in the connection pool. The
    default value is `0`

  - `maxIdleTimeMS`
    The maximum idle time of a pooled connection. The default value is
    `0` which means there is no limit

  - `maxLifeTimeMS`
    The maximum time a pooled connection can live for. The default value
    is `0` which means there is no limit

  - `waitQueueMultiple`
    The maximum number of waiters for a connection to become available
    from the pool. Default value is `500`

  - `waitQueueTimeoutMS`
    The maximum time that a thread may wait for a connection to become
    available. Default value is `120000` (2 minutes)

  - `maintenanceFrequencyMS`
    The time period between runs of the maintenance job. Default is `0`.

  - `maintenanceInitialDelayMS`
    The period of time to wait before running the first maintenance job
    on the connection pool. Default is `0`.

  - `username`
    The username to authenticate. Default is `null` (meaning no
    authentication required)

  - `password`
    The password to use to authenticate.

  - `authSource`
    The database name associated with the user’s credentials. Default
    value is the `db_name` value.

  - `authMechanism`
    The authentication mechanism to use. See
    \[Authentication\](<http://docs.mongodb.org/manual/core/authentication/>)
    for more details.

  - `gssapiServiceName`
    The Kerberos service name if `GSSAPI` is specified as the
    `authMechanism`.

  - `connectTimeoutMS`
    The time in milliseconds to attempt a connection before timing out.
    Default is `10000` (10 seconds)

  - `socketTimeoutMS`
    The time in milliseconds to attempt a send or receive on a socket
    before the attempt times out. Default is `0` meaning there is no
    timeout

  - `sendBufferSize`
    Sets the send buffer size (SO\_SNDBUF) for the socket. Default is
    `0`, meaning it will use the OS default for this option.

  - `receiveBufferSize`
    Sets the receive buffer size (SO\_RCVBUF) for the socket. Default is
    `0`, meaning it will use the OS default for this option.

  - `keepAlive`
    Sets the keep alive (SO\_KEEPALIVE) for the socket. Default is
    `false`

  - `heartbeat.socket`
    Configures the socket settings for the cluster monitor of the
    MongoDB java driver.

  - `heartbeatFrequencyMS`
    The frequency that the cluster monitor attempts to reach each
    server. Default is `5000` (5 seconds)

  - `minHeartbeatFrequencyMS`
    The minimum heartbeat frequency. The default value is `1000` (1
    second)

  - `ssl`
    Enable ssl between the vertx-mongo-client and mongo

  - `trustAll`
    When using ssl, trust *ALL* certificates. **WARNING** - Trusting
    *ALL* certificates will open you up to potential security issues
    such as MITM attacks.

  - `caPath`
    Set a path to a file that contains a certificate that will be used
    as a source of trust when making SSL connections to mongo.

> **Note**
>
> Most of the default values listed above use the default values of the
> MongoDB Java Driver. Please consult the driver documentation for up to
> date information.
