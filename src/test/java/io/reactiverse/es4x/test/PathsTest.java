package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
import io.reactiverse.es4x.Loader;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunnerWithParametersFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assume.assumeTrue;

@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(VertxUnitRunnerWithParametersFactory.class)
public class PathsTest {

  @Parameterized.Parameters
  public static List<String> engines() {
    return Arrays.asList("Nashorn", "GraalJS");
  }

  private final String engineName;
  private Loader loader;

  public PathsTest(String engine) {
    System.setProperty("es4x.engine", engine);
    engineName = engine;
  }

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    try {
      loader = Runtime.getCurrent().loader(rule.vertx());
    } catch (IllegalStateException e) {
      assumeTrue(engineName + " is not available", false);
    }
  }

  @Test(timeout = 10000)
  public void testPaths(TestContext ctx) throws Exception {

    final Async async = ctx.async();

    loader.put("ctx", ctx);
    loader.put("async", async);

    final String script =
      /// @language=JavaScript
      "require.paths().forEach(function (p) {\n" +
      "  ctx.assertTrue(p.indexOf('\\\\') === -1);\n" +
      "});\n" +
      "async.complete();";

    loader.eval(script);
  }

  @Test(timeout = 10000)
  public void testPathsWin(TestContext ctx) throws Exception {

    final Async async = ctx.async();

    loader.put("ctx", ctx);
    loader.put("async", async);

    final String userDir = System.getProperty("user.dir");
    final String userHome = System.getProperty("user.home");
    final String osName = System.getProperty("os.name");

    try {
      // fake windows
      System.setProperty("os.name", "Windows 10");
      System.setProperty("user.dir", "C:\\Users\\Administrator\\Projects\\vertx.x");
      System.setProperty("user.home", "C:\\Users\\Administrator");

      final String script =
        /// @language=JavaScript
        "require.paths().forEach(function (p) {\n" +
        "  ctx.assertTrue(p.indexOf('\\\\') === -1);\n" +
        "});\n" +
        "async.complete();";

      loader.eval(script);

    } finally {
      System.setProperty("os.name", osName);
      System.setProperty("user.dir", userDir);
      System.setProperty("user.home", userHome);
    }
  }

  @Test(timeout = 10000)
  public void testPathsNodePath(TestContext ctx) throws Exception {

    final Async async = ctx.async();

    loader.put("ctx", ctx);
    loader.put("async", async);

    try {
      final String script =
        /// @language=JavaScript
        "require.NODE_PATH=\"" + System.getProperty("java.class.path") + "\"\n" +
          "require.paths().forEach(function (p) {\n" +
          "  ctx.assertTrue(p.indexOf('\\\\') === -1);\n" +
          "});\n" +
          "async.complete();";

      loader.eval(script);
    } finally {
      loader.eval("delete require.NODE_PATH;");
    }
  }

  @Test(timeout = 10000)
  public void testPathsNodePathWin(TestContext ctx) throws Exception {

    final Async async = ctx.async();

    loader.put("ctx", ctx);
    loader.put("async", async);

    final String userDir = System.getProperty("user.dir");
    final String userHome = System.getProperty("user.home");
    final String osName = System.getProperty("os.name");

    try {
      // fake windows
      System.setProperty("os.name", "Windows 10");
      System.setProperty("user.dir", "C:\\Users\\Administrator\\Projects\\vertx.x");
      System.setProperty("user.home", "C:\\Users\\Administrator");

      // fake a complex path
      String[] paths = System.getProperty("java.class.path").split(File.pathSeparator);
      StringBuilder sb = new StringBuilder();
      for (String p : paths) {
        sb.append(p.replaceAll("/", "\\\\\\\\"));
        sb.append(";");
      }

      final String script =
        /// @language=JavaScript
        "require.NODE_PATH=\"" + sb.toString() + "\"\n" +
          "require.paths().forEach(function (p) {\n" +
          "  ctx.assertTrue(p.indexOf('\\\\') === -1);\n" +
          "});\n" +
          "async.complete();";

      loader.eval(script);
    } finally {
      System.setProperty("os.name", osName);
      System.setProperty("user.dir", userDir);
      System.setProperty("user.home", userHome);
      loader.eval("delete require.NODE_PATH;");
    }
  }
}
