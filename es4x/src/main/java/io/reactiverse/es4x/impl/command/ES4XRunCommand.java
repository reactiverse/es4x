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
package io.reactiverse.es4x.impl.command;

import io.reactiverse.es4x.ES4X;
import io.vertx.core.cli.CLIException;
import io.vertx.core.cli.annotations.*;
import io.vertx.core.impl.launcher.VertxCommandLauncher;
import io.vertx.core.impl.launcher.commands.RunCommand;
import io.vertx.core.spi.launcher.ExecutionContext;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

@Name(value = "run", priority = 1000)
@Summary("Runs a JS script called <main-verticle> in its own instance of vert.x.")
public class ES4XRunCommand extends RunCommand {

  private String verticle;
  private boolean esm;

  /**
   * Sets the main verticle that is deployed.
   *
   * @param verticle the verticle
   */
  @Override
  @Argument(index = 0, argName = "main-verticle", required = false)
  @Description("The main verticle to deploy, it can be a script file or a package directory.")
  public void setMainVerticle(String verticle) {
    this.verticle = verticle;
  }

  @Option(argName = "es-module", longName = "esm", flag = true)
  @Description("Assume .mjs module when no extension is provided.")
  public void setEsm(boolean esm) {
    this.esm = esm;
  }

  @Option(argName = "prefix", longName = "prefix-path")
  @Description("The the prefix to apply to relative modules (for compiled languages). (e.g.: ./dist).")
  public void setPrefix(String prefix) {
    if (!prefix.endsWith("/")) {
      prefix += "/";
    }
    System.setProperty("es4x.prefix", prefix);
  }

  @Option(argName = "polyglot", longName = "polyglot-access", flag = true)
  @Description("Enable GraalVM Polyglot access (default: false).")
  @DefaultValue("false")
  public void setPolyglot(boolean polyglot) {
    System.setProperty("es4x.polyglot", Boolean.toString(polyglot));
  }

  @Option(argName = "inspect", longName = "inspector-port")
  @Description("Specifies the node inspector port to listen on (GraalJS required).")
  public void setInspect(int inspect) {
    System.setProperty("polyglot.inspect", Integer.toString(inspect));
    System.setProperty("vertx.options.blockedThreadCheckInterval", "1000000");
  }

  @Option(argName = "inspect-brk", longName = "inspector-brk-port")
  @Description("Breaks on start the node inspector listening on given port (GraalJS required).")
  public void setInspectBrk(int inspect) {
    System.setProperty("polyglot.inspect", Integer.toString(inspect));
    System.setProperty("polyglot.inspect.Suspend", "true");
    System.setProperty("vertx.options.blockedThreadCheckInterval", "1000000");
  }

  private String getFromManifest(String key) {
    try {
      Enumeration<URL> resources = ES4XRunCommand.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
      while (resources.hasMoreElements()) {
        try (InputStream stream = resources.nextElement().openStream()) {
          Manifest manifest = new Manifest(stream);
          Attributes attributes = manifest.getMainAttributes();
          String mainClass = attributes.getValue("Main-Class");
          if (ES4X.class.getName().equals(mainClass)) {
            String value = attributes.getValue(key);
            if (value != null) {
              return value;
            }
          }
        }
      }
    } catch (IOException e) {
      // ignore
      return null;
    }
    return null;
  }

  @Override
  public void run() {

    if (verticle == null) {
      verticle = getFromManifest("Main-Verticle");
    }

    if (verticle == null) {
      verticle = ">";
    }

    boolean mjs = verticle.endsWith(".mjs") || esm;
    // force swapping verticle factory
    if (!verticle.contains(":")) {
      super.setMainVerticle((mjs ? "mjs:" : "js:") + verticle);
    } else {
      super.setMainVerticle(verticle);
    }
    // delegate to default implementation
    super.run();
  }
}
