package io.reactiverse.es4x.impl.command;

import org.junit.Test;

import static org.junit.Assert.*;

public class ES4XStartCommandTest {

  @Test
  public void checkClass() {
    try {
      new ES4XStartCommand();
    } catch (RuntimeException e) {
      fail(e.getMessage());
    }
  }
}
