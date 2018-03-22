/// <reference types="@vertx/core/runtime" />
// @ts-check

vertx
  .createHttpServer()
  .requestHandler(function (req) {
    req.response().end("Hello!");
  })
  .listen(8080);

console.log('Server listening at: http://localhost:8080/');
