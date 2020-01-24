const DeploymentOptions = Java.type('io.vertx.core.DeploymentOptions');

var options = {
  "instances" : 1
};
console.log(options);
vertx.deployVerticle('regression.Verticle', new DeploymentOptions(options));
