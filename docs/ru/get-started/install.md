# Установка

Предположим, что вы уже установили [Node.js](https://nodejs.org/), в таком случае вам понадобится рабочая JVM, либо
[Java](https://adoptopenjdk.net/), либо [GraalVM](http://www.graalvm.org/).

```bash
$ java -version
openjdk version "1.8.0_265"
OpenJDK Runtime Environment (build 1.8.0_265-8u265-b01-0ubuntu2~20.04-b01)
OpenJDK 64-Bit Server VM (build 25.265-b01, mixed mode)
```

Если вы видите похожий результат, значит, ваша система использует `java` **8**, что является не самым лучшим вариантом,
так как вы потеряете выгоду от высокой производительности движка `es4x`.

## GraalVM/OpenJDK

Чтобы получить совместимую среду выполнения, рекомендуется поставить среду выполнения более высокой версии (например, с
помощью [jabba](https://github.com/shyiko/jabba)). Инструкции по установке `jabba` можно найти в официальном
[руководстве](https://github.com/shyiko/jabba#installation).

::: tip
С помощью `jabba` вы можете установить `openjdk 11` и/или `graalvm` следующим способом:

```bash
jabba install openjdk@1.11.0
jabba install graalvm@20.2.0
```

И позже выбрать предпочитаемую среду разработки с помощью команды:

```bash
jabba use openjdk@1.11 # OR jabba use graalvm@20.2
```
:::

Когда необходимая JVM установлена, вы можете дополнительно установить инструменты разработки для управления проектами.

## Инструменты для управления проектами

```bash
npm install -g @es4x/create # OR yarn global add @es4x/create
```

Пакет установит команду `es4x` как глобальную, и она будет доступна для создания проектов и выполнения других задач.
Чтобы узнать дополнительную информацию об инструменте, используйте команду:

```bash
es4x --help
```

### Использование NPX

Тот же пакет может быть вызван операцией с `npx`, в таком случае используйте следующую команду:

```bash
npx @es4x/create --help
```

## Пакеты OS

При работе с окружением CI, где ограничено число пакетов, менеджер пакетов может быть распакован из подготовленного
заранее файла tar/zip.

```bash
ES4X='0.9.0' \
  curl -sL \
  https://github.com/reactiverse/es4x/releases/download/$ES4X/es4x-pm-$ES4X-bin.tar.gz \
  | tar zx --strip-components=1 -C /usr/local
```

Для операционных систем Windows подобная операция может быть произведена с файлом `zip`.

::: tip
Предпочтительно использовать `npm`, так как это делает обновления проще, а также делает решение переносимым на разные
*операционные системы*.
:::


## Проверка

Сейчас у вас должна быть доступна команда `es4x`, вы можете это проверить с помощью:

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
Для лучшего опыта использования и лучшей производительности используйте [GraalVM](https://www.graalvm.org). При работе
со стандартным JDK, использование Java < 11 приведет к работе в режиме `интерпретации`, что ударит по
производительности, а потому не рекомендуется в продакшне.
:::
