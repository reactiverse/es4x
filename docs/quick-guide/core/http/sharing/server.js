/// <reference types="es4x" />
// @ts-check

import { DeploymentOptions } from "@vertx/core/options";

vertx
  .deployVerticle(
    "http_server_verticle.js",
    new DeploymentOptions().setInstances(2));
