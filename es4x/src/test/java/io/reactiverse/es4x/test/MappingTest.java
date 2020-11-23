package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.graalvm.polyglot.Source;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.reactiverse.es4x.test.JS.commonjs;

@RunWith(VertxUnitRunner.class)
public class MappingTest {

  private Runtime runtime;
  private final Mapping mapping = new Mapping();

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    runtime = commonjs(rule.vertx());
    runtime.put("mapping", mapping);
  }


  @Test
  public void testInterop(TestContext should) {
    final Async test = should.async();
    runtime.put("should", should);
    runtime.put("test", test);

    try {
      runtime.eval(rule.vertx().fileSystem().readFileBlocking("mapping/json-object.js").toString());
    } catch (RuntimeException e) {
      should.fail(e);
    }

    rule.vertx().setTimer(300L, t -> test.complete());
  }
}
