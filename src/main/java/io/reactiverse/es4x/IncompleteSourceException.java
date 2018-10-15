package io.reactiverse.es4x;

import org.graalvm.polyglot.PolyglotException;

public class IncompleteSourceException extends Exception {
  public IncompleteSourceException(PolyglotException cause) {
    super(cause);
  }
}
