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
public class JSConsoleTest {

  private Runtime runtime;

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    runtime = commonjs(rule.vertx());
  }

  @Test
  public void shouldPrintToStdOut() {
    runtime.eval("console.log('test');");
  }

  @Test
  public void shouldPrintToStdOutAFormattedString() {
    runtime.eval("console.log('test %s', JSON.stringify({k:1}));");
  }

  @Test
  public void shouldPrintErrorToStdOut() {
    runtime.eval("console.error('test');");
  }

  @Test
  public void shouldPrintErrorToStdOutAFormattedString() {
    runtime.eval("console.error('test %s', JSON.stringify({k:1}));");
  }

  @Test
  public void throwsTest() {
    runtime.eval("try { throw new Error('Boom!'); } catch (e) { console.trace(e); }");
  }
}
