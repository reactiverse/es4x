# JARs

ES4X работает на JVM, потому добавление или использование `jar`-файлов из центрального репозитория Maven тривиально.
Эта возможность полезна, когда мы хотим добавить библиотеку среды выполнения, у которой нет аналога `npm`, или просто
при поддержке библиотек. Как пример, в некоторых случаях мы можем улучшить производительность чтения-записи `vert.x`,
добавив [native-transports](https://netty.io/wiki/native-transports.html).

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

Добавив массив `mvnDependencies` в package json, мы добавили эти зависимости в среду выполнения приложения.
Используется обычный формат для maven:


```
group:artifact[:type][:classifier]:version
```

* **group** Организация, которая владеет модулем
* **artifact** Сам модуль
* **type** Опциональный тип файла
* **classifier** Опциональный классификатор, который позволяет иметь отдельные модули для отдельных целей
* **version** Версия модуля




