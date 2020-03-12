The Reactive MySQL Client is a client for MySQL with a straightforward
API focusing on scalability and low overhead.

**Features**

  - Event driven

  - Lightweight

  - Built-in connection pooling

  - Prepared queries caching

  - Cursor support

  - Row streaming

  - RxJava 1 and RxJava 2

  - Direct memory to object without unnecessary copies

  - Java 8 Date and Time

  - Stored Procedures support

  - TLS/SSL support

  - MySQL utilities commands support

  - Working with MySQL and MariaDB

  - Rich collation and charset support

# Usage

To use the Reactive MySQL Client add the following dependency to the
*dependencies* section of your build descriptor:

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
import { MySQLPool } from "@vertx/mysql-client"

// Connect options
let connectOptions = new MySQLConnectOptions()
  .setPort(3306)
  .setHost("the-host")
  .setDatabase("the-db")
  .setUser("user")
  .setPassword("secret");

// Pool options
let poolOptions = new PoolOptions()
  .setMaxSize(5);

// Create the client pool
let client = MySQLPool.pool(connectOptions, poolOptions);

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

# Connecting to MySQL

Most of the time you will use a pool to connect to MySQL:

``` js
import { MySQLPool } from "@vertx/mysql-client"

// Connect options
let connectOptions = new MySQLConnectOptions()
  .setPort(3306)
  .setHost("the-host")
  .setDatabase("the-db")
  .setUser("user")
  .setPassword("secret");

// Pool options
let poolOptions = new PoolOptions()
  .setMaxSize(5);

// Create the pooled client
let client = MySQLPool.pool(connectOptions, poolOptions);
```

The pooled client uses a connection pool and any operation will borrow a
connection from the pool to execute the operation and release it to the
pool.

If you are running with Vert.x you can pass it your Vertx instance:

``` js
import { MySQLPool } from "@vertx/mysql-client"

// Connect options
let connectOptions = new MySQLConnectOptions()
  .setPort(3306)
  .setHost("the-host")
  .setDatabase("the-db")
  .setUser("user")
  .setPassword("secret");

// Pool options
let poolOptions = new PoolOptions()
  .setMaxSize(5);
// Create the pooled client
let client = MySQLPool.pool(vertx, connectOptions, poolOptions);
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
import { MySQLPool } from "@vertx/mysql-client"

// Connect options
let connectOptions = new MySQLConnectOptions()
  .setPort(3306)
  .setHost("the-host")
  .setDatabase("the-db")
  .setUser("user")
  .setPassword("secret");

// Pool options
let poolOptions = new PoolOptions()
  .setMaxSize(5);

// Create the pooled client
let client = MySQLPool.pool(vertx, connectOptions, poolOptions);

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

# Configuration

There are several alternatives for you to configure the client.

## Data Object

A simple way to configure the client is to specify a
`MySQLConnectOptions` data object.

``` js
import { MySQLPool } from "@vertx/mysql-client"

// Data object
let connectOptions = new MySQLConnectOptions()
  .setPort(3306)
  .setHost("the-host")
  .setDatabase("the-db")
  .setUser("user")
  .setPassword("secret");

// Pool Options
let poolOptions = new PoolOptions()
  .setMaxSize(5);

// Create the pool from the data object
let pool = MySQLPool.pool(vertx, connectOptions, poolOptions);

pool.getConnection((ar, ar_err) => {
  // Handling your connection
});
```

### collations and character sets

The Reactive MySQL client supports configuring collations or character
sets and map them to a correlative `java.nio.charset.Charset`. For
example, you can specify charset for a connection like

``` js
let connectOptions = new MySQLConnectOptions();

// set connection character set to utf8 instead of the default charset utf8mb4
connectOptions.charset = "utf8";
```

The Reactive MySQL Client will take `utf8mb4` as the default charset.
String values like password and error messages are always decoded in
`UTF-8` charset.

`characterEncoding` option is used to determine which Java charset will
be used to encode String values such as query string and parameter
values, the charset is `UTF-8` by default and if it’s set to `null` then
the client will use the default Java charset instead.

You can also specify collation for a connection like

``` js
let connectOptions = new MySQLConnectOptions();

