# Cursors and streaming

By default prepared query execution fetches all rows, you can use a
`Cursor` to control the amount of rows you want to read:

``` js
import { Tuple } from "@vertx/sql-client"
connection.prepare("SELECT * FROM users WHERE age > ?", (ar1) => {
  if (ar1.succeeded()) {
    let pq = ar1.result();

    // Create a cursor
    let cursor = pq.cursor(Tuple.of(18));

    // Read 50 rows
    cursor.read(50, (ar2) => {
      if (ar2.succeeded()) {
        let rows = ar2.result();

        // Check for more ?
        if (cursor.hasMore()) {
          // Repeat the process...
        } else {
          // No more rows - close the cursor
          cursor.close();
        }
      }
    });
  }
});
```

Cursors shall be closed when they are released prematurely:

``` js
cursor.read(50, (ar2) => {
  if (ar2.succeeded()) {
    // Close the cursor
    cursor.close();
  }
});
```

A stream API is also available for cursors, which can be more
convenient, specially with the Rxified version.

``` js
import { Tuple } from "@vertx/sql-client"
connection.prepare("SELECT * FROM users WHERE age > ?", (ar1) => {
  if (ar1.succeeded()) {
    let pq = ar1.result();

    // Fetch 50 rows at a time
    let stream = pq.createStream(50, Tuple.of(18));

    // Use the stream
    stream.exceptionHandler((err) => {
      console.log("Error: " + err.getMessage());
    });
    stream.endHandler((v) => {
      console.log("End of stream");
    });
    stream.handler((row) => {
      console.log("User: " + row.getString("last_name"));
    });
  }
});
```

The stream read the rows by batch of `50` and stream them, when the rows
have been passed to the handler, a new batch of `50` is read and so on.

The stream can be resumed or paused, the loaded rows will remain in
memory until they are delivered and the cursor will stop iterating.
