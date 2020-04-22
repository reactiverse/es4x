var eb = vertx.eventBus();

eb.request("ping-address", {"k": 1}, function (request) {
  if (request.failed()) {
    console.trace(request.cause());
  } else {
    var result = request.result();
    console.log('Sender received: ' + result.body());

    process.nextTick(function () {
      eb.send("test-complete", 'OK');
    });
  }
});

console.log("Sender ready!");
