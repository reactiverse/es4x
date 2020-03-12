The Async MySQL / PostgreSQL Client is responsible for providing an
interface for Vert.x applications that need to interact with a MySQL or
PostgreSQL database.

It uses Mauricio Linhares [async
driver](https://github.com/mauricio/postgresql-async) to interact with
the MySQL or PostgreSQL databases in a non blocking way.

# Using the MySQL and PostgreSQL client

This section describes how to configure your project to be able to use
the MySQL / PostgreSQL client in your application.

## In a regular application

To use this client, you need to add the following jar to your
`CLASSPATH`:

  - ${maven.artifactId} ${maven.version} (the client)

  - scala-library 2.11.4

  - the postgress-async-2.11 and mysdql-async-2.11 from
    <https://github.com/mauricio/postgresql-async>

  - joda time

All these jars are downloadable from Maven Central.

## In an application packaged in a fat jar

If you are building a *Fat-jar* using Maven or Gradle, just add the
following dependencies:

  - Maven (in your `pom.xml`):

<!-- end list -->

``` xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-mysql-postgresql-client</artifactId>
 <version>${maven.version}</version>
</dependency>
```

  - Gradle (in your `build.gradle` file):

<!-- end list -->

``` groovy
compile 'io.vertx:vertx-mysql-postgresql-client:${maven.version}'
```

## In an application using a vert.x distributions

If you are using a vert.x distribution, add the jar files listed above
to the `$VERTX_HOME/lib` directory.

Alternatively, you can edit the `vertx-stack.json` file located in
`$VERTX_HOME`, and set `"included": true` for the
`vertx-mysql-postgresql-client` dependency. Once done, launch: `vertx
resolve --dir=lib --stack=
./vertx-stack.json`. It downloads the client and its dependencies.

# Creating a client

There are several ways to create a client. Let’s go through them all.

## Using default shared pool

In most cases you will want to share a pool between different client
instances.

E.g. you scale your application by deploying multiple instances of your
verticle and you want each verticle instance to share the same pool so
you don’t end up with multiple pools

You do this as follows:

``` java
import { MySQLClient } from "@vertx/mysql-postgresql"
import { PostgreSQLClient } from "@vertx/mysql-postgresql"

// To create a MySQL client:

let mySQLClientConfig = {
  "host" : "mymysqldb.mycompany"
};
let mySQLClient = MySQLClient.createShared(vertx, mySQLClientConfig);

// To create a PostgreSQL client:

let postgreSQLClientConfig = {
  "host" : "mypostgresqldb.mycompany"
};
let postgreSQLClient = PostgreSQLClient.createShared(vertx, postgreSQLClientConfig);
```

The first call to `MySQLClient.createShared` or
`PostgreSQLClient.createShared` will actually create the data source,
and the specified config will be used.

Subsequent calls will return a new client instance that uses the same
data source, so the configuration won’t be used.

## Specifying a pool name

You can create a client specifying a pool name as follows

``` java
import { MySQLClient } from "@vertx/mysql-postgresql"
import { PostgreSQLClient } from "@vertx/mysql-postgresql"

// To create a MySQL client:

let mySQLClientConfig = {
  "host" : "mymysqldb.mycompany"
};
let mySQLClient = MySQLClient.createShared(vertx, mySQLClientConfig, "MySQLPool1");

// To create a PostgreSQL client:

let postgreSQLClientConfig = {
  "host" : "mypostgresqldb.mycompany"
};
let postgreSQLClient = PostgreSQLClient.createShared(vertx, postgreSQLClientConfig, "PostgreSQLPool1");
```

If different clients are created using the same Vert.x instance and
specifying the same pool name, they will share the same data source.

The first call to `MySQLClient.createShared` or
`PostgreSQLClient.createShared` will actually create the data source,
and the specified config will be used.

Subsequent calls will return a new client instance that uses the same
pool, so the configuration won’t be used.

Use this way of creating if you wish different groups of clients to have
different pools, e.g. they’re interacting with different databases.

## Creating a client with a non shared data source

In most cases you will want to share a pool between different client
instances. However, it’s possible you want to create a client instance
that doesn’t share its pool with any other client.

In that case you can use `MySQLClient.createNonShared` or
`PostgreSQLClient.createNonShared`

``` java
import { MySQLClient } from "@vertx/mysql-postgresql"
import { PostgreSQLClient } from "@vertx/mysql-postgresql"

// To create a MySQL client:

let mySQLClientConfig = {
  "host" : "mymysqldb.mycompany"
};
let mySQLClient = MySQLClient.createNonShared(vertx, mySQLClientConfig);

// To create a PostgreSQL client:

let postgreSQLClientConfig = {
  "host" : "mypostgresqldb.mycompany"
};
let postgreSQLClient = PostgreSQLClient.createNonShared(vertx, postgreSQLClientConfig);
```

This is equivalent to calling `MySQLClient.createShared` or
`PostgreSQLClient.createShared` with a unique pool name each time.

# Closing the client

You can hold on to the client for a long time (e.g. the life-time of
your verticle), but once you have finished with it, you should close it
using `close` or `close`

# Getting a connection

Use `getConnection` to get a connection.

This will return the connection in the handler when one is ready from
the pool.

``` java
// Now do stuff with it:

client.getConnection((res) => {
  if (res.succeeded()) {

    let connection = res.result();

    // Got a connection

  } else {
    // Failed to get connection - deal with it
  }
});
```

Once you’ve finished with the connection make sure you close it
afterwards.

The connection is an instance of `SQLConnection` which is a common
interface used by other SQL clients.

You can learn how to use it in the [common sql
interface](http://vertx.io/docs/vertx-sql-common/js/) documentation.

## Configuring reconnections

This service is able to recover from temporary database outages, such as
those which occur during a database restart or brief loss of network
connectivity. You can configure the expected behaviour when acquiring
connections via the following properties:

  - `maxConnectionRetries`

  - `connectionRetryDelay`

When the internal connection pool attempts to acquire an open connection
and fails, it will retry up to `maxConnectionRetries` times, with a
delay of `connectionRetryDelay` milliseconds between each attempt. If
all attempts fail, any clients waiting for connections from the pool
will be notified with an Error, indicating that a Connection could not
be acquired. Note that clients will not be notified with an Error until
a full round of attempts fail, which may be some time after the initial
connection attempt.

If `maxConnectionRetries` is set to `0`, the internal connection pool
will not perform any reconnection (default). If `maxConnectionRetries`
is set to `-1`, the internal connection pool will attempt to acquire new
connections indefinitely, so any call to `getConnection` may be
indefinitely waiting for a successful acquisition.

Once a full round of acquisition attempts fails, the internal connection
pool will remain active, and will try again to acquire connections in
response to future requests for connections.

Note that if a database restart occurs, a pool may contain previously
acquired but now stale Connections that will only be detected and purged
lazily, when the pool attempts to reuse them.

## Note about date and timestamps

Whenever you get dates back from the database, this service will
implicitly convert them into ISO 8601 (`yyyy-MM-ddTHH:mm:ss.SSS`)
formatted strings. MySQL usually discards milliseconds, so you will
regularly see `.000`.

## Note about last inserted ids

When inserting new rows into a table, you might want to retrieve
auto-incremented ids from the database. The JDBC API usually lets you
retrieve the last inserted id from a connection. If you use MySQL, it
will work the way it does like the JDBC API. In PostgreSQL you can add
the ["RETURNING"
clause](http://www.postgresql.org/docs/current/static/sql-insert.html)
to get the latest inserted ids. Use one of the `query` methods to get
access to the returned columns.

## Note about stored procedures

The `call` and `callWithParams` methods are not implemented currently.

# Configuration

Both the PostgreSql and MySql clients take the same configuration:

    {
     "host" : <your-host>,
     "port" : <your-port>,
     "maxPoolSize" : <maximum-number-of-open-connections>,
     "username" : <your-username>,
     "password" : <your-password>,
     "database" : <name-of-your-database>,
     "charset" : <name-of-the-character-set>,
     "connectTimeout" : <timeout-in-milliseconds>,
     "testTimeout" : <timeout-in-milliseconds>,
     "queryTimeout" : <timeout-in-milliseconds>,
     "maxConnectionRetries" : <maximum-number-of-connection-retries>,
     "connectionRetryDelay" : <delay-in-milliseconds>,
     "sslMode" : <"disable"|"prefer"|"require"|"verify-ca"|"verify-full">,
     "sslRootCert" : <path to file with certificate>
    }

  - `host`
    The host of the database. Defaults to `localhost`.

  - `port`
    The port of the database. Defaults to `5432` for PostgreSQL and
    `3306` for MySQL.

  - `maxPoolSize`
    The number of connections that may be kept open. Defaults to `10`.

  - `username`
    The username to connect to the database. Defaults to `vertx`.

  - `password`
    The password to connect to the database. Defaults to `password`.

  - `database`
    The name of the database you want to connect to. Defaults to
    `testdb`.

  - `charset`
    The name of the character set you want to use for the connection.
    Defaults to `UTF-8`.

  - `connectTimeout`
    The timeout to wait for connecting to the database. Defaults to
    `10000` (= 10 seconds).

  - `testTimeout`
    The timeout for connection tests performed by pools. Defaults to
    `10000` (= 10 seconds).

  - `queryTimeout`
    The timeout to wait for a query in milliseconds. Default is not set.

  - `maxConnectionRetries`
    Maximum number of connection retries. Defaults to `0` (no
    retries).
    Special values:

      - \-1
        Unlimited number of connection retries

      - 0
        No connection retries will be done

  - `connectionRetryDelay`
    Delay in milliseconds between each retry attempt. Defaults to `5000`
    (= 5 seconds).

  - `sslMode`
    If you want to enable SSL support you should enable this parameter.
    For example to connect Heroku you will need to use **prefer**.

      - "disable"
        only try a non-SSL connection

      - "prefer"
        first try an SSL connection; if that fails, try a non-SSL
        connection

      - "require"
        only try an SSL connection, but don’t verify Certificate
        Authority

      - "verify-ca"
        only try an SSL connection, and verify that the server
        certificate is issued by a trusted certificate authority (CA)

      - "verify-full"
        only try an SSL connection, verify that the server certificate
        is issued by a trusted CA and that the server host name matches
        that in the certificate

  - `sslRootCert`
    Path to SSL root certificate file. Is used if you want to verify
    privately issued certificate. Refer to
    [postgresql-async](https://github.com/mauricio/postgresql-async)
    documentation for more details.
