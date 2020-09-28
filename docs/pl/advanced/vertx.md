# Eclipse Vert.x

Vert.x jest domyślnym modelem programowania używanym przez ES4X. Jednak w tym wydaniu jest tu kilka usprawnień, w
porównaniu do standardowego [Vert.x APIs](https://vertx.io).

## Wygenerowane API

Wszystkie API publikowane w `npm` pod nazwami `@vertx` i `@reactiverse` są kodem wygenerowanym. Generacja kodu jest
pozwala na to, żeby wszystkie takie API mogły być używane przez użytkowników `JavaScript` w takim formacie, jaki jest
dla nich wygodny bez wpływu na wydajność aplikacji.

Interakcja z JVM dzieje się w obiektach `Javy`. Najważniejszą częścią jest wyciągnięcie klasy JVM do JS:

```js
// Import the java.lang.Math class to be usable
// as a JS type in the script
const Math = Java.type('javalang.Math');
```

Teraz można tak zrobić dla wszystkich API, jakie chcemy, jednak istnieje kilka ograniczeń, które ES4X próbuje rozwiązać:

* **Podatność na usterki** - Korzystający musi dokładnie znać Java API i Typ, aby poprawnie użyć ich w JavaScript.
* **Brak możliwości zdefiniowania zależności** - Jeśli potrzebujesz użyć API z innych modułów, importowanie klasy za
klasą nie może definiować zależności między nimi.
* **Brak wsparcia IDE** - Developer musi znać API zanim zacznie z niego korzystać, ponieważ IDE mu w tym nie pomoże.

Generator ES4X rozwiązuje problemy poprzez stworzenie modułu `npm` dla każdego modułu `vertx` i wypisuje definicje dla
każdej klasy.

Każdy moduł ma następujące pliki:

* `package.json` - Definiuje zależności między modułami
* `index.js` - interfejsy commonjs API
* `index.mjs` - interfejsy ESM API
* `index.d.ts` - Pełne definicje typów dla interfejsów API
* `enum.js` - wyliczenia commonjs API
* `enum.mjs` - wyliczenia ESM API
* `enum.d.ts` - Pełne definicje typów dla wyliczeń API
* `options.js` - obiekty danych commonjs API.
* `options.mjs` - obiekty danych ESM API.
* `options.d.ts` - Pełne definicje typów dla obiektów danych API

Wszystkie pliki `index` będą uproszczały import klas JVM poprzez zamianę, np:

```js
// without ES4X
const Router = Java.type('io.vertx.ext.web.Router');
// with
import { Router } from '@vertx/web';
```

Ta mała zmiana spowoduje, że IDE będą asystować w developmencie, jak również menadżerom pakietów w pobieraniu
potrzebnych dependencji. Wszystkie pliki `.d.ts` będą podpowiadać IDE w kwestii typów oraz będą wspierały proces
uzupełniania kodu.

## Promise/Future

Vert.x ma 2 typy:

* `io.vertx.core.Future`
* `io.vertx.core.Promise`

Co dziwne, `Promise` z Vert.x nie jest tym samym co `Future` z JavaScript. W języku JavaScript:

* Vert.x `Future` === JavaScript `Promise Like (Thenable)`
* Vert.x `Promise` === JavaScript `Executor Function`

## async/await

`async/await` jest wspierany bez żadnej konieczności kompilacji ze strony `GraalVM`. ES4X dodaje extra funkcjonalność do
typu `Future` Vert.x. API, które zwracają Vert.x `Future` mogą być użyte jako `Thenable`, to znaczy że:

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

Może zostać użyte jako `Thenable`:

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
`async/await` działa nawet z pętlami, co powoduje, że praca z asynchronicznym kodem staje się prostsza, nawet podczas
mieszania kodu Javy i JavaScript.
:::

## Konwersja typów

Vert.x jest stworzony w `Javie`, jednak w `JavaScript` nie musimy się martwić w takim stopniu jak tam. ES4X wykonuje
kilka automatycznych konwersji:

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
