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

import static io.reactiverse.es4x.test.JS.commonjs;

@RunWith(VertxUnitRunner.class)
public class UtilTest {

  private Runtime runtime;

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void initialize() {
    runtime = commonjs(rule.vertx());
  }

  @Test(timeout = 10000)
  public void shouldPromisifyAVertxAPI(TestContext should) {
    final Async test = should.async();
    // pass the assertion to the engine
    runtime.put("should", should);
    runtime.put("test", test);

    //language=JavaScript
    String script =
      "var util = require('util');\n" +
      "// vert.x promise\n" +
      "var fs = util.promisify(vertx.fileSystem());\n" +
      "fs.readFile('test.js').then(function (buff) {\n" +
      "  should.assertEquals('', buff.toString());\n" +
      "  test.complete();\n" +
      "}).catch(function (error) {\n" +
      "  should.fail(error);\n" +
      "});\n";

    runtime.eval(script);
  }

  @Test(timeout = 10000)
  public void shouldPromisifyAJavaScriptAPI(TestContext should) {
    final Async test = should.async();
    // pass the assertion to the engine
    runtime.put("should", should);
    runtime.put("test", test);

    //language=JavaScript
    String script =
      "var util = require('util');\n" +
      "// JS promise\n" +
      "var fs = {\n" +
      "  readFile: function (filename, callback) {\n" +
      "    if (filename === 'test.js') {\n" +
      "      process.nextTick(function () {\n" +
      "        callback(null, '');\n" +
      "      });\n" +
      "    } else {\n" +
      "      process.nextTick(function () {\n" +
      "        callback('Ooops!');\n" +
      "      });\n" +
      "    }\n" +
      "  }\n" +
      "}\n" +
      "var readFile = util.promisify(fs.readFile);\n" +
      "readFile('test.js').then(function (buff) {\n" +
      "  should.assertEquals('', buff.toString());\n" +
      "  test.complete();\n" +
      "}).catch(function (error) {\n" +
      "  should.fail(error);\n" +
      "});\n";

    runtime.eval(script);
  }
}
