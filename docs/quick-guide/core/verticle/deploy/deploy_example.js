/// <reference types="es4x" />
// @ts-check

import { DeploymentOptions } from "@vertx/core/options";


console.log("Main verticle has started, let's deploy some others...");

// Different ways of deploying verticles

// Deploy a verticle and don't wait for it to start
vertx.deployVerticle("io.vertx.example.core.verticle.deploy.OtherVerticle");

// Deploy another instance and  want for it to start
vertx.deployVerticle("io.vertx.example.core.verticle.deploy.OtherVerticle", onDeployVerticle => {
  if (onDeployVerticle.succeeded()) {

    var deploymentID = onDeployVerticle.result();

    console.log("Other verticle deployed ok, deploymentID = " + deploymentID);

    // You can also explicitly undeploy a verticle deployment.
    // Note that this is usually unnecessary as any verticles deployed by a verticle will be automatically
    // undeployed when the parent verticle is undeployed

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

// Deploy specifying some config
var config = {
  "foo": "bar"
};

vertx.deployVerticle(
  "io.vertx.example.core.verticle.deploy.OtherVerticle",
  new DeploymentOptions()
    .setConfig({
      "config": config
    }));

// Deploy 10 instances
vertx.deployVerticle(
  "io.vertx.example.core.verticle.deploy.OtherVerticle",
  new DeploymentOptions()
    .setInstances(10));

// Deploy it as a worker verticle
vertx.deployVerticle(
  "io.vertx.example.core.verticle.deploy.OtherVerticle",
  new DeploymentOptions()
    .setWorker(true));


