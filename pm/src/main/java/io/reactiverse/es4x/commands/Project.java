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
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import static io.reactiverse.es4x.cli.Helper.fatal;

public class Project implements Runnable {

  public static final String NAME = "project";
  public static final String SUMMARY = "Initializes the 'package.json' to work with ES4X.";

  private File cwd;
  private boolean typeScript;
  private boolean importMap;

  public Project() {
  }

  public Project(String[] args) {
    CmdLineParser parser = new CmdLineParser();
    CmdLineParser.Option<Boolean> helpOption = parser.addBooleanOption('h', "help");
    CmdLineParser.Option<Boolean> importMapOption = parser.addBooleanOption('i', "importmap");
    CmdLineParser.Option<Boolean> tsOption = parser.addBooleanOption('t', "ts");

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

    Boolean typeScript = parser.getOptionValue(tsOption, Boolean.FALSE);
    if (typeScript != null && typeScript) {
      setTypeScript(true);
    }
    Boolean importMap = parser.getOptionValue(importMapOption, Boolean.FALSE);
    if (importMap != null && importMap) {
      setImportMap(true);
    }

    String[] commandArgs = parser.getRemainingArgs();

    switch (commandArgs.length) {
      case 0:
        setCwd(new File("."));
        break;
      case 1:
        File d = new File(commandArgs[0]);
        if (d.exists()) {
          if (!d.isDirectory()) {
            fatal(commandArgs[0] + " is not a directory!");
          }
        } else {
          if (!d.mkdirs()) {
            fatal("Failed to create directory(s): " + commandArgs[0] + "!");
          }
        }
        setCwd(d);
        break;
      default:
        fatal("Too many arguments, only 1 project name allowed!");
    }
  }

  private void printUsage() {
    System.err.println("Usage: es4x " + NAME + " [OPTIONS] [arg...]");
    System.err.println();
    System.err.println(SUMMARY);
    System.err.println();
    System.err.println("Options and Arguments:");
    System.err.println(" -i,--importmap\t\t\tCreate a import-map.json instead of package.json.");
    System.err.println(" -t,--ts\t\t\t\tCreate a TypeScript project instead of JavaScript.");
    System.err.println();
  }

  public Project setCwd(File cwd) {
    this.cwd = cwd;
    return this;
  }

  public void setTypeScript(boolean typeScript) {
    this.typeScript = typeScript;
  }

  public void setImportMap(boolean importMap) {
    this.importMap = importMap;
  }

  @Override
  public void run() {
    try {
      final File file = new File(cwd, "package.json");

      if (file.exists()) {
        fatal(file.toPath().toRealPath() + " already exists!");
      }

      String[] templates = typeScript ?
        new String[]
          {
            importMap ? "META-INF/es4x-commands/init/ts/import-map.json" : "META-INF/es4x-commands/init/ts/package.json",
            "META-INF/es4x-commands/init/ts/index.ts",
            "META-INF/es4x-commands/init/ts/index.test.ts",
            "META-INF/es4x-commands/init/ts/tsconfig.json"
          } :
        new String[]
          {
            importMap ? "META-INF/es4x-commands/init/js/import-map.json" : "META-INF/es4x-commands/init/js/package.json",
            "META-INF/es4x-commands/init/js/index.js",
            "META-INF/es4x-commands/init/js/index.test.js"
          };

      for (String template : templates) {
        // Load the file from the class path
        try (InputStream in = Project.class.getClassLoader().getResourceAsStream(template)) {
          if (in == null) {
            fatal("Cannot load: " + template);
          } else {
            File target = new File(cwd, template.substring(template.lastIndexOf('/') + 1));
            Files.copy(in, target.toPath());
          }
        } catch (IOException e) {
          fatal(e.getMessage());
        }
      }

      JSONObject npm = JSON.parseObject(file);
      String name = null;

      // this was a new project, either derive the name from the cwd or set to "unnamed"
      if (cwd != null) {
        try {
          name = cwd.toPath().toRealPath().getFileName().toString();
        } catch (NullPointerException e) {
          // lots of de-referencing, which can be ignored in case of error
          name = null;
        }
      }

      if (name == null || "".equals(name)) {
        name = "unnamed";
      }

      npm.put("name", name);

      JSON.encodeObject(file, npm);

    } catch (IOException e) {
      fatal(e.getMessage());
    }
  }
}
