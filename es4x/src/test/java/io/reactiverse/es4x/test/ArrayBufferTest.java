package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.reactiverse.es4x.test.JS.runtime;
import static org.junit.Assert.*;

@RunWith(VertxUnitRunner.class)
public class ArrayBufferTest {

  private Runtime runtime;

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    runtime = runtime(rule.vertx());
  }

  @Test
  public void testBasicBuffer() {
    assertEquals(
      8,
      runtime.eval(
      "let buffer = new ArrayBuffer(8);\n" +
      "buffer.byteLength;\n").asInt());
  }

  @Test
  public void testWrappedBuffer() {
    assertEquals(
      10,
      runtime.eval(
        "const ByteBuffer = Java.type('java.nio.ByteBuffer');\n" +
        "let bbuf = ByteBuffer.allocate(10);\n" +
        "let buffer = new ArrayBuffer(bbuf);\n" +
          "buffer.byteLength;\n").asInt());
  }

  public static class Helper {
    public void callMe(Buffer buffer) {
      assertNotNull(buffer);
      assertEquals(10, buffer.length());
    }
  }

  @Test
  public void testWrappedBufferCast() {

    Helper h = new Helper();

    runtime.put("helper", h);

    runtime.eval(
      "const ByteBuffer = Java.type('java.nio.ByteBuffer');\n" +
        "let bbuf = ByteBuffer.allocate(10);\n" +
        "let buffer = new ArrayBuffer(bbuf);\n" +
        "helper.callMe(buffer);\n");

    try {
      runtime.eval(
        "let buffer2 = new ArrayBuffer(10);\n" +
          "helper.callMe(buffer2);\n");

      fail("Should fail as raw buffers are not automatically casted.");
    } catch (RuntimeException e) {
      // OK!
      assertNotNull(e.getMessage(), e);
    }
  }
}
