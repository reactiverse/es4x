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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.cli.CLIException;
import io.vertx.core.cli.annotations.*;
import io.vertx.core.spi.launcher.DefaultCommand;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

import static io.reactiverse.es4x.commands.Helper.*;

@Name("vscode")
@Summary("Play with your vscode project.")
public class VscodeCommand extends DefaultCommand {

	private static final ObjectMapper MAPPER = new ObjectMapper();
	private boolean launcher;

	@Option(longName = "launcher", shortName = "l", flag = true)
	@Description("Will install a basic launch config in the current project.")
	public void setLauncher(boolean create) {
		this.launcher = create;
	}

	private static void processLauncher(File json) throws IOException {
		Map launch = MAPPER.readValue(json, Map.class);
		if (launch.containsKey("configurations") == false) {
			launch.put("configurations", new ArrayList<>());
		}
		final List configurations = (List) launch.get("configurations");
		Map<String, Object> config = new LinkedHashMap<String, Object>();
		config.put("name", "Launch via npm");
		config.put("type", "node");
		config.put("request", "launch");
		config.put("cwd", "${workspaceFolder}");
		config.put("runtimeExecutable", "npm");
		List<String> npmArgs = new ArrayList<String>();
		npmArgs.add("run-script");
		npmArgs.add("start");
		npmArgs.add("--");
		npmArgs.add("-inspect=5858");
		config.put("runtimeArgs", npmArgs);
		config.put("port", "5858");
		config.put("outputCapture", "std");
		configurations.add(config);
		MAPPER.writeValue(json, launch);
	}

	@Override
	public void run() throws CLIException {
		File vscodefile = new File(getCwd(), ".vscode/launch.json");

		if (!vscodefile.exists()) {
			new File(getCwd(), ".vscode").mkdirs();
			try (InputStream in = InitCommand.class.getClassLoader()
					.getResourceAsStream("META-INF/es4x-commands/vscode-launcher.json")) {
				if (in == null) {
					err("Cannot load vscode launcher.json template.");
				} else {
					Files.copy(in, vscodefile.toPath());
				}
			} catch (IOException e) {
				err(e.getMessage());
			}
		}

		if (this.launcher) {
			try {
				processLauncher(vscodefile);
			} catch (IOException e) {
				err(e.getMessage());
			}
		}
	}
}