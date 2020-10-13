# Politica de Seguridad

ES4X te permite que ejecutes tus aplicacoines en un sandbox totalmente seguro. Igual que [deno](https://deno.land/), ES4X puede
aislar tu aplicacion. La tecnologia que permite esto es el [security manager](https://docs.oracle.com/javase/tutorial/essential/environment/security.html) de JVM.
Los managers de seguridad fueron los que aislaban el antiguo codigo `Applet` de la maquina anfitriona. Son mas precisos que lo
que te permite `deno` a dia de hoy.

## Crea un security.policy

Para crear una politica de segurida, ejecuta la herramienta `es4x`:

```bash
$ es4x security-policy

Creating a new 'security.policy' with full network access and
read-only IO access to the working directory.
```

El archivo generado es una plantilla simple, si lo abres lo puedes leer:

```js
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
Esta plantilla permite acceso total a la red y solo lectura a todos los archivos desde `CWD` donde se inicia la aplicacion.

::: Consejo
Una vez que conozcas todos los permisos que necesita tu aplicacion puedes comenzar a restringirlos incluso mas!
:::

::: Advertencia
Aunque esta plantilla parece buena para comenzar, recuerda que el `CWD` es de solo lectura, asi que si por ejemplo
estas ejecutando un servidor http que escribe uploads, a menos que estos uploads vayan al directorio `$TEMP`, van
a fallar porque la aplicacion no tiene persmisos para escribir.
:::
