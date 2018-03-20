# Vert.x Lang ES

This is the EcmaScript (5.1+) language support for [Eclipse Vert.x](http://vertx.io)

[![Build Status](https://travis-ci.org/reactiverse/es4x.svg?branch=develop)](https://travis-ci.org/reactiverse/es4x)

EcmaScript (or JavaScript from now onwards) can be used with Vert.x, however it's language
level is limited to the underlying `Nashorn` engine. The engine does support `ES5.1` plus
some `ES6` features. For a full list of supported features please have a look at the
[compat-tables](https://kangax.github.io/compat-table/es6/) project, depending on your
runtime [JDK 1.8](https://kangax.github.io/compat-table/es6/#nashorn1_8) or
[JDK9](https://kangax.github.io/compat-table/es6/#nashorn9) you should have more or less
language features.

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

```sh
mkdir myproject
cd myproject
npm init -y
```

As this moment there should be a minimal `package.json`. To simplify working with `vert.x`
add the package [vertx-scripts](https://www.npmjs.com/package/vertx-scripts) to your
`devDependencies`. During install you should get a warning tip to add also a set of custom
`scripts` to your `package.json` so it looks like this:

```json
{
  "name": "myproject",
  "version": "0.0.1",
  "main": "./index.js",
  "scripts": {
    "postinstall": "vertx-scripts init",
    "test": "vertx-scripts launcher test -t -v",
    "start": "vertx-scripts launcher run -v",
    "package": "vertx-scripts package -c -v"
  },
  "license": "ISC",
  "private": true,
  "devDependencies": {
    "vertx-scripts": "^1.0.7"
  }
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

## More information

To know more details about the `vertx-scripts`, please refer to it's [manual](vertx-scripts/README.md).

## Generating your own ES bindings

If you're working on the Java side and would like to generate `JavaScript` binding for your vert.x library, all you need
is to create a new `Maven` `pom.xml` file as follows:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">

  <modelVersion>4.0.0</modelVersion>

  <parent>
    <groupId>xyz.jetdrone</groupId>
    <artifactId>vertx-lang-es-generator</artifactId>
    <version>1.0</version>
  </parent>

  <artifactId>hot-reload</artifactId>
  <version>0.0.5</version>

  <packaging>jar</packaging>

  <properties>
    <maven.groupId>xyz.jetdrone</maven.groupId>
    <!-- language=json -->
    <npm-dependencies>
      {
        "${npm-scope}vertx": "${stack.version}",
        "${npm-scope}vertx-web": "${stack.version}"
      }
    </npm-dependencies>
  </properties>

  <dependencies>
    <!-- SOURCE TO GENERATE -->
    <dependency>
      <groupId>${maven.groupId}</groupId>
      <artifactId>${project.artifactId}</artifactId>
      <version>${project.version}</version>
    </dependency>
    <dependency>
      <groupId>${maven.groupId}</groupId>
      <artifactId>${project.artifactId}</artifactId>
      <version>${project.version}</version>
      <scope>provided</scope>
      <classifier>sources</classifier>
    </dependency>
    <dependency>
      <groupId>io.vertx</groupId>
      <artifactId>vertx-web</artifactId>
      <version>${stack.version}</version>
      <optional>true</optional>
    </dependency>
  </dependencies>
</project>
```

Is it important to use the given parent:

```xml
<parent>
  <groupId>xyz.jetdrone</groupId>
  <artifactId>vertx-lang-es-generator</artifactId>
  <version>1.0</version>
</parent>
```

The npm module name is extracted from the `artifactId` so in order to properly generate the code you must specify what
is the `maven.groupId` from the java project.

If the project has other dependencies they should be listed in the dependencies section as in this example `vertx-web`.

Finally if there were extra dependencies it will be good to provide that information to `NPM` so it can be achived with
a property:

```xml
<!-- language=json -->
<npm-dependencies>
  {
    "${npm-scope}vertx": "${stack.version}",
    "${npm-scope}vertx-web": "${stack.version}"
  }
</npm-dependencies>
```

The variables `npm-scope` and `stack.version` are provided by the parent pom. Once you are happy with the generated code
by running:

```sh
mvn clean package
```

You can publish it to a local `NPM` registry:

```sh
mvn install
```

or to a custom registry:

```sh
mvn -Dnpm-registry=https://registry.npmjs.org install
```
