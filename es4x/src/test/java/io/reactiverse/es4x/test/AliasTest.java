package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.reactiverse.es4x.test.JS.runtime;

@RunWith(VertxUnitRunner.class)
public class AliasTest {

  private Runtime runtime;

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    System.setProperty(
      "js.commonjs-core-modules-replacements",
      "crypto:./alias/alias/crypto.js,fs:./alias2/alias/fs.js,stream:./alias2/specialStream.js");
    runtime = runtime(rule.vertx());
    System.getProperties().remove("js.commonjs-core-modules-replacements");
  }

  @Test(timeout = 10000)
  public void testAlias() {
    runtime.eval("require('./alias')");
  }

  @Test(timeout = 10000)
  public void testAlias2() {
    runtime.eval("require('./alias2')");
  }
}
