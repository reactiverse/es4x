# CommonJS Modules

[CommonJS](http://www.commonjs.org/) module loader, the `require()` function, is also available on ES4X. It is important
to know that this loader is not the same as `nodejs` loader. It is a fork of [npm-jvm](https://github.com/nodyn/jvm-npm)
that is ES4X specific.

## Specific differences

Modules can be loaded from the file system or from within `jar` files. The loading process always goes over the
[Vert.x FileSystem](https://vertx.io/docs/vertx-core/java/#_using_the_file_system_with_vert_x).

## ESM import syntax

Editors such as [Visual Studio Code](https://code.visualstudio.com/) prefers to auto complete import statement using ESM
syntax. Of course this syntax is not compatible with `commonjs`, however the loader will attempt to adapt the import
statements to `commonjs` if possible.

Consider the following example:

```js{1}
import { TestSuite } from '@vertx/unit';

const suite = TestSuite.create("the_test_suite");
// ...
suite.run();
```

This code is not compatible with `commonjs`, however the `require()` function will transform the source code to:

```js{1}
const TestSuite = require('@vertx/unit').TestSuite;

const suite = TestSuite.create("the_test_suite");
// ...
suite.run();
```

::: warning
Even though import statement will be adapted, exports are not. All exports must use the commonjs format:
```js
module.exports = { /* ... */ }
```
:::
