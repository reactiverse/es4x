/// <reference types="es4x" />
// @ts-check

import { HttpClientOptions } from "@vertx/core/options";
import { HttpVersion } from "@vertx/core/enums";
import { Buffer } from '@vertx/core';

// Note! in real-life you wouldn't often set trust all to true as
// it could leave you open to man in the middle attacks.

const options = new HttpClientOptions()
  .setSsl(true)
  .setUseAlpn(true)
  .setProtocolVersion(HttpVersion.HTTP_2)
  .setTrustAll(true);

var request = vertx.createHttpClient(options).get(8443, "localhost", "/");

request.handler(resp => {
  // Print custom frames received from server
  resp.customFrameHandler(frame => {
    console.log("Got frame from server " + frame.payload().toString("UTF-8"));
  });
});

request.sendHead(version => {
  // Once head has been sent we can send custom frames
  vertx.setPeriodic(1000, t => {
    console.log("Sending ping frame to server");
    request.writeCustomFrame(10, 0, Buffer.buffer("ping"));
  });
});
