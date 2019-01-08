package io.reactiverse.es4x.runtime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.eclipse.aether.artifact.Artifact;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

class InitCommand {

  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  public static void main() throws IOException {

    Map npm = readJSON(new File("package.json"));

    Map scripts = (Map) npm.get("scripts");
    if (scripts == null) {
      scripts = new HashMap();
      npm.put("scripts", scripts);
    }

    String main = (String) npm.get("main");
    if (main == null) {
      main = "index.js";
    }
    String test = main;
    if (test.endsWith(".js")) {
      test = test.substring(0, test.length() - 3) + ".test.js";
    }

    scripts.put("start", "es4x run js:" + main);
    scripts.put("test", "es4x test js:" + test);

    try(FileWriter writer = new FileWriter("package.json")) {
      GSON.toJson(npm, writer);
    }
  }

  private static Map readJSON(File file) throws IOException {
    try (InputStream is = new FileInputStream(file)) {
      return GSON.fromJson(new InputStreamReader(is), Map.class);
    }
  }
}
