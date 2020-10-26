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

import io.vertx.core.cli.CLIException;
import io.vertx.core.cli.annotations.*;
import io.vertx.core.spi.launcher.DefaultCommand;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import static io.reactiverse.es4x.cli.Helper.fatal;

@Name("webstorm")
@Summary("Launcher for vscode project.")
public class WebstormCommand extends DefaultCommand {

	@Override
	public void run() throws CLIException {
		File dotRun = new File(getCwd(), ".run");
    final File debug = new File(dotRun, "Debug.run.xml");

    if (!debug.exists()) {
			if (!dotRun.exists() && !dotRun.mkdirs()) {
        fatal("Failed to mkdir .run");
      }
			try (InputStream in = WebstormCommand.class.getClassLoader()
					.getResourceAsStream("META-INF/es4x-commands/Debug.run.xml")) {
				if (in == null) {
					fatal("Cannot load webstorm launcher template.");
				} else {
					Files.copy(in, debug.toPath());
				}
			} catch (IOException e) {
				fatal(e.getMessage());
			}
    }
	}
}
