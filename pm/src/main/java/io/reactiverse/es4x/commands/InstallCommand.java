/*
 * Copyright 2019 Red Hat, Inc.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  The Apache License v2.0 is available at
 *  http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */
package io.reactiverse.es4x.commands;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactiverse.es4x.ES4X;
import io.vertx.core.cli.CLIException;
import io.vertx.core.cli.annotations.*;
import io.vertx.core.spi.launcher.DefaultCommand;
import org.eclipse.aether.artifact.Artifact;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

@Name("install")
@Summary("Installs required jars from maven to 'node_modules'.")
public class InstallCommand extends DefaultCommand {

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final Properties VERSIONS = new Properties();

  private static String OS = System.getProperty("os.name").toLowerCase();

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

  private static boolean isWindows() {
    return OS.contains("win");
  }

  private static boolean isUnix() {
    return
        OS.contains("nix") ||
        OS.contains("nux") ||
        OS.contains("aix") ||
        OS.contains("mac") ||
        OS.contains("sunos");
  }

  private boolean force;
  private String launcher;

  @Option(longName = "force", shortName = "f", flag = true)
  @Description("Will always install a basic runtime in the current working dir.")
  public void setForce(boolean force) {
    this.force = force;
  }

  @Option(longName = "launcher", shortName = "l")
  @Description("Will always install a basic runtime in the current working dir.")
  public void setLauncher(String launcher) {
    this.launcher = launcher;
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
    final Set<String> artifacts = new HashSet<>();
    installNodeModules(artifacts);

    final File base = new File("node_modules");
    final File libs = new File(base, ".lib");

    if (force || libs.exists()) {
      final double version = Double.parseDouble(System.getProperty("java.specification.version"));
      final String vm = System.getProperty("java.vm.name");
      if (!vm.toLowerCase().contains("graalvm")) {
        if (version >= 11) {
          System.err.println("\u001B[1m\u001B[33mInstalling JVMCI to run GraalJS on stock JDK!\u001B[0m");
          // graaljs + dependencies
          installGraalJS(artifacts);
          // jvmci compiler + dependencies
          installGraalJMVCICompiler();
        } else {
          System.err.println("\u001B[1m\u001B[31mCurrent JDK only supports Nashorn!\u001B[0m");
        }
      }
    }

    if (force || artifacts.size() > 0) {
      createLauncher(artifacts);
    }
  }

