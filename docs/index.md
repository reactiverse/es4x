# Fast, unopinionated,<br/> minimalist JavaScript runtime for [Vert.x](https://vertx.io)

<asciinema-player src="cast/install.cast" rows="24" cols="120"></asciinema-player>

[![Travis branch](https://img.shields.io/travis/reactiverse/es4x/master.svg?style=for-the-badge)](https://travis-ci.com/reactiverse/es4x)
[![Maven Central](https://img.shields.io/maven-central/v/io.reactiverse/es4x.svg?style=for-the-badge)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.reactiverse%22%20AND%20a%3A%22es4x%22)
[![Codecov branch](https://img.shields.io/codecov/c/github/reactiverse/es4x/develop.svg?style=for-the-badge)](https://codecov.io/gh/reactiverse/es4x)

## Performance

ES4X provides a thin layer of JavaScript, without obscuring Vert.x features that you know and love. 

![performance-chart](performance-chart.png)

## Familiar API

```js
/// <reference types="@vertx/core/runtime" />
// @ts-check
import { Router } from '@vertx/web';

const app = Router.router(vertx);

app.route().handler(function (ctx) {
  ctx.response().end('Hello from ES4X Vert.x Web!');
});

vertx.createHttpServer()
  .requestHandler(app)
  .listen(8080);
```

Familiar API for Vert.x developers and type safety provided by [TypeScript](https://www.typescriptlang.org/) definitions
on all known APIs.

## DevOps friendly

<asciinema-player src="cast/hello-world.cast" rows="24" cols="120"></asciinema-player>

Deploy your applications as a [Docker](https://www.docker.com/) container.
