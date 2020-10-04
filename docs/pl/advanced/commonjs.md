# Moduły CommonJS

[CommonJS](http://www.commonjs.org/) module loader, funkcja `require()`, jest także dostępna na ES4X. Ważne jest żeby
wiedzieć, że ten loader nie jest tym samym co `nodejs` loader. Jest to wersja [npm-jvm](https://github.com/nodyn/jvm-npm)
przeznaczona dla ES4X.

## Różnice w specyfikacji

Moduły mogą być załadowane z systemu plików lub z plików `jar`. Proces ładowania zawsze przechodzi przez
[Vert.x FileSystem](https://vertx.io/docs/vertx-core/java/#_using_the_file_system_with_vert_x).

## ESM import syntax

Edytory takie jak [Visual Studio Code](https://code.visualstudio.com/) preferują autouzupełnianie poleceń importu z
wykorzystywniem składni ESM. Oczywiście ta składnia nie jest kompatybilna z `commonjs`, jednakże loader będzie próbował
zaadaptować polecenia importu do `commonjs` jeśli będzie to możliwe.

Przyjrzyj się poniższemu przykładowi:

```js{1}
import { TestSuite } from '@vertx/unit';

const suite = TestSuite.create("the_test_suite");
// ...
suite.run();
```

Ten kod nie jest kompatybilny z `commonjs`, jednak funkcja `require()` zmieni kod źródłowy na:

```js{1}
const TestSuite = require('@vertx/unit').TestSuite;

const suite = TestSuite.create("the_test_suite");
// ...
suite.run();
```

::: warning
Nawet jeśli polecenia importu zostaną zaadaptowane, to w przypadku exportu tak się nie stanie. Wszystkie polecenia
exportu muszą być zgodne z formatem:
```js
module.exports = { /* ... */ }
```
:::
