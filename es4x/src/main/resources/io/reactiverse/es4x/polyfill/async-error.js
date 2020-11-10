/**
 *  Copyright 2019 Red Hat, Inc.
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

  const AsyncError = Java.type('io.reactiverse.es4x.impl.AsyncError');
  const AsyncResult = Java.type('io.vertx.core.AsyncResult');
  const Throwable = Java.type('java.lang.Throwable');

  global.Error.asyncTrace = function asyncTrace(message) {
    let err = message;
    let currentStack = new Error().stack;

    if (currentStack) {
      if (err) {
        if (err instanceof AsyncResult || err instanceof Throwable) {
          return AsyncError.combine(err, currentStack);
        }
      }

      // if the err is not a Error object make it one
      if (!(err instanceof Error)) {
        err = new Error(err);
      }

      if (err.stack) {
        err.stack = AsyncError.combine(err.stack, currentStack);
      }

      return err;
    }

    // nothing could be inferred (return as is)
    return err;
  }
})(this);
