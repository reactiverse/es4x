package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import static io.reactiverse.es4x.test.JS.commonjs;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@RunWith(VertxUnitRunner.class)
public class ProcessTest {

  private Runtime runtime;

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    runtime = commonjs(rule.vertx());
  }

  @Test(timeout = 10000)
  public void testProcessEnv() {
    Map env = runtime.eval("process.env").as(Map.class);
    // PATH is usually available on all OSes
    assertNotNull(env.get("PATH"));
  }

  @Test(timeout = 10000)
  public void testProcessCWD() {
    String cwd = runtime.eval("process.cwd()").as(String.class);
    assertNotNull(cwd);
    if (cwd.length() > 1) {
      assertFalse(cwd.endsWith("/"));
    }
  }
}
