package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.graalvm.polyglot.Value;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.reactiverse.es4x.test.JS.*;
import static org.junit.Assert.assertTrue;

@RunWith(VertxUnitRunner.class)
public class CommonJSCyclicTest {

  private Runtime runtime;

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    runtime = commonjs(rule.vertx());
  }

  @Test
  public void shouldHaveTheSameSenseOfAnObjectInAllPlaces() {
    Value stream = require(runtime, "./lib/cyclic2/stream.js");
    assertTrue(isFunction(stream));
    Value readable = getMember(stream, "Readable");
    assertTrue(isFunction(readable));
    Value stream2 = getMember(readable, "Stream");
    assertTrue(isFunction(stream2));
  }
}
