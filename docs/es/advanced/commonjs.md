# Modulos CommonJS 

[CommonJS](http://www.commonjs.org/) cargador de modulos, la funcion `require()`, tambien esta disponible en ES4X. Es importante
saber que este cargador no es lo mismo que el cargador `nodejs`. Es una bifurcacion (fork) de [npm-jvm](https://github.com/nodyn/jvm-npm)
que es especifica a ES4X.

## Diferencias especificas

Los modulos pueden ser cargados desde el sistema de archivos o desde dentro de archivos `jar`. El proceso de cargado siempre va sobre 
[Vert.x FileSystem](https://vertx.io/docs/vertx-core/java/#_using_the_file_system_with_vert_x).

## Sintaxis ESM para importar

Editores como [Visual Studio Code](https://code.visualstudio.com/) prefieren auto completar las declaraciones de importancion usando la sintaxis ESM.
Por supuesto esta sintaxis no es compatible con `commonjs`, sin embargo el cargador intentara adaptar las declaraciones de importancion 
a `commonjs` cuando sea posible.

Considera el siguiente ejemplo:

```js{1}
import { TestSuite } from '@vertx/unit';

const suite = TestSuite.create("the_test_suite");
// ...
suite.run();
```

Este codigo no es compatible con `commonjs`, sin embargo la funcion `require()` transformara el codigo fuente a:

```js{1}
const TestSuite = require('@vertx/unit').TestSuite;

const suite = TestSuite.create("the_test_suite");
// ...
suite.run();
```

::: Aviso
Aunque las declaraciones de importancion seran adaptadas, las exportaciones no lo son. Todas las exportaciones deben usar el formato commonjs:
```js
module.exports = { /* ... */ }
```
:::
