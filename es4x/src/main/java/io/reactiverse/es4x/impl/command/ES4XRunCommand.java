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

import io.vertx.core.cli.annotations.*;
import io.vertx.core.impl.launcher.commands.RunCommand;

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
  @DefaultValue(">")
  @Description("The main verticle to deploy, it can be a script file or a package directory.")
  public void setMainVerticle(String verticle) {
    this.verticle = verticle;
  }

  @Option(longName = "esm", argName = "es-module", flag = true)
  @Description("Assume .mjs module when no extension is provided.")
  public void setEsm(boolean esm) {
    this.esm = esm;
  }

  @Option(longName = "polyglot", argName = "polyglot-access", flag = true)
  @Description("Enable GraalVM Polyglot access (default: false).")
  @DefaultValue("false")
  public void setPolyglot(boolean polyglot) {
    System.setProperty("es4x.polyglot", Boolean.toString(polyglot));
  }

  @Option(longName = "inspect", argName = "inspector-port")
  @Description("Specifies the node inspector port to listen on (GraalJS required).")
  public void setInspect(int inspect) {
    System.setProperty("polyglot.inspect", Integer.toString(inspect));
    System.setProperty("vertx.options.blockedThreadCheckInterval", "1000000");
  }

  @Option(longName = "inspect-brk", argName = "inspector-brk-port")
  @Description("Breaks on start the node inspector listening on given port (GraalJS required).")
  public void setInspectBrk(int inspect) {
    System.setProperty("polyglot.inspect", Integer.toString(inspect));
    System.setProperty("polyglot.inspect.Suspend", "true");
    System.setProperty("vertx.options.blockedThreadCheckInterval", "1000000");
  }

  @Override
  public void run() {
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
