Since the package es4x-pm is added to the project and the scripts section is using it, running your application is as
simple as:

```bash
> npm start
```

This command replaces the default `npm` operation by running the application on the JVM runtime.

```bash
Server listening at: http://localhost:8080/
Succeeded in deploying verticle
```

You can now interact with the application with a browser or a http client:

```bash
> curl localhost:8080

Hello from Vert.x Web!
```

To pass arguments use the double dash `--` notation and all arguments after will be provided as is to the
running application.
