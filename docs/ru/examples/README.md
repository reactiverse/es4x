# Примеры

## Hello World

Как и с любой другой библиотекой, мы начнем с примера hello world. Первым шагом мы создадим проект:

<<< @/docs/examples/hello-world/package.json

### Установим требуемые зависимости

```bash
$ npm i

> hello-es4x@1.0.0 postinstall .../hello-world
> es4x install

npm notice created a lockfile as package-lock.json. You should commit this file.
added 2 packages from 1 contributor and audited 2 packages in 6.704s
found 0 vulnerabilities
```

::: Внимание
Если вы видите сообщение `Installing GraalJS...`, это значит, что ваша `java` - не GraalVM. Это вполне
нормально, так как дополнительные пакеты будут загружены для лучшей производительности.
:::

::: Опасно!
Если вы видите сообщение `Current JDK only supports GraalJS in Interpreted mode!`, это значит, что ваша
`java` ниже версией, чем 11 или `OpenJ9`.
:::

### Пишем код

Когда наш проект создался, мы можем писать код:

<<< @/docs/examples/hello-world/index.js

### Запускаем его

```bash
$ npm start

> hello-es4x@1.0.0 start .../hello-world
> es4x

Hello ES4X
Succeeded in deploying verticle
```

::: Опасно!
Если вы видите сообщение `Current JDK only supports GraalJS in Interpreted mode!`, это значит, что ваша
`java` ниже версией, чем 11 или `OpenJ9`.
:::

## Web-приложение

В этом примере мы создадим простое web-приложение:

<<< @/docs/examples/web-application/package.json

### Установим требуемые зависимости

```bash
$ npm i

> hello-es4x@1.0.0 postinstall .../hello-world
> es4x install

npm notice created a lockfile as package-lock.json. You should commit this file.
added 2 packages from 1 contributor and audited 2 packages in 6.704s
found 0 vulnerabilities
```

::: Внимание
Если вы видите сообщение `Installing GraalJS...`, это значит, что ваша `java` - не GraalVM. Это вполне
нормально, так как дополнительные пакеты будут загружены для лучшей производительности.
:::

::: Опасно!
Если вы видите сообщение `Current JDK only supports GraalJS in Interpreted mode!`, это значит, что ваша
`java` ниже версией, чем 11 или `OpenJ9`.
:::

### Пишем код

Когда наш проект создался, мы можем писать код:

<<< @/docs/examples/web-application/index.js

## Доступ к Postgres

В этом примере мы напишем просто приложение с запросами в Postgres:

<<< @/docs/examples/postgresql/package.json

### Установим требуемые зависимости

```bash
$ npm i

> hello-es4x@1.0.0 postinstall .../hello-world
> es4x install

npm notice created a lockfile as package-lock.json. You should commit this file.
added 2 packages from 1 contributor and audited 2 packages in 6.704s
found 0 vulnerabilities
```

::: Внимание
Если вы видите сообщение `Installing GraalJS...`, это значит, что ваша `java` - не GraalVM. Это вполне
нормально, так как дополнительные пакеты будут загружены для лучшей производительности.
:::

::: Опасно!
Если вы видите сообщение `Current JDK only supports GraalJS in Interpreted mode!`, это значит, что ваша
`java` ниже версией, чем 11 или `OpenJ9`.
:::

### Пишем код

Когда наш проект создался, мы можем писать код:

<<< @/docs/examples/postgresql/index.js

## Больше примеров?

Если вам нужны дополнительные примеры, ознакомьтесь с [vertx-examples](https://github.com/vert-x3/vertx-examples). Хотя
примеры написаны на Java, с помощью [данного](../advanced) руководства, вы сможете увидеть, что использование
java API может быть простым.
