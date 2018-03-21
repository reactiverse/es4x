/*
 * Copyright 2018 Red Hat, Inc.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  The Apache License v2.0 is available at
 *  http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */
package io.reactiverse.es4x.nashorn.runtime;

import jdk.nashorn.api.scripting.AbstractJSObject;
import jdk.nashorn.api.scripting.JSObject;

import java.util.Map;

class Polyfill {

  private Polyfill() {
    throw new RuntimeException("Should not be instantiated");
  }


  public static void install(Map<String, Object> bindings) {

    // get a reference to the global object
    final JSObject global = (JSObject) bindings.get("global");
    assert global != null;

    // get a reference to the "undefined type"
    final Object undefined = ((JSObject) global.eval("[undefined]")).getSlot(0);
    assert undefined != null;

    // get a reference to the "JSON" object
    final JSObject object = (JSObject) global.getMember("Object");
    assert object != null;

    // alas we need to add a polyfill
    if (object.getMember("assign") == null) {
      // https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/assign
      object.setMember("assign", new AbstractJSObject() {
        @Override
        public boolean isStrictFunction() {
          return true;
        }

        @Override
        public boolean hasMember(String name) {
          return "assign".equals(name) || super.hasMember(name);
        }

        @Override
        public Object getMember(String name) {
          if ("assign".equals(name)) {
            return 2;
          }
          return super.getMember(name);
        }

        @Override
        public Object call(Object thiz, Object... args) {
          if (args != null && args.length >= 2) {
            final JSObject target = (JSObject) args[0];
            final JSObject varArgs = (JSObject) args[1];

            if (target == null) {
              // not correct as it should be a TypeError...
              throw new RuntimeException("Cannot convert undefined or null to object");
            }

            for (int index = 1; index < args.length; index++) {
              Object nextSource = args[index];

              if (nextSource != null && nextSource instanceof JSObject) {
                for (String nextKey : ((JSObject) nextSource).keySet()) {
                  // Avoid bugs when hasOwnProperty is shadowed
                  if (((JSObject) nextSource).hasMember(nextKey)) {
                    target.setMember(nextKey, ((JSObject) nextSource).getMember(nextKey));
                  }
                }
              }
            }

            return target;
          }

          return undefined;
        }
      });
    }
  }
}
