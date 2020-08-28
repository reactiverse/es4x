/// <reference types="es4x" />
// @ts-check

console.log("Main verticle has started, let's deploy some others...");

// Deploy another instance and  want for it to start
vertx.deployVerticle("io.vertx.example.core.verticle.asyncstart.OtherVerticle", onDeployVerticle => {
  if (onDeployVerticle.succeeded()) {

    var deploymentID = onDeployVerticle.result();

    console.log("Other verticle deployed ok, deploymentID = " + deploymentID);

    vertx.undeploy(deploymentID, onUndeploy => {
      if (onUndeploy.succeeded()) {
        console.log("Undeployed ok!");
      } else {
        console.trace(onUndeploy.cause());
      }
    });
  } else {
    console.trace(onDeployVerticle.cause());
  }
});


