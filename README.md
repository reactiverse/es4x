# node-jvm
Node.jvm is an open-source, cross-platform, Java runtime environment with a unifyed Context based API for embedded 3th Party languages like Javascript, Python, R. It executes JavaScript code and other Code that is supported by the truffleapi. For more information on using Node.jvm, see the Website.


## Why?
nodejs is simply faster as it is precompiled in C and uses V8 which is also written in C so it has less overhead when calling the bytecode
when we run node-graal we use C code to call our GraalVM code that is not Pre Compiled and will take a lot of effort to compile via JIT to Fast Code.
The Only way to get as good and fast development with graalvm we need to port every NodeJS internal Module to Java.
Vertx has done a lot in this sector already and i want to say thanks to Paolo for that he is a Hero as he belived in this idea as the first!

## What needs to get done?
We need to create Java Factorys that do expose a context first for JavaScript / ECMAScript so that we can use a http object or an fs object like we could do it in nodejs this gets then compiled into 1 single java bytecode that should be more performant then the original nodejs code as vertx has proven already.

same needs then to get done for other interpreted languages the whole IO part needs to get replaced with java async code we need something like libuv in java. And vertx is a good starting point for that. 


## Vertx Results.



[![10 things I've learned making the fastest JS runtime in the world](https://img.youtube.com/vi/JUJ85k3aEg4/0.jpg)](https://www.youtube.com/watch?v=JUJ85k3aEg4)

This is the EcmaScript (5.1+) language support for [Eclipse Vert.x](http://vertx.io)

[![Build Status](https://travis-ci.com/reactiverse/es4x.svg?branch=develop)](https://travis-ci.com/reactiverse/es4x)
[![Join the chat at https://gitter.im/es4x/Lobby](https://badges.gitter.im/es4x/Lobby.svg)](https://gitter.im/es4x/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Security Status](https://snyk-widget.herokuapp.com/badge/mvn/io.reactiverse/es4x/badge.svg)](https://snyk.io/vuln/maven:io.reactiverse:es4x?utm_medium=referral&utm_source=badge&utm_campaign=snyk-widget)


## Usage

```bash
# add es4x-pm globally
npm install -g es4x-pm
```

Create a project:

```
# create a generic project
mkdir my-app
cd my-app
# init the project
es4x init
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
