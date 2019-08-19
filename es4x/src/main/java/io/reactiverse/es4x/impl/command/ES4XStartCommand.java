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
import io.vertx.core.cli.annotations.Description;
import io.vertx.core.cli.annotations.Name;
import io.vertx.core.cli.annotations.Summary;
import io.vertx.core.impl.launcher.commands.StartCommand;

@Name(value = "start", priority = 1000)
@Summary("Start a vert.x application in background")
@Description("Start a vert.x application as a background service. The application is identified with an id that can be set using the `vertx-id` option. If not set a random UUID is generated. The application can be stopped with the `stop` command.")
public class ES4XStartCommand extends StartCommand {

  public ES4XStartCommand() {
    super.setLauncherClass(ES4X.class.getName());
  }
}
