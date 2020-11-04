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

import io.vertx.codegen.EnumModel;
import io.vertx.codegen.Generator;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Map;

import static io.reactiverse.es4x.codegen.generator.Util.getNPMScope;

public class EnumMJS extends Generator<EnumModel> {

  public EnumMJS() {
    incremental = true;

    kinds = new HashSet<>();
    kinds.add("enum");

    name = "es4x-generator (enum.mjs)";
  }

  @Override
  public String filename(EnumModel model) {
    return "npm/enums.mjs";
  }

  @Override
  public String render(EnumModel model, int index, int size, Map<String, Object> session) {

    StringWriter sw = new StringWriter();
    PrintWriter writer = new PrintWriter(sw);

    if (index == 0) {
      Util.generateLicense(writer);
      writer.printf("/// <reference types=\"%s/enums\" />\n\n", getNPMScope(model.getType().getRaw().getModule()));
      writer.printf(
        "/**\n" +
          " * @typedef { import(\"@vertx/core\") } Java\n" +
          " */\n");
    }

    writer.printf("export const %s = Java.type('%s');\n", model.getType().getRaw().getSimpleName(), model.getType().getName());

    return sw.toString();
  }
}
