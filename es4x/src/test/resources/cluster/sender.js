var eb = vertx.eventBus();

eb.send("ping-address", {"k": 1}, function (send) {
  if (send.failed()) {
    console.trace(send.cause());
  }
  var result = send.result();
  console.log('Sender received: ' + result.body());

  process.nextTick(function () {
    eb.send("test-complete", 'OK');
  });
});

console.log("Sender ready!");
