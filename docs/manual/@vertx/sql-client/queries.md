# Running queries

When you don’t need a transaction or run single queries, you can run
queries directly on the pool; the pool will use one of its connection to
run the query and return the result to you.

Here is how to run simple queries:

``` js
`queries01`
```

## Prepared queries

You can do the same with prepared queries.

The SQL string can refer to parameters by position, using the database
syntax {PREPARED\_PARAMS}

``` js
`queries02`
```

Query methods provides an asynchronous `RowSet` instance that works for
*SELECT* queries

``` js
`queries03`
```

or *UPDATE*/*INSERT* queries:

``` js
`queries04`
```

The `Row` gives you access to your data by index

``` js
`queries05`
```

or by name

``` js
`queries06`
```

The client will not do any magic here and the column name is identified
with the name in the table regardless of how your SQL text is.

You can access a wide variety of of types

``` js
`queries07`
```

You can cache prepared queries:

``` js
`queries09`
```

## Batches

You can execute prepared batch

``` js
`queries08`
```
