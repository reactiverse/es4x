/// <reference types="es4x" />
// @ts-check

import {Router} from '@vertx/web';

const app = Router.router(vertx);

// serve a simple Hello, World! text message
app.get("/plaintext").handler(ctx => {
  ctx.response()
    .putHeader("Content-Type", "text/plain")
    .end('Hello, World!');
});

// serve a simple Hello, World! JSON message
app.get("/json").handler(ctx => {
  ctx.response()
    .putHeader("Content-Type", "application/json")
    .end(JSON.stringify({message: 'Hello, World!'}));
});

// create an HTTP server and let it be handled by the application
vertx
  .createHttpServer()
  .requestHandler(app)
  .listen(8080);

console.log('Server listening at: http://localhost:8080/');