// set connection collation to utf8_general_ci instead of the default collation utf8mb4_general_ci
// setting a collation will override the charset option
connectOptions.charset = "gbk";
connectOptions.collation = "utf8_general_ci";
```

Note setting a collation on the data object will override the
**charset** and **characterEncoding** option.

You can execute SQL `SHOW COLLATION;` or `SHOW CHARACTER SET;` to get
the supported collations and charsets by the server.

More information about MySQL charsets and collations can be found in the
[MySQL Reference
Manual](https://dev.mysql.com/doc/refman/8.0/en/charset.html).

### connection attributes

You can also configure the connection attributes with the
`setProperties` or `addProperty` methods. Note `setProperties` will
override the default client properties.

``` js
Code not translatable
```

More information about client connection attributes can be found in the
[MySQL Reference
Manual](https://dev.mysql.com/doc/refman/8.0/en/performance-schema-connection-attribute-tables.html).

### useAffectedRows

You can configure the `useAffectedRows` option to decide whether to set
`CLIENT_FOUND_ROWS` flag when connecting to the server. If the
`CLIENT_FOUND_ROWS` flag is specified then the affected rows count is
the numeric value of rows found rather than affected.

More information about this can be found in the [MySQL Reference
Manual](https://dev.mysql.com/doc/refman/8.0/en/mysql-affected-rows.html)

## connection URI

Apart from configuring with a `MySQLConnectOptions` data object, We also
provide you an alternative way to connect when you want to configure
with a connection URI:

``` js
import { MySQLPool } from "@vertx/mysql-client"
import { MySQLConnection } from "@vertx/mysql-client"

// Connection URI
let connectionUri = "mysql://dbuser:secretpassword@database.server.com:3211/mydb";

// Create the pool from the connection URI
let pool = MySQLPool.pool(connectionUri);

// Create the connection from the connection URI
MySQLConnection.connect(vertx, connectionUri, (res, res_err) => {
  // Handling your connection
});
```

More information about connection string formats can be found in the
[MySQL Reference
Manual](https://dev.mysql.com/doc/refman/8.0/en/connecting-using-uri-or-key-value-pairs.html#connecting-using-uri).

Currently the client supports the following parameter key words in
connection uri(keys are case-insensitive)

  - host

  - port

  - user

  - password

  - schema

  - socket

  - useAffectedRows

Unresolved directive in index.adoc - include::queries.adoc\[\]

# MySQL LAST\_INSERT\_ID

You can get the auto incremented value if you insert a record into the
table.

``` js
import { MySQLClient } from "@vertx/mysql-client"
client.query("INSERT INTO test(val) VALUES ('v1')", (ar, ar_err) => {
  if (ar.succeeded()) {
    let rows = ar.result();
    let lastInsertId = rows.property(MySQLClient.LAST_INSERTED_ID);
    console.log("Last inserted id is: " + lastInsertId);
  } else {
    console.log("Failure: " + ar.cause().getMessage());
  }
});
```

More information can be found in [How to Get the Unique ID for the Last
Inserted
Row](https://dev.mysql.com/doc/refman/8.0/en/getting-unique-id.html).

Unresolved directive in index.adoc - include::connections.adoc\[\]

Unresolved directive in index.adoc - include::transactions.adoc\[\]

Unresolved directive in index.adoc - include::cursor.adoc\[\]

# MySQL type mapping

Currently the client supports the following MySQL types

  - BOOL,BOOLEAN (`java.lang.Byte`)

  - TINYINT (`java.lang.Byte`)

  - TINYINT UNSIGNED(`java.lang.Short`)

  - SMALLINT (`java.lang.Short`)

  - SMALLINT UNSIGNED(`java.lang.Integer`)

  - MEDIUMINT (`java.lang.Integer`)

  - MEDIUMINT UNSIGNED(`java.lang.Integer`)

  - INT,INTEGER (`java.lang.Integer`)

  - INTEGER UNSIGNED(`java.lang.Long`)

  - BIGINT (`java.lang.Long`)

  - BIGINT UNSIGNED(`io.vertx.sqlclient.data.Numeric`)

  - FLOAT (`java.lang.Float`)

  - FLOAT UNSIGNED(`java.lang.Float`)

  - DOUBLE (`java.lang.Double`)

  - DOUBLE UNSIGNED(`java.lang.Double`)

  - BIT (`java.lang.Long`)

  - NUMERIC (`io.vertx.sqlclient.data.Numeric`)

  - NUMERIC UNSIGNED(`io.vertx.sqlclient.data.Numeric`)

  - DATE (`java.time.LocalDate`)

  - DATETIME (`java.time.LocalDateTime`)

  - TIME (`java.time.Duration`)

  - TIMESTAMP (`java.time.LocalDateTime`)

  - YEAR (`java.lang.Short`)

  - CHAR (`java.lang.String`)

  - VARCHAR (`java.lang.String`)

  - BINARY (`io.vertx.core.buffer.Buffer`)

  - VARBINARY (`io.vertx.core.buffer.Buffer`)

  - TINYBLOB (`io.vertx.core.buffer.Buffer`)

  - TINYTEXT (`java.lang.String`)

  - BLOB (`io.vertx.core.buffer.Buffer`)

  - TEXT (`java.lang.String`)

  - MEDIUMBLOB (`io.vertx.core.buffer.Buffer`)

  - MEDIUMTEXT (`java.lang.String`)

  - LONGBLOB (`io.vertx.core.buffer.Buffer`)

  - LONGTEXT (`java.lang.String`)

  - ENUM (`java.lang.String`)

  - SET (`java.lang.String`)

  - JSON (`io.vertx.core.json.JsonObject`,
    `io.vertx.core.json.JsonArray`, `Number`, `Boolean`, `String`,
    `io.vertx.sqlclient.Tuple#JSON_NULL`)

