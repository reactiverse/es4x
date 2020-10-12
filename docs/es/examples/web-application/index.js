/// <reference types="es4x" />
// @ts-check

import {Router} from '@vertx/web';

const app = Router.router(vertx);

// Proporciona un simple mensaje "Hola, mundo!"
app.get("/plaintext").handler(ctx => {
  ctx.response()
    .putHeader("Content-Type", "text/plain")
    .end('Hola, mundo!');
});

// Proporciona un simple mensaje "Hola, mundo!" en JSON
app.get("/json").handler(ctx => {
  ctx.response()
    .putHeader("Content-Type", "application/json")
    .end(JSON.stringify({message: 'Hola, mundo!'}));
});

// Crea un servidor HTTP y deja que lo controle una aplicacion
vertx
  .createHttpServer()
  .requestHandler(app)
  .listen(8080);

console.log('Server listening at: http://localhost:8080/');
