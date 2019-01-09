package io.reactiverse.es4x.commands;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.cli.CLIException;
import io.vertx.core.cli.annotations.*;
import io.vertx.core.spi.launcher.DefaultCommand;
import org.eclipse.aether.artifact.Artifact;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

@Name("install")
@Summary("Installs required jars from maven to 'node_modules'.")
public class InstallCommand extends DefaultCommand {

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final Properties VERSIONS = new Properties();

  static {
    MAPPER.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
    try (InputStream is = InstallCommand.class.getClassLoader().getResourceAsStream("META-INF/es4x-commands/VERSIONS.properties")) {
      if (is == null) {
        throw new IllegalStateException("Cannot find 'META-INF/es4x-commands/VERSIONS.properties' on classpath");
      }
      VERSIONS.load(is);
    } catch (IOException e) {
      throw new IllegalStateException(e.getMessage());
    }
  }

  private boolean force;

  @Option(longName = "force", shortName = "f", flag = true)
  @Description("Will always install a basic runtime in the current working dir.")
  public void setForce(boolean force) {
    this.force = true;
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
          Map npm = MAPPER.readValue(json, Map.class);
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

  @Override
  public void run() throws CLIException {
    installNodeModules();

    final File base = new File("node_modules");
    final File libs = new File(base, "@lib");

    if (force || libs.exists()) {
      final double version = Double.parseDouble(System.getProperty("java.specification.version"));
      final String vm = System.getProperty("java.vm.name");
      if (!vm.toLowerCase().contains("graalvm")) {
        if (version >= 11) {
          System.err.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
          System.err.println("@ Installing JVMCI to run GraalJS on stock JDK! @");
          System.err.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
          // graaljs + dependencies
          installGraalJS();
          // jvmci compiler + dependencies
          installGraalJMVCICompiler();
        } else {
          System.err.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
          System.err.println("@ Current JDK only supports Nashorn! @");
          System.err.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        }
      }
    }
  }

  private void installGraalJS() throws CLIException {
    final File base = new File("node_modules");

    File libs = new File(base, "@lib");
    if (!libs.exists()) {
      if (!libs.mkdirs()) {
        throw new IllegalStateException("Failed to mkdirs 'node_modules/@lib'.");
      }
    }

    try {
      Resolver resolver = new Resolver();

      for (Artifact a : resolver.resolve("org.graalvm.js:js:" + VERSIONS.getProperty("graalvm"), Arrays.asList("org.graalvm.tools:profiler:" + VERSIONS.getProperty("graalvm"), "org.graalvm.tools:chromeinspector:" + VERSIONS.getProperty("graalvm")))) {
        File destination = new File(libs, a.getFile().getName());
        if (!destination.exists()) {
            Files.copy(a.getFile().toPath(), destination.toPath());
        }
      }
    } catch (IOException e) {
      throw new CLIException(e.getMessage(), e);
    }
  }

  private void installGraalJMVCICompiler() throws CLIException {
    final File base = new File("node_modules");

    File libs = new File(base, "@jvmci");
    if (!libs.exists()) {
      if (!libs.mkdirs()) {
        throw new IllegalStateException("Failed to mkdirs 'node_modules/@jvmci'.");
      }
    }

    try {
      Resolver resolver = new Resolver();

      for (Artifact a : resolver.resolve("org.graalvm.compiler:compiler:" + VERSIONS.getProperty("graalvm"), Collections.emptyList())) {
        File destination = new File(libs, a.getArtifactId() + "." + a.getExtension());
        if (!destination.exists()) {
          Files.copy(a.getFile().toPath(), destination.toPath());
        }
      }
    } catch (IOException e) {
      throw new CLIException(e.getMessage(), e);
    }
  }

  private void installNodeModules() throws CLIException {
    final File base = new File("node_modules");
    final Set<String> dependencies = new HashSet<>();

    if (base.exists() && base.isDirectory()) {
      try {
        processModules(base, dependencies);
      } catch (IOException e) {
        throw new CLIException(e.getMessage(), e);
      }
    }

    if (force || dependencies.size() > 0) {
      File libs = new File(base, "@lib");

      try {
        Resolver resolver = new Resolver();

        for (Artifact a : resolver.resolve("io.reactiverse:es4x:" + VERSIONS.getProperty("es4x"), dependencies)) {

          if (!libs.exists()) {
            if (!libs.mkdirs()) {
              throw new IllegalStateException("Failed to mkdirs 'node_modules/@lib'.");
            }
          }

          File destination = new File(libs, a.getFile().getName());
          if (!destination.exists()) {
            Files.copy(a.getFile().toPath(), destination.toPath());
          }
        }
      } catch (IOException e) {
        throw new CLIException(e.getMessage(), e);
      }
    }
  }
}
