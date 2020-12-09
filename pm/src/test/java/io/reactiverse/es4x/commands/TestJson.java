package io.reactiverse.es4x.commands;

import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class TestJson {

  @Test
  public void testEncodeSlash() {
    String s = new JSONObject("{\"@es4x/create\":1, \"a\":true}").toString(2);
    assertFalse(s.contains("\\/"));
  }
}
