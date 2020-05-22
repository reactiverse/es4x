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

  const System = Java.type('java.lang.System');
  const Logger = Java.type('java.util.logging.Logger');
  const Level = Java.type('java.util.logging.Level');
  const Json = Java.type('io.vertx.core.json.Json');

  const log = Logger.getLogger('io.reactiverse.es4x');

  const formatRegExp = /%[sdj%]/g;

  function stringify(value) {
    try {
      if (Java.isJavaObject(value)) {
        return Json.encodePrettily(value);
      } else {
        // special cases
        if (value === undefined) {
          return "undefined"
        }
        if (typeof value === 'function') {
          return "[Function]"
        }
        if (value instanceof RegExp) {
          return value.toString();
        }
        // fallback to JSON
        return JSON.stringify(value, null, 2);
      }
    } catch (e) {
      return '[Circular: ' + e + ']';
    }
  }

  function format(f) {
    if (typeof f !== 'string') {
      var objects = [];
      for (var index = 0; index < arguments.length; index++) {
        objects.push(stringify(arguments[index]));
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
            return stringify(args[i++]);
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
        str += ' ' + stringify(x);
      }
    }
    return str;
  }

  const counters = {};
  const timers = {};

  global['console'] = {
    'assert': function (expression, message) {
      if (!expression) {
        if (log.isLoggable(Level.SEVERE)) {
          log.log(Level.SEVERE, message);
        }
      }
    },

    count: function (label) {
      let counter;

      if (label) {
        if (counters.hasOwnProperty(label)) {
          counter = counters[label];
        } else {
          counter = 0;
        }

        // update
        counters[label] = ++counter;
        if (log.isLoggable(Level.FINER)) {
          log.log(Level.FINER, format.apply(null, [label + ':', counter]));
        }
      }
    },

    debug: function () {
      if (log.isLoggable(Level.FINER)) {
        log.log(Level.FINER, format.apply(null, arguments));
      }
    },

    info: function () {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, format.apply(null, arguments));
      }
    },

    log: function () {
      if (log.isLoggable(Level.INFO)) {
        log.log(Level.INFO, format.apply(null, arguments));
      }
    },

    warn: function () {
      if (log.isLoggable(Level.WARNING)) {
        log.log(Level.WARNING, format.apply(null, arguments));
      }
    },

    error: function () {
      if (log.isLoggable(Level.SEVERE)) {
        log.log(Level.SEVERE, format.apply(null, arguments));
      }
    },

    trace: function (e) {
      if (Java.isJavaObject(e)) {
        if (log.isLoggable(Level.SEVERE)) {
          log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
      } else {
        if (e.stack) {
          // js error's have a stack (usually)
          if (log.isLoggable(Level.SEVERE)) {
            log.log(Level.SEVERE, e.stack);
          }
        } else {
          if (e.message) {
            if (log.isLoggable(Level.SEVERE)) {
              log.log(Level.SEVERE, format.apply(null, [(e.name || 'Error') + ':', e.message]));
            }
          } else {
            if (log.isLoggable(Level.SEVERE)) {
              log.log(Level.SEVERE, (e.name || 'Error'));
            }
          }
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
        const now = System.currentTimeMillis();
        if (timers.hasOwnProperty(label)) {
          if (log.isLoggable(Level.FINER)) {
            log.log(Level.FINER, format.apply(null, [label + ':', (now - timers[label]) + 'ms']));
          }
          delete timers[label];
        } else {
          if (log.isLoggable(Level.SEVERE)) {
            log.log(Level.SEVERE, format.apply(null, [label + ':', '<no timer>']));
          }
        }
      }
    }
  };
})(this);
