# ES4X

This is the EcmaScript (5.1+) language support for [Eclipse Vert.x](http://vertx.io)

[![CI](https://github.com/reactiverse/es4x/actions/workflows/develop.yml/badge.svg?branch=develop)](https://github.com/reactiverse/es4x/actions/workflows/develop.yml)
[![Join the chat at https://gitter.im/es4x/Lobby](https://badges.gitter.im/es4x/Lobby.svg)](https://gitter.im/es4x/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Security Status](https://snyk-widget.herokuapp.com/badge/mvn/io.reactiverse/es4x/badge.svg)](https://snyk.io/vuln/maven:io.reactiverse:es4x?utm_medium=referral&utm_source=badge&utm_campaign=snyk-widget)

## Why?

[![10 things I've learned making the fastest JS runtime in the world](https://img.youtube.com/vi/JUJ85k3aEg4/0.jpg)](https://www.youtube.com/watch?v=JUJ85k3aEg4)

JavaScript is fast, and this is the way to make it even faster 🚀🚀🚀

## Usage

Create a project:

```
# create a generic project
mkdir my-app
cd my-app
# init the project
npm init @es4x project
# add other dependencies...
npm install @vertx/unit --save-dev
npm install @vertx/core --save-prod
# will trigger the download
# of the java dependencies
npm install
```

Create your `index.js`:

```js
/// <reference types="es4x" />
// @ts-check

vertx
  .createHttpServer()
  .requestHandler(function (req) {
    req.response().end("Hello!");
  })
  .listen(8080);

console.log('Server listening at: http://localhost:8080/');
```

and your `index.test.js`:

```js
import { TestSuite } from '@vertx/unit';

const suite = TestSuite.create("the_test_suite");

suite.test("my_test_case", function (context) {
  var s = "value";
  context.assertEquals("value", s);
});

suite.run();
```


```bash
npm start
```

Profit!

## Documentation

For more documentation please see [docs](./docs).
