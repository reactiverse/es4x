var eb = vertx.eventBus();

eb.send("ping-address", {"k": 1}, function (message) {
  console.log("Received message: " + message.body());
});

console.log("Sender ready!");
