package io.reactiverse.es4x.runtime;

import com.google.gson.Gson;
import org.eclipse.aether.artifact.Artifact;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

class InstallCommand {

  private static final Gson GSON = new Gson();

  public static void main(List<String> arguments, String output) throws IOException {
    main(arguments, output, false);
  }

  public static void main(List<String> arguments, String output, boolean excludeVersion) throws IOException {
    final Set<String> dependencies = new HashSet<>();
    final File base = new File("node_modules");

    final String root;
    final String target = output == null ? "@lib" : output;

    if (arguments.size() > 0) {
      root = arguments.get(0);
      dependencies.addAll(arguments.subList(1, arguments.size()));
    } else {
      root = "io.reactiverse:es4x:[0.6.1,)";
      if (base.exists() && base.isDirectory()) {
        processModules(base, dependencies);
      }
    }

    Resolver resolver = new Resolver();
    File libs = new File(base, target);
    if (!libs.exists()) {
      if (!libs.mkdirs()) {
        throw new RuntimeException("Failed to mkdirs 'node_modules/" + target + "'.");
      }
    }
    for (Artifact a : resolver.resolve(root, dependencies)) {
      File destination;
      if (excludeVersion) {
        destination = new File(libs, a.getArtifactId() + "." + a.getExtension());
      } else {
        destination = new File(libs, a.getFile().getName());
      }
      if (!destination.exists()) {
        Files.copy(a.getFile().toPath(), destination.toPath());
      }
    }
  }

  private static void processModules(File dir, Set<String> dependencies) throws IOException {
    File[] mods = dir.listFiles(File::isDirectory);
    if (mods != null) {
      for (File mod : mods) {
        String name = mod.getName();
        if (name.charAt(0) == '@') {
          processModules(mod, dependencies);
          continue;
        }

        File json = new File(mod, "package.json");

        if (json.exists()) {
          Map npm = readJSON(json);
          if (npm.containsKey("maven")) {
            final Map maven = (Map) npm.get("maven");
            // add this dependency
            dependencies.add(maven.get("groupId") + ":" + maven.get("artifactId") + ":" + maven.get("version"));
          }
        }
        File submod = new File(mod, "node_modules");
        if (submod.exists() && submod.isDirectory()) {
          processModules(submod, dependencies);
        }
      }
    }
  }

  private static Map readJSON(File file) throws IOException {
    try (InputStream is = new FileInputStream(file)) {
      return GSON.fromJson(new InputStreamReader(is), Map.class);
    }
  }
}
