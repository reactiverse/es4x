vertx.executeBlocking(function (fut) {
  console.debug('requiring dependency');
  var adder = require('./adder');

  console.debug('creating consumer');

  vertx.eventBus().consumer("es4x.mt.test", function (message) {
    var onePlusOne = adder.add();
    message.reply({result: onePlusOne})
  });

  fut.complete();
}, function(executeBlocking) {
  if (executeBlocking.failed()) {
    throw executeBlocking.cause();
  }
});
