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
  const colors = System.console() != null;

  const map = function (xs, f) {
    if (xs.map) return xs.map(f);
    const res = [];
    for (let i = 0; i < xs.length; i++) {
      const x = xs[i];
      if (xs.hasOwnProperty(i)) res.push(f(x, i, xs));
    }
    return res;
  };

  const forEach = function forEach(obj, fn, ctx) {
    if (Object.prototype.toString.call(fn) !== '[object Function]') {
      throw new TypeError('iterator must be a function');
    }
    const l = obj.length;
    if (l === +l) {
      for (let i = 0; i < l; i++) {
        fn.call(ctx, obj[i], i, obj);
      }
    } else {
      for (let k in obj) {
        if (obj.hasOwnProperty(k)) {
          fn.call(ctx, obj[k], k, obj);
        }
      }
    }
  };

  const reduce = function (xs, f, acc) {
    let hasAcc = arguments.length >= 3;
    if (hasAcc && xs.reduce) return xs.reduce(f, acc);
    if (xs.reduce) return xs.reduce(f);

    for (let i = 0; i < xs.length; i++) {
      if (!xs.hasOwnProperty(i)) continue;
      if (!hasAcc) {
        acc = xs[i];
        hasAcc = true;
        continue;
      }
      acc = f(acc, xs[i], i);
    }
    return acc;
  };

  /**
   * Echos the value of a value. Trys to print the value out
   * in the best way possible given the different types.
   *
   * @param {Object} obj The object to print out.
   * @param {Object} opts Optional options object that alters the output.
   * @license MIT (© Joyent)
   */
  /* legacy: obj, showHidden, depth, colors*/

  function inspect(obj, opts) {
    // default options
    const ctx = {
      seen: [],
      stylize: stylizeNoColor
    };
    // legacy...
    if (arguments.length >= 3) ctx.depth = arguments[2];
    if (arguments.length >= 4) ctx.colors = arguments[3];
    if (isBoolean(opts)) {
      // legacy...
      ctx.showHidden = opts;
    } else if (opts) {
      // got an "options" object
      _extend(ctx, opts);
    }
    // set default options
    if (isUndefined(ctx.showHidden)) ctx.showHidden = false;
    if (isUndefined(ctx.depth)) ctx.depth = 2;
    if (isUndefined(ctx.colors)) ctx.colors = false;
    if (isUndefined(ctx.customInspect)) ctx.customInspect = true;
    if (ctx.colors) ctx.stylize = stylizeWithColor;
    return formatValue(ctx, obj, ctx.depth);
  }

// http://en.wikipedia.org/wiki/ANSI_escape_code#graphics
  inspect.colors = {
    'bold' : [1, 22],
    'italic' : [3, 23],
    'underline' : [4, 24],
    'inverse' : [7, 27],
    'white' : [37, 39],
    'grey' : [90, 39],
    'black' : [30, 39],
    'blue' : [34, 39],
    'cyan' : [36, 39],
    'green' : [32, 39],
    'magenta' : [35, 39],
    'red' : [31, 39],
    'yellow' : [33, 39]
  };

