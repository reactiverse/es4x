# Test

Para probar el codigo se debe agregar un marco de prueba al proyecto, ya que vert.x provee
[vert.x unit](https://github.com/vert-x3/vertx-unit), lo podemos añadir facilmete con:

```bash
npm install @vertx/unit --save-dev # O yarn add -D @vertx/unit

# asegura que es4x consigue las dependencia que no son npm
npm install # OR yarn
```

## Escribiendo tests

Escribir tests deberia seguir las mismas reglas que el cualquier otro codigo JavaScript, una convencion comun es
utilizar el sufijo `.test.js` para probar el codigo desde el script base.

Cuando utilizes `vert.x unit`, los test deberian ser organizados en `suites` y la suite principal deberia usarse
para empezar el proceso de prueba. Por ejemplo:

```js
import { TestSuite } from '@vertx/unit';

const suite = TestSuite.create("the_test_suite");

suite.test("my_test_case", function (context) {
  var s = "value";
  context.assertEquals("value", s);
});

suite.run();
```


## Ejecutando tests

```bash
> npm test
```

Este comando reemplaza la operacion `npm` por defecto con la ejecucion de la aplicacion en el runtime de JVM.

```bash
Running: java ...
Begin test suite the_test_suite
Begin test my_test_case
Passed my_test_case
End test suite the_test_suite , run: 1, Failures: 0, Errors: 0
```

::: warning
Para ejecutar tests usando `npm`/`yarn` el script `test` debe estar presente en `package.json`:

```json{4}
{
   ...
  "scripts" : {
    "test" : "es4x test index.test.js",
    ...
}
```
:::
