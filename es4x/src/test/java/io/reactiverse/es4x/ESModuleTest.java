package io.reactiverse.es4x;

import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class ESModuleTest {

  private Runtime runtime;

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    runtime = new ECMAEngine(rule.vertx()).newContext();
  }

  @Test
  public void testInline(TestContext should) {

    // mjs/foobar.mjs is not on the CWD but on the classpath
    // all IO is captured by a Vert.x file system implementation that
    // allows transparent access like in every other vert.x API
    Object result = runtime.eval("import {foo, bar} from './mjs/foobar';\n" +
        "foo();\n" +
        "bar();\n", "durp.mjs", false);

    should.assertEquals("bar", result.toString());
  }

  @Test
  public void testRelative(TestContext should) {

    // mjs/foobar.mjs is not on the CWD but on the classpath
    // all IO is captured by a Vert.x file system implementation that
    // allows transparent access like in every other vert.x API
    Object result = runtime.eval("import { a } from './mjs/moduleA'\n a();\n", "script.mjs", false);

    should.assertEquals("bar", result.toString());
  }

  @Test
  public void testMeta(TestContext should) {

    Object result = runtime.eval("import { f } from './mjs/meta'\n f();\n", "script.mjs", false);

    should.assertEquals("./mjs/meta", result.toString());
  }
}
