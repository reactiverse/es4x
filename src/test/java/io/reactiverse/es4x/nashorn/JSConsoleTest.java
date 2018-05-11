package io.reactiverse.es4x.nashorn;

import io.reactiverse.es4x.Loader;
import io.vertx.core.Vertx;
import org.junit.Before;
import org.junit.BeforeClass;
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
  final Loader loader;

  public JSConsoleTest(String engine) {
    System.setProperty("es4x.engine", engine);
    engineName = engine;
    loader = Loader.create(Vertx.vertx());
  }

  @Before
  public void initialize() {
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
