package io.reactiverse.es4x.test;

import io.reactiverse.es4x.Runtime;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static io.reactiverse.es4x.test.JS.commonjs;

@RunWith(VertxUnitRunner.class)
public class PathsTest {

  private Runtime runtime;

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    runtime = commonjs(rule.vertx());
  }

  @Test(timeout = 10000)
  public void testPaths(TestContext ctx) {

    final Async async = ctx.async();

    runtime.put("ctx", ctx);
    runtime.put("async", async);

    final String script =
      /// @language=JavaScript
      "require.paths().forEach(function (p) {\n" +
      "  ctx.assertTrue(p.indexOf('\\\\') === -1);\n" +
      "});\n" +
      "async.complete();";

    runtime.eval(script);
  }

  @Test(timeout = 10000)
  public void testPathsWin(TestContext ctx) {

    final Async async = ctx.async();

    runtime.put("ctx", ctx);
    runtime.put("async", async);

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

      runtime.eval(script);

    } finally {
      System.setProperty("os.name", osName);
      System.setProperty("user.dir", userDir);
      System.setProperty("user.home", userHome);
    }
  }

  @Test(timeout = 10000)
  public void testPathsNodePath(TestContext ctx) {

    final Async async = ctx.async();

    runtime.put("ctx", ctx);
    runtime.put("async", async);

    try {
      final String script =
        /// @language=JavaScript
        "require.NODE_PATH=\"" + System.getProperty("java.class.path") + "\"\n" +
          "require.paths().forEach(function (p) {\n" +
          "  ctx.assertTrue(p.indexOf('\\\\') === -1);\n" +
          "});\n" +
          "async.complete();";

      runtime.eval(script);
    } finally {
      runtime.eval("delete require.NODE_PATH;");
    }
  }

  @Test(timeout = 10000)
  public void testPathsNodePathWin(TestContext ctx) {

    final Async async = ctx.async();

    runtime.put("ctx", ctx);
    runtime.put("async", async);

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

      runtime.eval(script);
    } finally {
      System.setProperty("os.name", osName);
      System.setProperty("user.dir", userDir);
      System.setProperty("user.home", userHome);
      runtime.eval("delete require.NODE_PATH;");
    }
  }
}
