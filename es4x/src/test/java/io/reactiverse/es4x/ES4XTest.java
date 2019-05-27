package io.reactiverse.es4x;

import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.junit.Assert.*;

public class ES4XTest {

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
}
