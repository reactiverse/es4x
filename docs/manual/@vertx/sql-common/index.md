# Vert.x Common SQL interface

The common SQL interface is used to interact with Vert.x SQL services.

You obtain a connection to the database via the service interface for
the specific SQL service that you are using (e.g.
JDBC/MySQL/PostgreSQL).

To use this project, add the following dependency to the *dependencies*
section of your build descriptor:

  - Maven (in your `pom.xml`):

<!-- end list -->

``` xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-sql-common</artifactId>
 <version>${maven.version}</version>
</dependency>
```

  - Gradle (in your `build.gradle` file):

<!-- end list -->

``` groovy
compile 'io.vertx:vertx-sql-common:${maven.version}'
```

# Simple SQL Operations

There are times when you will want to run a single SQL operation, e.g.:
a single select of a row, or a update to a set of rows which do not
require to be part of a transaction or have dependencies on the previous
or next operation.

For these cases, clients provide a boilerplate-less API `SQLOperations`.
This interface will perform the following steps for you:

1.  acquire a connection from the connection pool

2.  perform your action

3.  close and return the connection to the connection pool

An example where users get loaded from the `USERS` table could be:

``` js
client.query("SELECT * FROM USERS", (ar, ar_err) => {
  if (ar.succeeded()) {
    if (ar.succeeded()) {
      let result = ar.result();
    } else {
      // Failed!
    }
    // NOTE that you don't need to worry about
    // the connection management (e.g.: close)
  }
});
```

You can perform the following operations as a simple one "shot" method
call:

  - `query`

  - `queryWithParams`

  - `querySingle`

  - `querySingleWithParams`

  - `update`

  - `updateWithParams`

  - `call`

  - `callWithParams`

For further details on these API please refer to the `SQLOperations`
interface.

# The SQL Connection

A connection to the database is represented by `SQLConnection`.

## Auto-commit

When you obtain a connection auto commit is set to `true`. This means
that each operation you perform will effectively execute in its own
transaction.

If you wish to perform multiple operations in a single transaction you
should set auto commit to false with `setAutoCommit`.

When the operation is complete, the handler will be called:

``` js
connection.setAutoCommit(false, (res, res_err) => {
  if (res.succeeded()) {
    // OK!
  } else {
    // Failed!
  }
});
```

## Executing queries

To execute a query use `query`

The query string is raw SQL that is passed through without changes to
the actual database.

The handler will be called with the results, represented by `ResultSet`
when the query has been run.

``` js
connection.query("SELECT ID, FNAME, LNAME, SHOE_SIZE from PEOPLE", (res, res_err) => {
  if (res.succeeded()) {
    // Get the result set
    let resultSet = res.result();
  } else {
    // Failed!
  }
});
```

The `ResultSet` instance represents the results of a query.

The list of column names are available with `getColumnNames`, and the
actual results available with `getResults`

The results are a list of `JsonArray` instances, one for each row of the
results.

``` js
let columnNames = resultSet.columnNames;

let results = resultSet.results;

results.forEach(row => {

  let id = row[0];
  let fName = row[1];
  let lName = row[2];
  let shoeSize = row[3];

});
```

You can also retrieve the rows as a list of Json object instances with
`getRows` - this can give you a somewhat simpler API to work with, but
please be aware that SQL results can contain duplicate column names - if
that’s the case you should use `getResults` instead.

Here’s an example of iterating through the results as Json object
instances:

``` js
let rows = resultSet.rows;

rows.forEach(row => {

  let id = row.ID;
  let fName = row.FNAME;
  let lName = row.LNAME;
  let shoeSize = row.SHOE_SIZE;

});
```

## Prepared statement queries

To execute a prepared statement query you can use `queryWithParams`.

This takes the query, containing the parameter place holders, and a
`JsonArray` or parameter values.

``` js
let query = "SELECT ID, FNAME, LNAME, SHOE_SIZE from PEOPLE WHERE LNAME=? AND SHOE_SIZE > ?";
let params = [
  "Fox",
  9
];

connection.queryWithParams(query, params, (res, res_err) => {

  if (res.succeeded()) {
    // Get the result set
    let resultSet = res.result();
  } else {
    // Failed!
  }
});
```

## Executing INSERT, UPDATE or DELETE

To execute an operation which updates the database use `update`.

The update string is raw SQL that is passed through without changes to
the actual database.

The handler will be called with the results, represented by
`UpdateResult` when the update has been run.

The update result holds the number of rows updated with `getUpdated`,
and if the update generated keys, they are available with `getKeys`.

``` js
connection.update("INSERT INTO PEOPLE VALUES (null, 'john', 'smith', 9)", (res, res_err) => {
  if (res.succeeded()) {

    let result = res.result();
    console.log("Updated no. of rows: " + result.updated);
    console.log("Generated keys: " + result.keys);

  } else {
    // Failed!
  }
});
```

## Prepared statement updates

To execute a prepared statement update you can use `updateWithParams`.

This takes the update, containing the parameter place holders, and a
`JsonArray` or parameter values.

``` js
let update = "UPDATE PEOPLE SET SHOE_SIZE = 10 WHERE LNAME=?";
let params = [
  "Fox"
];

connection.updateWithParams(update, params, (res, res_err) => {

  if (res.succeeded()) {

    let updateResult = res.result();

    console.log("No. of rows updated: " + updateResult.updated);

  } else {

    // Failed!

  }
});
```

## Callable statements

To execute a callable statement (either SQL functions or SQL procedures)
you can use `callWithParams`.

This takes the callable statement using the standard JDBC format `{ call
func_proc_name() }`, optionally including parameter place holders e.g.:
`{ call func_proc_name(?, ?) }`, a `JsonArray` containing the parameter
values and finally a `JsonArray` containing the output types e.g.:
`[null, 'VARCHAR']`.

