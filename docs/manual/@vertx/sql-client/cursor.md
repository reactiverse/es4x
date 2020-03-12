# Cursors and streaming

By default prepared query execution fetches all rows, you can use a
`Cursor` to control the amount of rows you want to read:

``` js
`usingCursors01`
```

Cursors shall be closed when they are released prematurely:

``` js
`usingCursors02`
```

A stream API is also available for cursors, which can be more
convenient, specially with the Rxified version.

``` js
`usingCursors03`
```

The stream read the rows by batch of `50` and stream them, when the rows
have been passed to the handler, a new batch of `50` is read and so on.

The stream can be resumed or paused, the loaded rows will remain in
memory until they are delivered and the cursor will stop iterating.
