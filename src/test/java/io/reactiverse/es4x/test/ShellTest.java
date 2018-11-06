package io.reactiverse.es4x.test;

import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ShellTest {

  private static final String CP = System.getProperty("java.class.path");
  private static final String JAVA = new File(System.getProperty("java.home"), "bin/java").getAbsolutePath();
  private static final File CWD = new File(System.getProperty("user.dir"));

  private int runScript(String script, List<String> shellArgs) throws IOException, InterruptedException {

    final List<String> args = new ArrayList<>(
      Arrays.asList(
        JAVA,
        "-cp",
        CP,
        "io.reactiverse.es4x.Shell"));

    if (script != null) {
      final File file = File.createTempFile("ex4x-test", ".js");
      file.deleteOnExit();

      try (FileOutputStream out = new FileOutputStream(file)) {
        out.write(script.getBytes(StandardCharsets.UTF_8));
      }

      args.add(file.getAbsolutePath());
    }

    if (shellArgs != null) {
      args.addAll(shellArgs);
    }

    return
      new ProcessBuilder(args)
        .directory(CWD)
        .inheritIO()
        .start()
        .waitFor();
  }

  @Test(timeout = 10000)
  public void shouldRunAScript() throws Exception {
    assertEquals(0, runScript("process.exit(0);", null));
  }

  @Test(timeout = 10000)
  public void shouldFailWhenTheresNoScript() throws Exception {
    assertEquals(1, runScript(null, null));
  }

  @Test(timeout = 10000)
  public void shouldRunAScriptInClusterMode() throws Exception {
    assertEquals(0, runScript("process.exit(0);", Arrays.asList("-clustered")));
  }
}
