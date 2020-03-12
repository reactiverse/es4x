# Record Parser

The record parser allows you to easily parse protocols which are
delimited by a sequence of bytes, or fixed size records. It transforms a
sequence of input buffer to a sequence of buffer structured as
configured (either fixed size or separated records).

For example, if you have a simple ASCII text protocol delimited by '\\n'
and the input is the following:

    buffer1:HELLO\nHOW ARE Y
    buffer2:OU?\nI AM
    buffer3: DOING OK
    buffer4:\n

The record parser would produce

    buffer1:HELLO
    buffer2:HOW ARE YOU?
    buffer3:I AM DOING OK

Let’s see the associated code:

``` js
import { RecordParser } from "@vertx/core"
import { Buffer } from "@vertx/core"
let parser = RecordParser.newDelimited("\n", (h) => {
  console.log(h.toString());
});

parser.handle(Buffer.buffer("HELLO\nHOW ARE Y"));
parser.handle(Buffer.buffer("OU?\nI AM"));
parser.handle(Buffer.buffer("DOING OK"));
parser.handle(Buffer.buffer("\n"));
```

You can also produce fixed sized chunks as follows:

``` js
import { RecordParser } from "@vertx/core"
RecordParser.newFixed(4, (h) => {
  console.log(h.toString());
});
```

For more details, check out the `RecordParser` class.

# Json Parser

You can easily parse JSON structures but that requires to provide the
JSON content at once, but it may not be convenient when you need to
parse very large structures.

The non-blocking JSON parser is an event driven parser able to deal with
very large structures. It transforms a sequence of input buffer to a
sequence of JSON parse events.

``` js
Code not translatable
```

The parser is non-blocking and emitted events are driven by the input
buffers.

``` js
import { JsonParser } from "@vertx/core"
import { Buffer } from "@vertx/core"

let parser = JsonParser.newParser();

// start array event
// start object event
// "firstName":"Bob" event
parser.handle(Buffer.buffer("[{\"firstName\":\"Bob\","));

// "lastName":"Morane" event
// end object event
parser.handle(Buffer.buffer("\"lastName\":\"Morane\"},"));

// start object event
// "firstName":"Luke" event
// "lastName":"Lucky" event
// end object event
parser.handle(Buffer.buffer("{\"firstName\":\"Luke\",\"lastName\":\"Lucky\"}"));

// end array event
parser.handle(Buffer.buffer("]"));

// Always call end
parser.end();
```

Event driven parsing provides more control but comes at the price of
dealing with fine grained events, which can be inconvenient sometimes.
The JSON parser allows you to handle JSON structures as values when it
is desired:

``` js
Code not translatable
```

The value mode can be set and unset during the parsing allowing you to
switch between fine grained events or JSON object value events.

``` js
Code not translatable
```

You can do the same with arrays as well

``` js
Code not translatable
```

You can also decode POJOs

``` js
parser.handler((event) => {
  // Handle each object
  // Get the field in which this object was parsed
  let id = event.fieldName();
  let user = event.mapTo(Java.type("examples.ParseToolsExamples.User").class);
  console.log("User with id " + id + " : " + user.firstName + " " + user.lastName);
});
```

Whenever the parser fails to process a buffer, an exception will be
thrown unless you set an exception handler:

``` js
import { JsonParser } from "@vertx/core"

let parser = JsonParser.newParser();

parser.exceptionHandler((err) => {
  // Catch any parsing or decoding error
});
```

The parser also parses json streams:

  - concatenated json streams: `{"temperature":30}{"temperature":50}`

  - line delimited json streams: `{"an":"object"}\r\n3\r\n"a
    string"\r\nnull`

For more details, check out the `JsonParser` class.
