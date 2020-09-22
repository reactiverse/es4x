# JARs

ES4X runs on a JVM, so adding or using `jar`s from Maven Central is simple to add. This feature is useful, for example
when we need to add runtime libraries that have no `npm` counter part, or are just support libraries. As an example,
we can in some cases improve the IO performance of `vert.x` by adding
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

By adding `mvnDependencies` array to the package json, those dependencies are added to the application runtime. The
format is the usual for maven:

```
group:artifact[:type][:classifier]:version
```

* **group** the organization that owns the module
* **artifact** the module per se
* **type** optional file type
* **classifier** optional classifier, this allows having distinct modules for specific roles
* **version** the module version




