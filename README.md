# ES4X

This is the EcmaScript (5.1+) language support for [Eclipse Vert.x](http://vertx.io)

[![Build Status](https://travis-ci.org/reactiverse/es4x.svg?branch=develop)](https://travis-ci.org/reactiverse/es4x)

## Usage

Create a project:

```
mkdir my-app
cd my-app
npm init -y
npm add vertx-scripts --save-dev
npm add @vertx/unit --save-dev
npm add @vertx/core --save-prod
```

Update your `package.json` as highlighted by the npm log:

```json
{
  ...
  "scripts": {
    "postinstall": "vertx-scripts init",
    "test": "vertx-scripts launcher test -v",
    "start": "vertx-scripts launcher run",
    "package": "vertx-scripts package"
  },
  ...
}
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
