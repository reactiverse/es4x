/// <reference types="es4x" />
// @ts-check
vertx
  .createNetClient()
  .connect(1234, "localhost", onConnect => {

  if (onConnect.succeeded()) {
    let socket = onConnect.result();

    socket.handler(buffer => {
      console.log("Net client receiving: " + buffer.toString("UTF-8"));
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
