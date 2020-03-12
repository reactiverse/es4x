# Using connections

When you need to execute sequential queries (without a transaction), you
can create a new connection or borrow one from the pool:

``` js
`usingConnections01`
```

Prepared queries can be created:

``` js
`usingConnections02`
```

> **Note**
> 
> prepared query caching depends on the `setCachePreparedStatements` and
> does not depend on whether you are creating prepared queries or use
> `direct prepared queries`

`PreparedQuery` can perform efficient batching:

``` js
`usingConnections03`
```
