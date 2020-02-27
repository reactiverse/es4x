import {Router, StaticHandler} from '@vertx/web'

import React from 'react';
import { renderToString } from 'react-dom/server';
import { StaticRouter } from 'react-router-dom';
import App from './client/app';
import Routes from './routes';

const app = Router.router(vertx);

const HTML = (ctx, url) => {
  const body = renderToString(
    <StaticRouter location={url || ctx.normalisedPath()} context={ctx.data()}>
      <App />
    </StaticRouter>
  );

  return `<html>
    <head>
        <title>React basic SSR</title>
        <meta name='viewport" content="width=device-width, initial-scale=1.0" />
    </head>
    <body style="margin:0;">
        <div id="app">
           ${body}
        </div>
        <script src="client.js"></script>
    </body>
    </html>`;
};

app.route().handler(StaticHandler.create('dist'));

Routes.forEach(route => {
  if (route.url === '*') {
    app.get().handler(ctx => {
      return ctx.response().end(HTML(ctx, '/404'));
    });
  } else {
    app
      .get(route.url).handler(ctx => {
      return ctx.response().end(HTML(ctx));
    });
  }
});

vertx.createHttpServer()
  .requestHandler(app)
  .listen(8080, listen => {
    if (listen.succeeded()) {
      console.log(`Example app listening on port ${listen.result().actualPort()}!`)
    } else {
      console.trace(listen.cause());
      process.exit(1);
    }
  });
