# Логирование

Логирование широко распространено во всех приложениях. ES4X не реализует свой логгер, а использует логгер из JDK,
также известный как *java util logging* или *JUL*. Даже объект `console` связан с ним, так что вы можете отключить
логирование console на нужном уровне во время выполнения приложения с помощью настроек.

Для того, чтобы определить свои настройки логирования, создайте файл `logging.properties` с вашей конфигурацией.
Стандартная конфигурация:

```properties
handlers=java.util.logging.ConsoleHandler
java.util.logging.ConsoleHandler.formatter=io.reactiverse.es4x.jul.ES4XFormatter
java.util.logging.ConsoleHandler.level=FINEST

.level=INFO
io.reactiverse.level=INFO
io.vertx.level=INFO
com.hazelcast.level=INFO
io.netty.util.internal.PlatformDependent.level=SEVERE
```

Здесь вы можете видеть подготовленный заранее форматер, который предлагает логирование с цветами `ANSI`. При выключении
форматера все логи будут представлены только текстом, без кодов `ANSI`.

::: Заметка
Чтобы логировать только замечания и ошибки во время выполнения приложения, в том числе из объекта `console`, просто
поднимите ConsoleHandler.level

Вы также можете отправить логи куда-либо еще, добавив больше обработчиков.
:::
