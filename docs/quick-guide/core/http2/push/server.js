/// <reference types="es4x" />
// @ts-check

import { HttpServerOptions, PemKeyCertOptions } from "@vertx/core/options";
import { HttpMethod } from "@vertx/core/enums";

var server = vertx.createHttpServer(new HttpServerOptions()
  .setUseAlpn(true)
  .setSsl(true)
  .setPemKeyCertOptions(
    new PemKeyCertOptions()
      .setKeyPath("server-key.pem")
      .setCertPath("server-cert.pem")));

server.requestHandler(function (req) {
  var path = req.path();
  var resp = req.response();
  if ("/" == path) {
    resp.push(HttpMethod.GET, "/script.js", onPush => {
      if (onPush.succeeded()) {
        console.log("sending push");
        var pushedResp = onPush.result();
        pushedResp.sendFile("script.js");
      } else {
        // Sometimes Safari forbids push : "Server push not allowed to opposite endpoint."
      }
    });

    resp.sendFile("index.html");
  } else if ("/script.js" == path) {
    resp.sendFile("script.js");
  } else {
    console.log("Not found " + path);
    resp.setStatusCode(404).end();
  }
});

server.listen(8443);
