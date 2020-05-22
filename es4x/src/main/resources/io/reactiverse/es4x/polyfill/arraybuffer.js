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

  const ByteBuffer = Java.type('java.nio.ByteBuffer');

  // we need to redefine the constructor of arraybuffer to allow us to access the
  // underlying j.n.ByteBuffer if present

  class EArrayBuffer extends ArrayBuffer {
    constructor(...args) {
      super(...args);
      if (args[0] && Java.isJavaObject(args[0]) && args[0] instanceof ByteBuffer) {
        Object.defineProperty(this, "nioByteBuffer", {
          value: args[0]
        });
      }
    }
  }

  // replace the default impl with the extended one
  global.ArrayBuffer = EArrayBuffer;
})(this);
