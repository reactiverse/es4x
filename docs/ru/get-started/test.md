# Тестирование

Чтобы провести тестирование, вашему проекту нужен фреймворк для тестирования. Так как vert.x предлагает
[vert.x unit](https://github.com/vert-x3/vertx-unit), мы можем его легко добавить:

```bash
npm install @vertx/unit --save-dev # OR yarn add -D @vertx/unit

# убедимся, что es4x получил не-npm зависимости
npm install # OR yarn
```

## Пишем тесты

Тесты пишутся также, как и любой другой код на JavaScript, обычно используется суффикс `.test.js` для теста кода из
главного скрипта.

Работая с `vert.x unit`, тесты должны быть организованы в `suites`, и главный suite используется для теста приложения,
например:

```js
import { TestSuite } from '@vertx/unit';

const suite = TestSuite.create("the_test_suite");

suite.test("my_test_case", function (context) {
  var s = "value";
  context.assertEquals("value", s);
});

suite.run();
```


## Запуск тестов

```bash
> npm test
```

Данная команда заменяет стандартную операцию `npm` запуском приложения на среде выполнения JVM.

```bash
Running: java ...
Begin test suite the_test_suite
Begin test my_test_case
Passed my_test_case
End test suite the_test_suite , run: 1, Failures: 0, Errors: 0
```

::: Внимание
Для запуска тестов с `npm`/`yarn` скрипт `test` должен быть объявлен в `package.json`:

```json{4}
{
   ...
  "scripts" : {
    "test" : "es4x test index.test.js",
    ...
}
```
:::
