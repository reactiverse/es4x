# Reactiverse ES4X

This is the EcmaScript (5.1+) language support for [Eclipse Vert.x](http://vertx.io)

[![Travis branch](https://img.shields.io/travis/reactiverse/es4x/develop.svg?style=for-the-badge)](https://travis-ci.org/reactiverse/es4x)
[![Maven Central](https://img.shields.io/maven-central/v/io.reactiverse/es4x.svg?style=for-the-badge)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.reactiverse%22%20AND%20a%3A%22es4x%22)
[![Codecov branch](https://img.shields.io/codecov/c/github/reactiverse/es4x/develop.svg?style=for-the-badge)](https://codecov.io/gh/reactiverse/es4x)

EcmaScript (or JavaScript from now onwards) can be used with Vert.x, however it's language
level is limited to the underlying `Nashorn` engine. The engine does support `ES5.1` plus
some `ES6` features. For a full list of supported features please have a look at the
[compat-tables](https://kangax.github.io/compat-table/es6/) project, depending on your
runtime [JDK 1.8](https://kangax.github.io/compat-table/es6/#nashorn1_8) or
[JDK10](https://kangax.github.io/compat-table/es6/#nashorn10) you should have more or less
language features.

# Links

* [API docs](./API)
* [Worker API](./worker)
* [GraalVM Support](./graal)
* [Asynchronous Programming](./asynchronous)

## Goals

The goals of this verticle factory are to offer a [NPM](https://www.npmjs.com) development
approach first, instead of a java project based approach. This should make the entry level
for JavaScript developers quite low, if they are coming from a `nodejs` background.

## Usage

Minimum requirements: Since `vert.x` is a JVM based runtime, it is expected that `Java`
and [Apache Maven](http://maven.apache.org) is available in the development machine. Also
it is expected that both `nodejs` and (`npm` or `yarn`) are available too.

### Bootstrapping a Project

Bootstrapping a project should be as simple as:

```
# create a generic project
mkdir my-app
cd my-app
npm init -y
# init the es4x bits
npx es4x-cli init
# add some dependencies
npm install @vertx/unit --save-dev
npm install @vertx/core --save-prod
npm install @vertx/web --save-prod
# will trigger the download
# of the java dependencies
npm install
```

As this moment there should be a minimal `package.json`. To simplify working with `vert.x`
add the package [es4x-cli](https://www.npmjs.com/package/es4x-cli) to your
`devDependencies`. During install the `package.json` should get a set of custom
`scripts` added and it should look similar to this:

```json
{
  "scripts": {
    "postinstall": "es4x postinstall",
    "test": "es4x launcher test",
    "start": "es4x launcher run",
    "shell": "es4x shell"
  },
  "license": "ISC",
  "private": true,
  "devDependencies": {
    "es4x-cli": "*"
  }
}
``` 

As of this moment you can follow the normal workflow. For example in order to create a
`vert.x` HTTP server using `vert.x web` you could add the following `dependencies`:

```json
{
  "dependencies": {
    "@vertx/core": "3.5.3",
    "@vertx/web": "3.5.3"
  }
}
```

And in your `index.js`:

```js
/// <reference types="@vertx/core/runtime" />
// @ts-check
import { Router } from '@vertx/web';

const app = Router.router(vertx);

app.route().handler(function (ctx) {
  ctx.response().end('Hello from Vert.x Web!');
});

vertx.createHttpServer()
  .requestHandler(function (result) {
    return app.accept(result);
  } )
  .listen(8080);
```

Note that the first 2 lines are helpful if you're using [Visual Studio Code](https://code.visualstudio.com/)
and they will give you proper type hinting and error reporting.


### Important note on ES compatibility

Depending on the runtime your application will run, the ES level will be different. You can verify what works and what
doesn't at the kangax compat table project.

* [JDK 1.8](https://kangax.github.io/compat-table/es6/#nashorn1_8)
* [JDK 10](https://kangax.github.io/compat-table/es6/#nashorn10)
* [GraalVM 1.0](https://kangax.github.io/compat-table/es6/#graalvm)

For reference you can expect that `JDK1.8` will be less feature rich as it only contains **7%** of the spec implemented,
`JDK10` implements **28%** while `GraalVM` implements **97%**.


### Running your app

Since the package `es4x-cli` is added to the project and the `scripts` section is using it, running your
application is as simple as:

```sh
npm start
```

### Debugging your app

When working on the standard JDK you can start your application as:

```sh
npm start -- -d
```

This will start a JVM debugger agent on port 9229 that you can attach for a remote
debug session from your IDE (currently only tested with IntelliJ IDEA).

The experimental GraalVM support allows debugging over the JVM agent and also supoprt
the Chrome devtools protocol to debug the scripts. In order to activate this mode:

```sh
npm start -- -i
```

And follow the instructions.

### Packaging

It is common to package JVM applications as runnable `JAR` files, the `es4x-cli` creates a `pom.xml` with the
`maven-shade-plugin` configured for this:

```sh
mvn clean package
```

And a new `JAR` file should be built in your `target` directory.

Packaging will re-arrange your application code to be moved to the directory `node_modules/your-module-name` so
it can be used from other JARs. In order for this to work correctly, the current `node_modules` are also
bundled in the jar as well as all files listed under the `files` property of your `package.json`.


### Shell/ REPL

When working in a more interactive mode you'll be able to use the standard Nashorn REPL `jjs` or Graal.js REPL `js` or
if you just want a REPL to be available in your Graal runtime you can switch the main class of your runnable jar to:

```
io.reactiverse.es4x.GraalShell
```

Using this `main` will allow you to pass any configuration to your vertx instance by using a `kebab` case format
prefixed with a `-` sign. For example to start a clustered shell:

```sh
java -cp your_fatjar.jar io.reactiverse.es4x.GraalShell -clustered ./index.js
```

The if no script is passed then the shell will be just a bootstrapped environment. You will be able to
load any module by calling later:

```js
require('./my-module.js');
```
