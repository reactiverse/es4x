package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
import io.vertx.ext.unit.junit.RunTestOnContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

import static io.reactiverse.es4x.test.JS.getMember;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

@RunWith(Parameterized.class)
public class CommonJsNODEPATHTest {

  @Parameterized.Parameters
  public static List<String> engines() {
    return Arrays.asList("Nashorn", "GraalJS");
  }

  private final String engineName;
  private Runtime runtime;

  public CommonJsNODEPATHTest(String engine) {
    System.setProperty("es4x.engine", engine);
    engineName = engine;
  }

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    try {
      runtime = Runtime.getCurrent(rule.vertx());
    } catch (IllegalStateException e) {
      assumeTrue(engineName + " is not available", false);
    }
  }

  @Test
  public void shouldLoadAModuleFromACustomROOT() throws Exception {
    // this test shows that neither the path.js from the jar root or the node_modules module
    // is loaded as the node path takes precedence
    runtime.eval("process.env = { NODE_PATH: '" + "./src/test/resources/dist" + "' }");
    Object mod = runtime.require("./path");
    assertEquals("dist/path", getMember(mod, "message", String.class));
  }
}
