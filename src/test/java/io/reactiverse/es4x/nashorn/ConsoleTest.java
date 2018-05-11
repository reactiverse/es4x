package io.reactiverse.es4x.nashorn;

import io.reactiverse.es4x.Loader;
import io.vertx.core.Vertx;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assume.assumeTrue;

@RunWith(Parameterized.class)
public class ConsoleTest {

  @Parameterized.Parameters
  public static List<String> engines() {
    return Arrays.asList("Nashorn", "GraalVM");
  }

  final String engineName;
  final Loader loader;

  public ConsoleTest(String engine) {
    System.setProperty("es4x.engine", engine);
    engineName = engine;
    loader = Loader.create(Vertx.vertx());
  }

  @Before
  public void initialize() {
    assumeTrue(loader.name().equalsIgnoreCase(engineName));
  }

  @Test
  public void testBasicConsole() throws Exception {
    // print some stuff
    loader.eval("console.log('Hello', 'World', '!')");
    // print some stuff
    loader.eval("console.log('J: %j', {key:1})");
  }

  @Test
  public void testConsole() throws Exception {
    // print some stuff
    loader.eval("console.debug('Hello', 'World', '!')");
    loader.eval("console.info('Hello', 'World', '!')");
    loader.eval("console.log('Hello', 'World', '!')");
    loader.eval("console.warn('Hello', 'World', '!')");
    loader.eval("console.error('Hello', 'World', '!')");
  }

  @Test
  public void testConsoleAssert() throws Exception {
    // print some stuff
    loader.eval("console.assert(true, 'Hello1')");
    loader.eval("console.assert(false, 'Hello2')");
    loader.eval("console.assert(0, 'Hello3')");
    loader.eval("console.assert({}, 'Hello4')");
  }

  @Test
  public void testConsoleTrace() throws Exception {
    loader.eval("try { throw new Error('durp!'); } catch (e) { console.trace(e); }\n//@ sourceURL=/index.js");
  }

  @Test
  public void testConsoleCount() throws Exception {
    loader.eval("console.count('durp'); console.count('durp'); console.count('durp'); console.count('durp')");
  }

  @Test
  public void testConsoleTime() throws Exception {
    loader.eval("console.time('durp'); for (var i = 0; i < 1000; i++); console.timeEnd('durp')");
    loader.eval("console.timeEnd('durp');");
  }
}
