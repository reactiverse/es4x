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

import io.vertx.codegen.ClassModel;
import io.vertx.codegen.Generator;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Map;

import static io.reactiverse.es4x.codegen.generator.Util.*;
import static io.reactiverse.es4x.codegen.generator.Util.jvmClasses;

public class IndexJS extends Generator<ClassModel> {

  public IndexJS() {
    incremental = true;

    kinds = new HashSet<>();
    kinds.add("class");

    name = "es4x-generator (index.js)";
  }

  @Override
  public String filename(ClassModel model) {
    return "npm/index.js";
  }

  @Override
  public String render(ClassModel model, int index, int size, Map<String, Object> session) {

    StringWriter sw = new StringWriter();
    PrintWriter writer = new PrintWriter(sw);

    if (index == 0) {
      Util.generateLicense(writer);

      writer.printf("/// <reference types=\"%s\" />\n\n", getNPMScope(model.getType().getRaw().getModule()));
      writer.printf(
        "/**\n" +
          " * @typedef { import(\"@vertx/core\") } Java\n" +
          " */\n");
      writer.print("module.exports = {\n");

      registerJvmClasses();
      for (Object fqcn : jvmClasses("api")) {
        JVMClass.generateJS(writer, fqcn.toString());
        writer.println(',');
      }

      // include a file if present
      writer.print(includeFileIfPresent("index.header.js"));
    }

    writer.printf("  %s: Java.type('%s')", model.getType().getRaw().getSimpleName(), model.getType().getName());

    if (index != size - 1) {
      writer.print(',');
    }

    writer.print('\n');

    if (index == size - 1) {
      // include a file if present
      writer.print(includeFileIfPresent("index.footer.js"));
      writer.print("};\n");
    }

    return sw.toString();
  }
}
