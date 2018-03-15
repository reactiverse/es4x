// This is a similar bootstrap script to JSVerticle, it is useful when working with JJS repl.
(function (global) {
  'use strict';

  if (global.vertx) {
    // already installed
    return;
  }

  // remove the exit and quit functions
  delete global.exit;
  delete global.quit;

  var Vertx = Java.type('io.vertx.core.Vertx');
  var VertxRuntime = Java.type('io.reactiverse.es4x.nashorn.runtime.VertxRuntime');

  // the vertx instance
  global.vertx = Vertx.vertx();
  // install the global object
  global.global = global;
  // configure the runtime
  VertxRuntime.install(global);

  // install the common js loader
  load.call(global, 'classpath:jvm-npm.js');
  // add support for promises
  load.call(global, 'classpath:promise.js');
})(this);
