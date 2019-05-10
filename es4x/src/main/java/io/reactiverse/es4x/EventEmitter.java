package io.reactiverse.es4x;

public interface EventEmitter {

  void on(String eventName, Runnable callback);

  void emit(String eventName);
}
