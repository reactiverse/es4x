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
import io.vertx.codegen.EnumModel;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Map;

import static io.reactiverse.es4x.codegen.generator.Util.*;

public class EnumJS extends Generator<EnumModel> {

  public EnumJS() {
    incremental = true;

    kinds = new HashSet<>();
    kinds.add("enum");

    name = "es4x-generator (enum.js)";
  }

  @Override
  public String filename(EnumModel model) {
    return "npm/enums.js";
  }

  @Override
  public String render(EnumModel model, int index, int size, Map<String, Object> session) {

    StringWriter sw = new StringWriter();
    PrintWriter writer = new PrintWriter(sw);

    if (index == 0) {
      Util.generateLicense(writer);
      writer.printf("/// <reference types=\"%s/enums\" />\n", getNPMScope(model.getType().getRaw().getModule()));
      writer.print("module.exports = {\n");
    }

    writer.printf("  %s: Java.type('%s')", model.getType().getRaw().getSimpleName(), model.getType().getName());

    if (index != size - 1) {
      writer.print(',');
    }

    writer.print('\n');

    if (index == size - 1) {
      writer.print("};\n");
    }

    return sw.toString();
  }
}
