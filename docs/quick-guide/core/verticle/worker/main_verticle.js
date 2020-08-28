/// <reference types="es4x" />
// @ts-check

import { DeploymentOptions } from "@vertx/core/options";

console.log("[Main] Running in " + Java.type("java.lang.Thread").currentThread().getName());

vertx.deployVerticle(
  "io.vertx.example.core.verticle.worker.WorkerVerticle",
  new DeploymentOptions().setWorker(true));

vertx.eventBus()
.send("sample.data", "hello vert.x", onSend => {
  console.log("[Main] Receiving reply ' " + onSend.result().body() + "' in " + Java.type("java.lang.Thread").currentThread().getName());
});
