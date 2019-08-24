# 介绍

ES4X是一个支持EcmaScript >=5应用的小型运行时。该运行时借助[vert.x](https://vertx.io)并使用了[graaljs](https://github.com/graalvm/graaljs)实现。JavaScript是一个需要运行时支持的语言，但node.js并非唯一的运行时，JavaScript可 **不** 借助`nodejs`运行。

## 工作原理

部署ES4X应用跟开发其他 `JavaScript` 应用并无太大区别。项目使用 `package.json` 文件定义。依赖从 [npm](https://www.npmjs.com/) 中获取， **同时** 也从[maven central](https://search.maven.org/)获取。

ES4X makes use of [GraalVM](https://www.graalvm.org) which is a polyglot runtime on the JVM. This means it is possible
to use any JVM language as well as `JavaScript` in applications.

Vert.x is used by ES4X in order to provide an optimized event loop and high performance IO library. Using `Java` from
the `JavaScript` however it can be tedious as there's no way for IDEs to infer type information or APIs out of the box.
For this reason ES4X has a few packages published on `npmjs` that make the development easier by providing a small
`shim` to map the `Java` API to `JavaScript` plus the full API as `TypeScript` `.d.ts` definition files.


## 性能表现

ES4X is **the fastest** `JavaScript` according to TechEmpower Frameworks Benchmark
[Round #18](https://www.techempower.com/benchmarks/#section=data-r18). ES4X is the fastest on all tests when compared to
`JavaScript` frameworks:

![round-18-js](./res/round-18-js.png)

And ES4X is on the top #10 among all other frameworks in several tests, showing better performance that the most popular
JVM frameworks:

![round-18-js](./res/round-18.png)
