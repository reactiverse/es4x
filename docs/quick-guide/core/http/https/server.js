/// <reference types="es4x" />
// @ts-check

import { HttpServerOptions, JksOptions } from '@vertx/core/options';


const server = vertx.createHttpServer(
  new HttpServerOptions()
    .setSsl(true)
    .setKeyStoreOptions(
      new JksOptions()
        .setPath('server-keystore.jks')
        .setPassword('wibble')));

server.requestHandler(req => {
  req.response()
    .putHeader("content-type", "text/html")
    .end("<html><body><h1>Hello from vert.x!</h1></body></html>");
}).listen(4443);
