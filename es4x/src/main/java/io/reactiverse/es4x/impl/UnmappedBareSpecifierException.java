package io.reactiverse.es4x.impl;

public final class UnmappedBareSpecifierException extends Exception {

  public UnmappedBareSpecifierException(String bareSpecifier) {
    super("Unmapped bare specifier: " + bareSpecifier);
  }
}
