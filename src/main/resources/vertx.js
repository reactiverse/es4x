/**
 *  Copyright 2014-2018 Red Hat, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License")
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
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
  load.call(global, 'classpath:io/reactiverse/es4x/jvm-npm.js');
  // add support for polyfill
  load.call(global, 'classpath:io/reactiverse/es4x/promise.js');
})(this);
