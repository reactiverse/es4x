# Using transactions

## Transactions with connections

You can execute transaction using SQL `BEGIN`/`COMMIT`/`ROLLBACK`, if
you do so you must use a `SqlConnection` and manage it yourself.

Or you can use the transaction API of `SqlConnection`:

``` js
`transaction01`
```

When PostgreSQL reports the current transaction is failed (e.g the
infamous *current transaction is aborted, commands ignored until end of
transaction block*), the transaction is rollbacked and the
`abortHandler` is called:

``` js
`transaction02`
```

## Simplified transaction API

When you use a pool, you can start a transaction directly on the pool.

It borrows a connection from the pool, begins the transaction and
releases the connection to the pool when the transaction ends.

``` js
`transaction03`
```

> **Note**
> 
> this code will not close the connection because it will always be
> released back to the pool when the transaction
