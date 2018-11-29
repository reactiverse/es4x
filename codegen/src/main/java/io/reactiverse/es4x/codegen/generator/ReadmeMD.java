/*
 * Copyright 2018 Paulo Lopes.
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
package io.reactiverse.es4x.codegen.generator;

import io.vertx.codegen.Generator;
import io.vertx.codegen.ModuleModel;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Map;

import static io.reactiverse.es4x.codegen.generator.Util.*;

public class ReadmeMD extends Generator<ModuleModel> {

  public ReadmeMD() {
    kinds = new HashSet<>();
    kinds.add("module");

    name = "es4x-generator (README.md)";
  }

  @Override
  public String filename(ModuleModel model) {
    return "npm/README.md";
  }

  @Override
  public String render(ModuleModel model, int index, int size, Map<String, Object> session) {

    StringWriter sw = new StringWriter();
    PrintWriter writer = new PrintWriter(sw);

    writer.printf("![npm (scoped)](https://img.shields.io/npm/v/%s.svg)\n", getNPMScope(model.getModule()));
    writer.printf("![npm](https://img.shields.io/npm/l/%s.svg)\n", getNPMScope(model.getModule()));
    writer.print("\n");
    writer.print("Generated JavaScript bindings for Eclipse Vert.x.\n");
    writer.print("\n");
    writer.print("## Documentation\n");
    writer.print("\n");
    writer.printf("* [API Docs](https://reactiverse.io/es4x/%s)\n", getNPMScope(model.getModule()));
    writer.printf("* [NPM module](https://www.npmjs.com/package/%s)\n", getNPMScope(model.getModule()));
    writer.print("\n");
    writer.print("## Usage\n");
    writer.print("\n");
    writer.print("Import the required `API`/`Enum`/`DataObject` and profit!\n");
    writer.print("\n");
    writer.print("```js\n");
    writer.print("// Base API\n");
    writer.printf("const Api = require('%s');\n", getNPMScope(model.getModule()));
    writer.print("// Base ENUMs\n");
    writer.printf("const Enums = require('%s/enums');\n", getNPMScope(model.getModule()));
    writer.print("// DataObject's\n");
    writer.printf("const Options = require('%s/options');\n", getNPMScope(model.getModule()));
    writer.print("\n");
    writer.print("// refer to the API docs for specific help...\n");
    writer.print("\n");
    writer.print("// your code here!!!\n");
    writer.print("\n");
    writer.print("```\n");
    writer.print("\n");
    writer.print("## Typescript\n");
    writer.print("\n");
    writer.print("This package includes [Typescript](http://www.typescriptlang.org/) type");
    writer.print("definitions and your IDE should find then automatically.\n");
    writer.print("\n");
    writer.print("When working in a project you can enable type hinting for the runtime as:\n");
    writer.print("\n");
    writer.print("```js\n");
    writer.print("/// <definition types=\"@vertx/core/runtime\" />\n");
    writer.print("// @ts-check\n");
    writer.print("\n");
    writer.print("// your TypeScript code here...\n");
    writer.print("```\n");
    writer.print("\n");
    writer.print(includeFileIfPresent("MANUAL.md"));
    writer.print("## Links\n");
    writer.print("\n");
    writer.print("* [Eclipse Vert.x](https://vertx.io)\n");
    writer.print("* [ES4X](https://reactiverse.io/es4x)\n");

    return sw.toString();
  }
}
