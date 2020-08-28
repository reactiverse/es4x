/// <reference types="es4x" />
// @ts-check

vertx
  .createHttpClient()
  .getNow(8080, "localhost", "/", resp => {
    console.log("Got response " + resp.statusCode());
    resp.bodyHandler(body => {
      console.log("Got data " + body.toString("ISO-8859-1"));
    });
  });
