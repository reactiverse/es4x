The Reactive PostgreSQL Client is a client for PostgreSQL with a
straightforward API focusing on scalability and low overhead.

The client is reactive and non blocking, allowing to handle many
database connections with a single thread.

  - Event driven

  - Lightweight

  - Built-in connection pooling

  - Prepared queries caching

  - Publish / subscribe using PostgreSQL `NOTIFY/LISTEN`

  - Batch and cursor

  - Row streaming

  - Command pipeling

  - RxJava 1 and RxJava 2

  - Direct memory to object without unnecessary copies

  - Java 8 Date and Time

  - SSL/TLS

  - Unix domain socket

  - HTTP/1.x CONNECT, SOCKS4a or SOCKS5 proxy support

# Usage

To use the Reactive PostgreSQL Client add the following dependency to
the *dependencies* section of your build descriptor:

  - Maven (in your `pom.xml`):

<!-- end list -->

``` xml
<dependency>
 <groupId>${maven.groupId}</groupId>
 <artifactId>${maven.artifactId}</artifactId>
 <version>${maven.version}</version>
</dependency>
```

  - Gradle (in your `build.gradle` file):

<!-- end list -->

``` groovy
dependencies {
 compile '${maven.groupId}:${maven.artifactId}:${maven.version}'
}
```

# Getting started

Here is the simplest way to connect, query and disconnect

``` js
import { PgPool } from "@vertx/pg-client"

// Connect options
let connectOptions = new PgConnectOptions()
  .setPort(5432)
  .setHost("the-host")
  .setDatabase("the-db")
  .setUser("user")
  .setPassword("secret");

// Pool options
let poolOptions = new PoolOptions()
  .setMaxSize(5);

// Create the client pool
let client = PgPool.pool(connectOptions, poolOptions);

// A simple query
client.query("SELECT * FROM users WHERE id='julien'", (ar, ar_err) => {
  if (ar.succeeded()) {
    let result = ar.result();
    console.log("Got " + result.size() + " rows ");
  } else {
    console.log("Failure: " + ar.cause().getMessage());
  }

  // Now close the pool
  client.close();
});
```

# Connecting to PostgreSQL

Most of the time you will use a pool to connect to PostgreSQL:

``` js
import { PgPool } from "@vertx/pg-client"

// Connect options
let connectOptions = new PgConnectOptions()
  .setPort(5432)
  .setHost("the-host")
  .setDatabase("the-db")
  .setUser("user")
  .setPassword("secret");

// Pool options
let poolOptions = new PoolOptions()
  .setMaxSize(5);

// Create the pooled client
let client = PgPool.pool(connectOptions, poolOptions);
```

The pooled client uses a connection pool and any operation will borrow a
connection from the pool to execute the operation and release it to the
pool.

If you are running with Vert.x you can pass it your Vertx instance:

``` js
import { PgPool } from "@vertx/pg-client"

// Connect options
let connectOptions = new PgConnectOptions()
  .setPort(5432)
  .setHost("the-host")
  .setDatabase("the-db")
  .setUser("user")
  .setPassword("secret");

// Pool options
let poolOptions = new PoolOptions()
  .setMaxSize(5);
// Create the pooled client
let client = PgPool.pool(vertx, connectOptions, poolOptions);
```

You need to release the pool when you don’t need it anymore:

``` js
// Close the pool and all the associated resources
pool.close();
```

When you need to execute several operations on the same connection, you
need to use a client `connection`.

You can easily get one from the pool:

``` js
import { PgPool } from "@vertx/pg-client"

// Connect options
let connectOptions = new PgConnectOptions()
  .setPort(5432)
  .setHost("the-host")
  .setDatabase("the-db")
  .setUser("user")
  .setPassword("secret");

// Pool options
let poolOptions = new PoolOptions()
  .setMaxSize(5);

// Create the pooled client
let client = PgPool.pool(vertx, connectOptions, poolOptions);

// Get a connection from the pool
client.getConnection((ar1, ar1_err) => {

  if (ar1.succeeded()) {

    console.log("Connected");

    // Obtain our connection
    let conn = ar1.result();

    // All operations execute on the same connection
    conn.query("SELECT * FROM users WHERE id='julien'", (ar2, ar2_err) => {
      if (ar2.succeeded()) {
        conn.query("SELECT * FROM users WHERE id='emad'", (ar3, ar3_err) => {
          // Release the connection to the pool
          conn.close();
        });
      } else {
        // Release the connection to the pool
        conn.close();
      }
    });
  } else {
    console.log("Could not connect: " + ar1.cause().getMessage());
  }
});
```

