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

  var System = Java.type('java.lang.System');

  var formatRegExp = /%[sdj%]/g;

  function format(f) {
    if (typeof f !== 'string') {
      var objects = [];
      for (var index = 0; index < arguments.length; index++) {
        objects.push(JSON.stringify(arguments[index]));
      }
      return objects.join(' ');
    }

    if (arguments.length === 1) return f;

    var i = 1;
    var args = arguments;
    var len = args.length;
    var str = String(f).replace(formatRegExp, function(x) {
      if (x === '%%') return '%';
      if (i >= len) return x;
      switch (x) {
        case '%s': return String(args[i++]);
        case '%d': return Number(args[i++]);
        case '%j':
          try {
            return JSON.stringify(args[i++]);
          } catch (_) {
            return '[Circular]';
          }
        // falls through
        default:
          return x;
      }
    });
    for (var x = args[i]; i < len; x = args[++i]) {
      if (x === null || (typeof x !== 'object' && typeof x !== 'symbol')) {
        str += ' ' + x;
      } else {
        str += ' ' + JSON.stringify(x);
      }
    }
    return str;
  }

  var counters = {};
  var timers = {};

  var RESET = '\u001B[0m';
  var BOLD = '\u001B[1m';
  var RED = '\u001B[31m';
  var GREEN = '\u001B[32m';
  var YELLOW = '\u001B[33m';
  var BLUE = '\u001B[34m';

  global['console'] = {
    'assert': function (expression, message) {
      if (!expression) {
        System.out.println(format.apply(null, [RED + message + RESET]));
      }
    },

    count: function (label) {
      var counter;

      if (label) {
        if (counters.hasOwnProperty(label)) {
          counter = counters[label];
        } else {
          counter = 0;
        }

        // update
        counters[label] = ++counter;
        System.out.println(format.apply(null, [GREEN + label + ':' + RESET, counter]));
      }
    },

    debug: function () {
      var args = Array.prototype.slice.call(arguments);
      if (args.length > 0) {
        args[0] = GREEN + args[0];
        args[args.length - 1] = args[args.length - 1] + RESET;
      }
      System.out.println(format.apply(null, args));
    },

    info: function () {
      var args = Array.prototype.slice.call(arguments);

      if (args.length > 0) {
        args[0] = BLUE + args[0];
        args[args.length - 1] = args[args.length - 1] + RESET;
      }
      System.out.println(format.apply(null, args));
    },

    log: function () {
      System.out.println(format.apply(null, arguments));
    },

    warn: function () {
      var args = Array.prototype.slice.call(arguments);

      if (args.length > 0) {
        args[0] = YELLOW + args[0];
        args[args.length - 1] = args[args.length - 1] + RESET;
      }
      System.out.println(format.apply(null, args));
    },

    error: function () {
      var args = Array.prototype.slice.call(arguments);

      if (args.length > 0) {
        args[0] = RED + args[0];
        args[args.length - 1] = args[args.length - 1] + RESET;
      }
      System.out.println(format.apply(null, args));
    },

    trace: function (e) {
      if (e.stack) {
        // isolate first the first line
        var idx = e.stack.indexOf('\n');

        var msg = BOLD + RED + e.stack.substr(0, idx) + RESET;
        var trace = e.stack.substr(idx);

        System.out.println(format.apply(null, [msg + trace]));
      } else {
        if (e.printStackTrace) {
          e.printStackTrace(System.out);
        } else {
          System.out.println(format.apply(null, [BOLD + RED + e + RESET]));
        }
      }
    },

    time: function (label) {
      if (label) {
        timers[label] = System.currentTimeMillis();
      }
    },
    timeEnd: function (label) {
      if (label) {
        var now = System.currentTimeMillis();
        if (timers.hasOwnProperty(label)) {
          System.out.println(format.apply(null, [GREEN + label + ':' + RESET, (now - timers[label]) + 'ms']));
          delete timers[label];
        } else {
          System.out.println(format.apply(null, [RED + label + ':' + RESET, '<no timer>']));
        }
      }
    }
  };
})(this);
