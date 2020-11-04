# Политика безопасности

ES4X позволяет запускать приложения в абсолютно безопасных "песочницах". как и [deno](https://deno.land/), ES4X может
изолировать приложения, для этих целей используется [менеджер безопасности](https://docs.oracle.com/javase/tutorial/essential/environment/security.html) JVM.
Ранее менеджеры безопасности изолировали `Applet` от доступа к хост-машине. Они более многогранные, чем то, что сейчас
предлагает `deno`.

## Создание security.policy

Чтобы создать политику безопасности, запустите утилиту `es4x`:

```bash
$ es4x security-policy

Создание нового файла 'security.policy' с полным доступом к сети
и доступом только на чтение к рабочей директории

```

Созданный файл представляет собой простой шаблон, внутри которого вы можете найти следующее:

```text
// Предоставляем следующие разрешения коду, который будет запущен из директории
// node_modules/.lib/*
grant codeBase "file:\${user.dir}\${/}node_modules\${/}.lib\${/}*" {
  // Для vert.x требуется полный доступ к директории temp.
  permission java.io.FilePermission "\${java.io.tmpdir}\${/}-", "read,write,delete";

  // Коду необходимо читать библиотеки среды выполнения JVM/GraalVM
  permission java.io.FilePermission "\${java.home}", "read";
  permission java.io.FilePermission "\${java.home}\${/}..\${/}release", "read";
  permission java.io.FilePermission "\${java.home}\${/}-", "read";

  // Приложению нужно читать все файлы из CWD
  permission java.io.FilePermission "\${user.dir}\${/}-", "read";
  // uncomment the following to allow full read access
  //permission java.io.FilePermission "<<ALL FILES>>", "read";

  // Netty нуждается в некоторых разрешениях на рефлексию
  permission java.lang.reflect.ReflectPermission "suppressAccessChecks";

  // По умолчанию мы предоставляем все разрешения времени выполнения
  // Пользователи могут ограничить их в дальнейшем, например
  // ограничить доступ к переменным среды окружения и т.д.
  permission java.lang.RuntimePermission "*";

  // ES4X устанавливает более приятный логгер
  permission java.util.logging.LoggingPermission "control";

  // Предоставляем полный доступ к системным свойствам JVM
  permission java.util.PropertyPermission "*", "read,write";

  // В текущий момент мы предоставляем полный доступ к сети
  permission java.net.SocketPermission "*", "accept,connect,listen,resolve";
};
```

Таким образом, этот шаблон дает полный доступ к сети и доступ на чтение ко всем файлам из `CWD`,
откуда было запущено приложение.

::: tip
Зная, какие ограничения требуются для вашего приложения, вы можете ограничивать права сильнее!
:::

::: warning
Несмотря на то, что шаблон выглядит достаточно неплохим для работы, нужно помнить, что доступ к `CWD` только на чтение,
так что если ваше приложение запущено, и http-сервер загружает файл, данная запись приведет к ошибке, если только запись
не будет вестись в директорию `$TEMP`.
:::
