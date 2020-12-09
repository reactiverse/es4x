# Logowanie

Logowanie jest bardzo popularną funkcjonalnością we wszystkich aplikacjach. ES4X nie implementuje loggera, w zamian
korzystając z loggera zapewnianego przez JDK, znanego jako *java util logging* lub *JUL*. Nawet obiekt  `console` jest
 z nim powiązany, więc możesz odłączyć konsolę od logowania na konkretnym poziomie runtime poprzez odpowiednią
 konfigurację.

Aby dostosować logowanie stwórz plik  `logging.properties` z taką konfiguracją jaką potrzebujesz. Domyślna konfiguracja
to:

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

Możesz zauważyć, że użyto tu dostosowanego formattera - `ANSI` color logger. Gdy jest on wyłączony wszystkie logi będą
plain textem bez użycia kodów `ANSI`.

::: tip
Aby logować wyłącznie ostrzeżenia i errory podczas działania programu, włączając w to te z obiektu `console` podnieś
poziom w console handler.

Możesz przenosić logi do innych lokalizacji poprzez dodanie większej ilości handlerów.
:::
