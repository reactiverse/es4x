# JARs

ES4X se ejecuta en una JVM, asi que añadir o utilizar `jar` de Maven Central es simple. Esta caracteristica es util, por ejemplo
cuando necesitamos añadir bibliotecas en tiempo de ejecucion (runtime) que no tiene una version `npm`, o son librerias de apoyo.
Como ejemplo, podemos en algunos casos mejorar el rendimiento IO en `vert.x` añadiendo
[native-transports](https://netty.io/wiki/native-transports.html) to the runtime.

```json
{
  "name": "benchmark",
  "version": "0.12.0",
  "private": true,
  "main": "index.js",
  "dependencies": {
    "@vertx/core": "3.9.2"
  },
  "mvnDependencies": [
    "io.netty:netty-transport-native-epoll:jar:linux-x86_64:4.1.49.Final"
  ]
}
```

Al añadir el array `mvnDependencies` en el paquete json, esas dependencias son añadidas a la ejecucion de la aplicacion.
El formato es el usual para maven:

```
group:artifact[:type][:classifier]:version
```

* **group** the organization that owns the module
* **artifact** the module per se
* **type** optional file type
* **classifier** optional classifier, this allows having distinct modules for specific roles
* **version** the module version




