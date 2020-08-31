---
prev: ../
next: false
sidebarDepth: 2
---
# Vert.x Redis Client examples

Here you will find examples demonstrating the usage of the Vert.x Redis Client. For detailed documentation, consult
the Vert.x [core manual](https://vertx.io/docs).

## Project setup

To use es4x your own project use the following project as a template:

<<< @/docs/quick-guide/redis/package.json

## Simple Client

To run this example, you need a running Redis instance. Once running, you can configure the
verticles with the redis host:

<<< @/docs/quick-guide/redis/simple/redis_client_verticle.js

By default it uses `127.0.0.1` as host. The post is set to `6379`.
