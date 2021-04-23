package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static io.reactiverse.es4x.test.JS.esm;

@RunWith(VertxUnitRunner.class)
public class ESMPrefixTest {

  private Runtime runtime;

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() throws IOException {
    runtime = esm(rule.vertx(), "import-map");
  }

  @Test
  public void testESMPrefix() {
    // this test shows that if the prefix was set
    // then all relative scripts will be prefixed by it
    runtime.eval("import { hi } from 'verticle4'; hi();", "boot.mjs", false);
  }
}
