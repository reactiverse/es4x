/// <reference types="es4x" />
// @ts-check

import { HttpClientOptions } from "@vertx/core/options";

// Note! in real-life you wouldn't often set trust all
// to true as it could leave you open to man in the
// middle attacks.

vertx.createHttpClient(
  new HttpClientOptions()
    .setSsl(true)
    .setTrustAll(true))

  .getNow(4443, "localhost", "/", resp => {
    console.log("Got response " + resp.statusCode());
    resp.bodyHandler(function (body) {
      console.log("Got data " + body.toString("ISO-8859-1"));
    });
  });
