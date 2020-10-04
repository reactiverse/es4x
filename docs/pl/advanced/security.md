# Polityki bezpieczeństwa

ES4X pozwala na uruchomienie aplikacji w całkowicie bezpiecznym sandboxie. Tak samo jak [deno](https://deno.land/), ES4X
pozwala na izolację aplikacji. Technologia stojąca za tą możliwością to JVM
[security manager](https://docs.oracle.com/javase/tutorial/essential/environment/security.html). Menadżerowie
bezpieczeństwa byli tymi, którzy izolowali stary kod `Applet` od dostępu na maszynie host. Są oni bardziej drobiazgowi
od tego, co można dzisiaj dostać od `deno`.

## Stworzenie security.policy (polityki bezpieczeństwa)

Aby stworzyć politykę bezpieczeństwa uruchom narzędzie `es4x`:

```bash
$ es4x security-policy

Creating a new 'security.policy' with full network access and
read-only IO access to the working directory.
```

Wygenerowany plik jest czystym szablonem, więc jeśli go otworzysz, to przeczytasz:

```text
// Grant the following permissions to code that shall be executed from
// the node_modules/.lib/* directory
grant codeBase "file:\${user.dir}\${/}node_modules\${/}.lib\${/}*" {
  // vert.x will need full access to the temp dir.
  permission java.io.FilePermission "\${java.io.tmpdir}\${/}-", "read,write,delete";

  // the code should be able to read the JVM/GraalVM runtime libs
  permission java.io.FilePermission "\${java.home}", "read";
  permission java.io.FilePermission "\${java.home}\${/}..\${/}release", "read";
  permission java.io.FilePermission "\${java.home}\${/}-", "read";

  // applications are allowed to read all files from the CWD
  permission java.io.FilePermission "\${user.dir}\${/}-", "read";
  // uncomment the following to allow full read access
  //permission java.io.FilePermission "<<ALL FILES>>", "read";

  // Netty performs some reflection we need to allow it
  permission java.lang.reflect.ReflectPermission "suppressAccessChecks";

  // By default we allow all runtime permissions
  // users may want to restrict this further say for example to
  // deny access to environment variables, etc...
  permission java.lang.RuntimePermission "*";

  // ES4X setup a nice looking logger
  permission java.util.logging.LoggingPermission "control";

  // Allow full access to JVM system properties
  permission java.util.PropertyPermission "*", "read,write";

  // currently we allow all access to the network
  permission java.net.SocketPermission "*", "accept,connect,listen,resolve";
};
```

Ten szablon daje pełen dostęp do sieci i prawa tylko do odczyty dla wszystkich plików z `CWD`, skąd startuje aplikacja.

::: tip
Kiedy znasz już wszystkie wymagane prawa dla Twojej aplikacji możesz zacząć zastrzegać je jeszcze bardziej!
:::

::: warning
Mimo że ten szablon wygląda przyzwoicie jak na początek, pamiętaj, że `CWD` jest tylko do odczytu, więc np. jeśli
będziesz uruchamiał serwer http, który pozwala na upload plików, to nie powiedzie się to, z racji tego, że Twoja
aplikacja nie ma praw do zapisywania, oczywiście zdarzy się tak o ile pliki te nie będą zapisywane w folderze `$TEMP`
tylko w innym miejscu.
:::
