/// <reference types="es4x" />
// @ts-check

import { Promise } from '@vertx/core'

var anotherAsyncAction = function (name) {
  var promise = Promise.promise();
  // mimic something that take times
  vertx.setTimer(100, function (l) {
    promise.complete("hello " + name);
  });
  return promise.future()
};
var anAsyncAction = function () {
  var promise = Promise.promise();
  // mimic something that take times
  vertx.setTimer(100, function (l) {
    promise.complete("world");
  });
  return promise.future()
};
var future = anAsyncAction();
future.compose(anotherAsyncAction).onComplete(onComplete => {
  if (onComplete.failed()) {
    console.log("Something bad happened");
    console.trace(onComplete.cause());
  } else {
    console.log("Result: " + onComplete.result());
  }
});
