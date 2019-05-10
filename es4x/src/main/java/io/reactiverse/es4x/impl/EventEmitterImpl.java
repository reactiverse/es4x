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
