package io.reactiverse.es4x;

import io.reactiverse.es4x.impl.Utils;
import org.junit.Test;

import java.io.File;
import java.net.URL;

public class UtilsTest {

  @Test
  public void testFileToURI() {
    URL url = Utils.fileToURL(new File("C:/Program Files/REST Service/bin/"));
    System.out.println(url);
  }
}
