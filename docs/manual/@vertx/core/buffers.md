# Buffers

Most data is shuffled around inside Vert.x using buffers.

A buffer is a sequence of zero or more bytes that can read from or
written to and which expands automatically as necessary to accommodate
any bytes written to it. You can perhaps think of a buffer as smart byte
array.

## Creating buffers

Buffers can create by using one of the static `Buffer.buffer` methods.

Buffers can be initialised from strings or byte arrays, or empty buffers
can be created.

Here are some examples of creating buffers:

Create a new empty buffer:

``` js
import { Buffer } from "@vertx/core"
let buff = Buffer.buffer();
```

Create a buffer from a String. The String will be encoded in the buffer
using UTF-8.

``` js
import { Buffer } from "@vertx/core"
let buff = Buffer.buffer("some string");
```

Create a buffer from a String: The String will be encoded using the
specified encoding, e.g:

``` js
import { Buffer } from "@vertx/core"
let buff = Buffer.buffer("some string", "UTF-16");
```

Create a buffer from a byte\[\]

``` java
{@link docoverride.buffer.Examples#example4}
```

Create a buffer with an initial size hint. If you know your buffer will
have a certain amount of data written to it you can create the buffer
and specify this size. This makes the buffer initially allocate that
much memory and is more efficient than the buffer automatically resizing
multiple times as data is written to it.

Note that buffers created this way **are empty**. It does not create a
buffer filled with zeros up to the specified size.

``` js
import { Buffer } from "@vertx/core"
let buff = Buffer.buffer(10000);
```

## Writing to a Buffer

There are two ways to write to a buffer: appending, and random access.
In either case buffers will always expand automatically to encompass the
bytes. It’s not possible to get an `IndexOutOfBoundsException` with a
buffer.

### Appending to a Buffer

To append to a buffer, you use the `appendXXX` methods. Append methods
exist for appending various different types.

The return value of the `appendXXX` methods is the buffer itself, so
these can be chained:

``` js
import { Buffer } from "@vertx/core"
let buff = Buffer.buffer();

buff.appendInt(123).appendString("hello\n");

socket.write(buff);
```

### Random access buffer writes

You can also write into the buffer at a specific index, by using the
`setXXX` methods. Set methods exist for various different data types.
All the set methods take an index as the first argument - this
represents the position in the buffer where to start writing the data.

The buffer will always expand as necessary to accommodate the data.

``` js
import { Buffer } from "@vertx/core"
let buff = Buffer.buffer();

buff.setInt(1000, 123);
buff.setString(0, "hello");
```

## Reading from a Buffer

Data is read from a buffer using the `getXXX` methods. Get methods exist
for various datatypes. The first argument to these methods is an index
in the buffer from where to get the data.

``` js
import { Buffer } from "@vertx/core"
let buff = Buffer.buffer();
for (let i = 0; i < buff.length(); 4) {
  console.log("int value at " + i + " is " + buff.getInt(i));
}
```

## Working with unsigned numbers

Unsigned numbers can be read from or appended/set to a buffer with the
`getUnsignedXXX`, `appendUnsignedXXX` and `setUnsignedXXX` methods. This
is useful when implementing a codec for a network protocol optimized to
minimize bandwidth consumption.

In the following example, value 200 is set at specified position with
just one byte:

``` js
import { Buffer } from "@vertx/core"
let buff = Buffer.buffer(128);
let pos = 15;
buff.setUnsignedByte(pos, 200);
console.log(buff.getUnsignedByte(pos));
```

The console shows '200'.

## Buffer length

Use `length` to obtain the length of the buffer. The length of a buffer
is the index of the byte in the buffer with the largest index + 1.

## Copying buffers

Use `copy` to make a copy of the buffer

## Slicing buffers

A sliced buffer is a new buffer which backs onto the original buffer,
i.e. it does not copy the underlying data. Use `slice` to create a
sliced buffers

## Buffer re-use

After writing a buffer to a socket or other similar place, they cannot
be re-used.
