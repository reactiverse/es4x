/// <reference types="es4x" />
// @ts-check

import { HttpServerOptions, PemKeyCertOptions } from "@vertx/core/options";
import { Buffer } from '@vertx/core';

var server = vertx.createHttpServer(
  new HttpServerOptions()
    .setUseAlpn(true)
    .setSsl(true)
    .setPemKeyCertOptions(
      new PemKeyCertOptions()
        .setKeyPath("server-key.pem")
        .setCertPath("server-cert.pem")));

server.requestHandler(function (req) {
  var resp = req.response();

  req.customFrameHandler(function (frame) {
    console.log("Received client frame " + frame.payload().toString("UTF-8"));

    // Write the sam
    resp.writeCustomFrame(10, 0, Buffer.buffer("pong"));
  });
}).listen(8443);
