This client allows you to interact with any JDBC compliant database
using an asynchronous API from your Vert.x application.

The client API is represented with the interface `JDBCClient`.

To use this project, add the following dependency to the *dependencies*
section of your build descriptor:

  - Maven (in your `pom.xml`):

<!-- end list -->

``` xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-jdbc-client</artifactId>
 <version>${maven.version}</version>
</dependency>
```

  - Gradle (in your `build.gradle` file):

<!-- end list -->

``` groovy
compile 'io.vertx:vertx-jdbc-client:${maven.version}'
```

# Creating a the client

There are several ways to create a client. Let’s go through them all.

## Using default shared data source

In most cases you will want to share a data source between different
client instances.

E.g. you scale your application by deploying multiple instances of your
verticle and you want each verticle instance to share the same
datasource so you don’t end up with multiple pools

You do this as follows:

``` java
import { JDBCClient } from "@vertx/jdbc-client"

let client = JDBCClient.createShared(vertx, config);
```

The first call to `JDBCClient.createShared` will actually create the
data source, and the specified config will be used.

Subsequent calls will return a new client instance that uses the same
data source, so the configuration won’t be used.

## Specifying a data source name

You can create a client specifying a data source name as follows

``` java
import { JDBCClient } from "@vertx/jdbc-client"


let client = JDBCClient.createShared(vertx, config, "MyDataSource");
```

If different clients are created using the same Vert.x instance and
specifying the same data source name, they will share the same data
source.

The first call to `JDBCClient.createShared` will actually create the
data source, and the specified config will be used.

Subsequent calls will return a new client instance that uses the same
data source, so the configuration won’t be used.

Use this way of creating if you wish different groups of clients to have
different data sources, e.g. they’re interacting with different
databases.

## Creating a client with a non shared data source

In most cases you will want to share a data source between different
client instances. However, it’s possible you want to create a client
instance that doesn’t share its data source with any other client.

In that case you can use `JDBCClient.create`.

``` java
import { JDBCClient } from "@vertx/jdbc-client"

let client = JDBCClient.create(vertx, config);
```

This is equivalent to calling `JDBCClient.createShared` with a unique
data source name each time.

## Specifying a data source

If you already have a pre-existing data source, you can also create the
client directly specifying that:

``` java
import { JDBCClient } from "@vertx/jdbc-client"

let client = JDBCClient.create(vertx, dataSource);
```

# Closing the client

It’s fine to keep hold of the client for a long time (e.g. the lifetime
of your verticle), but once you’re done with it you should close it.

Clients that share a data source with other client instances are
reference counted. Once the last one that references the same data
source is closed, the data source will be closed.

## Automatic clean-up in verticles

If you’re creating clients from inside verticles, the clients will be
automatically closed when the verticle is undeployed.

# Getting a connection

Once you’ve created a client you use `getConnection` to get a
connection.

This will return the connection in the handler when one is ready from
the pool.

``` java
// Now do stuff with it:

client.getConnection((res, res_err) => {
  if (res.succeeded()) {

    let connection = res.result();

    connection.query("SELECT * FROM some_table", (res2, res2_err) => {
      if (res2.succeeded()) {

        let rs = res2.result();
        // Do something with results
      }
    });
  } else {
    // Failed to get connection - deal with it
  }
});
```

The connection is an instance of `SQLConnection` which is a common
interface not only used by the Vert.x JDBC Client.

You can learn how to use it in the [common sql
interface](http://vertx.io/docs/vertx-sql-common/js/) documentation.

# Configuration

Configuration is passed to the client when creating or deploying it.

The following configuration properties generally apply:

  - `provider_class`  
    The class name of the class actually used to manage the database
    connections. By default this is
    `io.vertx.ext.jdbc.spi.impl.C3P0DataSourceProvider` but if you want
    to use a different provider you can override this property and
    provide your implementation.

  - `row_stream_fetch_size`  
    The size of `SQLRowStream` internal cache which used to better
    performance. By default it equals to `128`

Assuming the C3P0 implementation is being used (the default), the
following extra configuration properties apply:

  - `url`  
    the JDBC connection URL for the database

  - `driver_class`  
    the class of the JDBC driver

  - `user`  
    the username for the database

  - `password`  
    the password for the database

  - `max_pool_size`  
    the maximum number of connections to pool - default is `15`

  - `initial_pool_size`  
    the number of connections to initialise the pool with - default is
    `3`

  - `min_pool_size`  
    the minimum number of connections to pool

  - `max_statements`  
    the maximum number of prepared statements to cache - default is `0`.

  - `max_statements_per_connection`  
    the maximum number of prepared statements to cache per connection -
    default is `0`.

  - `max_idle_time`  
    number of seconds after which an idle connection will be closed -
    default is `0` (never expire).

Other Connection Pool providers are:

  - BoneCP (**DEPRECATED** you should avoid this pool as it has been
    deprecated upstream)

  - Hikari

Similar to C3P0 they can be configured by passing the configuration
values on the JSON config object. For the special case where you do not
want to deploy your app as a fat jar but run with a vert.x distribution,
then it is recommented to use BoneCP if you have no write permissions to
add the JDBC driver to the vert.x lib directory and are passing it using
the `-cp` command line flag.

If you want to configure any other C3P0 properties, you can add a file
`c3p0.properties` to the classpath.

Here’s an example of configuring a service:

``` java
import { JDBCClient } from "@vertx/jdbc-client"

let config = {
  "url" : "jdbc:hsqldb:mem:test?shutdown=true",
  "driver_class" : "org.hsqldb.jdbcDriver",
  "max_pool_size" : 30
};

let client = JDBCClient.createShared(vertx, config);
```

Hikari uses a different set of properties:

  - `jdbcUrl` for the JDBC URL

  - `driverClassName` for the JDBC driven class name

  - `maximumPoolSize` for the pool size

  - `username` for the login (`password` for the password)

Refer to the [Hikari
documentation](https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby)
for further details. Also refer to the [BoneCP
documentation](http://www.jolbox.com/configuration.html) to configure
BoneCP.

# JDBC Drivers

If you are using the default `DataSourceProvider` (relying on c3p0), you
would need to copy the JDBC driver class in your *classpath*.

If your application is packaged as a *fat jar*, be sure to embed the
jdbc driver. If your application is launched with the `vertx` command
line, copy the JDBC driver to `${VERTX_HOME}/lib`.

The behavior may be different when using a different connection pool.

# Data types

Due to the fact that Vert.x uses JSON as its standard message format
there will be many limitations to the data types accepted by the client.
You will get out of the box the standard:

  - null

  - boolean

  - number

  - string

There is also an optimistic cast for temporal types (TIME, DATE,
TIMESTAMP) and optionally disabled for UUID. UUIDs are supported by many
databases but not all. For example MySQL does not support it so the
recommended way is to use a VARCHAR(36) column. For other engines UUID
optimistic casting can be enabled using the client config json as:

    { "castUUID": true }

When this config is present UUIDs will be handled as a native type.

# Use as OSGi bundle

Vert.x JDBC client can be used as an OSGi bundle. However notice that
you would need to deploy all dependencies first. Some connection pool
requires the JDBC driver to be loaded from the classpath, and so cannot
be packaged / deployed as bundle.
