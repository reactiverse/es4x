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
(function () {
  'use strict';

  const Instant = Java.type('java.time.Instant');

  Date.fromInstant = function (instant) {
    if (instant && Java.isJavaObject(instant) && instant instanceof Instant) {
      let date = new Date();
      date.setTime(instant.toEpochMilli());
      return date;
    }
    throw new TypeError('Not an java.time.Instant');
  };

})();
