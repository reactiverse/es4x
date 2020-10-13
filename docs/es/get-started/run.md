# Ejecuta

La aplicacion ES4X deberia crear un `es4x-launcher` durante la instalacion de `npm`. Si el lanzador no existe, ejecutar:

```bash
npm install # O yarn
```

::: Consejo
El lanzador deberia crearse en `node_modules/.bin/es4x-lancher.*`.
:::

Ejecutar tu aplicacion es tan sencillo como:

```bash
npm start # O yarn start
```

Este comando 
Este comando reemplaza la operación `npm` predeterminada ejecutando la aplicación en el tiempo de ejecución de JVM y usando el
proyecto *Hello World* deberia producir el siguiente resultado:

```bash
Server listening at: http://localhost:8080/
Succeeded in deploying verticle
```

Ahora puedes interactuar con la aplicacion con un navegador un un cliente http:

```bash
> curl localhost:8080

Hello from Vert.x Web!
```

## Ejecutando sin npm/yarn

Al implementar aplicaciones en produccion, puede parecer natural **no** incluir un administrador de paquetes con
tu aplicacion. Para este caso la aplicacion no utilizara `npm`/`yarn`. Ejecutar la aplicacion seria:

```bash
./node_modules/.bin/es4x-launcher
```

::: Consejo
Es posible personalizar el inicio de la aplicacion, comprueba:

```bash
./node_modules/.bin/es4x-launcher -help
```
:::

## Escalando el numero de vertices

Escalar el numero de vértices (que en algunos casos aumenta el rendimiento) se puede hacer como:

```bash
# numero de vertices:
N=2 \
  ./node_modules/.bin/es4x-launcher -instances $N
```

::: Consejo

En general, aumentar el numero de vertices hasta el doble del numero de nucleos proporciona el mejor rendimiento.
:::

## Clustering

Igual que con el numero de vertices, una aplicacion ES4X puede agrupar usando:

```bash
./node_modules/.bin/es4x-launcher -cluster
```

Para saber mas sobre agrupacion (clustering) por favor lee la documentacion oficial [vert.x](https://www.vertx.io).
