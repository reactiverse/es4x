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

import io.reactiverse.es4x.ES4X;
import io.reactiverse.es4x.commands.proxies.JsonArrayProxy;
import io.reactiverse.es4x.commands.proxies.JsonObjectProxy;
import io.vertx.core.cli.CLIException;
import io.vertx.core.cli.annotations.*;
import io.vertx.core.spi.launcher.DefaultCommand;
import org.eclipse.aether.artifact.Artifact;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.jar.*;

import static io.reactiverse.es4x.commands.Helper.*;

@Name("install")
@Summary("Installs required jars from maven to 'node_modules'.")
public class InstallCommand extends DefaultCommand {

  private static final Properties VERSIONS = new Properties();

  static {
    try (InputStream is = InstallCommand.class.getClassLoader().getResourceAsStream("META-INF/es4x-commands/VERSIONS.properties")) {
      if (is == null) {
        fatal("Cannot find 'META-INF/es4x-commands/VERSIONS.properties' on classpath");
      } else {
        VERSIONS.load(is);
      }
    } catch (IOException e) {
      fatal(e.getMessage());
    }
  }

  private boolean force;
  private List<String> vendor;
  private File coreJar;

  @Option(longName = "force", shortName = "f", flag = true)
  @Description("Will always install a basic runtime in the current working dir.")
  public void setForce(boolean force) {
    this.force = force;
  }

  @Option(longName = "vendor", shortName = "v")
  @Description("Specify a comma separated list of vendor jars")
  public void setVendor(String vendor) {
    if (vendor != null) {
      this.vendor = new ArrayList<>();
      for (String v : vendor.split(",")) {
        // the jar lives in node_modules/.bin (rebase to the project root)
        this.vendor.add(".." + File.separator + ".." + File.separator + v);
      }
    }
  }

