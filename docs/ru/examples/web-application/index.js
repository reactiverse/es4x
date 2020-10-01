/// <reference types="es4x" />
// @ts-check

import {Router} from '@vertx/web';

const app = Router.router(vertx);

// отправляем простое текстовое сообщение Hello, World!
app.get("/plaintext").handler(ctx => {
  ctx.response()
    .putHeader("Content-Type", "text/plain")
    .end('Hello, World!');
});

// отправляем JSON сообщение Hello, World!
app.get("/json").handler(ctx => {
  ctx.response()
    .putHeader("Content-Type", "application/json")
    .end(JSON.stringify({message: 'Hello, World!'}));
});

// создаем HTTP-сервер и даем управление приложению
vertx
  .createHttpServer()
  .requestHandler(app)
  .listen(8080);

console.log('Server listening at: http://localhost:8080/');
