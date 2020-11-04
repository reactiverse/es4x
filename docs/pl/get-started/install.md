# Instalacja

Zakładając, że masz już zainstalowany [Node.js](https://nodejs.org/), będziesz potrzebował działąjącego JVM. Do
prawidłowego działania wymagana jest [Java](https://adoptopenjdk.net/) lub [GraalVM](http://www.graalvm.org/).

```bash
$ java -version
openjdk version "1.8.0_265"
OpenJDK Runtime Environment (build 1.8.0_265-8u265-b01-0ubuntu2~20.04-b01)
OpenJDK 64-Bit Server VM (build 25.265-b01, mixed mode)
```

Jeśli widzisz podobny output, znaczy to że obecnie używanym JVM jest `java` **8**, co nie jest najlepszą opcją ze
względu na gorszą wydajność z jaką może działać silnik `es4x` niż w przypadku innego JVM.

## GraalVM/OpenJDK

Aby posiadać kompatybilny runtime rekomenduje się instalację wyższego runtime (np. skorzystać z
[jabby](https://github.com/shyiko/jabba)). Instrukcji do poprawnej instalacji `jabba`należy szukać w oficjalnej
[dokumentacji](https://github.com/shyiko/jabba#installation).

::: tip
Używając `jabby` możesz zainstalować `openjdk 11` i/lub `graalvm` (tylko raz) wywołując:

```bash
jabba install openjdk@1.11.0
jabba install graalvm@20.2.0
```

A następnie przełączać się między nimi wywołując:

```bash
jabba use openjdk@1.11 # LUB jabba use graalvm@20.2
```
:::

Po instalacji odpowiedniego JVM możesz również zainstalować project management utilities development tool (opcjonalnie).

## Narzędzia projektowe

```bash
npm install -g @es4x/create # LUB yarn global add @es4x/create
```

Pakiet zainstaluje globalnie komendę `es4x`, aby mogła ona zostać użyta przy tworzeniu projektu i przy wykonywaniu
innych zadań. Aby dowiedzieć się więcej o narzędziu wywołaj:

```bash
es4x --help
```

### Używanie NPX

Jednorazowo ten sam pakiet może być użyty przy wołaniu za pomocą `npx`. W takim przypadku należy skorzystać z:

```bash
npx @es4x/create --help
```

## Pakiet OS

POdczas pracy na środowiskach CI, gdzie ilość pakietów jest ograniczona, menadżer pakietów może zostać zainstalowany
przez rozpakowanie przygotowanego wcześniej pliku o rozszerzeniu tar/zip.

```bash
ES4X='0.9.0' \
  curl -sL \
  https://github.com/reactiverse/es4x/releases/download/$ES4X/es4x-pm-$ES4X-bin.tar.gz \
  | tar zx --strip-components=1 -C /usr/local
```

Dla systemów Windows można użyć pliku `zip`, co wywoła ten sam efekt.

::: tip
Preferowane jest korzystanie z `npm` jako sposobu instalacji, gdyż pozwala na łatwe aktualizacje oraz zapewnia
przenaszalność względem różnych *systemów operacyjnych*.
:::


## Weryfikacja

Do tego momentu powinieneś mieć już działającą komendę `es4x`. Aby to przetestować wykonaj test wywołując:

```
$ es4x --help

Usage: java -jar /usr/local/bin/es4x-bin.jar [COMMAND] [OPTIONS]
            [arg...]

Commands:
    bare         Creates a bare instance of vert.x.
    dockerfile   Creates a generic Dockerfile for building and deploying the
                 current project.
    project      Initializes the 'package.json' to work with ES4X.
    install      Installs required jars from maven to 'node_modules'.
    list         List vert.x applications
    run          Runs a JS script called <main-verticle> in its own instance of
                 vert.x.
    start        Start a vert.x application in background
    stop         Stop a vert.x application
    version      Displays the version.

Run 'java -jar /usr/local/bin/es4x-bin.jar COMMAND --help' for
more information on a command.
```

::: warning
Dla najlepszych wrażeń i wydajności zainstaluj [GraalVM](https://www.graalvm.org). Podczas pracy ze standardowym JDK i
używając Javy w wersji < 11 aplikacja zostanie uruchomiona w trybie `Interpreted` co nie jest rekomendowane i wydajne
w przypadku pracy na produkcji.
:::
