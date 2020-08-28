/// <reference types="es4x" />
// @ts-check

import { Buffer } from "@vertx/core";
const client = vertx.createHttpClient();

client.websocket(8080, "localhost", "/some-uri", websocket => {
  websocket.handler(data => {
    console.log("Received data " + data.toString("ISO-8859-1"));
    client.close();
  });
  websocket.writeBinaryMessage(Buffer.buffer("Hello world"));
});
