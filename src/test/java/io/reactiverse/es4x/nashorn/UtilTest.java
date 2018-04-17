package io.reactiverse.es4x.nashorn;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import jdk.nashorn.api.scripting.JSObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(VertxUnitRunner.class)
public class UtilTest {

  private static ScriptEngine engine;

  @BeforeClass
  public static void setup() throws ScriptException, NoSuchMethodException {
    Loader loader = new Loader(Vertx.vertx());
    engine = loader.getEngine();
  }

  @Test(timeout = 10000)
  public void shouldPromisifyAVertxAPI(TestContext should) throws ScriptException {
    final Async test = should.async();
    // pass the assertion to the engine
    engine.put("should", should);
    engine.put("test", test);

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

    engine.eval(script);

    test.await();

    engine.put("should", null);
    engine.put("test", null);
  }

  @Test(timeout = 10000)
  public void shouldPromisifyAJavaScriptAPI(TestContext should) throws ScriptException {
    final Async test = should.async();
    // pass the assertion to the engine
    engine.put("should", should);
    engine.put("test", test);

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

    engine.eval(script);

    test.await();

    engine.put("should", null);
    engine.put("test", null);
  }
}
