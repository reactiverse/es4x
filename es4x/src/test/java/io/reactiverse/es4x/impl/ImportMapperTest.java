package io.reactiverse.es4x.impl;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(VertxUnitRunner.class)
public class ImportMapperTest {

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Test
  public void testSimple() throws MalformedURLException {
    ImportMapper mapper = new ImportMapper(
      new JsonObject()
        .put("imports", new JsonObject()
          .put("dotSlash", "./foo")
          .put("dotDotSlash", "../foo")
          .put("slash", "/foo")),
      new URL("https://base.example/path1/path2/path3"));

    assertEquals("https://base.example/foo", mapper.resolve("slash"));
    assertEquals("https://base.example/path1/path2/foo", mapper.resolve("dotSlash"));
    assertEquals("https://base.example/path1/foo", mapper.resolve("dotDotSlash"));
  }

  static class Case {
    final String specifier;
    final String referrer;
    final String result;

    Case(String specifier, String referrer, String result) {
      this.specifier = specifier;
      this.referrer = referrer;
      this.result = result;
    }
  }

  @Test
  public void testExampleComplex() throws MalformedURLException {
    ImportMapper mapper = new ImportMapper(
      new JsonObject(rule.vertx().fileSystem().readFileBlocking("import-map.json")));

    List<Case> cases = Arrays.asList(
      new Case("a", "/scope1/foo.mjs", "file:///a-1.mjs"),
      new Case("b", "/scope1/foo.mjs", "file:///b-1.mjs"),
      new Case("c", "/scope1/foo.mjs", "file:///c-1.mjs"),

      new Case("a", "/scope2/foo.mjs", "file:///a-2.mjs"),
      new Case("b", "/scope2/foo.mjs", "file:///b-1.mjs"),
      new Case("c", "/scope2/foo.mjs", "file:///c-1.mjs"),

      new Case("a", "/scope2/scope3/foo.mjs", "file:///a-2.mjs"),
      new Case("b", "/scope2/scope3/foo.mjs", "file:///b-3.mjs"),
      new Case("c", "/scope2/scope3/foo.mjs", "file:///c-1.mjs")
    );

    for (Case c : cases) {
      assertEquals(c.result, mapper.resolve(c.specifier, c.referrer));
    }
  }

  @Test
  public void testFromDeno1() throws MalformedURLException {
    ImportMapper mapper = new ImportMapper(
      new JsonObject()
        .put("imports", new JsonObject()
          .put("fmt/", "https://deno.land/std@0.90.0/fmt/")));

    assertEquals("https://deno.land/std@0.90.0/fmt/colors.ts", mapper.resolve("fmt/colors.ts"));
  }

  @Test
  public void testFromDeno2() throws MalformedURLException {
    ImportMapper mapper = new ImportMapper(
      new JsonObject()
        .put("imports", new JsonObject()
          .put("/", "./")));

    assertEquals("file://" + VertxFileSystem.getCWD() + "util.ts", mapper.resolve("/util.ts"));
  }

  @Test
  public void testFromDeno3() throws MalformedURLException {
    ImportMapper mapper = new ImportMapper(
      new JsonObject()
        .put("imports", new JsonObject()
          .put("/", "./src/")));

    assertEquals("file://" + VertxFileSystem.getCWD() + "src/util.ts", mapper.resolve("/util.ts"));
  }
}
