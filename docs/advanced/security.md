# Security Policy

ES4X allows you to run your applications in a total secure sandbox. Just like [deno](https://deno.land/), ES4X can
isolate the application. The technology behind this is the JVM
[security manager](https://docs.oracle.com/javase/tutorial/essential/environment/security.html). Security managers were
what isolated the old `Applet` code from accessing the host machine. They are more fine grained than what you get today
in `deno`.

## Create a security.policy

To create a security policy, run the `es4x` tool:

```bash
$ es4x security-policy

Creating a new 'security.policy' with full network access and
read-only IO access to the working directory.
```

The generated file is a plain template, if you open it you can read:

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

So this template gives full network access and read only access to the all files from the `CWD` where the application
started.

::: tip
Once you know all the required permissions for your application you can start restricting even more!
:::

::: warning
Even though this template looks like a good start, remember that the `CWD` is read only, so for example if you're
running and http server that does file uploads, unless the uploads land on the `$TEMP` directory, they will fail as
there is no way to write from the application.
:::
