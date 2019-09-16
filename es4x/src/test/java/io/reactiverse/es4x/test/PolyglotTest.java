package io.reactiverse.es4x.test;

import io.reactiverse.es4x.ECMAEngine;
import io.reactiverse.es4x.Runtime;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.graalvm.polyglot.Value;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(VertxUnitRunner.class)
public class PolyglotTest {

  private Runtime runtime;

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    System.setProperty("es4x.polyglot", "true");
    runtime = new ECMAEngine(rule.vertx()).newContext();
    System.setProperty("es4x.polyglot", "false");
  }

  @Test
  public void testBasicPolyglotSupport() throws Exception {
    Value polyglot = runtime.eval("Polyglot");
    assertNotNull(polyglot);
    assertFalse(polyglot.isNull());
  }
}
