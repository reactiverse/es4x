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

import java.util.HashMap;
import java.util.Map;

public class EventEmitterImpl implements EventEmitter {

  private Map<String, Runnable> events;

  @Override
  public void on(String eventName, Runnable callback) {
    if (events == null) {
      events = new HashMap<>();
    }
    events.put(eventName, callback);
  }

  public void emit(String eventName) {
    if (events != null) {
      Runnable r = events.get(eventName);
      if (r != null) {
        r.run();
      }
    }
  }
}
