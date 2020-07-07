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
package io.reactiverse.es4x.impl;

import io.reactiverse.es4x.EventEmitter;
import org.graalvm.polyglot.Value;

import java.util.HashMap;
import java.util.Map;

public class EventEmitterImpl implements EventEmitter {

  private Map<String, Value> events;

  @Override
  public void on(String eventName, Value callback) {
    if (callback.canExecute()) {
      if (events == null) {
        events = new HashMap<>();
      }
      events.put(eventName, callback);
    }
  }

  @Override
  public int emit(String eventName, Object... args) {
    if (events != null) {
      Value cb = events.get(eventName);
      if (cb != null) {
        int length = cb.getMember("length").asInt();
        cb.executeVoid(args);
        return length;
      }
    }
    return 0;
  }
}
