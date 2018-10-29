package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
import io.vertx.ext.unit.junit.RunTestOnContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assume.assumeTrue;

@RunWith(Parameterized.class)
public class JSConsoleTest {

  @Parameterized.Parameters
  public static List<String> engines() {
    return Arrays.asList("Nashorn", "GraalJS");
  }

  private final String engineName;
  private Runtime runtime;

  public JSConsoleTest(String engine) {
    System.setProperty("es4x.engine", engine);
    engineName = engine;
  }

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    try {
      runtime = Runtime.getCurrent(rule.vertx());
    } catch (IllegalStateException e) {
      assumeTrue(engineName + " is not available", false);
    }
  }

  @Test
  public void shouldPrintToStdOut() throws Exception {
    runtime.eval("console.log('test');");
  }

  @Test
  public void shouldPrintToStdOutAFormattedString() throws Exception {
    runtime.eval("console.log('test %s', JSON.stringify({k:1}));");
  }

  @Test
  public void shouldPrintErrorToStdOut() throws Exception {
    runtime.eval("console.error('test');");
  }

  @Test
  public void shouldPrintErrorToStdOutAFormattedString() throws Exception {
    runtime.eval("console.error('test %s', JSON.stringify({k:1}));");
  }

  @Test
  public void throwsTest() throws Exception {
    runtime.eval("try { throw new Error('Boom!'); } catch (e) { console.trace(e); }");
  }
}
