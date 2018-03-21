package io.reactiverse.es4x.nashorn;

import io.vertx.core.Vertx;
import org.junit.*;

import javax.script.*;

public class ConsoleTest {

  private ScriptEngine engine;

  private static Vertx vertx;

  @BeforeClass
  public static void beforeClass() {
    vertx = Vertx.vertx();
  }

  @AfterClass
  public static void afterClass() {
    vertx.close();
  }

  @Before
  public void setup() throws ScriptException, NoSuchMethodException {
    Loader loader = new Loader(vertx);
    engine = loader.getEngine();
  }

  @Test
  public void testBasicConsole() throws ScriptException {
    // print some stuff
    engine.eval("console.log('Hello', 'World', '!')");
    // print some stuff
    engine.eval("console.log('J: %j', {key:1})");
  }

  @Test
  public void testConsole() throws ScriptException {
    // print some stuff
    engine.eval("console.debug('Hello', 'World', '!')");
    engine.eval("console.info('Hello', 'World', '!')");
    engine.eval("console.log('Hello', 'World', '!')");
    engine.eval("console.warn('Hello', 'World', '!')");
    engine.eval("console.error('Hello', 'World', '!')");
  }

  @Test
  public void testConsoleAssert() throws ScriptException {
    // print some stuff
    engine.eval("console.assert(true, 'Hello1')");
    engine.eval("console.assert(false, 'Hello2')");
    engine.eval("console.assert(0, 'Hello3')");
    engine.eval("console.assert({}, 'Hello4')");
  }

  @Test
  public void testConsoleTrace() throws ScriptException {
    engine.eval("try { throw new Error('durp!'); } catch (e) { console.trace(e); }\n//@ sourceURL=/index.js");
  }

  @Test
  public void testConsoleCount() throws ScriptException {
    engine.eval("console.count('durp'); console.count('durp'); console.count('durp'); console.count('durp')");
  }

  @Test
  public void testConsoleTime() throws ScriptException {
    engine.eval("console.time('durp'); for (var i = 0; i < 1000; i++); console.timeEnd('durp')");
    engine.eval("console.timeEnd('durp');");
  }
}
