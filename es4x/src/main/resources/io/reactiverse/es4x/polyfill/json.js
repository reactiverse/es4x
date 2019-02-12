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
  var Json = Java.type('io.vertx.core.json.Json');
  var JsonArray = Java.type('io.vertx.core.json.JsonArray');
  var JsonObject = Java.type('io.vertx.core.json.JsonObject');

  var Map = Java.type('java.util.Map');
  var List = Java.type('java.util.List');
  var Instant = Java.type('java.time.Instant');

  // this will wrap the original function to handle Vert.x native types too
  var _stringify = JSON.stringify;
  var _parse = JSON.parse;

  // patch the original JSON object
  JSON.stringify = function () {
    var val = arguments[0];
    if (val instanceof JsonArray || val instanceof JsonObject) {
      return val.encode();
    }
    // convert from map to object
    if (val instanceof Map) {
      return new Json.encode(val);
    }
    // convert from list to array
    if (val instanceof List) {
      return new Json.encode(val);
    }
    if (val instanceof Instant) {
      return val.toString();
    }

    return _stringify.apply(JSON, Array.prototype.slice.call(arguments))
  };

  // patch the original JSON object
  JSON.parse = function () {
    var val = arguments[0];
    if (val instanceof JsonArray) {
      return val.getList();
    }
    if (val instanceof JsonObject) {
      return val.getMap();
    }

    return _parse.apply(JSON, Array.prototype.slice.call(arguments))
  };

  // enhancement that converts json to java native objects
  JSON.wrap = function (arg) {
    if (arg === null || arg === undefined) {
      return null;
    }

    if (Array.isArray(arg)) {
      return new JsonArray(arg);
    }

    if (typeof arg === 'object') {
      return new JsonObject(arg);
    }

    // fallback
    return arg;
  };
})(JSON);
