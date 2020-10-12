# Modulos EcmaScript

Los modulos EcmaScript son el formato **oficial** de modulo para el lenguaje JavaScript. `ESM` son compatibles con ES4X usando
una de estas dos opciones:

* El script inicial tiene la extension `.mjs`
* El script inicial tiene el prefijo: `mjs:`

## Script Inicial

A simple vista, el script inicial no es muy diferente del script commonjs, por ejemplo `index.mjs`:

```js
import { Router } from '@vertx/web';
import { someRoute } from './routes';

const app = Router.router(vertx);

app.route('/').handler(someRoute);

vertx.createHttpServer()
  .requestHandler(app)
  .listen(8080);
```

En este caso `someRoute` se importa desde el archivo `routes.mjs`:

```js
export function someRoute(ctx) {
  ctx.response()
    .end('Hola desde ES4X!');
}
```

## Compatibilidad

Por razones de compatibilidad, puede que hayas notado que la declaracion `import` en el script inicial no include la 
extension:

```js{2}
import { Router } from '@vertx/web';
import { someRoute } from './routes';

// ...
```

Esta es una minuscula divergencia de la especificacion original en la que el cargador de ES4X busca modulos asi:

1. Look up the exact file name: `./routes`
2. Look up with `.mjs` suffix: `./routes.mjs`
2. Look up with `.js` suffix: `./routes.js`

::: Advertencia
Cuando trabajas con `ESM` el `require()` no esta disponible!
:::

## Descargar Modulos

Descargar modulos durante durante la ejecucion (runtime) tambien es posible. Esta caracteristica no es especifica de `ES4X`. De hecho, solo 
depende del cargador oficial de `GraalJS`. Importar estos modules es tan simple como:

```js
import { VertxOptions } from 'https://unpkg.io/@vertx/core@3.9.1/mod.mjs';
```

Hay algunas reglas que debes conocer:

1. Los modulos **HTTP** no podran descargarse si no hay un [security manager](./security).
2. Si el modulo tiene un modulo `maven` de contrapartida, **NO** se descargara.
3. Descargar codigo ejecutable durante la ejecucion (runtime) puede ser un problema de seguridad.

Pueden existir casos en los que esto sea util, por ejemplo, para evitar depender de `npm` cuando el codigo no es publico.

::: Advertencia
Modulos descargados no procesaran ninguna dependencia o contrapartidas maven.
:::
