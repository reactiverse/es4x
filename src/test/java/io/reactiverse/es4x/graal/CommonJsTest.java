package io.reactiverse.es4x.graal;

import io.vertx.core.Vertx;
import org.graalvm.polyglot.Value;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CommonJsTest {

  private static final Vertx vertx = Vertx.vertx();
  private static Loader loader;

  @BeforeClass
  public static void beforeClass() {
    loader = new Loader(vertx);
  }

  @Test
  public void shouldThrowAnErrorIfFileCantBeFound() {
    try {
      loader.require("./not_found.js");
      fail();
    } catch (Exception e) {
      assertEquals("ModuleError: Module \"./not_found.js\" was not found", e.getMessage());
    }
  }

  @Test
  public void shouldNotWrapErrorsEncounteredWhenLoadingModule() {
    try {
      loader.require("./lib/throws");
      fail();
    } catch (Exception e) {
      assertEquals("ReferenceError: bar is not defined", e.getMessage());
    }
  }

  @Test
  public void shouldSupportNestedRequires() {
    Value outer = loader.require("./lib/outer");
    Value quadruple = outer.getMember("quadruple");
    Value n = quadruple.execute(2);
    assertEquals(8, n.asInt());
  }

  @Test
  public void shouldSupportAnIdWithAnExtension() {
    Value outer = loader.require("./lib/outer.js");
    Value quadruple = outer.getMember("quadruple");
    Value n = quadruple.execute(2);
    assertEquals(8, n.asInt());
  }

  @Test
  public void shouldReturnDotJsonFileAsJsonObject() {
    Value json = loader.require("./lib/some_data.json");
    assertEquals("This is a JSON file", json.getMember("description").asString());
  }

  @Test
  @Ignore
  public void shouldCacheModulesInRequireCache() {
    Value outer = loader.require("./lib/outer.js");
    Value require = loader.eval("(function(name) { return global[name] })").execute("require");
    assertEquals(require.getMember("cache").getMember(outer.getMember("filename").asString()), outer);
    Value outer2 = loader.require("./lib/outer.js");
    assertEquals(outer, outer2);
  }

  @Test
  public void shoudlHandleCyclicDependencies() {
    Value main = loader.require("./lib/cyclic");
    assertEquals("Hello from A", (main.getMember("a")).getMember("fromA").asString());
    assertEquals("Hello from B", (main.getMember("b")).getMember("fromB").asString());
  }
}
