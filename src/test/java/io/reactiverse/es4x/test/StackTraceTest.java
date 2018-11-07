package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunnerWithParametersFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

import static io.reactiverse.es4x.AsyncError.*;
import static org.junit.Assume.assumeTrue;

@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(VertxUnitRunnerWithParametersFactory.class)
public class StackTraceTest {

  @Parameterized.Parameters
  public static List<String> engines() {
    return Arrays.asList("Nashorn", "GraalJS");
  }

  private final String engineName;
  private Runtime runtime;

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  public StackTraceTest(String engine) {
    System.setProperty("es4x.engine", engine);
    engineName = engine;
  }

  @Before
  public void initialize() {
    try {
      runtime = Runtime.getCurrent(rule.vertx());
    } catch (IllegalStateException e) {
      assumeTrue(engineName + " is not available", false);
    }
  }

  @Test(timeout = 10000)
  public void shouldGenerateUsefulStackTrace(TestContext should) throws Exception {
    final Async test = should.async();
    // pass the assertion to the engine
    runtime.put("should", should);
    runtime.put("test", test);

    runtime.eval("require('./stacktraces')");
  }

  @Test(timeout = 10000)
  public void shouldGenerateUsefulStackTraceFromJS(TestContext should) throws Exception {
    final Async test = should.async();
    // pass the assertion to the engine
    runtime.put("should", should);
    runtime.put("test", test);

    runtime.eval("require('./stacktraces/jserror')");
  }

  @Test
  public void shouldReturnNullWhenNull(TestContext should) {
    should.assertNull(asyncError((Throwable) null, null));
  }

  @Test
  public void shouldReturnThrowableWhenNotNull(TestContext should) {
    should.assertTrue(asyncError(new RuntimeException("Oops!"), null) instanceof Throwable);
  }
}
