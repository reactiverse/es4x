/// <reference types="es4x" />
// @ts-check

import { HttpClientOptions } from "@vertx/core/options";
import { HttpVersion } from "@vertx/core/enums";


// Note! in real-life you wouldn't often set trust all to true as
// it could leave you open to man in the middle attacks.

const options = new HttpClientOptions()
  .setSsl(true)
  .setUseAlpn(true)
  .setProtocolVersion(HttpVersion.HTTP_2)
  .setTrustAll(true);

vertx.createHttpClient(options).getNow(8443, "localhost", "/", resp => {
  console.log("Got response " + resp.statusCode() + " with protocol " + resp.version());
  resp.bodyHandler(body => {
    console.log("Got data " + body.toString("ISO-8859-1"));
  });
});
