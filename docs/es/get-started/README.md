# Introduccion

ES4X es un pequeño runtime para aplicaciones EcmaScript >= 5 que se ejecuta en [graaljs](https://github.com/graalvm/graaljs)
con la ayuda de [vert.x](https://vertx.io). JavaScript es el lenguaje runtime pero **no** hace uso de `nodejs`.

## Como funciona

Desarrollar aplicaciones ES4X no es diferente de desarollar otras aplicaciones `JavaScript`. El archivo `package.json`
define los proyectos. Un proyecto utilizara y conseguira dependencias de 2 fuentes diferentes:

* [npm](https://www.npmjs.com/)
* **y** [maven central](https://search.maven.org/)

ES4X utiliza [GraalVM](https://www.graalvm.org) que es un runtime poliglota en JVM. Esto significa que es posible usar
cualquier lenguaje JVM y tambien `JavaScript` en las aplicaciones.

Vert.x es usado en ES4X para proveer un bucle de eventos optimizado y una libreria IO de alto rendimiento. Utilizar `Java`
desde `JavaScript` puede ser tedioso porque no hay de que los IDEs infieran informacion de tipo o APIs. Por esta razon
ES4X tiene algunos paquetes publicados en `npm` que hacen el desarrollo mas sencillo al hacer disponibles un pequeño `shim`
para trazar API de `Java` a `JavaScript` ademas de la API completa como archivos de definicion `TypeScript` `.d.ts`.


## Rendimiento

ES4X fue el `JavaScript` **mas rapido** de acuerdo a TechEmpower Frameworks Benchmark
[Round #18](https://www.techempower.com/benchmarks/#section=data-r18). ES4X es el mas rapido en todos los tests cuando es
comparado con marcos (frameworks) de `JavaScript`:

![round-18-js](./res/round-18-js.png)

Y ES4X estaba en el top #10 de todos los otros marcos (frameworks) en otros tests, demonstrando un rendimiento superior que
el de los marcos (frameworks) JVM mas populares:

![round-18-js](./res/round-18.png)
