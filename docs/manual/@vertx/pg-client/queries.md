# Running queries

When you don’t need a transaction or run single queries, you can run
queries directly on the pool; the pool will use one of its connection to
run the query and return the result to you.

Here is how to run simple queries:

``` js
client.query("SELECT * FROM users WHERE id='julien'").execute((ar) => {
  if (ar.succeeded()) {
    let result = ar.result();
    console.log("Got " + result.size() + " rows ");
  } else {
    console.log("Failure: " + ar.cause().getMessage());
  }
});
```

## Prepared queries

You can do the same with prepared queries.

The SQL string can refer to parameters by position, using the database
syntax {PREPARED\_PARAMS}

``` js
import { Tuple } from "@vertx/sql-client"
client.preparedQuery("SELECT * FROM users WHERE id=$1").execute(Tuple.of("julien"), (ar) => {
  if (ar.succeeded()) {
    let rows = ar.result();
    console.log("Got " + rows.size() + " rows ");
  } else {
    console.log("Failure: " + ar.cause().getMessage());
  }
});
```

Query methods provides an asynchronous `RowSet` instance that works for
*SELECT* queries

``` js
client.preparedQuery("SELECT first_name, last_name FROM users").execute((ar) => {
  if (ar.succeeded()) {
    let rows = ar.result();
    rows.forEach(row => {
      console.log("User " + row.getString(0) + " " + row.getString(1));
    });
  } else {
    console.log("Failure: " + ar.cause().getMessage());
  }
});
```

or *UPDATE*/*INSERT* queries:

``` js
import { Tuple } from "@vertx/sql-client"
client.preparedQuery("INSERT INTO users (first_name, last_name) VALUES ($1, $2)").execute(Tuple.of("Julien", "Viet"), (ar) => {
  if (ar.succeeded()) {
    let rows = ar.result();
    console.log(rows.rowCount());
  } else {
    console.log("Failure: " + ar.cause().getMessage());
  }
});
```

The `Row` gives you access to your data by index

``` js
console.log("User " + row.getString(0) + " " + row.getString(1));
```

or by name

``` js
console.log("User " + row.getString("first_name") + " " + row.getString("last_name"));
```

The client will not do any magic here and the column name is identified
with the name in the table regardless of how your SQL text is.

You can access a wide variety of of types

``` js
let firstName = row.getString("first_name");
let male = row.getBoolean("male");
let age = row.getInteger("age");

// ...
```

You can use cached prepared statements to execute one-shot prepared
queries:

``` js
import { Tuple } from "@vertx/sql-client"

// Enable prepare statements caching
connectOptions.cachePreparedStatements = true;
client.preparedQuery("SELECT * FROM users WHERE id = $1").execute(Tuple.of("julien"), (ar) => {
  if (ar.succeeded()) {
    let rows = ar.result();
    console.log("Got " + rows.size() + " rows ");
  } else {
    console.log("Failure: " + ar.cause().getMessage());
  }
});
```

You can create a `PreparedStatement` and manage the lifecycle by
yourself.

``` js
import { Tuple } from "@vertx/sql-client"
sqlConnection.prepare("SELECT * FROM users WHERE id = $1", (ar) => {
  if (ar.succeeded()) {
    let preparedStatement = ar.result();
    preparedStatement.query().execute(Tuple.of("julien"), (ar2) => {
      if (ar2.succeeded()) {
        let rows = ar2.result();
        console.log("Got " + rows.size() + " rows ");
        preparedStatement.close();
      } else {
        console.log("Failure: " + ar2.cause().getMessage());
      }
    });
  } else {
    console.log("Failure: " + ar.cause().getMessage());
  }
});
```

## Batches

You can execute prepared batch

``` js
import { Tuple } from "@vertx/sql-client"

// Add commands to the batch
let batch = [];
batch.push(Tuple.of("julien", "Julien Viet"));
batch.push(Tuple.of("emad", "Emad Alblueshi"));

// Execute the prepared batch
client.preparedQuery("INSERT INTO USERS (id, name) VALUES ($1, $2)").executeBatch(batch, (res) => {
  if (res.succeeded()) {

    // Process rows
    let rows = res.result();
  } else {
    console.log("Batch failed " + res.cause());
  }
});
```
