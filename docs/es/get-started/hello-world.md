# Hola Mundo

La aplicacion mas sencilla que podemos crear es un 'Hola mundo' en el archivo `hello-es4x.js`:

```js
vertx.createHttpServer()
  .requestHandler(req => {
    req.response()
      .end('Hola mundo ES4X!');
  })
  .listen(8080);
```

Ahora puedes ejecutar esta aplicacion usando:

```bash
$ es4x hello-es4x.js
```

::: Consejo
En sistemas UNIX, los scripts se pueden hacer ejecutables y puedes usar `#!/usr/bin/env es4x` para hacerlos
auto ejecutables. Recuerda de todos modos que las dependencias deberian estar listas en el directorio de trabajo.
:::

En una segunda terminal:

```bash
$ curl localhost:8080
Hola mundo ES4X!
```

::: Aviso
Ejecutar scripts utilizando el comando `es4x` directamente puede ser util para scripts basicos que no tienen dependencias
a parte de `vertx`. Para aplicaciones mas complejas se deberia utilizar un proyecto y un gestor de paquetes.
:::

## Crea un proyecto nuevo

ES4X utiliza `npm` como la herramienta de gestion del proyecto, para crear un proyecto nuevo se provee el siguiente comando:

```bash
# Crea un directorio del proyecto
mkdir myapp

# Cambia al directorio del proyecto
cd myapp

# Crea el proyecto
es4x project
```

Un proyecto es un archivo `package.json` con algunas cosas pre-configuradas:

```json{7-9,11-17}
{
  "version" : "1.0.0",
  "description" : "This is a ES4X empty project.",
  "name" : "myapp",
  "main" : "index.js",
  "scripts" : {
    "test" : "es4x test index.test.js",
    "postinstall" : "es4x install",
    "start" : "es4x"
  },
  "dependencies": {
    "@es4x/create": "latest",
    "@vertx/unit": "latest"
  },
  "dependencies": {
    "@vertx/core": "latest"
  },
  "keywords" : [ ],
  "author" : "",
  "license" : "ISC"
}
```

::: Consejo
Para proyectos `TypeScript`, ejecuta la herramienta para crear proyectos con: `es4x project --ts`
:::

El gancho `post-install` delegara en es4x para resolver todos las dependencias `maven` y crear el script `es4x-launcher`

::: Consejo
El script `es4x-launcher` se asegurara de que la aplicacion se ejecuta utilizando es4x y no `nodejs`. Este script puede
utilizarse en produccion, donde se puede evitar el paquete `@es4x/create`.
:::

### create-vertx-app

Con `create-vertx-app` puedes arrancar rapidamente tu aplicacion ES4X TypeScript o JavaScript con unas cuantas teclas.
Si GUI es la forma preferida de crear aplicaciones, el mismo generador puede ser utilizado como
 [PWA](https://vertx-starter.jetdrone.xyz/#npm).

<asciinema :src="$withBase('/cast/es4x-ts.cast')" cols="80" rows="24" />

## Añadir dependencias

Añadir dependencias no es diferente de como los desarrolladores `JavaScript` estan acostumbrados:

```bash
# añade otras dependencias...
npm install @vertx/unit --save-dev # O yarn add -D @vertx/unit
npm install @vertx/web --save-prod # O yarn add @vertx/web

# Activara la descarga de dependencias npm + java
npm install
```

## Coding


Con la configuracion del proyecto completa, es hora de escribir algo de codigo. Como se describio antes, ES4X utiliza definiciones
`TypeScript` para mejorar la experiencia de desarrollo con codigo auto completado y comprobaciones de codigo opcionales.

Con todas las aplicaciones ES4X hay un objeto global `vertx` que es la instancia configurada de *vert.x* que se utiliza
en la aplicacion.

::: Consejo
Para conseguir codigo auto completado en [Visual Studio Code](https://code.visualstudio.com/) la primera linea en tu script principal
deberia ser:

```js
/// <reference types="es4x" />
```
:::

La aplicacion hello `index.js` deberia ser:

```js{1-2}
/// <reference types="es4x" />
// @ts-check
import { Router } from '@vertx/web';

const app = Router.router(vertx);

app.route('/').handler(ctx => {
  ctx.response()
    .end('Saludos desde Vert.x Web!');
});

vertx.createHttpServer()
  .requestHandler(app)
  .listen(8080);

console.log('Server escuchando: http://localhost:8080/')
```

Esta app arranca el servidor y escucha en el puerto 8080 para las conexiones. El app responde con "`Saludos desde Vert.x Web!`" 
para las peticiones en la URL principal (`/`) o ruta. Para cualquier otro camino, respondera con un **404 Not Found**.

::: Advertencia
La sintaxis del modulo ES6 puede ser utilizada en archivos `.js`. ES4X los traduce a declaraciones `commonjs` `require()`
sin embrago `exports` no seran traducidos. Esta funcion es solo para ayudar a trabajar con IDEs que pueden auto importar 
tal como `Visual Studio Code`.
:::

## Soporte MJS

ES4X tiene archivos `.mjs` disponibles. Es este caso la resolucion de los modulos no utilizara `commonjs` `require()` pero
usara el cargador nativo de modulos de graaljs.

Con soporte graaljs para `.mjs` tanto `import` como `export` funcionaran como han sido diseñados en la especificacion ES6.

::: Consejo
Para permitir soporte `.mjs` utiliza la extension `.mjs` en tus archivos `JavaScript, o arranca tu aplicacion con `-Desm`.
:::

::: Advertencia
No es posible mezclar `commonjs` con `esm` en el mismo proyecto. Si no estas seguro, usa `commonjs`.
:::
