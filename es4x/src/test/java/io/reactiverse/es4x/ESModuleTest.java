package io.reactiverse.es4x;

import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.reactiverse.es4x.test.Helper.getRuntime;
import static org.junit.Assert.fail;

@RunWith(VertxUnitRunner.class)
public class ESModuleTest {

  private Runtime runtime;

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    runtime = getRuntime(rule.vertx(), "GraalJS");
    if (runtime != null) {
      runtime.setContentType("application/javascript+module");
    } else {
      fail("NULL runtime");
    }
  }

  @Test
  public void testInline(TestContext should) throws Exception {

    // mjs/foobar.mjs is not on the CWD but on the classpath
    // all IO is captured by a Vert.x file system implementation that
    // allows transparent access like in every other vert.x API
    Object result = runtime.eval("import {foo, bar} from 'mjs/foobar.mjs';\n" +
        "foo();\n" +
        "bar();\n", "durp.mjs", false);

    should.assertEquals("bar", result.toString());
  }

  @Test
  public void testRelative(TestContext should) throws Exception {

    // mjs/foobar.mjs is not on the CWD but on the classpath
    // all IO is captured by a Vert.x file system implementation that
    // allows transparent access like in every other vert.x API
    Object result = runtime.eval("import { a } from 'mjs/moduleA.mjs'\n a();\n");

    should.assertEquals("bar", result.toString());
  }
}
