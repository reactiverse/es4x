# Ejemplos

## Hola Mundo

Como cualquier otra libreria, vamos a comenzar con un ejemplo hola mundo. El primer paso es crear el proyecto:

<<< @/docs/examples/hello-world/package.json

### Instala las dependencias necesarias

```bash
$ npm i

> hello-es4x@1.0.0 postinstall .../hello-world
> es4x install

npm notice created a lockfile as package-lock.json. You should commit this file.
added 2 packages from 1 contributor and audited 2 packages in 6.704s
found 0 vulnerabilities
```

::: warning
Si ves el mensaje `Installing GraalJS...`, significa que tu instalacion `java` en el sistema no es una instalacion GraalVM.
Esto es totalmente OK porque se descargan paquetes adicionales para asegurar el mejor rendimiento.
:::

::: danger
Si ves el mensaje `Current JDK only supports GraalJS in Interpreted mode!`, significa que tu instalacion `java` en la linea
de comandos es menor que 11 o `OpenJ9`.
:::

### Escribe tu codigo

Ahora que tu proyecto esta listo para ser usado, podemos escribir el codigo:

<<< @/docs/examples/hello-world/index.js

### Ejecutalo

```bash
$ npm start

> hello-es4x@1.0.0 start .../hello-world
> es4x

Hello ES4X
Succeeded in deploying verticle
```

::: danger
Si ves el mensaje `Current JDK only supports GraalJS in Interpreted mode!`, significa que tu instalacion `java` en la linea
de comandos es menor que 11 o `OpenJ9`.
:::

## Aplicacion Web

En este ejemplo vamos a creer una sencilla aplicacion web:

<<< @/docs/examples/web-application/package.json

### Instala las dependencias necesarias

```bash
$ npm i

> hello-es4x@1.0.0 postinstall .../hello-world
> es4x install

npm notice created a lockfile as package-lock.json. You should commit this file.
added 2 packages from 1 contributor and audited 2 packages in 6.704s
found 0 vulnerabilities
```

::: warning
Si ves el mensaje `Installing GraalJS...`, significa que tu instalacion `java` en el sistema no es una instalacion GraalVM.
Esto es totalmente OK porque se descargan paquetes adicionales para asegurar el mejor rendimiento.
:::

::: danger
Si ves el mensaje `Current JDK only supports GraalJS in Interpreted mode!`, significa que tu instalacion `java` en la linea
de comandos es menor que 11 o `OpenJ9`.
:::

### Escribe tu codigo

Ahora que tu proyecto esta listo para ser usado, podemos escribir el codigo:

<<< @/docs/examples/web-application/index.js

## Acceso Postgres

En este ejemplo vamos a crear una sencilla aplicacion de consulta Postgres:

<<< @/docs/examples/postgresql/package.json

### Instala las dependencias necesarias

```bash
$ npm i

> hello-es4x@1.0.0 postinstall .../hello-world
> es4x install

npm notice created a lockfile as package-lock.json. You should commit this file.
added 2 packages from 1 contributor and audited 2 packages in 6.704s
found 0 vulnerabilities
```

::: warning
Si ves el mensaje `Installing GraalJS...`, significa que tu instalacion `java` en el sistema no es una instalacion GraalVM.
Esto es totalmente OK porque se descargan paquetes adicionales para asegurar el mejor rendimiento.
:::

::: danger
Si ves el mensaje `Current JDK only supports GraalJS in Interpreted mode!`, significa que tu instalacion `java` en la linea
de comandos es menor que 11 o `OpenJ9`.
:::

### Escribe tu codigo

Ahora que tu proyecto esta listo para ser usado, podemos escribir el codigo:

<<< @/docs/examples/postgresql/index.js

## Mas ejemplos?

Se te gustaria ver mas ejemplos, ve a [vertx-examples](https://github.com/vert-x3/vertx-examples). Aunque los ejemplos estan
escritos en Java, siguiendo la guia [advanced](../advanced), veras como usar los APIs java es trivial.
