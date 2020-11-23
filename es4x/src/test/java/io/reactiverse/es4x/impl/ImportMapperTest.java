package io.reactiverse.es4x.impl;

import io.vertx.core.json.JsonObject;
import org.junit.Test;

import java.net.URL;

public class ImportMapperTest {

  @Test
  public void testSimple() throws Exception {
    System.out.println(
      new ImportMapper(
        new JsonObject()
          .put("imports", new JsonObject()
            .put("dotSlash", "./foo")
            .put("dotDotSlash", "../foo")
            .put("slash", "/foo")),
        new URL("https://base.example/path1/path2/path3"))
        .resolve("slash")
    );
  }
}
