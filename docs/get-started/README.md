# Introduction

ES4X is small runtime for EcmaScript >=5 applications that runs on [graaljs](https://github.com/graalvm/graaljs) with
the help of [vert.x](https://vertx.io). JavaScript is the runtime language but it does **not** use `nodejs`.

## How it Works

Developing ES4X applications is not different than developing any other `JavaScript` application. A project is defined
using a `package.json` file. Project dependencies are fetched from [npm](https://www.npmjs.com/) and **also** from
[maven central](https://search.maven.org/).

ES4X makes use of [GraalVM](https://www.graalvm.org) which is a polyglot runtime on the JVM. This means it is possible
to use any JVM language as well as `JavaScript` in applications.

Vert.x is used by ES4X in order to provide an optimized event loop and high performance IO library. Using `Java` from
the `JavaScript` however it can be tedious as there's no way for IDEs to infer type information or APIs out of the box.
For this reason ES4X has a few packages published on `npmjs` that make the development easier by providing a small
`shim` to map the `Java` API to `JavaScript` plus the full API as `TypeScript` `.d.ts` definition files.


## Performance

ES4X is **the fastest** `JavaScript` according to TechEmpower Frameworks Benchmark
[Round #18](https://www.techempower.com/benchmarks/#section=data-r18). ES4X is the fastest on all tests when compared to
`JavaScript` frameworks:

![round-18-js](./res/round-18-js.png)

And ES4X is on the top #10 among all other frameworks in several tests, showing better performance that the most popular
JVM frameworks:

![round-18-js](./res/round-18.png)
