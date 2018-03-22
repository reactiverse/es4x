# Reactiverse ES4X

This is the EcmaScript (5.1+) language support for [Eclipse Vert.x](http://vertx.io)

[![Build Status](https://travis-ci.org/reactiverse/es4x.svg?branch=develop)](https://travis-ci.org/reactiverse/es4x)

EcmaScript (or JavaScript from now onwards) can be used with Vert.x, however it's language
level is limited to the underlying `Nashorn` engine. The engine does support `ES5.1` plus
some `ES6` features. For a full list of supported features please have a look at the
[compat-tables](https://kangax.github.io/compat-table/es6/) project, depending on your
runtime [JDK 1.8](https://kangax.github.io/compat-table/es6/#nashorn1_8) or
[JDK9](https://kangax.github.io/compat-table/es6/#nashorn9) you should have more or less
language features.

# Links

* [API docs](./API)

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
mkdir my-app
cd my-app
npm init -y
npm add vertx-scripts --save-dev
npm add @vertx/unit --save-dev
npm add @vertx/core --save-prod
npm add @vertx/web --save-prod
```

Update your `package.json` as highlighted by the npm log:

```json
{
  "scripts": {
    "postinstall": "vertx-scripts init",
    "test": "vertx-scripts launcher test -v",
    "start": "vertx-scripts launcher run",
    "package": "vertx-scripts package"
  },
}
``` 

As of this moment you can follow the normal workflow. For example in order to create a
`vert.x` HTTP server using `vert.x web` you could add the following `dependencies`:

```json
{
  "dependencies": {
    "@vertx/core": "3.5.1",
    "@vertx/web": "3.5.1"
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
  .requestHandler(app)
  .listen(8080);
```

Note that the first 2 lines are helpful if you're using [Visual Studio Code](https://code.visualstudio.com/)
and they will give you proper type hinting and error reporting.

### Running your app

Since the package `vertx-scripts` is added to the project and the `scripts` section is using it, running your
application is as simple as:

```sh
npm start
```

### Packaging

It is common to package JVM applications as runnable `JAR` files, the `vertx-scripts` also provides this feature:

```sh
npm run package
```

And a new `JAR` file should be built in your `target` directory.
