# Package

Spakowane aplikacje powinny trzymać się konwencji `NPM`:

```sh
npm pack
```

[npm pack](https://docs.npmjs.com/cli/pack) wyprodukuje plik `TGZ` z aplikacją, który możesz przenosić do innych
lokalizacji. Oprócz tego aplikacje mogą być również [publikowane](https://docs.npmjs.com/cli/publish) w rejestrach NPM.

Ważnym jest, by zauważyć, że aby `opublikowane/spakowane` aplikacje działały na docelowym środowisku musi mieć ono
dostęp do paczki [@es4x/create](https://www.npmjs.com/package/@es4x/create), która jest wymagana do instalacji kawałków
`javy`.


## Docker

Z aplikacji można tworzyć również obrazy dockerowe.

```bash
es4x dockerfile
```

Ta komenda wyprodukuje prosty `dockerfile`, który możesz konfigurować według własnych potrzeb. Domyślnie plik będzie 3
etapem builda.

1. W pierwszym etapie używany jest `node` do instalacji wszystkich dependencji `NPM`.
2. W drugim etapie używana jest `java` do instalacji dependencji `Mavena`.
3. W końcowym etapie obraz GraalVM jest używany do uruchomienia programu.

Domyślnie używany jest obraz dockera [oracle/graalvm-ce](https://hub.docker.com/r/oracle/graalvm-ce). Można go jednak
zastąpić każdym innym obrazem JDK (najlepiej wersji 11 lub wyższej), który wspiera JVMCI.

```bash
docker build . --build-arg BASEIMAGE=openjdk:11
```

## JLink

Java 11 wspiera [jlink](https://docs.oracle.com/en/java/javase/11/tools/jlink.html). Możesz użyć jlink do złożenia i
optymalizacji modułów oraz ich dependencji w spersonalizowany obraz runtime.

```bash
es4x jlink
```

Komenda stworzy **zoptymalizowany** runtime, co znaczy że może on być użyty zamiast pełnej instalacji JDK.
Dla porównania, program hello world stworzy plik ważący około **80Mb**, podczas gdy pełna instalacja JDK wymaga około
**200Mb**.

Ta funkcjonalność może być użyta łącznie z `Dockerfile`. Zamiast używania podstawowego obrazu graal, użyj postawowego
obrazu `OpenJDK`. Następnie w drugim etapie uruchom jlink:

```dockerfile
# Etap drugi (tworzenie JVM związanego z kodem)
FROM openjdk:11 AS JVM
ARG ES4X_VERSION=${project.version}
# Wymuś rozwiązanie es4x maven tylko dla dependencji produkcyjnych
ENV ES4X_ENV=production
# Skopiuj build z poprzedniego etapu
COPY --from=NPM /usr/src/app /usr/src/app
# Użyj skopiowanego workspace'u
WORKDIR /usr/src/app
# Pobierz ES4X runtime
RUN curl -sL https://github.com/reactiverse/es4x/releases/download/${ES4X_VERSION}/es4x-pm-${ES4X_VERSION}-bin.tar.gz | \
    tar zx --strip-components=1 -C /usr/local
# Zainstaluj dependencje Javy
RUN es4x install
# Stwórz zoptymalizowany runtime
RUN es4x jlink -t /usr/local
```

To stworzy zoptymalizowany runtime dla jre, który może zostać użyty z małym obrazem podstawowym dla ostatniego etapu:

```dockerfile
FROM debian:slim
# Zbierz pliki jar z poprzedniego etapu
COPY --from=JVM /usr/local /usr/local
COPY --from=JVM /usr/src/app /usr/src/app
# Użyj skopiowanego workspace'u
WORKDIR /usr/src/app
# Bundle app source
COPY . .
# Zdefiniuj opcje javy dla kontenerów
ENV JAVA_OPTS="-XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:+UseContainerSupport"
# Zdefiniuj entrypoint
ENTRYPOINT [ "./node_modules/.bin/es4x-launcher" ]
```

Ten kod wyprodukuje mały obraz końcowy.
