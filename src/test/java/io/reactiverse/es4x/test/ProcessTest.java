package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
import io.reactiverse.es4x.Loader;
import io.vertx.ext.unit.junit.RunTestOnContext;
import org.graalvm.polyglot.Value;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

@RunWith(Parameterized.class)
public class ProcessTest {

  @Parameterized.Parameters
  public static List<String> engines() {
    return Arrays.asList("Nashorn", "GraalJS");
  }

  private final String engineName;
  private Loader loader;

  public ProcessTest(String engine) {
    System.setProperty("es4x.engine", engine);
    engineName = engine;
  }

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    loader = Runtime.getCurrent().loader(rule.vertx());
    assumeTrue(loader.name().equalsIgnoreCase(engineName));
  }

  @Test(timeout = 10000)
  public void testProcessEnv() throws Exception {
    Object res = loader.eval("process.env");
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
