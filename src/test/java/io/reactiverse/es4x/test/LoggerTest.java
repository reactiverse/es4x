package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Loader;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.unit.junit.RunTestOnContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assume.assumeTrue;

@RunWith(Parameterized.class)
public class LoggerTest {

  final Logger log = LoggerFactory.getLogger(LoggerTest.class);

  @Parameterized.Parameters
  public static List<String> engines() {
    return Arrays.asList("Nashorn", "GraalVM");
  }

  final String engineName;
  private Loader loader;

  public LoggerTest(String engine) {
    System.setProperty("es4x.engine", engine);
    engineName = engine;
  }

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    loader = Loader.create(rule.vertx());
    assumeTrue(loader.name().equalsIgnoreCase(engineName));
  }

  @Test
  public void shouldThrowAnErrorIfFileCantBeFound() {
    try {
      throw new RuntimeException();
    } catch (RuntimeException e) {
      log.error("Oops!", e);
    }
  }

  @Test
  public void shouldPrettyPrintException() {
    Object module = loader.require("./exp.js");
    try {
      loader.invokeMethod(module, "a");
    } catch (RuntimeException e) {
      log.error("Error from Script", e);
    }
  }
}
