package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.reactiverse.es4x.test.JS.commonjs;

@RunWith(VertxUnitRunner.class)
public class ConsoleTest {

  private Runtime runtime;

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    runtime = commonjs(rule.vertx());
  }

  @Test
  public void testBasicConsole() {
    // print some stuff
    runtime.eval("console.log('Hello', 'World', '!')");
    // print some stuff
    runtime.eval("console.log('J: %j', {key:1})");
  }

  @Test
  public void testConsole() {
    // print some stuff
    runtime.eval("console.debug('Hello', 'World', '!')");
    runtime.eval("console.info('Hello', 'World', '!')");
    runtime.eval("console.log('Hello', 'World', '!')");
    runtime.eval("console.warn('Hello', 'World', '!')");
    runtime.eval("console.error('Hello', 'World', '!')");
  }

  @Test
  public void testConsoleAssert() {
    // print some stuff
    runtime.eval("console.assert(true, 'Hello1')");
    runtime.eval("console.assert(false, 'Hello2')");
    runtime.eval("console.assert(0, 'Hello3')");
    runtime.eval("console.assert({}, 'Hello4')");
  }

  @Test
  public void testConsoleTrace() {
    runtime.eval("try { throw new Error('durp!'); } catch (e) { console.trace(e); }\n//@ sourceURL=/index.js");
  }

  @Test
  public void testConsoleCount() {
    runtime.eval("console.count('durp'); console.count('durp'); console.count('durp'); console.count('durp')");
  }

  @Test
  public void testConsoleTime() {
    runtime.eval("console.time('durp'); for (var i = 0; i < 1000; i++); console.timeEnd('durp')");
    runtime.eval("console.timeEnd('durp');");
  }

  @Test
  public void testConsoleError() {
    runtime.eval("console.log('text')");
    runtime.eval("console.log(1)");
    runtime.eval("console.log(false)");
    runtime.eval("console.log(null)");
    runtime.eval("console.log(undefined)");
    runtime.eval("console.log(function () {})");
    runtime.eval("console.log(new TypeError('Oops'))");
    runtime.eval("console.log([1, 2, 3])");
    runtime.eval("console.log({key: 'value'})");
    runtime.eval("console.log(new Date())");
    runtime.eval("console.log(/abc/g)");
  }
}
