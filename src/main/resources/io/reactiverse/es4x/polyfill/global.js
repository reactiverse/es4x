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
(function (global) {
  'use strict';

  global.setTimeout = function (callback, timeout) {
    var args = Array.prototype.slice.call(arguments, 2);

    if (Number(timeout) === 0) {
      // special case
      vertx.runOnContext(function (v) {
        callback.apply(global, args);
      });
    } else {
      return vertx.setTimer(Number(timeout), function (t) {
        callback.apply(global, args);
      });
    }
  };

  global.setInterval = function (callback, timeout) {
    var args = Array.prototype.slice.call(arguments, 2);

    if (Number(timeout) === 0) {
      // special case
      vertx.runOnContext(function (v) {
        callback.apply(global, args);
      });
    } else {
      return vertx.setPeriodic(Number(timeout), function (t) {
        callback.apply(global, args);
      });
    }
  };

  global.setImmediate = function (callback) {
    var args = Array.prototype.slice.call(arguments, 1);

    vertx.runOnContext(function (v) {
      callback.apply(global, args);
    });
  };

  global.clearTimeout = function (id) {
    if (id !== undefined) {
      return vertx.cancelTimer(id);
    }
  };

  global.clearInterval = function (id) {
    if (id !== undefined) {
      return vertx.cancelTimer(id);
    }
  };

  global.setImmediate = function (id) {
    // NO-OP
  };

  // process

  var ManagementFactory = Java.type('java.lang.management.ManagementFactory');
  var System = Java.type('java.lang.System');
  var pid = ManagementFactory.getRuntimeMXBean().getName();

  global.process = {
    env: System.getenv(),
    pid: pid.substring(0, pid.indexOf('@')),

    exit: function (exitCode) {
      vertx.close(function (res) {
        if (res.failed()) {
          System.exit(-1);
        } else {
          System.exit(exitCode || 0);
        }
      });
    },

    nextTick: function (callback) {
      var args = Array.prototype.slice.call(arguments, 1);
      vertx.runOnContext(function (v) {
        callback.apply(global, args);
      });
    },

    stdout: System.out,
    stderr: System.err,
    stdin: System.in
  }

})(global || this);
