# Run

ES4X application should create a `es4x-launcher` during the install phase of `npm`. If the launcher isn't present,
running:

```bash
yarn # OR npm install
```

::: tip
The launcher should be created in `node_modules/.bin/es4x-lancher.*`.
:::

Running the application is now as simple as::

```bash
yarn start # OR npm start
```

This command replaces the default `npm` operation by running the application on the JVM runtime and using the *Hello
World* project the following output should be visible:

```bash
Server listening at: http://localhost:8080/
Succeeded in deploying verticle
```

You can now interact with the application with a browser or a http client:

```bash
> curl localhost:8080

Hello from Vert.x Web!
```

## Running without npm/yarn

When deploying applications to production it might feel natural **not** to bundle a package manager with your
application. For this case running the application shall not use `npm`/`yarn`. Running the application becomes:

```bash
./node_modules/.bin/es4x-launcher
```

::: tip
It is possible to customize the startup of the application, do check:

```bash
./node_modules/.bin/es4x-launcher -help
```
:::

## Scaling the number of verticles

Scaling the number of verticles (which is some cases increases the performance) can be done as:

```bash
# number of verticles to use:
N=2 \
  ./node_modules/.bin/es4x-launcher -instances $N
```

::: tip
Usually increasing the number of verticles up to twice the number of cores gives the best performance.
:::

## Clustering

Same as with the number of verticles, an ES4X application can be clustered as simple as:

```bash
./node_modules/.bin/es4x-launcher -cluster
```

To know more about clustering please read the official vert.x documentation.
