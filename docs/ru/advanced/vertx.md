# Eclipse Vert.x

Как уже должно быть ясно к данному моменту, ES4X использует Vert.x для чтения/записи, а также как стандартную модель
программирования. При этом ES4X предлагает несколько приятных улучшений по сравнению со стандартным
[Vert.x APIs](https://vertx.io).

## Сгенерированные API

Все API, опубликованные в `npm` под пространством имён `@vertx` и `@reactiverse`, генерируются кодом. Генерация кода
помогает использовать эти API пользователями `JavaScript` в знакомом им формате и без компромиссов с производительностью
приложения.

Все взаимодействия с JVM происходят через `Java`-объекты. Самый важный нюанс - правильно передать JVM класс в JS:

```js
// Импортируем java.lang.Math класс, чтобы использовать его
// как тип JS в скрипте
const Math = Java.type('javalang.Math');
```

Такой подход можно использовать для всех API, но есть несколько ограничений, которые ES4X пытается разрешить:

* **Подверженность ошибкам** - Необходимо точно знать Java API и необходимые типы для использования их в JavaScript.
* **Невозможность задать зависимости** - Если нужно использовать несколько API из разных модулей, импорт классов не
позволяет задать зависимости между ними.
* **Отсутствие поддержки IDE** - Разработчику нужно узнать API перед тем, как начать его использовать, и IDE в этом не
может помочь.

Генератор ES4X решает эти проблемы с помощью создания модуля `npm` для каждого модуля `vertx` и определений типов для
каждого класса.

Каждый модуль содержит следующие файлы:

* `package.json` - Определяет зависимости между модулями
* `index.js` - Интерфейсы commonjs API
* `index.mjs` - Интерфейсы ESM API
* `index.d.ts` - Полные определения типов для интерфейсов API
* `enum.js` - Перечисления commonjs API
* `enum.mjs` - Перечисления ESM API
* `enum.d.ts` - Полные определения типов для перечислений API
* `options.js` - Data objects для commonjs API
* `options.mjs` - Data objects для ESM API
* `options.d.ts` - Полные определения типов для Data Objects API

Все файлы `index` упрощают загрузку JVM классов с помощью замены, например:

```js
// без ES4X
const Router = Java.type('io.vertx.ext.web.Router');
// с ES4X
import { Router } from '@vertx/web';
```

Такая небольшая правка позволяет IDE помогать при разработке, а менеджерам пакетов - скачивать зависимости при
необходимости. Все файлы `.d.ts` подсказывают IDE типы и дают поддержку завершения кода.


## Promise/Future

В Vert.x есть 2 типа:

* `io.vertx.core.Future`
* `io.vertx.core.Promise`

Как ни странно, Vert.x `Promise` - это не то же самое, что JavaScript `Future`. Vert.x `Promise` - часть Vert.x `Future`
с доступом записи. В терминах JavaScript:

* Vert.x `Future` === JavaScript `Promise Like (Thenable)`
* Vert.x `Promise` === JavaScript `Executor Function`

## async/await

`async/await` поддерживается без каких-либо шагов компиляции `GraalVM`. ES4X добавляет новую возможность к типу Vert.x
`Future`. Те API, которые возвращают Vert.x `Future`, могут использоваться, как `Thenable`, а потому:

```js
// используем Java API
vertx.createHttpServer()
  .listen(0)
  .onSuccess(server => {
    console.log('Сервер готов!')
  })
  .onFailure(err => {
    console.log('Запуск сервера завершен неудачно!')
  });
```

Может использоваться, как `Thenable`:

```js
try {
  let server = await vertx
    .createHttpServer()
    .listen(0);

  console.log('Сервер готов!');
} catch (err) {
  console.log('Запуск сервера завершен неудачно!')
}
```

:::Совет
`async/await` работает даже с циклами, что сильно облегчает работу с асинхронным кодом, особенно при смешивании кода JS
и Java.
:::

## Приведение типов

Vert.x написан на `Java`, однако при работе с `JavaScript` пользователю не нужно так сильно заботиться о типах, как в
`Java`. ES4X выполняет некоторые приведения типов "из коробки":

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
