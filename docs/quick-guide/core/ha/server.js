/// <reference types="es4x" />
// @ts-check

vertx.createHttpServer()
  .requestHandler(function (req) {
    var name = process.pid;
    req.response().end("Happily served by " + name);
  }).listen(8080);
