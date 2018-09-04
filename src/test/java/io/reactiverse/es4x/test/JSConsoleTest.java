package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Loader;
import io.vertx.core.Vertx;
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
    return Arrays.asList("Nashorn", "GraalVM");
  }

  final String engineName;
  private Loader loader;

  public JSConsoleTest(String engine) {
    System.setProperty("es4x.engine", engine);
    engineName = engine;
  }

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    loader = Loader.create(rule.vertx());
    assumeTrue(loader.name().equalsIgnoreCase(engineName));
  }

  @Test
  public void shouldPrintToStdOut() throws Exception {
    loader.eval("console.log('test');");
  }

  @Test
  public void shouldPrintToStdOutAFormattedString() throws Exception {
    loader.eval("console.log('test %s', JSON.stringify({k:1}));");
  }

  @Test
  public void shouldPrintErrorToStdOut() throws Exception {
    loader.eval("console.error('test');");
  }

  @Test
  public void shouldPrintErrorToStdOutAFormattedString() throws Exception {
    loader.eval("console.error('test %s', JSON.stringify({k:1}));");
  }

  @Test
  public void throwsTest() throws Exception {
    loader.eval("try { throw new Error('Boom!'); } catch (e) { console.trace(e); }");
  }
}
