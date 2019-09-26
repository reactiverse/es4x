package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.graalvm.polyglot.Value;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.reactiverse.es4x.test.JS.commonjs;
import static org.junit.Assert.*;

@RunWith(VertxUnitRunner.class)
public class PolyglotTest {

  private Runtime runtime;

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    System.setProperty("es4x.polyglot", "true");
    runtime = commonjs(rule.vertx());
    System.setProperty("es4x.polyglot", "false");
  }

  @Test
  public void testBasicPolyglotSupport() {
    Value polyglot = runtime.eval("Polyglot");
    assertNotNull(polyglot);
    assertFalse(polyglot.isNull());
  }
}
