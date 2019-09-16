package io.reactiverse.es4x.test;

import io.reactiverse.es4x.ECMAEngine;
import io.reactiverse.es4x.Runtime;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.reactiverse.es4x.test.JS.getMember;
import static io.reactiverse.es4x.test.JS.isFunction;
import static org.junit.Assert.assertTrue;

@RunWith(VertxUnitRunner.class)
public class CommonJSCyclicTest {

  private Runtime runtime;

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    runtime = new ECMAEngine(rule.vertx()).newContext();
  }

  @Test
  public void shouldHaveTheSameSenseOfAnObjectInAllPlaces() {
    Object stream = runtime.require("./lib/cyclic2/stream.js");
    assertTrue(isFunction(stream));
    Object readable = getMember(stream, "Readable");
    assertTrue(isFunction(readable));
    Object stream2 = getMember(readable, "Stream");
    assertTrue(isFunction(stream2));
  }
}
