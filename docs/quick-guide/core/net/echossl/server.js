/// <reference types="es4x" />
// @ts-check
import { Pump } from '@vertx/core';
import { NetServerOptions, JksOptions } from '@vertx/core/options';


const options = new NetServerOptions()
  .setSsl(true)
  .setKeyStoreOptions(
    new JksOptions()
      .setPath('server-keystore.jks')
      .setPassword('wibble'));

vertx.createNetServer(options).connectHandler(socket => {

  // Create a pump
  Pump.pump(socket, socket).start();

}).listen(1234);

console.log("Echo server is now listening");
