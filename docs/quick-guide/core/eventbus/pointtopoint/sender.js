/// <reference types="es4x" />
// @ts-check

var eb = vertx.eventBus();

// Send a message every second

vertx.setPeriodic(1000, function (v) {

  eb.send("ping-address", "ping!", onSend => {
    if (onSend.succeeded()) {
      console.log("Received reply " + onSend.result().body());
    } else {
      console.log("No reply");
    }
  });

});
