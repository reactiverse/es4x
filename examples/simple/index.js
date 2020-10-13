/// <reference types="es4x" />
// @ts-check
vertx
  .createHttpServer()
  .requestHandler(function (req) {
    req.response().end("Hello!");
  })
  .listen(8080);

console.log('Vert.x started on port 8080');
