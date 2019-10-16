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
import io.vertx.codegen.EnumValueInfo;
import io.vertx.codegen.Generator;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Map;

import static io.reactiverse.es4x.codegen.generator.Util.isBlacklistedClass;

public class EnumDTS extends Generator<EnumModel> {

  public EnumDTS() {
    incremental = true;

    kinds = new HashSet<>();
    kinds.add("enum");

    name = "es4x-generator (enum.d.ts)";
  }

  @Override
  public String filename(EnumModel model) {
    return "npm/enums.d.ts";
  }

  @Override
  public String render(EnumModel model, int index, int size, Map<String, Object> session) {

    if (isBlacklistedClass(model.getType().getName())) {
      return null;
    }

    StringWriter sw = new StringWriter();
    PrintWriter writer = new PrintWriter(sw);

    if (index == 0) {
      Util.generateLicense(writer);
    } else {
      writer.print("\n");
    }

    if (model.getDoc() != null) {
      writer.print("/**\n");
      writer.printf(" *%s\n", model.getDoc().toString().replace("\n", "\n * "));
      writer.print(" */\n");
    }

    writer.printf("export enum %s {\n", model.getType().getRaw().getSimpleName());
    for (int i = 0; i < model.getValues().size(); i++) {
      EnumValueInfo value = model.getValues().get(i);
      writer.printf("  %s", value.getIdentifier());
      if (i != model.getValues().size() - 1) {
        writer.print(",");
      }
      writer.print("\n");
    }
    writer.print("}\n");

    return sw.toString();
  }
}