Note that the index of the output type is as important as the params
array. If the return value is the second argument then the output array
must contain a null value as the first element.

A SQL function returns some output using the `return` keyword, and in
this case one can call it like this:

``` js
// Assume that there is a SQL function like this:
//
// create function one_hour_ago() returns timestamp
//    return now() - 1 hour;

// note that you do not need to declare the output for functions
let func = "{ call one_hour_ago() }";

connection.call(func, (res, res_err) => {

  if (res.succeeded()) {
    let result = res.result();
  } else {
    // Failed!
  }
});
```

When working with Procedures you and still return values from your
procedures via its arguments, in the case you do not return anything the
usage is as follows:

``` js
// Assume that there is a SQL procedure like this:
//
// create procedure new_customer(firstname varchar(50), lastname varchar(50))
//   modifies sql data
//   insert into customers values (default, firstname, lastname, current_timestamp);

let func = "{ call new_customer(?, ?) }";

connection.callWithParams(func, [
  "John",
  "Doe"
], null, (res, res_err) => {

  if (res.succeeded()) {
    // Success!
  } else {
    // Failed!
  }
});
```

However you can also return values like this:

``` js
// Assume that there is a SQL procedure like this:
//
// create procedure customer_lastname(IN firstname varchar(50), OUT lastname varchar(50))
//   modifies sql data
//   select lastname into lastname from customers where firstname = firstname;

let func = "{ call customer_lastname(?, ?) }";

connection.callWithParams(func, [
  "John"
], [
  null,
  "VARCHAR"
], (res, res_err) => {

  if (res.succeeded()) {
    let result = res.result();
  } else {
    // Failed!
  }
});
```

Note that the index of the arguments matches the index of the `?` and
that the output parameters expect to be a String describing the type you
want to receive.

To avoid ambiguation the implementations are expected to follow the
following rules:

  - When a place holder in the `IN` array is `NOT NULL` it will be taken

  - When the `IN` value is NULL a check is performed on the OUT When the
    `OUT` value is not null it will be registered as a output parameter
    When the `OUT` is also null it is expected that the IN value is the
    `NULL` value.

The registered `OUT` parameters will be available as an array in the
result set under the output property.

## Batch operations

The SQL common interface also defines how to execute batch operations.
There are 3 types of batch operations:

  - Batched statements `batch`

  - Batched prepared statements `batchWithParams`

  - Batched callable statements `batchCallableWithParams`

A batches statement will exeucte a list of sql statements as for
example:

``` js
// Batch values
let batch = [];
batch.push("INSERT INTO emp (NAME) VALUES ('JOE')");
batch.push("INSERT INTO emp (NAME) VALUES ('JANE')");

connection.batch(batch, (res, res_err) => {
  if (res.succeeded()) {
    let result = res.result();
  } else {
    // Failed!
  }
});
```

While a prepared or callable statement batch will reuse the sql
statement and take an list of arguments as for example:

``` js
// Batch values
let batch = [];
batch.push([
  "joe"
]);
batch.push([
  "jane"
]);

connection.batchWithParams("INSERT INTO emp (name) VALUES (?)", batch, (res, res_err) => {
  if (res.succeeded()) {
    let result = res.result();
  } else {
    // Failed!
  }
});
```

## Executing other operations

To execute any other database operation, e.g. a `CREATE TABLE` you can
use `execute`.

The string is passed through without changes to the actual database. The
handler is called when the operation is complete

``` js
let sql = "CREATE TABLE PEOPLE (ID int generated by default as identity (start with 1 increment by 1) not null,FNAME varchar(255), LNAME varchar(255), SHOE_SIZE int);";

connection.execute(sql, (execute, execute_err) => {
  if (execute.succeeded()) {
    console.log("Table created !");
  } else {
    // Failed!
  }
});
```

## Multiple ResultSet responses

In some cases your query might return more than one result set, in this
case and to preserve the compatibility when the returned result set
object is converted to pure json, the next result sets are chained to
the current result set under the property `next`. A simple walk of all
result sets can be achieved like this:

``` js
// do something with the result set...

// next step
rs = rs.next;
;
```

## Streaming

When dealing with large data sets, it is not advised to use API just
described but to stream data since it avoids inflating the whole
response into memory and JSON and data is just processed on a row by row
basis, for example:

``` js
connection.queryStream("SELECT * FROM large_table", (stream, stream_err) => {
  if (stream.succeeded()) {
    stream.result().handler((row) => {
      // do something with the row...
    });
  }
});
```

You still have full control on when the stream is pauses, resumed and
ended. For cases where your query returns multiple result sets you
should use the result set ended event to fetch the next one if
available. If there is more data the stream handler will receive the new
data, otherwise the end handler is invoked.

``` js
connection.queryStream("SELECT * FROM large_table; SELECT * FROM other_table", (stream, stream_err) => {
  if (stream.succeeded()) {
    let sqlRowStream = stream.result();

    sqlRowStream.resultSetClosedHandler((v) => {
      // will ask to restart the stream with the new result set if any
      sqlRowStream.moreResults();
    }).handler((row) => {
      // do something with the row...
    }).endHandler((v) => {
      // no more data available...
    });
  }
});
```

## Using transactions

To use transactions first set auto-commit to false with `setAutoCommit`.

You then do your transactional operations and when you want to commit or
rollback use `commit` or `rollback`.

Once the commit/rollback is complete the handler will be called and the
next transaction will be automatically started.

``` js
// Do stuff with connection - updates etc

// Now commit

connection.commit((res, res_err) => {
  if (res.succeeded()) {
    // Committed OK!
  } else {
    // Failed!
  }
});
```

## Closing connections

When you’ve done with the connection you should return it to the pool
with `close`.