Tuple decoding uses the above types when storing values

Note: In Java there is no specific representations for unsigned numeric
values, so this client will convert an unsigned value to the correlated
Java type.

## Implicit type conversion

The Reactive MySQL Client supports implicit type conversions when
executing a prepared statement. Suppose you have a `TIME` column in your
table, the two examples below will both work here.

``` js
import { Tuple } from "@vertx/sql-client"
client.preparedQuery("SELECT * FROM students WHERE updated_time = ?", Tuple.of(Java.type("java.time.LocalTime").of(19, 10, 25)), (ar, ar_err) => {
  // handle the results
});
// this will also work with implicit type conversion
client.preparedQuery("SELECT * FROM students WHERE updated_time = ?", Tuple.of("19:10:25"), (ar, ar_err) => {
  // handle the results
});
```

The MySQL data type for encoding will be inferred from the parameter
values and here is the type mapping

| Parameter value type        | encoding MySQL type   |
| --------------------------- | --------------------- |
| null                        | MYSQL\_TYPE\_NULL     |
| java.lang.Byte              | MYSQL\_TYPE\_TINY     |
| java.lang.Boolean           | MYSQL\_TYPE\_TINY     |
| java.lang.Short             | MYSQL\_TYPE\_SHORT    |
| java.lang.Integer           | MYSQL\_TYPE\_LONG     |
| java.lang.Long              | MYSQL\_TYPE\_LONGLONG |
| java.lang.Double            | MYSQL\_TYPE\_DOUBLE   |
| java.lang.Float             | MYSQL\_TYPE\_FLOAT    |
| java.time.LocalDate         | MYSQL\_TYPE\_DATE     |
| java.time.Duration          | MYSQL\_TYPE\_TIME     |
| io.vertx.core.buffer.Buffer | MYSQL\_TYPE\_BLOB     |
| java.time.LocalDateTime     | MYSQL\_TYPE\_DATETIME |
| default                     | MYSQL\_TYPE\_STRING   |

## Handling BOOLEAN

In MySQL `BOOLEAN` and `BOOL` data types are synonyms for `TINYINT(1)`.
A value of zero is considered false, non-zero values are considered
true. A `BOOLEAN` data type value is stored in `Row` or `Tuple` as
`java.lang.Byte` type, you can call `Row#getValue` to retrieve it as a
`java.lang.Byte` value, or you can call `Row#getBoolean` to retrieve it
as `java.lang.Boolean` value.

``` js
client.query("SELECT graduated FROM students WHERE id = 0", (ar, ar_err) => {
  if (ar.succeeded()) {
    let rowSet = ar.result();
    rowSet.forEach(row => {
      let pos = row.getColumnIndex("graduated");
      let value = row.get(Java.type("java.lang.Byte").class, pos);
      let graduated = row.getBoolean("graduated");
    });
  } else {
    console.log("Failure: " + ar.cause().getMessage());
  }
});
```

When you want to execute a prepared statement with a param of a
`BOOLEAN` value, you can simply add the `java.lang.Boolean` value to the
params list.

``` js
import { Tuple } from "@vertx/sql-client"
client.preparedQuery("UPDATE students SET graduated = ? WHERE id = 0", Tuple.of(true), (ar, ar_err) => {
  if (ar.succeeded()) {
    console.log("Updated with the boolean value");
  } else {
    console.log("Failure: " + ar.cause().getMessage());
  }
});
```

## Handling JSON

MySQL `JSON` data type is represented by the following Java types:

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

## Handling BIT

The `BIT` data type is mapped to `java.lang.Long` type, but Java has no
notion of unsigned numeric values, so if you want to insert or update a
record with the max value of `BIT(64)`, you can do some tricks setting
the parameter to `-1L`.

## Handling NUMERIC

The `Numeric` Java type is used to represent the MySQL `NUMERIC` type.

