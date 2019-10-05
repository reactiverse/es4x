package io.reactiverse.es4x.commands;

import io.reactiverse.es4x.cli.CmdLineParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;

import static io.reactiverse.es4x.commands.Helper.*;

public class Init implements Runnable {

  public static final String NAME = "init";
  public static final String SUMMARY = "Initializes the 'package.json' to work with ES4X.";

  private File cwd;

  public Init() {
  }

  public Init(String[] args) {
    CmdLineParser parser = new CmdLineParser();
    CmdLineParser.Option<Boolean> helpOption = parser.addBooleanOption('h', "help");

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

    setCwd(new File("."));
  }

  private void printUsage() {
    System.err.println("Usage: es4x " + NAME + " [OPTIONS] [arg...]");
    System.err.println();
    System.err.println(SUMMARY);
    System.err.println();
  }

  public Init setCwd(File cwd) {
    this.cwd = cwd;
    return this;
  }

  @Override
  public void run() {
    try {
      final File file = new File(cwd, "package.json");

      if (!file.exists()) {
        // Load the file from the class path
        try (InputStream in = Init.class.getClassLoader().getResourceAsStream("META-INF/es4x-commands/package.json")) {
          if (in == null) {
            fatal("Cannot load package.json template.");
          } else {
            Files.copy(in, file.toPath());
          }
        } catch (IOException e) {
          fatal(e.getMessage());
        }
      }

      Map npm = JSON.parse(file, Map.class);
      String name = (String) npm.get("name");

      if (name == null) {
        // this was a new project, either derive the name from the cwd or set to "unnamed"
        if (cwd != null) {
          name = cwd.toPath().toRealPath().getFileName().toString();
        }

        if (name == null || "".equals(name)) {
          name = "unnamed";
        }

        npm.put("name", name);
      }

      Map scripts = (Map) npm.get("scripts");

      if (scripts == null) {
        scripts = new LinkedHashMap();
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
      if (test.endsWith(".mjs")) {
        test = test.substring(0, test.length() - 3) + ".test.mjs";
      }

      scripts.put("postinstall", "es4x install");
      scripts.put("start", "es4x");
      scripts.put("test", "es4x test " + test);

      JSON.encode(file, npm);

    } catch (IOException e) {
      fatal(e.getMessage());
    }
  }
}
