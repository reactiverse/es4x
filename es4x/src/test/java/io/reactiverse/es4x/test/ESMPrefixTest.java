package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
import io.reactiverse.es4x.impl.graal.GraalEngine;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class ESMPrefixTest {

  private Runtime runtime;

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    System.setProperty("es4x.prefix", "./prefix/");
    runtime = new GraalEngine(rule.vertx()).newContext();
    System.setProperty("es4x.prefix", "");
  }

  @Test
  public void testESMPrefix() throws Exception {
    // this test shows that if the prefix was set
    // then all relative scripts will be prefixed by it
    runtime.eval("import { hi } from './verticle4'; hi();", "boot.mjs", false);
  }
}
