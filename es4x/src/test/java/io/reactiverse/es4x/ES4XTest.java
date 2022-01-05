package io.reactiverse.es4x;

import io.vertx.core.VertxOptions;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import  static org.junit.Assert.*;

public class ES4XTest {

  @After
  public void cleanUp() {
    System.clearProperty("inspect");
    System.clearProperty("inspect-brk");
    System.clearProperty("polyglot.inspect");
    System.clearProperty("polyglot.inspect.Suspend");
  }

  @Test
  public void testLauncher() {
    ES4X.main("--help");
  }

  @Test
  public void testRun() throws IOException {
    File script = new File("unittest.js");
    script.deleteOnExit();

    try(OutputStream out = new FileOutputStream(script)) {
      out.write("console.log('Hello from unittest!');".getBytes());
    }

    ES4X.main("run", script.getAbsolutePath());
  }

  @Test
  @Ignore("If we run this the VM stops with error (as expected)")
  public void testInspect() {
    System.setProperty("inspect", "");
    ES4X runner = new ES4X();
    runner.beforeStartingVertx(new VertxOptions());
    assertNotNull(System.getProperty("polyglot.inspect"));
    assertNotNull(System.getProperty("polyglot.inspect.Suspend"));
    assertEquals("9229", System.getProperty("polyglot.inspect"));
    assertEquals("false", System.getProperty("polyglot.inspect.Suspend"));
  }

  @Test
  @Ignore("If we run this the VM stops with error (as expected)")
  public void testInspectBrk() {
    System.setProperty("inspect-brk", "");
    ES4X runner = new ES4X();
    runner.beforeStartingVertx(new VertxOptions());
    assertNotNull(System.getProperty("polyglot.inspect"));
    assertNotNull(System.getProperty("polyglot.inspect.Suspend"));
    assertEquals("9229", System.getProperty("polyglot.inspect"));
    assertEquals("true", System.getProperty("polyglot.inspect.Suspend"));
  }

  @Test
  public void testInspectWithValue() {
    System.setProperty("inspect", "11111");
    ES4X runner = new ES4X();
    runner.beforeStartingVertx(new VertxOptions());
    assertNotNull(System.getProperty("polyglot.inspect"));
    assertEquals("11111", System.getProperty("polyglot.inspect"));
  }

  @Test
  public void testInspectBrkWithValue() {
    System.setProperty("inspect-brk", "11223");
    ES4X runner = new ES4X();
    runner.beforeStartingVertx(new VertxOptions());
    assertNotNull(System.getProperty("polyglot.inspect"));
    assertNotNull(System.getProperty("polyglot.inspect.Suspend"));
    assertEquals("11223", System.getProperty("polyglot.inspect"));
    assertEquals("true", System.getProperty("polyglot.inspect.Suspend"));
  }
}
