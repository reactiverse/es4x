/// <reference types="@vertx/core/runtime" />

import { Router } from '@vertx/web';
import { home } from './routes';

const app = Router.router(vertx);

console.log(typeof home);

app.route('/').handler(home);

vertx.createHttpServer()
  .requestHandler(app)
  .listen(8080);