// Don't use 'blue' not visible on cmd.exe
  inspect.styles = {
    'special': 'cyan',
    'number': 'yellow',
    'boolean': 'yellow',
    'undefined': 'grey',
    'null': 'bold',
    'string': 'green',
    'date': 'magenta',
    // "name": intentionally not styling
    'regexp': 'red'
  };

  function stylizeNoColor(str, styleType) {
    return str;
  }

  function isBoolean(arg) {
    return typeof arg === 'boolean';
  }

  function isUndefined(arg) {
    return arg === void 0;
  }

  function stylizeWithColor(str, styleType) {
    const style = inspect.styles[styleType];

    if (style) {
      return '\u001b[' + inspect.colors[style][0] + 'm' + str +
        '\u001b[' + inspect.colors[style][1] + 'm';
    } else {
      return str;
    }
  }

  function isFunction(arg) {
    return typeof arg === 'function';
  }

  function isString(arg) {
    return typeof arg === 'string';
  }

  function isNumber(arg) {
    return typeof arg === 'number';
  }

  function isNull(arg) {
    return arg === null;
  }

  function isRegExp(re) {
    return isObject(re) && objectToString(re) === '[object RegExp]';
  }

  function isObject(arg) {
    return typeof arg === 'object' && arg !== null;
  }

  function isError(e) {
    return isObject(e) &&
      (objectToString(e) === '[object Error]' || e instanceof Error);
  }

  function isDate(d) {
    return isObject(d) && objectToString(d) === '[object Date]';
  }

  function objectToString(o) {
    return Object.prototype.toString.call(o);
  }

  function arrayToHash(array) {
    const hash = {};

    forEach(array, function(val, idx) {
      hash[val] = true;
    });

    return hash;
  }

  function formatArray(ctx, value, recurseTimes, visibleKeys, keys) {
    const output = [];
    let i = 0;
    const l = value.length;
    for (; i < l; ++i) {
      if (value.hasOwnProperty(String(i))) {
        output.push(formatProperty(ctx, value, recurseTimes, visibleKeys,
          String(i), true));
      } else {
        output.push('');
      }
    }
    forEach(keys, function(key) {
      if (!key.match(/^\d+$/)) {
        output.push(formatProperty(ctx, value, recurseTimes, visibleKeys,
          key, true));
      }
    });
    return output;
  }

  function formatError(value) {
    return '[' + Error.prototype.toString.call(value) + ']';
  }

  function formatValue(ctx, value, recurseTimes) {
    // Provide a hook for user-specified inspect functions.
    // Check that value is an object with an inspect function on it
    if (ctx.customInspect &&
      value &&
      isFunction(value.inspect) &&
      // Filter out the util module, it's inspect function is special
      value.inspect !== inspect &&
      // Also filter out any prototype objects using the circular check.
      !(value.constructor && value.constructor.prototype === value)) {
      let ret = value.inspect(recurseTimes, ctx);
      if (!isString(ret)) {
        ret = formatValue(ctx, ret, recurseTimes);
      }
      return ret;
    }

    // Primitive types cannot have properties
    const primitive = formatPrimitive(ctx, value);
    if (primitive) {
      return primitive;
    }

    // Look up the keys of the object.
    let keys = Object.keys(value);
    const visibleKeys = arrayToHash(keys);

    if (ctx.showHidden && Object.getOwnPropertyNames) {
      keys = Object.getOwnPropertyNames(value);
    }

    // IE doesn't make error fields non-enumerable
    // http://msdn.microsoft.com/en-us/library/ie/dww52sbt(v=vs.94).aspx
    if (isError(value)
      && (keys.indexOf('message') >= 0 || keys.indexOf('description') >= 0)) {
      return formatError(value);
    }

    // Some type of object without properties can be shortcutted.
    if (keys.length === 0) {
      if (isFunction(value)) {
        const name = value.name ? ': ' + value.name : '';
        return ctx.stylize('[Function' + name + ']', 'special');
      }
      if (isRegExp(value)) {
        return ctx.stylize(RegExp.prototype.toString.call(value), 'regexp');
      }
      if (isDate(value)) {
        return ctx.stylize(Date.prototype.toString.call(value), 'date');
      }
      if (isError(value)) {
        return formatError(value);
      }
    }

    let base = '', array = false, braces = ['{', '}'];

    // Make Array say that they are Array
    if (Array.isArray(value)) {
      array = true;
      braces = ['[', ']'];
    }

    // Make functions say that they are functions
    if (isFunction(value)) {
      const n = value.name ? ': ' + value.name : '';
      base = ' [Function' + n + ']';
    }

    // Make RegExps say that they are RegExps
    if (isRegExp(value)) {
      base = ' ' + RegExp.prototype.toString.call(value);
    }

    // Make dates with properties first say the date
    if (isDate(value)) {
      base = ' ' + Date.prototype.toUTCString.call(value);
    }

    // Make error with message first say the error
    if (isError(value)) {
      base = ' ' + formatError(value);
    }

    if (keys.length === 0 && (!array || value.length === 0)) {
      return braces[0] + base + braces[1];
    }

    if (recurseTimes < 0) {
      if (isRegExp(value)) {
        return ctx.stylize(RegExp.prototype.toString.call(value), 'regexp');
      } else {
        return ctx.stylize('[Object]', 'special');
      }
    }

    ctx.seen.push(value);

    let output;
    if (array) {
      output = formatArray(ctx, value, recurseTimes, visibleKeys, keys);
    } else {
      output = map(keys, function(key) {
        return formatProperty(ctx, value, recurseTimes, visibleKeys, key, array);
      });
    }

    ctx.seen.pop();

    return reduceToSingleString(output, base, braces);
  }

  function formatProperty(ctx, value, recurseTimes, visibleKeys, key, array) {
    let name, str, desc;
    desc = { value: value[key] };
    if (Object.getOwnPropertyDescriptor) {
      desc = Object.getOwnPropertyDescriptor(value, key) || desc;
    }
    if (desc.get) {
      if (desc.set) {
        str = ctx.stylize('[Getter/Setter]', 'special');
      } else {
        str = ctx.stylize('[Getter]', 'special');
      }
    } else {
      if (desc.set) {
        str = ctx.stylize('[Setter]', 'special');
      }
    }
    if (!visibleKeys.hasOwnProperty(key)) {
      name = '[' + key + ']';
    }
    if (!str) {
      if (ctx.seen.indexOf(desc.value) < 0) {
        if (isNull(recurseTimes)) {
          str = formatValue(ctx, desc.value, null);
        } else {
          str = formatValue(ctx, desc.value, recurseTimes - 1);
        }
        if (str.indexOf('\n') > -1) {
          if (array) {
            str = map(str.split('\n'), function(line) {
              return '  ' + line;
            }).join('\n').substr(2);
          } else {
            str = '\n' + map(str.split('\n'), function(line) {
              return '   ' + line;
            }).join('\n');
          }
        }
      } else {
        str = ctx.stylize('[Circular]', 'special');
      }
    }
    if (isUndefined(name)) {
      if (array && key.match(/^\d+$/)) {
        return str;
      }
      name = JSON.stringify('' + key);
      if (name.match(/^"([a-zA-Z_][a-zA-Z_0-9]*)"$/)) {
        name = name.substr(1, name.length - 2);
        name = ctx.stylize(name, 'name');
      } else {
        name = name.replace(/'/g, "\\'")
          .replace(/\\"/g, '"')
          .replace(/(^"|"$)/g, "'");
        name = ctx.stylize(name, 'string');
      }
    }

    return name + ': ' + str;
  }

  function formatPrimitive(ctx, value) {
    if (isUndefined(value))
      return ctx.stylize('undefined', 'undefined');
    if (isString(value)) {
      const simple = '\'' + JSON.stringify(value).replace(/^"|"$/g, '')
        .replace(/'/g, "\\'")
        .replace(/\\"/g, '"') + '\'';
      return ctx.stylize(simple, 'string');
    }
    if (isNumber(value))
      return ctx.stylize('' + value, 'number');
    if (isBoolean(value))
      return ctx.stylize('' + value, 'boolean');
    // For some reason typeof null is "object", so special case here.
    if (isNull(value))
      return ctx.stylize('null', 'null');
  }

  function reduceToSingleString(output, base, braces) {
    let numLinesEst = 0;
    const length = reduce(output, function (prev, cur) {
      numLinesEst++;
      if (cur.indexOf('\n') >= 0) numLinesEst++;
      return prev + cur.replace(/\u001b\[\d\d?m/g, '').length + 1;
    }, 0);

    if (length > 60) {
      return braces[0] +
        (base === '' ? '' : base + '\n ') +
        ' ' +
        output.join(',\n  ') +
        ' ' +
        braces[1];
    }

    return braces[0] + base + ' ' + output.join(', ') + ' ' + braces[1];
  }

  function _extend(origin, add) {
    // Don't do anything if add isn't an object
    if (!add || !isObject(add)) return origin;

    const keys = Object.keys(add);
    let i = keys.length;
    while (i--) {
      origin[keys[i]] = add[keys[i]];
    }
    return origin;
  }

  const formatRegExp = /%[sdj%]/g;

  function format(f) {
    if (typeof f !== 'string') {
      const objects = [];
      for (let index = 0; index < arguments.length; index++) {
        const obj = arguments[index];
        if (Java.isJavaObject(obj)) {
          if (obj) {
            objects.push(obj.toString());
          } else {
            objects.push('null');
          }
        } else {
          objects.push(inspect(obj, {}, 2, colors));
        }
      }
      return objects.join(' ');
    }

    if (arguments.length === 1) return f;

    let i = 1;
    const args = arguments;
    const len = args.length;
    let str = String(f).replace(formatRegExp, function (x) {
      if (x === '%%') return '%';
      if (i >= len) return x;
      switch (x) {
        case '%s':
          return String(args[i++]);
        case '%d':
          return Number(args[i++]);
        case '%j':
          try {
            return inspect(args[i++], {}, 2, colors);
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
        str += ' ' + inspect(x, {}, 2, colors);
      }
    }
    return str;
  }

  const counters = {};
  const timers = {};

  const RESET = '\u001B[0m';
  const BOLD = '\u001B[1m';
  const RED = '\u001B[31m';
  const GREEN = '\u001B[32m';
  const YELLOW = '\u001B[33m';
  const BLUE = '\u001B[34m';

  global['console'] = {
    'assert': function (expression, message) {
      if (!expression) {
        System.err.println(format.apply(null, [RED + message + RESET]));
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
        print(format.apply(null, [GREEN + label + ':' + RESET, counter]));
      }
    },

    debug: function () {
      System.out.println(GREEN + format.apply(null, arguments) + RESET);
    },

    info: function () {
      System.out.println(BLUE + format.apply(null, arguments) + RESET);
    },

    log: function () {
      System.out.println(format.apply(null, arguments));
    },

    warn: function () {
      System.err.println(YELLOW + format.apply(null, arguments) + RESET);
    },

    error: function () {
      System.err.println(RED + format.apply(null, arguments) + RESET);
    },

    trace: function (e) {
      if (e.stack) {
        // isolate first the first line
        const idx = e.stack.indexOf('\n');

        const msg = BOLD + RED + e.stack.substr(0, idx) + RESET;
        const trace = e.stack.substr(idx);

        System.out.println(format.apply(null, [msg + trace]));
      } else {
        if (e.printStackTrace) {
          e.printStackTrace(System.err);
        } else {
          System.err.println(format.apply(null, [BOLD + RED + e + RESET]));
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
          System.out.println(format.apply(null, [GREEN + label + ':' + RESET, (now - timers[label]) + 'ms']));
          delete timers[label];
        } else {
          System.err.println(format.apply(null, [RED + label + ':' + RESET, '<no timer>']));
        }
      }
    }
  };
})(this);
