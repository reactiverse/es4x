# Registro

El registro es una caracteristica comun en todas las aplicaciones. ES4X no implementa un registrador, en su lugar simplemente usa
el registrador de JDK, tambien conocido como *java util logging* o *JUL*. Hasta el objecto `console` esta enlazado a esto asi que puedes
deshabilitar el registro de la consola a un nivel especifico del tiempo de ejecucion (runtime) simplemente utilizando la configuracion.

Para personalizar el registro, crea un archivo `logging.properties` con la configuracion que tu quieras.
La configuracion por defecto es:

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

Puede ver que hay un formateador personalizado, esto es para ofrecerte un registrador coloreado `ANSI`. Si el formateador esta
deshabilitado todo los registros seran en texto simple sin los codigos `ANSI`.

::: tip
Para registrar solo los avisos y los error en la ejecucion, incluyendo del objeto `console`, simplemente aumenta el nivel del
handler de la consola.

Pueden include enviar los registros a otras localizaciones añadiendo mas handlers.
:::
