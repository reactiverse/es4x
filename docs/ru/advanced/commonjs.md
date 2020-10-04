# Модули CommonJS

Загрузчик модулей [CommonJS](http://www.commonjs.org/), функция `require()`, так же доступен для ES4X. Важно понимать, что этот загрузчик отличается от загрузчика `nodejs`. Это форк от [npm-jvm](https://github.com/nodyn/jvm-npm)
, специфичный для ES4X.

## Конкретные различия

Модули могут быть загружены из файловой системы или из содержимого `jar`-файлов. Процесс загрузки всегда идет через
[Vert.x FileSystem](https://vertx.io/docs/vertx-core/java/#_using_the_file_system_with_vert_x).

## Импорт синтаксиса ESM

Редакторы кода, такие как [Visual Studio Code](https://code.visualstudio.com/), предпочитают автоматически завершать выражения с помощью синтаксиса ESM. Разумеется, такой синтаксис не совместим с `commonjs`, однако загрузчик постарается переделать выражения импорта под `commonjs`, если это возможно.

Рассмотрим следующий пример:

```js{1}
import { TestSuite } from '@vertx/unit';

const suite = TestSuite.create("the_test_suite");
// ...
suite.run();
```

Этот код несовместим с  `commonjs`, однако функция `require()` изменит исходный код на следующий:

```js{1}
const TestSuite = require('@vertx/unit').TestSuite;

const suite = TestSuite.create("the_test_suite");
// ...
suite.run();
```

::: Внимание
Выражения импорта будут исправлены, но не выражения экспорта. Все выражения экспорта должны использовать формат commonjs:
```js
module.exports = { /* ... */ }
```
:::
