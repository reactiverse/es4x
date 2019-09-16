package io.reactiverse.es4x.test;

import io.reactiverse.es4x.ECMAEngine;
import io.reactiverse.es4x.Runtime;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class JSConsoleTest {

  private Runtime runtime;

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    runtime = new ECMAEngine(rule.vertx()).newContext();
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
