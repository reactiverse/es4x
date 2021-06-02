package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.reactiverse.es4x.test.JS.commonjs;
import static org.junit.Assert.*;

@RunWith(VertxUnitRunner.class)
public class PolyglotTest {

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Test
  public void testBasicPolyglotSupport() {
    Runtime runtime = commonjs(rule.vertx());
    Value polyglot = runtime.eval("Polyglot");
    assertNotNull(polyglot);
    assertFalse(polyglot.isNull());
  }

  @Test(expected = PolyglotException.class)
  public void testBasicPolyglotSupportDisabled() {
    System.setProperty("es4x.no-polyglot-access", "true");
    Runtime runtime = commonjs(rule.vertx());
    System.clearProperty("es4x.no-polyglot-access");
    runtime.eval("Polyglot");
  }

  @Test
  public void testBasicPolyglotSupportExplicit() {
    System.setProperty("es4x.no-polyglot-access", "false");
    Runtime runtime = commonjs(rule.vertx());
    Value polyglot = runtime.eval("Polyglot");
    assertNotNull(polyglot);
    assertFalse(polyglot.isNull());
  }
}
