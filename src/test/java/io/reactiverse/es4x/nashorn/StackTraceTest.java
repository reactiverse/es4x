package io.reactiverse.es4x.nashorn;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import static io.reactiverse.es4x.AsyncError.*;

@RunWith(VertxUnitRunner.class)
public class StackTraceTest {

  private static ScriptEngine engine;

  @BeforeClass
  public static void setup() throws ScriptException, NoSuchMethodException {
    Loader loader = new Loader(Vertx.vertx());
    engine = loader.getEngine();
  }

  @Test(timeout = 10000)
  public void shouldGenerateUselessStackTrace(TestContext should) throws ScriptException {
    final Async test = should.async();
    // pass the assertion to the engine
    engine.put("should", should);
    engine.put("test", test);

    engine.eval("require('./stacktraces')");
    test.await();
  }

  @Test
  public void shouldReturnNullWhenNull(TestContext should) {
    should.assertNull(asyncError((Throwable) null));
  }

  @Test
  public void shouldReturnThrowableWhenNotNull(TestContext should) {
    should.assertTrue(asyncError(new RuntimeException("Oops!")) instanceof Throwable);
  }
}
