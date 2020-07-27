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
(function (global, verticle) {
  'use strict';

  const System = Java.type('java.lang.System');
  const VertxFileSystem = Java.type('io.reactiverse.es4x.impl.VertxFileSystem');

  global.setTimeout = function (callback, timeout) {
    const args = Array.prototype.slice.call(arguments, 2);

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
    const args = Array.prototype.slice.call(arguments, 2);

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
    const args = Array.prototype.slice.call(arguments, 1);

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

  global.clearImmediate = function (id) {
    // NO-OP
  };

  // process
  let jvmLanguageLevel;
  let pid = undefined;

  try {
    // are we on java > 9
    jvmLanguageLevel = parseInt(System.getProperty('java.specification.version'), 10);
  } catch (e) {
    jvmLanguageLevel = 8;
  }

  if (jvmLanguageLevel >= 9) {
    // try to use the new pid API
    try {
      const ProcessHandle = Java.type('java.lang.ProcessHandle');
      pid = ProcessHandle.current().pid();
    } catch (e) {
      // ignore...
    }
  }

  if (jvmLanguageLevel === 8 || pid === undefined) {
    // try to use the ManagementFactory MXBean
    try {
      const ManagementFactory = Java.type('java.lang.management.ManagementFactory');
      const name = ManagementFactory.getRuntimeMXBean().getName();
      pid = parseInt(name.substring(0, name.indexOf('@')), 10);
    } catch (e) {
      // ignore...
    }
  }

  global.process = {
    env: new Proxy({}, {
      get: function (obj, prop) {
        return System.getenv(prop);
      }
    }),
    pid: pid,
    engine: 'graaljs',

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
      const args = Array.prototype.slice.call(arguments, 1);
      vertx.runOnContext(function () {
        callback.apply(global, args);
      });
    },

    on: function (event, callback) {
      if (verticle) {
        verticle.on(event, callback);
      }
    },

    stdout: System.out,
    stderr: System.err,
    stdin: System.in,
    // non standard
    properties: new Proxy({}, {
      set: function (obj, prop, value) {
        if (typeof prop !== 'string') {
          throw new TypeError('Property name must be a String');
        }
        return System.setProperty(prop, value);
      },
      get: function (obj, prop) {
        return System.getProperty(prop);
      }
    }),

    cwd : function () {
      // vertx cwd is always / ended
      // however node isn't so make it behave the same here
      let path = VertxFileSystem.getCWD();
      let len = path.length;
      if (len > 1 && path.charAt(len - 1) === '/') {
        return path.substr(0, len - 1);
      }
      return path;
    }
  };

})(this, verticle);
