/// <reference types="es4x" />
// @ts-check

vertx
  .setPeriodic(1000, t => {
    vertx
      .createHttpClient()
      .getNow(8080, "localhost", "/", resp => {
        resp.bodyHandler(function (body) {
          console.log(body.toString("ISO-8859-1"));
        });
      });
  });
