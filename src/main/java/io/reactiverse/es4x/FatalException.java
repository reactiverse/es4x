package io.reactiverse.es4x;

import org.graalvm.polyglot.PolyglotException;

public class FatalException extends Exception {
  public FatalException(PolyglotException cause) {
    super(cause);
  }
}
