# Json Pointers

Vert.x provides an implementation of [Json Pointers from
RFC6901](https://tools.ietf.org/html/rfc6901). You can use pointers both
for querying and for writing. You can build your `JsonPointer` using a
string, a URI or manually appending paths:

``` java
import { JsonPointer } from "@vertx/core"
// Build a pointer from a string
let pointer1 = JsonPointer.from("/hello/world");
// Build a pointer manually
let pointer2 = JsonPointer.create().append("hello").append("world");
```

After instantiating your pointer, use `queryJson` to query a JSON value.
You can update a Json Value using `writeJson`:

``` java
// Query a JsonObject
let result1 = objectPointer.queryJson(jsonObject);
// Query a JsonArray
let result2 = arrayPointer.queryJson(jsonArray);
// Write starting from a JsonObject
objectPointer.writeJson(jsonObject, "new element");
// Write starting from a JsonObject
arrayPointer.writeJson(jsonArray, "new element");
```

You can use Vert.x Json Pointer with any object model by providing a
custom implementation of `JsonPointerIterator`
