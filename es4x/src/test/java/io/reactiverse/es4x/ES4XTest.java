package io.reactiverse.es4x;

import io.vertx.core.VertxOptions;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import  static org.junit.Assert.*;

public class ES4XTest {

  @After
  public void cleanUp() {
    System.clearProperty("prefix");
    System.clearProperty("polyglot");
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
  public void testPrefix() {
    System.setProperty("prefix", "foo");
    ES4X runner = new ES4X();
    runner.beforeStartingVertx(new VertxOptions());
    assertNull(System.getProperty("prefix"));
    assertNotNull(System.getProperty("es4x.prefix"));
    assertEquals("foo/", System.getProperty("es4x.prefix"));
  }

  @Test
  public void testNullPrefix() {
    System.setProperty("prefix", "");
    ES4X runner = new ES4X();
    runner.beforeStartingVertx(new VertxOptions());
    assertNull(System.getProperty("prefix"));
    assertNull(System.getProperty("es4x.prefix"));
  }

  @Test
  public void testEmptyPolyglot() {
    System.setProperty("polyglot", "");
    ES4X runner = new ES4X();
    runner.beforeStartingVertx(new VertxOptions());
    assertNull(System.getProperty("polyglot"));
    assertNotNull(System.getProperty("es4x.polyglot"));
    assertEquals("true", System.getProperty("es4x.polyglot"));
  }

  @Test
  public void testPolyglot() {
    System.setProperty("polyglot", "false");
    ES4X runner = new ES4X();
    runner.beforeStartingVertx(new VertxOptions());
    assertNull(System.getProperty("polyglot"));
    assertNotNull(System.getProperty("es4x.polyglot"));
    assertEquals("false", System.getProperty("es4x.polyglot"));
  }
}
