/// <reference types="es4x" />
// @ts-check

import { Redis, Command, Request } from "@vertx/redis-client";

// Create the redis client
var client = Redis.createClient(vertx, "redis://127.0.0.1:6379");

client.send(Request.cmd(Command.SET).arg("key").arg("value"), onSet => {
  if (onSet.succeeded()) {
    console.log("key stored");
    client.send(Request.cmd(Command.GET).arg("key"), onGet => {
      console.log("Retrieved value: " + onGet.result().toString());
    });
  } else {
    console.trace(onSet.cause());
  }
});
