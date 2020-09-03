---
prev: ../
next: false
sidebarDepth: 2
---
# Vert.x Unit examples

Here you'll find some examples of how to use Vert.x unit to test your asynchronous applications. For detailed
documentation, consult the Vert.x [core manual](https://vertx.io/docs).

## Project setup

To use es4x your own project use the following project as a template:

<<< @/docs/quick-guide/unit/package.json

### Vertx Unit Test

The example demonstrates how the Vert.x Unit API can be used to run tests using the Vert.x Unit test runner.

<<< @/docs/quick-guide/unit/test/some_verticle.js

Examples can be run directly from the command line:

```shell script
es4x test test/some_verticle.js
```
