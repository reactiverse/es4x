# Using connections

When you need to execute sequential queries (without a transaction), you
can create a new connection or borrow one from the pool:

``` js
Code not translatable
```

Prepared queries can be created:

``` js
import { Tuple } from "@vertx/sql-client"
connection.prepare("SELECT * FROM users WHERE first_name LIKE ?", (ar1) => {
  if (ar1.succeeded()) {
    let prepared = ar1.result();
    prepared.query().execute(Tuple.of("julien"), (ar2) => {
      if (ar2.succeeded()) {
        // All rows
        let rows = ar2.result();
      }
    });
  }
});
```
