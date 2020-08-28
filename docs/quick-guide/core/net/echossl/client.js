/// <reference types="es4x" />
// @ts-check

const { NetClientOptions } = require("@vertx/core/options");


const options = new NetClientOptions()
  .setSsl(true)
  .setTrustAll(true);

vertx.createNetClient(options).connect(1234, "localhost", onConnect => {
  if (onConnect.succeeded()) {
    var socket = onConnect.result();
    socket.handler(buff => {
      console.log("client receiving " + buff.toString("UTF-8"));
    });

    // Now send some data
    for (var i = 0;i < 10;i++) {
      var str = `hello ${i}\n`;
      console.log(`Net client sending: ${str}`);
      socket.write(str);
    }
  } else {
    console.trace(onConnect.cause());
  }
});
