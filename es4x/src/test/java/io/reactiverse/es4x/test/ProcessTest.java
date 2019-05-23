package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
import io.reactiverse.es4x.impl.graal.GraalEngine;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.graalvm.polyglot.Value;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@RunWith(VertxUnitRunner.class)
public class ProcessTest {

  private Runtime runtime;

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    runtime = new GraalEngine(rule.vertx()).newContext();
  }

  @Test(timeout = 10000)
  public void testProcessEnv() throws Exception {
    Object res = runtime.eval("process.env");
    Map<String, String> env;
    if (res instanceof Map) {
      env = (Map) res;
    } else if (res instanceof Value) {
      env = ((Value) res).as(Map.class);
    } else {
      fail("Cannot cast response object!");
      return;
    }

    // PATH is usually available on all OSes
    assertNotNull(env.get("PATH"));
  }
}