``` js
let numeric = row.get(Java.type("io.vertx.sqlclient.data.Numeric").class, 0);
if (numeric.isNaN()) {
  // Handle NaN
} else {
  let value = numeric.bigDecimalValue();
}
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

# MySQL Stored Procedure

You can run stored procedures in queries. The result will be retrieved
from the server following the [MySQL
protocol](https://dev.mysql.com/doc/dev/mysql-server/8.0.12/page_protocol_command_phase_sp.html)
without any magic here.

``` js
client.query("CREATE PROCEDURE multi() BEGIN\n  SELECT 1;\n  SELECT 1;\n  INSERT INTO ins VALUES (1);\n  INSERT INTO ins VALUES (2);\nEND;", (ar1, ar1_err) => {
  if (ar1.succeeded()) {
    // create stored procedure success
    client.query("CALL multi();", (ar2, ar2_err) => {
      if (ar2.succeeded()) {
        // handle the result
        let result1 = ar2.result();
        let row1 = result1.iterator().next();
        console.log("First result: " + row1.getInteger(0));

        let result2 = result1.next();
        let row2 = result2.iterator().next();
        console.log("Second result: " + row2.getInteger(0));

        let result3 = result2.next();
        console.log("Affected rows: " + result3.rowCount());
      } else {
        console.log("Failure: " + ar2.cause().getMessage());
      }
    });
  } else {
    console.log("Failure: " + ar1.cause().getMessage());
  }
});
```

Note: Prepared statements binding OUT parameters is not supported for
now.

# MySQL LOCAL INFILE

This client supports for handling the LOCAL INFILE Request, if you want
to load data from a local file into the server, you can use query `LOAD
DATA LOCAL INFILE '<filename>' INTO TABLE <table>;`. More information
can be found in the [MySQL Reference
Manual](https://dev.mysql.com/doc/refman/8.0/en/load-data.html).

# Authentication

MySQL 8.0 introduces a new authentication method named
`caching_sha2_password` and it’s the default one to authenticate. In
order to connect to the server using this new authentication method, you
need either use a secure connection(i.e. enable TLS/SSL) or exchange the
encrypted password using an RSA key pair to avoid leaks of password. The
RSA key pair is automatically exchanged during the communication, but
the server RSA public key may be hacked during the process since it’s
transferred on a insecure connection. So if you’re on a insecure
connection and want to avoid the risk of exposing the server RSA public
key, you can set the server RSA public key like this:

``` js
import { Buffer } from "@vertx/core"

let options1 = new MySQLConnectOptions()
  .setPort(3306)
  .setHost("the-host")
  .setDatabase("the-db")
  .setUser("user")
  .setPassword("secret")
  .setServerRsaPublicKeyPath("tls/files/public_key.pem");

let options2 = new MySQLConnectOptions()
  .setPort(3306)
  .setHost("the-host")
  .setDatabase("the-db")
  .setUser("user")
  .setPassword("secret")
  .setServerRsaPublicKeyValue(Buffer.buffer("-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3yvG5s0qrV7jxVlp0sMj\nxP0a6BuLKCMjb0o88hDsJ3xz7PpHNKazuEAfPxiRFVAV3edqfSiXoQw+lJf4haEG\nHQe12Nfhs+UhcAeTKXRlZP/JNmI+BGoBduQ1rCId9bKYbXn4pvyS/a1ft7SwFkhx\naogCur7iIB0WUWvwkQ0fEj/Mlhw93lLVyx7hcGFq4FOAKFYr3A0xrHP1IdgnD8QZ\n0fUbgGLWWLOossKrbUP5HWko1ghLPIbfmU6o890oj1ZWQewj1Rs9Er92/UDj/JXx\n7ha1P+ZOgPBlV037KDQMS6cUh9vTablEHsMLhDZanymXzzjBkL+wH/b9cdL16LkQ\n5QIDAQAB\n-----END PUBLIC KEY-----\n"));
```

More information about the `caching_sha2_password` authentication method
can be found in the [MySQL Reference
Manual](https://dev.mysql.com/doc/refman/8.0/en/caching-sha2-pluggable-authentication.html).

# Using SSL/TLS

To configure the client to use SSL connection, you can configure the
`MySQLConnectOptions` like a Vert.x `NetClient`. All [SSL
modes](https://dev.mysql.com/doc/refman/8.0/en/connection-options.html#option_general_ssl-mode)
are supported and you are able to configure `sslmode`. The client is in
`DISABLED` SSL mode by default. `ssl` parameter is kept as a mere
shortcut for setting `sslmode`. `setSsl(true)` is equivalent to
`setSslMode(VERIFY_CA)` and `setSsl(false)` is equivalent to
`setSslMode(DISABLED)`.

``` js
import { MySQLConnection } from "@vertx/mysql-client"

