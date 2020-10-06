/// <reference types="es4x" />
// @ts-check

import {Router} from '@vertx/web';

const app = Router.router(vertx);

// Στείλτε ένα απλό "Hello, World!" γραπτό μήνυμα
app.get("/plaintext").handler(ctx => {
  ctx.response()
    .putHeader("Content-Type", "text/plain")
    .end('Hello, World!');
});

// Στείλτε ένα απλό "Hello, World!" JSON μήνυμα
app.get("/json").handler(ctx => {
  ctx.response()
    .putHeader("Content-Type", "application/json")
    .end(JSON.stringify({message: 'Hello, World!'}));
});

// δημιουργήστε έναν διακομιστή HTTP και αφήστε τον να χειριστεί από την εφαρμογή
vertx
  .createHttpServer()
  .requestHandler(app)
  .listen(8080);

console.log('Server listening at: http://localhost:8080/');
