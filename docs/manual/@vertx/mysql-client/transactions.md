# Using transactions

## Transactions with connections

You can execute transaction using SQL `BEGIN`/`COMMIT`/`ROLLBACK`, if
you do so you must use a `SqlConnection` and manage it yourself.

Or you can use the transaction API of `SqlConnection`:

``` js
pool.getConnection((res) => {
  if (res.succeeded()) {

    // Transaction must use a connection
    let conn = res.result();

    // Begin the transaction
    let tx = conn.begin();

    // Various statements
    conn.query("INSERT INTO Users (first_name,last_name) VALUES ('Julien','Viet')").execute((ar1) => {
      if (ar1.succeeded()) {
        conn.query("INSERT INTO Users (first_name,last_name) VALUES ('Emad','Alblueshi')").execute((ar2) => {
          if (ar2.succeeded()) {
            // Commit the transaction
            tx.commit((ar3) => {
              if (ar3.succeeded()) {
                console.log("Transaction succeeded");
              } else {
                console.log("Transaction failed " + ar3.cause().getMessage());
              }
              // Return the connection to the pool
              conn.close();
            });
          } else {
            // Return the connection to the pool
            conn.close();
          }
        });
      } else {
        // Return the connection to the pool
        conn.close();
      }
    });
  }
});
```

When the database server reports the current transaction is failed (e.g
the infamous *current transaction is aborted, commands ignored until end
of transaction block*), the transaction is rollbacked and the
`abortHandler` is called:

``` js
tx.abortHandler((v) => {
  console.log("Transaction failed => rollbacked");
});
```

## Simplified transaction API

When you use a pool, you can start a transaction directly on the pool.

It borrows a connection from the pool, begins the transaction and
releases the connection to the pool when the transaction ends.

``` js
// Acquire a transaction and begin the transaction
pool.begin((res) => {
  if (res.succeeded()) {

    // Get the transaction
    let tx = res.result();

    // Various statements
    tx.query("INSERT INTO Users (first_name,last_name) VALUES ('Julien','Viet')").execute((ar1) => {
      if (ar1.succeeded()) {
        tx.query("INSERT INTO Users (first_name,last_name) VALUES ('Emad','Alblueshi')").execute((ar2) => {
          if (ar2.succeeded()) {
            // Commit the transaction
            // the connection will automatically return to the pool
            tx.commit((ar3) => {
              if (ar3.succeeded()) {
                console.log("Transaction succeeded");
              } else {
                console.log("Transaction failed " + ar3.cause().getMessage());
              }
            });
          }
        });
      } else {
        // No need to close connection as transaction will abort and be returned to the pool
      }
    });
  }
});
```

> **Note**
> 
> this code will not close the connection because it will always be
> released back to the pool when the transaction
