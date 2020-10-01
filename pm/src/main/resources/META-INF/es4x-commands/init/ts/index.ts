/// <reference types="es4x" />
// @ts-check

vertx
  .createHttpServer()
  .requestHandler(function (req: any) {
    req.response()
      .putHeader("content-type", "text/plain")
      .end("Hello!");
  }).listen(3000);

console.log('Server started on port 3000');