  private void installGraalJS(Set<String> artifacts) throws CLIException {
    final File base = new File("node_modules");

    File libs = new File(base, ".lib");
    if (!libs.exists()) {
      if (!libs.mkdirs()) {
        throw new IllegalStateException("Failed to mkdirs 'node_modules/.lib'.");
      }
    }

    try {
      Resolver resolver = new Resolver();

      for (Artifact a : resolver.resolve("org.graalvm.js:js:" + VERSIONS.getProperty("graalvm"), Arrays.asList("org.graalvm.tools:profiler:" + VERSIONS.getProperty("graalvm"), "org.graalvm.tools:chromeinspector:" + VERSIONS.getProperty("graalvm")))) {
        artifacts.add("../.lib/" + a.getFile().getName());
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

    File libs = new File(base, ".jvmci");
    if (!libs.exists()) {
      if (!libs.mkdirs()) {
        throw new IllegalStateException("Failed to mkdirs 'node_modules/.jvmci'.");
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

  private void installNodeModules(Set<String> artifacts) throws CLIException {
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
      File libs = new File(base, ".lib");

      try {
        Resolver resolver = new Resolver();

        for (Artifact a : resolver.resolve("io.reactiverse:es4x:" + VERSIONS.getProperty("es4x"), dependencies)) {

          if (!libs.exists()) {
            if (!libs.mkdirs()) {
              throw new IllegalStateException("Failed to mkdirs 'node_modules/.lib'.");
            }
          }
          artifacts.add("../.lib/" + a.getFile().getName());
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

  private void createLauncher(Set<String> artifacts) throws CLIException {

    File json = new File("package.json");

    if (json.exists()) {

      try {
        if (launcher == null) {
          Map npm = MAPPER.readValue(json, Map.class);
          if (npm.containsKey("name")) {
            launcher = (String) npm.get("name");
          } else {
            throw new IllegalStateException("'package.json' doesn't contain a 'name' property!");
          }
        }

        final File base = new File("node_modules");
        File bin = new File(base, ".bin");
        if (!bin.exists()) {
          if (!bin.mkdirs()) {
            throw new IllegalStateException("Failed to mkdirs 'node_modules/.bin'.");
          }
        }

        final Manifest manifest = new Manifest();
        final Attributes attributes = manifest.getMainAttributes();

        attributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        attributes.put(new Attributes.Name("Created-By"), "ES4X " + VERSIONS.getProperty("es4x"));
        attributes.put(new Attributes.Name("Built-By"), System.getProperty("user.name"));
        attributes.put(new Attributes.Name("Build-Jdk"), System.getProperty("java.version"));
        attributes.put(Attributes.Name.MAIN_CLASS, ES4X.class.getName());
        attributes.put(Attributes.Name.CLASS_PATH, String.join(" ", artifacts));
        attributes.put(new Attributes.Name("Main-Verticle"), "js:./");
        attributes.put(new Attributes.Name("Main-Command"), "run");

        try (JarOutputStream target = new JarOutputStream(new FileOutputStream(new File(bin, launcher + ".jar")), manifest)) {
          // nothing to be added!
        }

        // create the launcher scripts
        if (isUnix()) {
          createUNIXScript(bin, launcher);
        }
        if (isWindows()) {
          createDOSScript(bin, launcher);
        }

      } catch (IOException e) {
        throw new CLIException(e.getMessage(), e);
      }
    }
  }

  private void createUNIXScript(File bin, String launcher) throws IOException {

    String script =
      "#!/bin/sh\n" +
      "(set -o igncr) 2>/dev/null && set -o igncr; # cygwin encoding fix\n" +
      "\n" +
      "# fight simlinks and avoid readlink -f which doesn't exist on Darwin and Solaris\n" +
      "pushd . > /dev/null\n" +
      "basedir=\"${BASH_SOURCE[0]}\";\n" +
      "while([ -h \"${basedir}\" ]); do\n" +
      "    cd \"`dirname \"${basedir}\"`\"\n" +
      "    basedir=\"$(readlink \"`basename \"${basedir}\"`\")\";\n" +
      "done\n" +
      "cd \"`dirname \"${basedir}\"`\" > /dev/null\n" +
      "basedir=\"`pwd`\";\n" +
      "popd  > /dev/null\n" +
      "\n" +
      "case `uname` in\n" +
      "    *CYGWIN*) basedir=`cygpath -w \"$basedir\"`;;\n" +
      "esac\n" +
      "\n" +
      "JAVA_EXE=\"$JAVA_HOME/bin/java\"\n" +
      "if ! [[ -x \"$JAVA_EXE\" ]]; then\n" +
      "  JAVA_EXE=java\n" +
      "fi\n" +
      "\n" +
      "if [[ -d \"$basedir/../.jvmci\" ]]; then\n" +
      "  JVMCI=\"--module-path=$basedir/../.jvmci -XX:+UnlockExperimentalVMOptions -XX:+EnableJVMCI --upgrade-module-path=$basedir/../.jvmci/compiler.jar\"\n" +
      "fi\n" +
      "\n" +
      "exec \"$JAVA_EXE\" -XX:+IgnoreUnrecognizedVMOptions $JVMCI $JAVA_OPTS -jar \"$basedir/" + launcher + ".jar\" \"$@\"\n";

    final File exe = new File(bin, launcher);
    try (FileOutputStream out = new FileOutputStream(exe)) {
      out.write(script.getBytes(StandardCharsets.UTF_8));
    }

    // this is a best effort
    if (!exe.setExecutable(true, false)) {
      throw new IllegalStateException("Cannot set script 'node_modules/.bin/" + launcher + "'executable!");
    }
  }

  private void createDOSScript(File bin, String launcher) throws IOException {

    String script =
      "@ECHO OFF\n" +
        "\n" +
        "SETLOCAL\n" +
        "\n" +
        "SET \"JAVA_EXE=%JAVA_HOME%\\bin\\java.exe\"\n" +
        "IF NOT EXIST \"%JAVA_EXE%\" (\n" +
        "  SET \"JAVA_EXE=java\"\n" +
        ")\n" +
        "\n" +
        "IF EXIST \"%~dp0\\..\\.jvmci\" (\n" +
        "  SET \"JVMCI=--module-path=%~dp0\\..\\.jvmci -XX:+UnlockExperimentalVMOptions -XX:+EnableJVMCI --upgrade-module-path=%~dp0\\..\\.jvmci\\compiler.jar\"\n" +
        ")\n" +
        "\n" +
        "\"%JAVA_EXE%\" -XX:+IgnoreUnrecognizedVMOptions \"%JVMCI%\" \"%JAVA_OPTS%\" -jar \"%~dp0\\" + launcher + ".jar\" %*\n";

    final File exe = new File(bin, launcher + ".cmd");
    try (FileOutputStream out = new FileOutputStream(exe)) {
      out.write(script.getBytes(StandardCharsets.UTF_8));
    }
  }
}
