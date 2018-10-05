# ES4X

This is the EcmaScript (5.1+) language support for [Eclipse Vert.x](http://vertx.io)

[![Build Status](https://travis-ci.org/reactiverse/es4x.svg?branch=develop)](https://travis-ci.org/reactiverse/es4x)

## Usage

Create a project:

```
# create a generic project
mkdir my-app
cd my-app
npm init -y
# init the es4x bits
npx es4x-cli init
# add dependencies
npm install @vertx/unit --save-dev
npm install @vertx/core --save-prod
# will trigger the download
# of the java dependencies
npm install
```

Create your `index.js`:

```js
/// <reference types="@vertx/core/runtime" />
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

Profit!

# Documentation

For more documentation please see [docs](./docs).
