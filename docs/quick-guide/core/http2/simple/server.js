/// <reference types="es4x" />
// @ts-check

import { HttpServerOptions, PemKeyCertOptions } from "@vertx/core/options";

var server = vertx.createHttpServer(
  new HttpServerOptions()
    .setUseAlpn(true)
    .setSsl(true)
    .setPemKeyCertOptions(
      new PemKeyCertOptions()
        .setKeyPath("server-key.pem")
        .setCertPath("server-cert.pem")));

server.requestHandler(function (req) {
  req.response()
    .putHeader("content-type", "text/html")
    .end(`<html><body><h1>Hello from vert.x!</h1><p>version = ${req.version()}</p></body></html>`);
}).listen(8443);
