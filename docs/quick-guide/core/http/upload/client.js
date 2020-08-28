/// <reference types="es4x" />
// @ts-check

import { Pump } from '@vertx/core';
import { OpenOptions } from '@vertx/core/options';

var req = vertx.createHttpClient()
  .put(8080, "localhost", "/someurl", resp => {
    console.log("Response " + resp.statusCode());
  });
var filename = "upload.txt";
var fs = vertx.fileSystem();

fs.props(filename, onProps => {
  var props = onProps.result();
  console.log("props is " + props);
  var size = props.size();
  req.headers().set("content-length", "" + size);
  fs.open(filename, new OpenOptions(), onOpen => {
    var file = onOpen.result();
    var pump = Pump.pump(file, req);
    file.endHandler(v => {
      req.end();
    });
    pump.start();
  });
});


