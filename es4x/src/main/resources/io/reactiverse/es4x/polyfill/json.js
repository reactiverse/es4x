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

  const JsonArray = Java.type('io.vertx.core.json.JsonArray');
  const JsonObject = Java.type('io.vertx.core.json.JsonObject');
  const Instant = Java.type('java.time.Instant');

  // this will wrap the original function to handle Vert.x native types too
  const _stringify = JSON.stringify;
  const _parse = JSON.parse;

  // patch the original JSON object
  JSON.stringify = function (value, replacer, space) {
    if (value && Java.isJavaObject(value)) {
      if (value instanceof JsonArray || value instanceof JsonObject) {
        return value.encode();
      }
      if (value instanceof Instant) {
        return value.toString();
      }
    }
    return _stringify(value, replacer, space);
  };

  // patch the original JSON object
  JSON.parse = function (text, reviver) {
    if (text && Java.isJavaObject(text)) {
      if (text instanceof JsonArray) {
        return text.getList();
      }
      if (text instanceof JsonObject) {
        return text.getMap();
      }
    }
    return _parse(text, reviver);
  };
})(JSON);