let options = new MySQLConnectOptions()
  .setPort(3306)
  .setHost("the-host")
  .setDatabase("the-db")
  .setUser("user")
  .setPassword("secret")
  .setSslMode("VERIFY_CA")
  .setPemTrustOptions(new PemTrustOptions()
    .setCertPaths(["/path/to/cert.pem"]));

MySQLConnection.connect(vertx, options, (res, res_err) => {
  if (res.succeeded()) {
    // Connected with SSL
  } else {
    console.log("Could not connect " + res.cause());
  }
});
```

More information can be found in the [Vert.x
documentation](http://vertx.io/docs/vertx-core/java/#ssl).

# MySQL utility command

Sometimes you want to use MySQL utility commands and we provide support
for this. More information can be found in the [MySQL utility
commands](https://dev.mysql.com/doc/dev/mysql-server/8.0.12/page_protocol_command_phase_utility.html).

## COM\_PING

You can use `COM_PING` command to check if the server is alive. The
handler will be notified if the server responds to the PING, otherwise
the handler will never be called.

``` js
connection.ping((ar, ar_err) => {
  console.log("The server has responded to the PING");
});
```

## COM\_RESET\_CONNECTION

You can reset the session state with `COM_RESET_CONNECTION` command,
this will reset the connection state like: - user variables - temporary
tables - prepared statements

``` js
connection.resetConnection((ar, ar_err) => {
  if (ar.succeeded()) {
    console.log("Connection has been reset now");
  } else {
    console.log("Failure: " + ar.cause().getMessage());
  }
});
```

## COM\_CHANGE\_USER

You can change the user of the current connection, this will perform a
re-authentication and reset the connection state like
`COM_RESET_CONNECTION`.

``` js
let authenticationOptions = new MySQLAuthOptions()
  .setUser("newuser")
  .setPassword("newpassword")
  .setDatabase("newdatabase");
connection.changeUser(authenticationOptions, (ar, ar_err) => {
  if (ar.succeeded()) {
    console.log("User of current connection has been changed.");
  } else {
    console.log("Failure: " + ar.cause().getMessage());
  }
});
```

## COM\_INIT\_DB

You can use `COM_INIT_DB` command to change the default schema of the
connection.

``` js
connection.specifySchema("newschema", (ar, ar_err) => {
  if (ar.succeeded()) {
    console.log("Default schema changed to newschema");
  } else {
    console.log("Failure: " + ar.cause().getMessage());
  }
});
```

## COM\_STATISTICS

You can use `COM_STATISTICS` command to get a human readable string of
some internal status variables in MySQL server.

``` js
connection.getInternalStatistics((ar, ar_err) => {
  if (ar.succeeded()) {
    console.log("Statistics: " + ar.result());
  } else {
    console.log("Failure: " + ar.cause().getMessage());
  }
});
```

## COM\_DEBUG

You can use `COM_DEBUG` command to dump debug info to the MySQL server’s
STDOUT.

``` js
connection.debug((ar, ar_err) => {
  if (ar.succeeded()) {
    console.log("Debug info dumped to server's STDOUT");
  } else {
    console.log("Failure: " + ar.cause().getMessage());
  }
});
```

## COM\_SET\_OPTION

You can use `COM_SET_OPTION` command to set options for the current
connection. Currently only `CLIENT_MULTI_STATEMENTS` can be set.

For example, you can disable `CLIENT_MULTI_STATEMENTS` with this
command.

``` js
connection.setOption('MYSQL_OPTION_MULTI_STATEMENTS_OFF', (ar, ar_err) => {
  if (ar.succeeded()) {
    console.log("CLIENT_MULTI_STATEMENTS is off now");
  } else {
    console.log("Failure: " + ar.cause().getMessage());
  }
});
```

# MySQL and MariaDB version support matrix

| MySQL   | MariaDB   |         |           |
| ------- | --------- | ------- | --------- |
| Version | Supported | Version | Supported |
| `5.5`   | ✔         | `10.1`  | ✔         |
| `5.6`   | ✔         | `10.2`  | ✔         |
| `5.7`   | ✔         | `10.3`  | ✔         |
| `8.0`   | ✔         | `10.4`  | ✔         |

Known issues:

  - Reset connection utility command does not work in MySQL 5.5, 5.6 and
    MariaDB 10.1

  - Change user utility command is not supported with MariaDB 10.2 and
    10.3
