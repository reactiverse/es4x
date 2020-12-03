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

import io.reactiverse.es4x.cli.CmdLineParser;
import io.reactiverse.es4x.asm.FutureBaseVisitor;
import io.reactiverse.es4x.asm.JsonArrayVisitor;
import io.reactiverse.es4x.asm.JsonObjectVisitor;
import io.reactiverse.es4x.cli.GraalVMVersion;
import org.eclipse.aether.artifact.Artifact;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.jar.*;

import static io.reactiverse.es4x.cli.Helper.*;

public class Install implements Runnable {

  public static final String NAME = "install";
  public static final String SUMMARY = "Installs required jars from maven to 'node_modules'.";

  enum Only {
    PROD,
    PRODUCTION,
    DEV,
    DEVELOPMENT,
    ALL
  }

  private static final Properties VERSIONS = new Properties();

  static {
    try (InputStream is = Install.class.getClassLoader().getResourceAsStream("META-INF/es4x-commands/VERSIONS.properties")) {
      if (is == null) {
        fatal("Cannot find 'META-INF/es4x-commands/VERSIONS.properties' on classpath");
      } else {
        VERSIONS.load(is);
      }
    } catch (IOException e) {
      fatal(e.getMessage());
    }
  }

  private boolean link;
  private List<String> vendor;
  private File coreJar;
  private Only only = Only.ALL;
  private String dest;

  private File cwd;

  public Install() {
  }

  public Install(String[] args) {
    CmdLineParser parser = new CmdLineParser();
    CmdLineParser.Option<Boolean> helpOption = parser.addBooleanOption('h', "help");
    CmdLineParser.Option<String> vendorOption = parser.addStringOption('v', "vendor");
    CmdLineParser.Option<Boolean> linkOption = parser.addBooleanOption('l', "link");
    CmdLineParser.Option<String> onlyOption = parser.addStringOption('o', "only");
    CmdLineParser.Option<String> destOption = parser.addStringOption('d', "dest");

    try {
      parser.parse(args);
    } catch (CmdLineParser.OptionException e) {
      printUsage();
      System.exit(2);
      return;
    }

    Boolean isHelp = parser.getOptionValue(helpOption, Boolean.FALSE);

    if (isHelp != null && isHelp) {
      printUsage();
      System.exit(0);
      return;
    }

    Boolean link = parser.getOptionValue(linkOption, false);
    if (link != null && link) {
      setLink(true);
    }

    setVendor(parser.getOptionValue(vendorOption));
    setOnly(parser.getOptionValue(onlyOption, "all"));
    setDestination(parser.getOptionValue(destOption, "node_modules"));
    setCwd(new File("."));
  }

  private void printUsage() {
    System.err.println("Usage: es4x " + NAME + " [OPTIONS] [arg...]");
    System.err.println();
    System.err.println(SUMMARY);
    System.err.println();
    System.err.println("Options and Arguments:");
    System.err.println(" -f,--force\t\t\t\tWill always install a basic runtime in the current working dir.");
    System.err.println(" -o,--only\t\t\t\tOnly install 'prod[uction]/dev[elopment]' (default: all).");
    System.err.println(" -l,--link\t\t\t\tSymlink jars instead of copy.");
    System.err.println(" -v,--vendor <value>\tComma separated list of vendor jars.");
    System.err.println();
  }

  public Install setCwd(File cwd) {
    this.cwd = cwd;
    return this;
  }

  public void setLink(boolean link) {
    this.link = link;
  }

  public void setVendor(String vendor) {
    if (vendor != null) {
      this.vendor = new ArrayList<>();
      for (String v : vendor.split(",")) {
        // the jar lives in node_modules/.bin (rebase to the project root)
        this.vendor.add(".." + File.separator + ".." + File.separator + v);
      }
    }
  }

  public void setOnly(String only) {
    if (only != null) {
      this.only = Only.valueOf(only.toUpperCase());
    }
  }

  public void setDestination(String dest) {
    if (only != null) {
      this.dest = dest;
    }
  }