Once you are done with the connection you must close it to release it to
the pool, so it can be reused.

Sometimes you want to improve performance via Unix domain socket
connection, we achieve this with Vert.x Native transports.

Make sure you have added the required `netty-transport-native`
dependency in your classpath and enabled the Unix domain socket option.

``` js
import { PgPool } from "@vertx/pg-client"

// Connect Options
// Socket file name will be /var/run/postgresql/.s.PGSQL.5432
let connectOptions = new PgConnectOptions()
  .setHost("/var/run/postgresql")
  .setPort(5432)
  .setDatabase("the-db");

// Pool options
let poolOptions = new PoolOptions()
  .setMaxSize(5);

// Create the pooled client
let client = PgPool.pool(connectOptions, poolOptions);

// Create the pooled client with a vertx instance
// Make sure the vertx instance has enabled native transports
let client2 = PgPool.pool(vertx, connectOptions, poolOptions);
```

More information can be found in the \[Vert.x
documentation\](<https://vertx.io/docs/vertx-core/java/#_native_transports>).

# Configuration

There are several alternatives for you to configure the client.

## data object

A simple way to configure the client is to specify a `PgConnectOptions`
data object.

``` js
import { PgPool } from "@vertx/pg-client"

// Data object
let connectOptions = new PgConnectOptions()
  .setPort(5432)
  .setHost("the-host")
  .setDatabase("the-db")
  .setUser("user")
  .setPassword("secret");

// Pool Options
let poolOptions = new PoolOptions()
  .setMaxSize(5);

// Create the pool from the data object
let pool = PgPool.pool(vertx, connectOptions, poolOptions);

pool.getConnection((ar, ar_err) => {
  // Handling your connection
});
```

You can also configure the generic properties with the `setProperties`
or `addProperty` methods. Note `setProperties` will override the default
client properties.

For example, you can set a default schema for the connection with adding
a `search_path` property.

``` js
Code not translatable
```

More information about the available properties can be found in the
[PostgreSQL
Manuals](https://www.postgresql.org/docs/current/runtime-config-client.html).

## connection uri

Apart from configuring with a `PgConnectOptions` data object, We also
provide you an alternative way to connect when you want to configure
with a connection URI:

``` js
import { PgPool } from "@vertx/pg-client"
import { PgConnection } from "@vertx/pg-client"

// Connection URI
let connectionUri = "postgresql://dbuser:secretpassword@database.server.com:3211/mydb";

// Create the pool from the connection URI
let pool = PgPool.pool(connectionUri);

// Create the connection from the connection URI
PgConnection.connect(vertx, connectionUri, (res, res_err) => {
  // Handling your connection
});
```

More information about connection string formats can be found in the
[PostgreSQL
Manuals](https://www.postgresql.org/docs/9.6/static/libpq-connect.html#LIBPQ-CONNSTRING).

Currently the client supports the following parameter key words in
connection uri

  - host

  - hostaddr

  - port

  - user

  - password

  - dbname

  - sslmode

  - properties including(application\_name, fallback\_application\_name,
    search\_path)

Note: configuring properties in connection URI will override the default
properties.

## environment variables

You can also use environment variables to set default connection setting
values, this is useful when you want to avoid hard-coding database
connection information. You can refer to the [official
documentation](https://www.postgresql.org/docs/9.6/static/libpq-envars.html)
for more details. The following parameters are supported:

  - `PGHOST`

  - `PGHOSTADDR`

  - `PGPORT`

  - `PGDATABASE`

  - `PGUSER`

  - `PGPASSWORD`

  - `PGSSLMODE`

If you don’t specify a data object or a connection URI string to
connect, environment variables will take precedence over them.

``` js
$ PGUSER=user \
 PGHOST=the-host \
 PGPASSWORD=secret \
 PGDATABASE=the-db \
 PGPORT=5432 \
 PGSSLMODE=DISABLE
```

``` js
import { PgPool } from "@vertx/pg-client"
import { PgConnection } from "@vertx/pg-client"

// Create the pool from the environment variables
let pool = PgPool.pool();

// Create the connection from the environment variables
PgConnection.connect(vertx, (res, res_err) => {
  // Handling your connection
});
```

Unresolved directive in index.adoc - include::queries.adoc\[\]

You can fetch generated keys with a 'RETURNING' clause in your query:

``` js
import { Tuple } from "@vertx/sql-client"
client.preparedQuery("INSERT INTO color (color_name) VALUES ($1), ($2), ($3) RETURNING color_id", Tuple.of("white", "red", "blue"), (ar, ar_err) => {
  if (ar.succeeded()) {
    let rows = ar.result();
    console.log(rows.rowCount());
    rows.forEach(row => {
      console.log("generated key: " + row.getInteger("color_id"));
    });
  } else {
    console.log("Failure: " + ar.cause().getMessage());
  }
});
```

Unresolved directive in index.adoc - include::connections.adoc\[\]

Unresolved directive in index.adoc - include::transactions.adoc\[\]

Unresolved directive in index.adoc - include::cursor.adoc\[\]

Note: PostreSQL destroys cursors at the end of a transaction, so the
cursor API shall be used within a transaction, otherwise you will likely
get the `34000` PostgreSQL error.

# PostgreSQL type mapping

Currently the client supports the following PostgreSQL types

  - BOOLEAN (`java.lang.Boolean`)

  - INT2 (`java.lang.Short`)

  - INT4 (`java.lang.Integer`)

  - INT8 (`java.lang.Long`)

  - FLOAT4 (`java.lang.Float`)

  - FLOAT8 (`java.lang.Double`)

  - CHAR (`java.lang.String`)

  - VARCHAR (`java.lang.String`)

  - TEXT (`java.lang.String`)

  - ENUM (`java.lang.String`)

  - NAME (`java.lang.String`)

  - SERIAL2 (`java.lang.Short`)

  - SERIAL4 (`java.lang.Integer`)

  - SERIAL8 (`java.lang.Long`)

  - NUMERIC (`io.vertx.sqlclient.data.Numeric`)

  - UUID (`java.util.UUID`)

  - DATE (`java.time.LocalDate`)

  - TIME (`java.time.LocalTime`)

  - TIMETZ (`java.time.OffsetTime`)

  - TIMESTAMP (`java.time.LocalDateTime`)

  - TIMESTAMPTZ (`java.time.OffsetDateTime`)

  - INTERVAL (`io.vertx.pgclient.data.Interval`)

  - BYTEA (`io.vertx.core.buffer.Buffer`)

  - JSON (`io.vertx.core.json.JsonObject`,
    `io.vertx.core.json.JsonArray`, `Number`, `Boolean`, `String`,
    `io.vertx.sqlclient.Tuple#JSON_NULL`)

  - JSONB (`io.vertx.core.json.JsonObject`,
    `io.vertx.core.json.JsonArray`, `Number`, `Boolean`, `String`,
    `io.vertx.sqlclient.Tuple#JSON_NULL`)

  - POINT (`io.vertx.pgclient.data.Point`)

  - LINE (`io.vertx.pgclient.data.Line`)

  - LSEG (`io.vertx.pgclient.data.LineSegment`)

  - BOX (`io.vertx.pgclient.data.Box`)

  - PATH (`io.vertx.pgclient.data.Path`)

  - POLYGON (`io.vertx.pgclient.data.Polygon`)

  - CIRCLE (`io.vertx.pgclient.data.Circle`)

  - TSVECTOR (`java.lang.String`)

  - TSQUERY (`java.lang.String`)

Tuple decoding uses the above types when storing values, it also
performs on the flu conversion the actual value when possible:

``` js
pool.query("SELECT 1::BIGINT \"VAL\"", (ar, ar_err) => {
  let rowSet = ar.result();
  let row = rowSet.iterator().next();

  // Stored as java.lang.Long
  let value = row.getValue(0);

  // Convert to java.lang.Integer
  let intValue = row.getInteger(0);
});
```

Tuple encoding uses the above type mapping for encoding, unless the type
is numeric in which case `java.lang.Number` is used instead:

``` js
pool.query("SELECT 1::BIGINT \"VAL\"", (ar, ar_err) => {
  let rowSet = ar.result();
  let row = rowSet.iterator().next();

  // Stored as java.lang.Long
  let value = row.getValue(0);

  // Convert to java.lang.Integer
  let intValue = row.getInteger(0);
});
```

Arrays of these types are supported.

## Handling JSON

PostgreSQL `JSON` and `JSONB` types are represented by the following
Java types:

  - `String`

  - `Number`

  - `Boolean`

  - `io.vertx.core.json.JsonObject`

  - `io.vertx.core.json.JsonArray`

  - `io.vertx.sqlclient.Tuple#JSON_NULL` for representing the JSON null
    literal

<!-- end list -->

``` js
Code not translatable
```

## Handling NUMERIC

The `Numeric` Java type is used to represent the PostgreSQL `NUMERIC`
type.

``` js
let numeric = row.get(Java.type("io.vertx.sqlclient.data.Numeric").class, 0);
if (numeric.isNaN()) {
  // Handle NaN
} else {
  let value = numeric.bigDecimalValue();
}
```

# Handling arrays

Arrays are available on `Tuple` and `Row`:

``` js
Code not translatable
```

# Handling custom types

Strings are used to represent custom types, both sent to and returned
from Postgres.

You can read from PostgreSQL and get the custom type as a string

``` js
import { Tuple } from "@vertx/sql-client"
client.preparedQuery("SELECT address, (address).city FROM address_book WHERE id=$1", Tuple.of(3), (ar, ar_err) => {
  if (ar.succeeded()) {
    let rows = ar.result();
    rows.forEach(row => {
      console.log("Full Address " + row.getString(0) + ", City " + row.getString(1));
    });
  } else {
    console.log("Failure: " + ar.cause().getMessage());
  }
});
```

You can also write to PostgreSQL by providing a string

``` js
import { Tuple } from "@vertx/sql-client"
client.preparedQuery("INSERT INTO address_book (id, address) VALUES ($1, $2)", Tuple.of(3, "('Anytown', 'Second Ave', false)"), (ar, ar_err) => {
  if (ar.succeeded()) {
    let rows = ar.result();
    console.log(rows.rowCount());
  } else {
    console.log("Failure: " + ar.cause().getMessage());
  }
});
```

# Handling text search

Text search is handling using java `String`

``` js
import { Tuple } from "@vertx/sql-client"
client.preparedQuery("SELECT to_tsvector( $1 ) @@ to_tsquery( $2 )", Tuple.of("fat cats ate fat rats", "fat & rat"), (ar, ar_err) => {
  if (ar.succeeded()) {
    let rows = ar.result();
    rows.forEach(row => {
      console.log("Match : " + row.getBoolean(0));
    });
  } else {
    console.log("Failure: " + ar.cause().getMessage());
  }
});
```

`tsvector` and `tsquery` can be fetched from db using java `String`

``` js
import { Tuple } from "@vertx/sql-client"
client.preparedQuery("SELECT to_tsvector( $1 ), to_tsquery( $2 )", Tuple.of("fat cats ate fat rats", "fat & rat"), (ar, ar_err) => {
  if (ar.succeeded()) {
    let rows = ar.result();
    rows.forEach(row => {
      console.log("Vector : " + row.getString(0) + ", query : " + row.getString(1));
    });
  } else {
    console.log("Failure: " + ar.cause().getMessage());
  }
});
```

# Collector queries

You can use Java collectors with the query API:

``` js
Code not translatable
```

The collector processing must not keep a reference on the `Row` as there
is a single row used for processing the entire set.

The Java `Collectors` provides many interesting predefined collectors,
for example you can create easily create a string directly from the row
set:

``` js
Code not translatable
```

# Pub/sub

PostgreSQL supports pub/sub communication channels.

You can set a `notificationHandler` to receive PostgreSQL notifications:

``` js
connection.notificationHandler((notification) => {
  console.log("Received " + notification.payload + " on channel " + notification.channel);
});

connection.query("LISTEN some-channel", (ar, ar_err) => {
  console.log("Subscribed to channel");
});
```

The `PgSubscriber` is a channel manager managing a single connection
that provides per channel subscription:

``` js
import { PgSubscriber } from "@vertx/pg-client"

let subscriber = PgSubscriber.subscriber(vertx, new PgConnectOptions()
  .setPort(5432)
  .setHost("the-host")
  .setDatabase("the-db")
  .setUser("user")
  .setPassword("secret"));

// You can set the channel before connect
subscriber.channel("channel1").handler((payload) => {
  console.log("Received " + payload);
});

subscriber.connect((ar, ar_err) => {
  if (ar.succeeded()) {

    // Or you can set the channel after connect
    subscriber.channel("channel2").handler((payload) => {
      console.log("Received " + payload);
    });
  }
});
```

The channel name that is given to the channel method will be the exact
name of the channel as held by PostgreSQL for sending notifications.
Note this is different than the representation of the channel name in
SQL, and internally `PgSubscriber` will prepare the submitted channel
name as a quoted identifier:

``` js
import { PgSubscriber } from "@vertx/pg-client"

let subscriber = PgSubscriber.subscriber(vertx, new PgConnectOptions()
  .setPort(5432)
  .setHost("the-host")
  .setDatabase("the-db")
  .setUser("user")
  .setPassword("secret"));

subscriber.connect((ar, ar_err) => {
  if (ar.succeeded()) {
    // Complex channel name - name in PostgreSQL requires a quoted ID
    subscriber.channel("Complex.Channel.Name").handler((payload) => {
      console.log("Received " + payload);
    });
    subscriber.channel("Complex.Channel.Name").subscribeHandler((subscribed) => {
      subscriber.actualConnection().query("NOTIFY \"Complex.Channel.Name\", 'msg'", (notified, notified_err) => {
        console.log("Notified \"Complex.Channel.Name\"");
      });
    });

    // PostgreSQL simple ID's are forced lower-case
    subscriber.channel("simple_channel").handler((payload) => {
      console.log("Received " + payload);
    });
    subscriber.channel("simple_channel").subscribeHandler((subscribed) => {
      // The following simple channel identifier is forced to lower case
      subscriber.actualConnection().query("NOTIFY Simple_CHANNEL, 'msg'", (notified, notified_err) => {
        console.log("Notified simple_channel");
      });
    });

    // The following channel name is longer than the current
    // (NAMEDATALEN = 64) - 1 == 63 character limit and will be truncated
    subscriber.channel("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaabbbbb").handler((payload) => {
      console.log("Received " + payload);
    });
  }
});
```

You can provide a reconnect policy as a function that takes the number
of `retries` as argument and returns an `amountOfTime` value:

  - when `amountOfTime < 0`: the subscriber is closed and there is no
    retry

  - when `amountOfTime = 0`: the subscriber retries to connect
    immediately

  - when `amountOfTime > 0`: the subscriber retries after `amountOfTime`
    milliseconds

<!-- end list -->

``` js
import { PgSubscriber } from "@vertx/pg-client"

let subscriber = PgSubscriber.subscriber(vertx, new PgConnectOptions()
  .setPort(5432)
  .setHost("the-host")
  .setDatabase("the-db")
  .setUser("user")
  .setPassword("secret"));

// Reconnect at most 10 times after 100 ms each
subscriber.reconnectPolicy((retries) => {
  if (retries < 10) {
    return 100
  } else {
    return -1
  }
});
```

The default policy is to not reconnect.

# Cancelling Request

PostgreSQL supports cancellation of requests in progress. You can cancel
inflight requests using `cancelRequest`. Cancelling a request opens a
new connection to the server and cancels the request and then close the
connection.

``` js
connection.query("SELECT pg_sleep(20)", (ar, ar_err) => {
  if (ar.succeeded()) {
    // imagine this is a long query and is still running
    console.log("Query success");
  } else {
    // the server will abort the current query after cancelling request
    console.log("Failed to query due to " + ar.cause().getMessage());
  }
});
connection.cancelRequest((ar, ar_err) => {
  if (ar.succeeded()) {
    console.log("Cancelling request has been sent");
  } else {
    console.log("Failed to send cancelling request");
  }
});
```

> The cancellation signal might or might not have any effect — for
> example, if it arrives after the backend has finished processing the
> query, then it will have no effect. If the cancellation is effective,
> it results in the current command being terminated early with an error
> message.

More information can be found in the [official
documentation](https://www.postgresql.org/docs/11/protocol-flow.html#id-1.10.5.7.9).

# Using SSL/TLS

To configure the client to use SSL connection, you can configure the
`PgConnectOptions` like a Vert.x `NetClient`. All [SSL
modes](https://www.postgresql.org/docs/current/libpq-ssl.html#LIBPQ-SSL-PROTECTION)
are supported and you are able to configure `sslmode`. The client is in
`DISABLE` SSL mode by default. `ssl` parameter is kept as a mere
shortcut for setting `sslmode`. `setSsl(true)` is equivalent to
`setSslMode(VERIFY_CA)` and `setSsl(false)` is equivalent to
`setSslMode(DISABLE)`.

``` js
import { PgConnection } from "@vertx/pg-client"

let options = new PgConnectOptions()
  .setPort(5432)
  .setHost("the-host")
  .setDatabase("the-db")
  .setUser("user")
  .setPassword("secret")
  .setSslMode("VERIFY_CA")
  .setPemTrustOptions(new PemTrustOptions()
    .setCertPaths(["/path/to/cert.pem"]));

PgConnection.connect(vertx, options, (res, res_err) => {
  if (res.succeeded()) {
    // Connected with SSL
  } else {
    console.log("Could not connect " + res.cause());
  }
});
```

More information can be found in the [Vert.x
documentation](http://vertx.io/docs/vertx-core/java/#ssl).

# Using a proxy

You can also configure the client to use an HTTP/1.x CONNECT, SOCKS4a or
SOCKS5 proxy.

More information can be found in the [Vert.x
documentation](http://vertx.io/docs/vertx-core/java/#_using_a_proxy_for_client_connections).
