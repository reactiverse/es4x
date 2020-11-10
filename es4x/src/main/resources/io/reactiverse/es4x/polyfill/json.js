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
(function (JSON) {
  'use strict';

  const Buffer = Java.type('io.vertx.core.buffer.Buffer');
  const Json = Java.type('io.vertx.core.json.Json');

  const ProxyUtil = Java.type('io.reactiverse.es4x.impl.ProxyUtil');

  // will wrap the original function to handle Vert.x native types too
  const _stringify = JSON.stringify;
  const _parse = JSON.parse;
  // vert.x json codec
  const _encodeToBuffer = Json.encodeToBuffer;
  const _encode = Json.encode;
  const _decodeValue = Json.decodeValue;
  const _encodePrettily = Json.encodePrettily;

  // patch the original JSON object
  JSON.stringify = function (value, replacer, space) {
    if (value === undefined) {
      return undefined;
    }

    if (value === null) {
      return 'null';
    }

    if (ProxyUtil.isJavaObject(value)) {
      if (replacer) {
        if (replacer === 'buffer') {
          return _encodeToBuffer(value);
        }
        if (typeof replacer === 'number' && replacer > 0) {
          return _encodePrettily(value);
        }
      }
      return _encode(value);
    }
    return _stringify(value, replacer, space);
  };

  // patch the original JSON object
  JSON.parse = function (text, reviver) {
    if (text === undefined) {
      return undefined;
    }

    if (text === null) {
      return null;
    }

    if (text instanceof Buffer) {
      return _decodeValue(text);
    }

    return _parse(text, reviver);
  };
})(JSON);
