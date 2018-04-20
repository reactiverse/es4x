//package io.reactiverse.es4x.graal;
//
//import io.vertx.core.Vertx;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import javax.script.ScriptEngine;
//import javax.script.ScriptException;
//
//public class JSConsoleTest {
//
//  private static ScriptEngine engine;
//
//  @BeforeClass
//  public static void beforeClass() throws ScriptException, NoSuchMethodException {
//    Loader loader = new Loader(Vertx.vertx());
//    engine = loader.getEngine();
//  }
//
//  @Test
//  public void shouldPrintToStdOut() throws ScriptException {
//    engine.eval("console.log('test');");
//  }
//
//  @Test
//  public void shouldPrintToStdOutAFormattedString() throws ScriptException {
//    engine.eval("console.log('test %s', JSON.stringify({k:1}));");
//  }
//
//  @Test
//  public void shouldPrintErrorToStdOut() throws ScriptException {
//    engine.eval("console.error('test');");
//  }
//
//  @Test
//  public void shouldPrintErrorToStdOutAFormattedString() throws ScriptException {
//    engine.eval("console.error('test %s', JSON.stringify({k:1}));");
//  }
//
//  @Test
//  public void throwsTest() throws ScriptException {
//    engine.eval("try { throw new Error('Boom!'); } catch (e) { console.trace(e); }");
//  }
//}
