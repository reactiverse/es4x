/// <reference types="es4x" />
// @ts-check

vertx
  .createHttpServer()
  .requestHandler(function (req: any) {
    req.response()
      .putHeader("content-type", "text/plain")
      .end("Hello!");
  }).listen(8080);

console.log('Listening at http://127.0.0.1:8080');