  private void processPackageJson(File json, Set<String> dependencies) throws IOException {
    if (json.exists()) {
      JSONObject npm = JSON.parseObject(json);
      if (npm.has("maven")) {
        final JSONObject maven = npm.getJSONObject("maven");
        // add this dependency
        dependencies.add(maven.get("groupId") + ":" + maven.get("artifactId") + ":" + maven.get("version"));
      }

      switch (only) {
        case ALL:
        case PROD:
        case PRODUCTION:
          if (npm.has("mvnDependencies")) {
            final JSONArray maven = npm.getJSONArray("mvnDependencies");
            for (Object el : maven) {
              // add this dependency
              dependencies.add((String) el);
            }
          }
      }

      switch (only) {
        case ALL:
        case DEV:
        case DEVELOPMENT:
          if (npm.has("mvnDevDependencies")) {
            final JSONArray maven = npm.getJSONArray("mvnDevDependencies");
            for (Object el : maven) {
              // add this dependency
              dependencies.add((String) el);
            }
          }
      }
    }
  }

  private void processModules(File dir, Set<String> dependencies) throws IOException {
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
  public void run() {

    final List<String> artifacts = new ArrayList<>();

    final Runnable run = () -> {
      installNodeModules(artifacts);

      if (!GraalVMVersion.isGraalVM()) {
        final double version = Double.parseDouble(System.getProperty("java.specification.version"));
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
        // not on graal, install graaljs and dependencies
        warn("Installing GraalJS...");
        // graaljs + dependencies
        installGraalJS(artifacts);
      }

      // always create a launcher even if no dependencies are needed
      createLauncher(artifacts);
    };

    switch (only) {
      case ALL:
      case DEV:
      case DEVELOPMENT:
        File control = new File(new File(cwd,"node_modules"), "es4x_install_successful");
        if (control.exists()) {
          warn("Skipping install (recent successful run)");
          return;
        }
        run.run();
        // touch the control file
        try (FileOutputStream fileOutputStream = new FileOutputStream(control)) {
          for (String s : artifacts) {
            fileOutputStream.write(s.getBytes(StandardCharsets.UTF_8));
            fileOutputStream.write('\n');
          }
        } catch (IOException e) {
          fatal(e.getMessage());
        }
        break;
      default:
        run.run();
    }
  }

  private static <T> void addIfMissing(Collection<T> collection, T element) {
    if (!collection.contains(element)) {
      collection.add(element);
    }
  }

  private void installGraalJS(Collection<String> artifacts) {
    final File base = new File(cwd,"node_modules");

    File lib = new File(base, ".lib");
    if (!lib.exists()) {
      if (!lib.mkdirs()) {
        fatal("Failed to mkdirs 'node_modules/.lib'.");
      }
    }

    File jvmci = new File(base, ".jvmci");

    try {
      Resolver resolver = new Resolver();
      List<String> mvnArtifacts = only == Only.ALL || only == Only.DEV ?
        Collections.singletonList("org.graalvm.tools:chromeinspector:" + VERSIONS.getProperty("graalvm")) :
        Collections.emptyList();

      for (Artifact a : resolver.resolve("org.graalvm.js:js:" + VERSIONS.getProperty("graalvm"), mvnArtifacts)) {
        File destination = new File(lib, a.getFile().getName());
        if (!destination.exists()) {
          // if jvmci is installed we refer to the common dependency
          if (jvmci.exists()) {
            if (new File(jvmci, a.getArtifactId() + "." + a.getExtension()).exists()) {
              addIfMissing(artifacts, ".." + File.separator + ".jvmci" + File.separator + a.getArtifactId() + "." + a.getExtension());
              continue;
            }
          }
          if (link) {
            Files.createSymbolicLink(destination.toPath(), a.getFile().toPath());
          } else {
            Files.copy(a.getFile().toPath(), destination.toPath());
          }
        }
        addIfMissing(artifacts, ".." + File.separator + ".lib" + File.separator +  a.getFile().getName());
      }
    } catch (IOException e) {
      fatal(e.getMessage());
    }
  }

  private void installGraalJMVCICompiler() {
    final File base = new File(cwd, "node_modules");

    File jvmci = new File(base, ".jvmci");
    if (!jvmci.exists()) {
      if (!jvmci.mkdirs()) {
        fatal("Failed to mkdirs 'node_modules/.jvmci'.");
      }
    }

    try {
      Resolver resolver = new Resolver();

      for (Artifact a : resolver.resolve("org.graalvm.compiler:compiler:" + VERSIONS.getProperty("graalvm"), Collections.emptyList())) {
        File destination = new File(jvmci, a.getArtifactId() + "." + a.getExtension());
        if (!destination.exists()) {
          if (link) {
            Files.createSymbolicLink(destination.toPath(), a.getFile().toPath());
          } else {
            Files.copy(a.getFile().toPath(), destination.toPath());
          }
        }
      }
    } catch (IOException e) {
      fatal(e.getMessage());
    }
  }

  private void installNodeModules(Collection<String> artifacts) {
    final File base = new File(cwd, "node_modules");
    final Set<String> dependencies = new HashSet<>();

    // process mvnDependencies from CWD package.json
    try {
      processPackageJson(new File(cwd, "package.json"), dependencies);
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
        addIfMissing(artifacts, ".." + File.separator + ".lib" + File.separator + a.getFile().getName());
        File destination = new File(libs, a.getFile().getName());

        // locate core jar
        if ("io.vertx".equals(a.getGroupId()) && "vertx-core".equals(a.getArtifactId())) {
          coreJar = a.getFile();
        }

        if (!destination.exists()) {
          if (link) {
            Files.createSymbolicLink(destination.toPath(), a.getFile().toPath());
          } else {
            Files.copy(a.getFile().toPath(), destination.toPath());
          }
        }
      }
    } catch (IOException e) {
      fatal(e.getMessage());
    }
  }

