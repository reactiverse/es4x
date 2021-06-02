package io.reactiverse.es4x.test;

import io.reactiverse.es4x.impl.VertxFileSystem;
import io.vertx.core.Vertx;
import io.vertx.core.impl.VertxInternal;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.*;

public class FSResolverTest {

  private static final String $ = File.separator;

  private static Vertx vertx;
  private static VertxFileSystem fs;

  @Before
  public void before() throws IOException {
    vertx = Vertx.vertx();
    fs = new VertxFileSystem(vertx, null, ".mjs", ".js");
  }

  @After
  public void after() {
    vertx.close();
  }

  @Test
  public void testResolver() throws URISyntaxException {
    String cwd = VertxFileSystem.getCWD();
    cwd = cwd.substring(0, cwd.length() - 1);
    String cache = ((VertxInternal) vertx).resolveFile("").getPath();

    // resolve to node modules
    assertEquals(cwd + $ + "node_modules" + $ + "index.js", fs.parsePath("index.js").toString());
    // resolve to cwd
    assertEquals(cwd + $ + "index.js", fs.parsePath("./index.js").toString());
    // resolve to cwd parent
    int s = cwd.lastIndexOf('/');
    assertEquals(cwd.substring(0, s) + $ + "index.js", fs.parsePath("../index.js").toString());
    // resolve to root
    assertEquals($ + "index.js", fs.parsePath("/index.js").toString());
    // rewrite to cwd
    assertEquals(cwd + $ + "index.js", fs.parsePath(cache + $ + "index.js").toString());
    // attempt download
    assertEquals(cwd + $ + "node_modules" + $ + ".download" + $ + "eedc890765ef80e2b57c447a50f911cd" + $ + "@vertx" + $ + "core@3.9.1" + $ + "options.mjs", fs.parsePath(new URI("https://unpkg.io/@vertx/core@3.9.1/options.mjs")).toString());
    // attempt from download cache
    assertEquals(cwd + $ + "node_modules" + $ + ".download" + $ + "eedc890765ef80e2b57c447a50f911cd" + $ + "@vertx" + $ + "core@3.9.1" + $ + "options.mjs", fs.parsePath("./node_modules/.download/eedc890765ef80e2b57c447a50f911cd/@vertx/core@3.9.1/options.mjs").toString());
    // attempt from download using missing url
    assertEquals(cwd + $ + "node_modules" + $ + ".download" + $ + "eedc890765ef80e2b57c447a50f911cd" + $ + "@vertx" + $ + "core@3.9.1" + $ + "module.mjs", fs.parsePath("./node_modules/.download/eedc890765ef80e2b57c447a50f911cd/@vertx/core@3.9.1/module.mjs").toString());
    // resolve to node modules index
    assertEquals(cwd + $ + "node_modules" + $ + "@vertx" + $ + "web", fs.parsePath("@vertx/web").toString());
  }
}
