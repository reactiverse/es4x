# JARs

ES4X działa na JVm, więc dodawanie lub usuwanie `jarów` z Maven Central jest proste do dodania. Ta funkcjonalność jest
przydatna np. kiedy potrzebujemy dodać biblioteki runtime, które nie mają swojego odpowiednika w `npm` lub są
bibliotekami supportu. Na przykład w niektórych przypadkach możemy poprawić wydajność IO w `vert.x` poprzez dodanie
[native-transports](https://netty.io/wiki/native-transports.html) do runtime.

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

Poprzez dodanie tablicy `mvnDependencies` do package json, te dependencje zostaną dodane do aplikacji w czasie działania.
Używany format jest identyczny do stosowanego w mavenie.

```
group:artifact[:type][:classifier]:version
```

* **group** organizacja będąca właścicielem modułu
* **artifact** moduł sam w sobie
* **type** opcjonalny typ pliku
* **classifier** opcjonalny klasyfikaor, który pozwala na rozrożnianie modułów ze wzgledu na ich okreslone role
* **version** wersja modułu