  private void createLauncher(Collection<String> artifacts) {
    File json = new File(cwd, "package.json");

    if (json.exists()) {
      try {
        JSONObject npm = JSON.parseObject(json);
        // default main script
        String main = ".";
        String verticleFactory = "js";

        // if package json declares a different main, then it shall be used
        if (npm.has("main")) {
          main = (String) npm.get("main");
          // allow main to be a mjs
          if (main != null && main.endsWith(".mjs")) {
            verticleFactory = "mjs";
          }
        }

        // if package json declares a different main, then it shall be used
        if (npm.has("module")) {
          main = (String) npm.get("module");
          verticleFactory = "mjs";
        }

        final File base = new File(cwd, "node_modules");
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
        attributes.put(Attributes.Name.MAIN_CLASS, "io.reactiverse.es4x.ES4X");
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
                  switch (je.getName()) {
                    case "io/vertx/core/json/JsonObject.class":
                      target.putNextEntry(je);
                      target.write(new JsonObjectVisitor().rewrite(jar));
                      target.closeEntry();
                      break;
                    case "io/vertx/core/json/JsonArray.class":
                      target.putNextEntry(je);
                      target.write(new JsonArrayVisitor().rewrite(jar));
                      target.closeEntry();
                      break;
                    case "io/vertx/core/impl/future/FutureBase.class":
                      target.putNextEntry(je);
                      target.write(new FutureBaseVisitor().rewrite(jar));
                      target.closeEntry();
                      break;
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
        "if [[ -f \"$basedir/../../security.policy\" ]]; then\n" +
        "  SECURITY_MANAGER=\"-Djava.security.manager -Djava.security.policy=$basedir/../../security.policy\"\n" +
        "fi\n" +
        "\n" +
        "if [[ -f \"$basedir/../../logging.properties\" ]]; then\n" +
        "  LOGGING_PROPERTIES=\"-Djava.util.logging.config.file=$basedir/../../logging.properties\"\n" +
        "fi\n" +
        "\n" +
        "exec \"$JAVA_EXE\" -XX:+IgnoreUnrecognizedVMOptions $JVMCI $SECURITY_MANAGER $LOGGING_PROPERTIES $JAVA_OPTS -jar \"$basedir/es4x-launcher.jar\" \"$@\"\n";

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
        "IF EXIST \"%~dp0\\..\\..\\security.policy\" (\n" +
        "  SET \"SECURITY_MANAGER=-Djava.security.manager -Djava.security.policy=\"\"%~dp0\\..\\..\\security.policy\"\"\"\n" +
        ")\n" +
        "\n" +
        "IF EXIST \"%~dp0\\..\\..\\logging.properties\" (\n" +
        "  SET \"LOGGING_PROPERTIES=-Djava.util.logging.config.file=\"\"%~dp0\\..\\..\\logging.properties\"\"\"\n" +
        ")\n" +
        "\n" +
        "\"%JAVA_EXE%\" -XX:+IgnoreUnrecognizedVMOptions %JVMCI% %SECURITY_MANAGER% %LOGGING_PROPERTIES% %JAVA_OPTS% -jar \"%~dp0\\es4x-launcher.jar\" %*\n";

    final File exe = new File(bin, "es4x-launcher.cmd");
    try (FileOutputStream out = new FileOutputStream(exe)) {
      out.write(script.getBytes(StandardCharsets.UTF_8));
    }
  }
}
