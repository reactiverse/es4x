/// <reference types="es4x" />
// @ts-check

import { Pump } from '@vertx/core';
import { OpenOptions } from '@vertx/core/options';

vertx.createHttpServer().requestHandler(req => {
  req.pause();
  var filename = `temp-${Math.random().toString(36).substring(7)}.uploaded`;
  vertx
    .fileSystem()
    .open(filename, new OpenOptions(), onOpen => {
      var file = onOpen.result();
      var pump = Pump.pump(req, file);
      req.endHandler(v => {
        file.close(onClose => {
          console.log("Uploaded to " + filename);
          req.response()
            .end();
        });
      });
      pump.start();
      req.resume();
    });
}).listen(8080);
