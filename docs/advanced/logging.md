# Logging

Logging is a very common feature for all applications. ES4X does not implement a logger, instead it just uses the logger
from the JDK, also known as *java util logging* or *JUL*. Even the `console` object is linked to it, so you can disable
console from logging at a specific level at runtime just by using configuration.

In order to customize the logging, create a file `logging.properties` with the configuration as you like. The default
configuration is:

```properties
handlers=java.util.logging.ConsoleHandler
java.util.logging.ConsoleHandler.formatter=io.reactiverse.es4x.jul.ANSIFormatter
java.util.logging.ConsoleHandler.level=FINEST

.level=INFO
io.reactiverse.level=INFO
io.vertx.level=INFO
com.hazelcast.level=INFO
io.netty.util.internal.PlatformDependent.level=SEVERE
```

You can see that there is a custom formatter, this is to offer you a `ANSI` color logger. If the formatter is disabled all
logs will be in plain text without `ANSI` codes.

::: tip
To only log warning and errors at runtime, including from the `console` object, just level up the level on the console
handler.

You can even ship the logs to other locations by adding more handlers.
:::
