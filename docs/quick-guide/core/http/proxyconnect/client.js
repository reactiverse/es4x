/// <reference types="es4x" />
// @ts-check

import { HttpClientOptions, ProxyOptions } from "@vertx/core/options";
import { ProxyType } from "@vertx/core/enums";

let request = vertx.createHttpClient(
  new HttpClientOptions()
    .setSsl(true)
    .setTrustAll(true)
    .setVerifyHost(false)
    .setProxyOptions(
      new ProxyOptions()
        .setType(ProxyType.HTTP)
        .setHost("localhost")
        .setPort(8080)))

  .put(8282, "localhost", "/", resp => {
    console.log("Got response " + resp.statusCode());
    resp.bodyHandler(function (body) {
      console.log("Got data " + body.toString("ISO-8859-1"));
    });
  });

request.setChunked(true);

for (var i = 0; i < 10; i++) {
  request.write("client-chunk-" + i);
}

request.end();
