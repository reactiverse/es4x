var eb = vertx.eventBus();

eb.consumer("ping-address", function (message) {

  console.log("Receiver received: " + message.body());
  // Now send back reply
  message.reply([1,2,3]);
});

console.log("Receiver ready!");
