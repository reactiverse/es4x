# Vert.x Interop

As it should be clear at this point, Vert.x is the IO and default programming model used by ES4X. There are however some
nice improvements to the standard [Vert.x APIs](https://vertx.io).

## Promise/Future

Vert.x has 2 types:

* `io.vertx.core.Future`
* `io.vertx.core.Promise`

Oddly enough, a Vert.x `Promise` is not the same as a JavaScript `Future`. A Vert.x `Promise` is the writable side of a
Vert.x `Future`. In JavaScript terms:

* Vert.x `Future` === JavaScript `Promise Like (Thenable)`
* Vert.x `Promise` === JavaScript `Executor Function`

## async/await

`async/await` is supported without any need for a compilation step by `GraalVM`. ES4X adds an extra feature to Vert.x
`Future` type. APIs that return a Vert.x `Future` can be used as a `Thenable`, this means that:

```js
// using the Java API
vertx.createHttpServer()
  .listen(0)
  .onSuccess(server => {
    console.log('Server ready!')
  })
  .onFailure(err => {
    console.log('Server startup failed!')
  });
```

Can be used as a `Thenable`:

```js
try {
  let server = await vertx
    .createHttpServer()
    .listen(0);

  console.log('Server Ready!');
} catch (err) {
  console.log('Server startup failed!')
}
```

:::tip
`async/await` works even with loops, which makes working with asynchronous code quite easy, even mixing JS and Java
code.
:::

## Type Conversions

Vert.x is coded in `Java`, however in `JavaScript` we don't need to worry about types as much as with `Java`. ES4X
performs some automated conversions out of the box:

| Java | TypeScript |
| :--- | ---------: |
| void | void |
| boolean | boolean |
| byte | number |
| short | number |
| int | number |
| long | number |
| float | number |
| double | number |
| char | string |
| boolean[] | boolean[] |
| byte[] | number[] |
| short[] | number[] |
| int[] | number[] |
| long[] | number[] |
| float[] | number[] |
| double[] | number[] |
| char[] | string[] |
| java.lang.Void | void |
| java.lang.Object | any |
| java.lang.Boolean | boolean |
| java.lang.Double | number |
| java.lang.Float | number |
| java.lang.Integer | number |
| java.lang.Long | number |
| java.lang.Short | number |
| java.lang.Char | string |
| java.lang.String | string |
| java.lang.CharSequence | string |
| java.lang.Boolean[] | boolean[] |
| java.lang.Double[] | number[] |
| java.lang.Float[] | number[] |
| java.lang.Integer[] | number[] |
| java.lang.Long[] | number[] |
| java.lang.Short[] | number[] |
| java.lang.Char[] | string[] |
| java.lang.String[] | string[] |
| java.lang.CharSequence[] | string[] |
| java.lang.Object[] | any[] |
| java.lang.Iterable | any[] |
| java.util.function.BiConsumer | &lt;T extends any, U extends any&gt;(arg0: T, arg1: U) =&gt; void |
| java.util.function.BiFunction | &lt;T extends any, U extends any, R extends any&gt;(arg0: T, arg1: U) =&gt; R |
| java.util.function.BinaryOperator | &lt;T extends any&gt;(arg0: T, arg1: T) =&gt; T |
| java.util.function.BiPredicate | &lt;T extends any, U extends any&gt;(arg0: T, arg1: U) =&gt; boolean |
| java.util.function.Consumer | &lt;T extends any&gt;(arg0: T) =&gt; void |
| java.util.function.Function | &lt;T extends any, R extends any&gt;(arg0: T) =&gt; R |
| java.util.function.Predicate | &lt;T extends any&gt;(arg0: T) =&gt; boolean |
| java.util.function.Supplier | &lt;T extends any&gt;() =&gt; T |
| java.util.function.UnaryOperator | &lt;T extends any&gt;(arg0: T) =&gt; T |
| java.time.Instant | Date |
| java.time.LocalDate | Date |
| java.time.LocalDateTime | Date |
| java.time.ZonedDateTime | Date |
| java.lang.Iterable&lt;T&gt; | &lt;T&gt;[] |
| java.util.Collection&lt;T&gt; | &lt;T&gt;[] |
| java.util.List&lt;T&gt; | &lt;T&gt;[] |
| java.util.Map&lt;K, V&gt; | { [key: &lt;K&gt;]: &lt;V&gt; } |
