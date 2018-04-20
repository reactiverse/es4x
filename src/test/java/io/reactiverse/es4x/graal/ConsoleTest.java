//package io.reactiverse.es4x.graal;
//
//import io.vertx.core.Vertx;
//import org.junit.*;
//
//import javax.script.*;
//
//public class ConsoleTest {
//
//  private static final Vertx vertx = Vertx.vertx();
//  private static Loader loader;
//
//  @BeforeClass
//  public static void beforeClass() {
//    loader = new Loader(vertx);
//  }
//
//  @Test
//  public void testBasicConsole() throws ScriptException {
//    // print some stuff
//    loader.eval("console.log('Hello', 'World', '!')");
//    // print some stuff
//    loader.eval("console.log('J: %j', {key:1})");
//  }
//
//  @Test
//  public void testConsole() throws ScriptException {
//    // print some stuff
//    loader.eval("console.debug('Hello', 'World', '!')");
//    loader.eval("console.info('Hello', 'World', '!')");
//    loader.eval("console.log('Hello', 'World', '!')");
//    loader.eval("console.warn('Hello', 'World', '!')");
//    loader.eval("console.error('Hello', 'World', '!')");
//  }
//
//  @Test
//  public void testConsoleAssert() throws ScriptException {
//    // print some stuff
//    loader.eval("console.assert(true, 'Hello1')");
//    loader.eval("console.assert(false, 'Hello2')");
//    loader.eval("console.assert(0, 'Hello3')");
//    loader.eval("console.assert({}, 'Hello4')");
//  }
//
//  @Test
//  public void testConsoleTrace() throws ScriptException {
//    loader.eval("try { throw new Error('durp!'); } catch (e) { console.trace(e); }\n//@ sourceURL=/index.js");
//  }
//
//  @Test
//  public void testConsoleCount() throws ScriptException {
//    loader.eval("console.count('durp'); console.count('durp'); console.count('durp'); console.count('durp')");
//  }
//
//  @Test
//  public void testConsoleTime() throws ScriptException {
//    loader.eval("console.time('durp'); for (var i = 0; i < 1000; i++); console.timeEnd('durp')");
//    loader.eval("console.timeEnd('durp');");
//  }
//}
