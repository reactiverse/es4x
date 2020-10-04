# Testowanie

Aby przetestować kod framework testowy powinien być dodany do projektu. Jako że vert.x zapewnia
[vert.x unit](https://github.com/vert-x3/vertx-unit), możemy łatwo dodać testy:

```bash
npm install @vertx/unit --save-dev # LUB yarn add -D @vertx/unit

# zapewnia że es4x pobierze dependencje inne niż npm
npm install # LUB yarn
```

## Pisanie testów

Pisanie testów powinno się odbywać przy zachowaniu tych samych zasad, co przy tworzeniu kodu w każdym innym programie
JavaScript. Ustaloną konwencją jest używanie sufixa do testowania kodu prosto z podstawowego skryptu.

Podczas pracy z `vert.x unit`, testy powinny być pogrupowane w `suity` i główna suita powinna rozpoczynać proces
testowania. Na przykład:

```js
import { TestSuite } from '@vertx/unit';

const suite = TestSuite.create("the_test_suite");

suite.test("my_test_case", function (context) {
  var s = "value";
  context.assertEquals("value", s);
});

suite.run();
```


## Puszczanie testów

```bash
> npm test
```

Ta komenda zamienia domyślną operację `npm` poprzez odpalenie aplikacji na JVM runtime.

```bash
Running: java ...
Begin test suite the_test_suite
Begin test my_test_case
Passed my_test_case
End test suite the_test_suite , run: 1, Failures: 0, Errors: 0
```

::: warning
Aby uruchomić testy za pomocą `npm`/`yarn` skrypt `test` musi być obecny w pliku `package.json`:

```json{4}
{
   ...
  "scripts" : {
    "test" : "es4x test index.test.js",
    ...
}
```
:::
