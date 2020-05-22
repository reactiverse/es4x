/// <reference types="es4x" />
// @ts-check
const System = Java.type('java.lang.System');
console.warn(System.getProperty("user.dir"));
console.warn(System.getProperty("java.home"));

vertx
  .createHttpServer()
  .requestHandler(function (req) {
    req.response().end("Hello!");
  })
  .listen(8080);

console.log('Vert.x started on port 8080');