  private static void processPackageJson(File json, Set<String> dependencies) throws IOException {
    if (json.exists()) {
      Map npm = read(json);
      if (npm.containsKey("maven")) {
        final Map maven = (Map) npm.get("maven");
        // add this dependency
        dependencies.add(maven.get("groupId") + ":" + maven.get("artifactId") + ":" + maven.get("version"));
      }

      if (npm.containsKey("mvnDependencies")) {
        final List maven = (List) npm.get("mvnDependencies");
        for (Object el : maven) {
          // add this dependency
          dependencies.add((String) el);
        }
      }

      // only run if not production
      if (!isProduction()) {
        if (npm.containsKey("mvnDevDependencies")) {
          final List maven = (List) npm.get("mvnDevDependencies");
          for (Object el : maven) {
            // add this dependency
            dependencies.add((String) el);
          }
        }
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

        // process
        processPackageJson(json, dependencies);

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

    final File base = new File(getCwd(), "node_modules");
    final File libs = new File(base, ".lib");

    if (force || libs.exists()) {
      final double version = Double.parseDouble(System.getProperty("java.specification.version"));
      final String vm = System.getProperty("java.vm.name");
      if (!vm.toLowerCase().contains("graalvm")) {

        // not on graal, install graaljs and dependencies
        warn("Installing GraalJS...");
        // graaljs + dependencies
        installGraalJS(artifacts);

        if (version >= 11) {
          // verify if the current JDK contains the jdk.internal.vm.ci module
          try {
            String modules = exec(javaHomePrefix() + "java", "--list-modules");
            if (modules.contains("jdk.internal.vm.ci")) {
              warn("Installing JVMCI Compiler...");
              // jvmci compiler + dependencies
              installGraalJMVCICompiler();
            }
          } catch (IOException | InterruptedException e) {
            err(e.getMessage());
          }
        } else {
          warn("Current JDK only supports GraalJS in Interpreted mode!");
        }
      }
    }

    // always create a launcher even if no dependencies are needed
    createLauncher(artifacts);
  }

  private void installGraalJS(Set<String> artifacts) throws CLIException {
    final File base = new File(getCwd(),"node_modules");

    File libs = new File(base, ".lib");
    if (!libs.exists()) {
      if (!libs.mkdirs()) {
        fatal("Failed to mkdirs 'node_modules/.lib'.");
      }
    }

    try {
      Resolver resolver = new Resolver();

      for (Artifact a : resolver.resolve("org.graalvm.js:js:" + VERSIONS.getProperty("graalvm"), Arrays.asList("org.graalvm.tools:profiler:" + VERSIONS.getProperty("graalvm"), "org.graalvm.tools:chromeinspector:" + VERSIONS.getProperty("graalvm")))) {
        artifacts.add(".." + File.separator + ".lib" + File.separator +  a.getFile().getName());
        File destination = new File(libs, a.getFile().getName());
        if (!destination.exists()) {
            Files.copy(a.getFile().toPath(), destination.toPath());
        }
      }
    } catch (IOException e) {
      fatal(e.getMessage());
    }
  }

  private void installGraalJMVCICompiler() throws CLIException {
    final File base = new File(getCwd(), "node_modules");

    File libs = new File(base, ".jvmci");
    if (!libs.exists()) {
      if (!libs.mkdirs()) {
        fatal("Failed to mkdirs 'node_modules/.jvmci'.");
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
      fatal(e.getMessage());
    }
  }

  private void installNodeModules(Set<String> artifacts) throws CLIException {
    final File base = new File(getCwd(), "node_modules");
    final Set<String> dependencies = new HashSet<>();

    // process mvnDependencies from CWD package.json
    try {
      processPackageJson(new File(getCwd(), "package.json"), dependencies);
    } catch (IOException e) {
      fatal(e.getMessage());
    }

    // crawl node modules
    if (base.exists() && base.isDirectory()) {
      try {
        processModules(base, dependencies);
      } catch (IOException e) {
        fatal(e.getMessage());
      }
    }

    if (force || dependencies.size() > 0) {
      File libs = new File(base, ".lib");

      try {
        Resolver resolver = new Resolver();

        // lookup root
        String root = "io.reactiverse:es4x:" + VERSIONS.getProperty("es4x");

        for (String el : dependencies) {
          // ensure we respect the wish of the user
          if (el.startsWith("io.reactiverse:es4x:")) {
            root = el;
            break;
          }
        }

        for (Artifact a : resolver.resolve(root, dependencies)) {

          if (!libs.exists()) {
            if (!libs.mkdirs()) {
              fatal("Failed to mkdirs 'node_modules/.lib'.");
            }
          }
          artifacts.add(".." + File.separator + ".lib" + File.separator + a.getFile().getName());
          File destination = new File(libs, a.getFile().getName());

          // locate core jar
          if ("io.vertx".equals(a.getGroupId()) && "vertx-core".equals(a.getArtifactId())) {
            coreJar = a.getFile();
          }

          if (!destination.exists()) {
            Files.copy(a.getFile().toPath(), destination.toPath());
          }
        }
      } catch (IOException e) {
        fatal(e.getMessage());
      }
    }
  }

  private void createLauncher(Set<String> artifacts) throws CLIException {

    File json = new File(getCwd(), "package.json");

    if (json.exists()) {
      try {
        Map npm = read(json);
        // default main script
        String main = ".";
        String verticleFactory = "js";

        // if package json declares a different main, then it shall be used
        if (npm.containsKey("main")) {
          main = (String) npm.get("main");
          // allow main to be a mjs
          if (main != null && main.endsWith(".mjs")) {
            verticleFactory = "mjs";
          }
        }

        // if package json declares a different main, then it shall be used
        if (npm.containsKey("module")) {
          main = (String) npm.get("module");
          verticleFactory = "mjs";
        }

        final File base = new File(getCwd(), "node_modules");
        File bin = new File(base, ".bin");
        if (!bin.exists()) {
          if (!bin.mkdirs()) {
            fatal("Failed to mkdirs 'node_modules/.bin'.");
          }
        }

        final Manifest manifest = new Manifest();
        final Attributes attributes = manifest.getMainAttributes();

        attributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        attributes.put(new Attributes.Name("Created-By"), "ES4X " + VERSIONS.getProperty("es4x"));
        attributes.put(new Attributes.Name("Built-By"), System.getProperty("user.name"));
        attributes.put(new Attributes.Name("Build-Jdk"), System.getProperty("java.version"));
        attributes.put(Attributes.Name.MAIN_CLASS, ES4X.class.getName());
        String classpath = String.join(" ", artifacts);
        if (vendor != null && vendor.size() > 0) {
          classpath += " " + String.join(" ", vendor);
        }
        attributes.put(Attributes.Name.CLASS_PATH, classpath);
        attributes.put(new Attributes.Name("Main-Verticle"), main);
        attributes.put(new Attributes.Name("Main-Command"), "run");
        attributes.put(new Attributes.Name("Default-Verticle-Factory"), verticleFactory);

        try (JarOutputStream target = new JarOutputStream(new FileOutputStream(new File(bin, "es4x-launcher.jar")), manifest)) {
          if (coreJar != null) {
            try (InputStream in = new FileInputStream(coreJar)) {
              try (JarInputStream jar = new JarInputStream(in)) {
                JarEntry je;
                while ((je = jar.getNextJarEntry()) != null) {
                  if ("io/vertx/core/json/JsonObject.class".equals(je.getName())) {
                    target.putNextEntry(je);
                    target.write(new JsonObjectProxy().rewrite(jar));
                    target.closeEntry();
                  }
                  if ("io/vertx/core/json/JsonArray.class".equals(je.getName())) {
                    target.putNextEntry(je);
                    target.write(new JsonArrayProxy().rewrite(jar));
                    target.closeEntry();
                  }
                }
              } catch (RuntimeException e) {
                warn(e.getMessage());
              }
            } catch (RuntimeException e) {
              warn(e.getMessage());
            }
          }
        }

        // create the launcher scripts
        if (isUnix()) {
          createUNIXScript(bin);
        }
        if (isWindows()) {
          createDOSScript(bin);
        }

      } catch (IOException e) {
        fatal(e.getMessage());
      }
    }
  }

  private void createUNIXScript(File bin) throws IOException {

    String script =
      "#!/usr/bin/env bash\n" +
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
      "exec \"$JAVA_EXE\" -XX:+IgnoreUnrecognizedVMOptions $JVMCI $JAVA_OPTS -jar \"$basedir/es4x-launcher.jar\" \"$@\"\n";

    final File exe = new File(bin, "es4x-launcher");
    try (FileOutputStream out = new FileOutputStream(exe)) {
      out.write(script.getBytes(StandardCharsets.UTF_8));
    }

    // this is a best effort
    if (!exe.setExecutable(true, false)) {
      fatal("Cannot set script 'node_modules/.bin/es4x-launcher' executable!");
    }
  }

  private void createDOSScript(File bin) throws IOException {

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
        "  SET \"JVMCI=--module-path=\"\"%~dp0\\..\\.jvmci\"\" -XX:+UnlockExperimentalVMOptions -XX:+EnableJVMCI --upgrade-module-path=\"\"%~dp0\\..\\.jvmci\\compiler.jar\"\"\"\n" +
        ")\n" +
        "\n" +
        "\"%JAVA_EXE%\" -XX:+IgnoreUnrecognizedVMOptions %JVMCI% %JAVA_OPTS% -jar \"%~dp0\\es4x-launcher.jar\" %*\n";

    final File exe = new File(bin, "es4x-launcher.cmd");
    try (FileOutputStream out = new FileOutputStream(exe)) {
      out.write(script.getBytes(StandardCharsets.UTF_8));
    }
  }

  private static boolean isProduction() {
    // NODE_ENV set to production
    if ("production".equalsIgnoreCase(System.getenv("NODE_ENV"))) {
      return true;
    }

    return "production".equalsIgnoreCase(System.getenv("ES4X_ENV"));
  }
}
