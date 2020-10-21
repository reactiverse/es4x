# Упаковка

Упаковка приложений должна следовать стилю `NPM`:

```sh
npm pack
```

[npm pack](https://docs.npmjs.com/cli/pack) создаст `TGZ` с вашим приложением, который можно в дальнейшем использовать
где-либо еще. Однако приложения также могут быть [опубликованы](https://docs.npmjs.com/cli/publish) в репозитории NPM.

Важно заметить, что для работы с `опубликованными/упакованными приложениями` целевое окружение должно иметь доступ к
пакету [@es4x/create](https://www.npmjs.com/package/@es4x/create).


## Docker

Также вы можете создавать образы Docker.

```bash
es4x dockerfile
```

Вы получите простой `dockerfile`, который вы можете настроить на свой вкус, по умолчанию файл содержит три стадии
сборки.

1. `node` установит все зависимости `NPM`
2. `java` установит зависимости `Maven`
3. Образ GraalVM запускает приложение

По умолчанию используется образ Docker [oracle/graalvm-ce](https://hub.docker.com/r/oracle/graalvm-ce), но вы можете
заменить его на любой другой образ JDK (желательно версии 11 или выше) с поддержкой JVMCI.

```bash
docker build . --build-arg BASEIMAGE=openjdk:11
```

## JLink

Java 11 поддерживает [jlink](https://docs.oracle.com/en/java/javase/11/tools/jlink.html). Вы можете использовать
инструмент jlink для сборки и оптимизации набора модулей и их зависимостей в один образ среды выполнения.

```bash
es4x jlink
```

Данный образ будет **оптимизированной** средой выполнения, и может быть использован без необходимости полной установки
JDK. Для сравнения, приложение hello world при запуске будет использовать **80Mb**, тогда как полная установка JDK
требует примерно **200Mb**.

Эта возможность может использоваться в содействии с  `Dockerfile`. Вместо использования базового образа graal,
используйте `OpenJDK`. Затем на второй стадии запустите jlink:

```dockerfile
# Вторая стадия (собираем код, относящийся к JVM)
FROM openjdk:11 AS JVM
ARG ES4X_VERSION=${project.version}
# Копируем предыдущий шаг сборки
COPY --from=NPM /usr/src/app /usr/src/app
# Используем скопированное окружение
WORKDIR /usr/src/app
# Скачиваем инструмент ES4X
RUN curl -sL https://github.com/reactiverse/es4x/releases/download/${ES4X_VERSION}/es4x-pm-${ES4X_VERSION}-bin.tar.gz | \
    tar zx --strip-components=1 -C /usr/local
# Устанавливаем зависимости Java
# Заставляем разрешение es4x maven учитывать только зависимости production
RUN es4x install --only=prod
# Создаем оптимизированный образ среды выполнения
RUN es4x jlink -t /usr/local
```

Будет создан оптимизированная среда выполнения для jre, которую можно использовать с небольшим базовым образом для
финальной стадии:

```dockerfile
FROM debian:slim
# Собираем файлы jar с прошлого шага
COPY --from=JVM /usr/local /usr/local
COPY --from=JVM /usr/src/app /usr/src/app
# Используем скопированное окружение
WORKDIR /usr/src/app
# Группируем исходники приложения
COPY . .
# Задаем свои опции java для контейнеров
ENV JAVA_OPTS="-XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:+UseContainerSupport"
# Объявляем точку входа
ENTRYPOINT [ "./node_modules/.bin/es4x-launcher" ]
```

Будет создан небольшой итоговый образ, с большим слоем, так как оптимизированная среда выполнения будет тоже упакована.
