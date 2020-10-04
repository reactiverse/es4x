# Examples

## Hello World

Like every other library, we will start with an hello world example. The first step is to create a project:

<<< @/docs/examples/hello-world/package.json

### Install the required dependencies

```bash
$ npm i

> hello-es4x@1.0.0 postinstall .../hello-world
> es4x install

npm notice created a lockfile as package-lock.json. You should commit this file.
added 2 packages from 1 contributor and audited 2 packages in 6.704s
found 0 vulnerabilities
```

::: warning
If you see the message `Installing GraalJS...`, this means that your system wide `java` is not a GraalVM installation.
This is perfectly OK as extra packages are downloaded to ensure the best performance.
:::

::: danger
If you see the message `Current JDK only supports GraalJS in Interpreted mode!`, this means that your system wide
`java` command version is either less than 11 or `OpenJ9`.
:::

### Write the code

Now that the project is ready to be used, we can write the code:

<<< @/docs/examples/hello-world/index.js

### Run it

```bash
$ npm start

> hello-es4x@1.0.0 start .../hello-world
> es4x

Hello ES4X
Succeeded in deploying verticle
```

::: danger
If you see the message `Current JDK only supports GraalJS in Interpreted mode!`, this means that your system wide
`java` command version is either less than 11 or `OpenJ9`.
:::

## Web Application

In this example we will create a simple web application:

<<< @/docs/examples/web-application/package.json

### Install the required dependencies

```bash
$ npm i

> hello-es4x@1.0.0 postinstall .../hello-world
> es4x install

npm notice created a lockfile as package-lock.json. You should commit this file.
added 2 packages from 1 contributor and audited 2 packages in 6.704s
found 0 vulnerabilities
```

::: warning
If you see the message `Installing GraalJS...`, this means that your system wide `java` is not a GraalVM installation.
This is perfectly OK as extra packages are downloaded to ensure the best performance.
:::

::: danger
If you see the message `Current JDK only supports GraalJS in Interpreted mode!`, this means that your system wide
`java` command version is either less than 11 or `OpenJ9`.
:::

### Write the code

Now that the project is ready to be used, we can write the code:

<<< @/docs/examples/web-application/index.js

## Postgres Access

In this example we will create a simple Postgres query application:

<<< @/docs/examples/postgresql/package.json

### Install the required dependencies

```bash
$ npm i

> hello-es4x@1.0.0 postinstall .../hello-world
> es4x install

npm notice created a lockfile as package-lock.json. You should commit this file.
added 2 packages from 1 contributor and audited 2 packages in 6.704s
found 0 vulnerabilities
```

::: warning
If you see the message `Installing GraalJS...`, this means that your system wide `java` is not a GraalVM installation.
This is perfectly OK as extra packages are downloaded to ensure the best performance.
:::

::: danger
If you see the message `Current JDK only supports GraalJS in Interpreted mode!`, this means that your system wide
`java` command version is either less than 11 or `OpenJ9`.
:::

### Write the code

Now that the project is ready to be used, we can write the code:

<<< @/docs/examples/postgresql/index.js

## More examples?

If you would like to see more examples, just go to [vertx-examples](https://github.com/vert-x3/vertx-examples). Even
though the examples are written in Java, by following the [advanced](../advanced) guide, you will see how the use of
java APIs can be trivial.
