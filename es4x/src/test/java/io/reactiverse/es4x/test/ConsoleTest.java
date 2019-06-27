package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
import io.reactiverse.es4x.impl.graal.GraalEngine;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class ConsoleTest {

  private Runtime runtime;

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    runtime = new GraalEngine(rule.vertx()).newContext();
  }

  @Test
  public void testBasicConsole() throws Exception {
    // print some stuff
    runtime.eval("console.log('Hello', 'World', '!')");
    // print some stuff
    runtime.eval("console.log('J: %j', {key:1})");
  }

  @Test
  public void testConsole() throws Exception {
    // print some stuff
    runtime.eval("console.debug('Hello', 'World', '!')");
    runtime.eval("console.info('Hello', 'World', '!')");
    runtime.eval("console.log('Hello', 'World', '!')");
    runtime.eval("console.warn('Hello', 'World', '!')");
    runtime.eval("console.error('Hello', 'World', '!')");
  }

  @Test
  public void testConsoleAssert() throws Exception {
    // print some stuff
    runtime.eval("console.assert(true, 'Hello1')");
    runtime.eval("console.assert(false, 'Hello2')");
    runtime.eval("console.assert(0, 'Hello3')");
    runtime.eval("console.assert({}, 'Hello4')");
  }

  @Test
  public void testConsoleTrace() throws Exception {
    runtime.eval("try { throw new Error('durp!'); } catch (e) { console.trace(e); }\n//@ sourceURL=/index.js");
  }

  @Test
  public void testConsoleCount() throws Exception {
    runtime.eval("console.count('durp'); console.count('durp'); console.count('durp'); console.count('durp')");
  }

  @Test
  public void testConsoleTime() throws Exception {
    runtime.eval("console.time('durp'); for (var i = 0; i < 1000; i++); console.timeEnd('durp')");
    runtime.eval("console.timeEnd('durp');");
  }

  @Test
  public void testConsoleError() throws Exception {
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
