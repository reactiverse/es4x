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

  const Vertx = Java.type('io.vertx.core.Vertx');

  function noop() {}

  // Polyfill for Function.prototype.bind
  function bind(fn, thisArg) {
    return function () {
      fn.apply(thisArg, arguments);
    };
  }

  function Promise(fn) {
    if (typeof this !== 'object') throw new TypeError('Promises must be constructed via new');
    if (typeof fn !== 'function') throw new TypeError('not a function');

    this._state = 0;
    this._handled = false;
    this._value = undefined;
    this._deferreds = [];
    this._context = Vertx.currentContext();

    doResolve(fn, this);
  }

  function handle(self, deferred) {
    while (self._state === 3) {
      self = self._value;
    }
    if (self._state === 0) {
      self._deferreds.push(deferred);
      return;
    }
    self._handled = true;
    Promise._immediateFn(self._context, function () {
      const cb = self._state === 1 ? deferred.onFulfilled : deferred.onRejected;
      if (cb === null) {
        (self._state === 1 ? resolve : reject)(deferred.promise, self._value);
        return;
      }
      let ret;
      try {
        ret = cb(self._value);
      } catch (e) {
        reject(deferred.promise, e);
        return;
      }
      resolve(deferred.promise, ret);
    });
  }

  function resolve(self, newValue) {
    try {
      // Promise Resolution Procedure:
      // https://github.com/promises-aplus/promises-spec#the-promise-resolution-procedure
      if (newValue === self) {
        reject(self, TypeError('A promise cannot be resolved with itself.'));
      } else {
        if (newValue && (typeof newValue === 'object' || typeof newValue === 'function')) {
          const then = newValue.then;
          if (newValue instanceof Promise) {
            self._state = 3;
            self._value = newValue;
            finale(self);
            return;
          } else if (typeof then === 'function') {
            doResolve(bind(then, newValue), self);
            return;
          }
        }
        self._state = 1;
        self._value = newValue;
        finale(self);
      }
    } catch (e) {
      reject(self, e);
    }
  }

  function reject(self, newValue) {
    self._state = 2;
    self._value = newValue;
    finale(self);
  }

  function finale(self) {
    if (self._state === 2 && self._deferreds.length === 0) {
      Promise._immediateFn(self._context, function() {
        if (!self._handled) {
          Promise._unhandledRejectionFn(self._value);
        }
      });
    }

    let i = 0;
    const len = self._deferreds.length;
    for (; i < len; i++) {
      handle(self, self._deferreds[i]);
    }
    self._deferreds = null;
  }

  function Handler(onFulfilled, onRejected, promise) {
    this.onFulfilled = typeof onFulfilled === 'function' ? onFulfilled : null;
    this.onRejected = typeof onRejected === 'function' ? onRejected : null;
    this.promise = promise;
  }

  /**
   * Take a potentially misbehaving resolver function and make sure
   * onFulfilled and onRejected are only called once.
   *
   * Makes no guarantees about asynchrony.
   */
  function doResolve(fn, self) {
    let done = false;
    try {
      fn(function (value) {
        if (done) return;
        done = true;
        resolve(self, value);
      }, function (reason) {
        if (done) return;
        done = true;
        reject(self, reason);
      });
    } catch (ex) {
      if (done) return;
      done = true;
      reject(self, ex);
    }
  }

  Promise.prototype['catch'] = function (onRejected) {
    return this.then(null, onRejected);
  };

  Promise.prototype.then = function (onFulfilled, onRejected) {
    let prom = new Promise(noop);
    handle(this, new Handler(onFulfilled, onRejected, prom));
    return prom;
  };

  Promise.prototype['finally'] = function (callback) {
    return this.then(
      function(value) {
        return Promise.resolve(callback()).then(function() {
          return value;
        });
      },
      function(reason) {
        return Promise.resolve(callback()).then(function() {
          return Promise.reject(reason);
        });
      }
    );
  };

  Promise.all = function (arr) {
    const args = Array.prototype.slice.call(arr);

    return new Promise(function (resolve, reject) {
      if (args.length === 0) return resolve([]);
      let remaining = args.length;

      function res(i, val) {
        try {
          if (val && (typeof val === 'object' || typeof val === 'function')) {
            const then = val.then;
            if (typeof then === 'function') {
              then.call(val, function (val) {
                res(i, val);
              }, reject);
              return;
            }
          }
          args[i] = val;
          if (--remaining === 0) {
            resolve(args);
          }
        } catch (ex) {
          reject(ex);
        }
      }

      for (let i = 0; i < args.length; i++) {
        res(i, args[i]);
      }
    });
  };

  Promise.resolve = function (value) {
    if (value && typeof value === 'object' && value.constructor === Promise) {
      return value;
    }

    return new Promise(function (resolve) {
      resolve(value);
    });
  };

  Promise.reject = function (value) {
    return new Promise(function (resolve, reject) {
      reject(value);
    });
  };

  Promise.race = function (values) {
    return new Promise(function (resolve, reject) {
      let i = 0;
      const len = values.length;
      for (; i < len; i++) {
        values[i].then(resolve, reject);
      }
    });
  };

  Promise._immediateFn = function (ctx, fn) {
    if (ctx) {
      ctx.runOnContext(fn);
    } else {
      vertx.runOnContext(fn);
    }
  };

  Promise._unhandledRejectionFn = function _unhandledRejectionFn(err) {
    if (typeof console !== 'undefined' && console) {
      console.warn('Possible Unhandled Promise Rejection:', err); // eslint-disable-line no-console
    }
  };

  // Install (or replace) the Promise implementation
  global.Promise = Promise;

})(this);
