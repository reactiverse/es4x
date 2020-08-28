/// <reference types="es4x" />
// @ts-check
import { Pump } from '@vertx/core';

vertx
  .createNetServer()
  .connectHandler(socket => {

    // Create a pump
    Pump.pump(socket, socket).start();

  }).listen(1234);

console.log("Echo server is now listening");

