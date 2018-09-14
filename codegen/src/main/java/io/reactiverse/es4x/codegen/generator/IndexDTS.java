/*
 * Copyright 2018 Red Hat, Inc.
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
import io.vertx.codegen.MethodInfo;
import io.vertx.codegen.ParamInfo;
import io.vertx.codegen.type.ApiTypeInfo;
import io.vertx.codegen.type.ClassTypeInfo;
import io.vertx.codegen.type.EnumTypeInfo;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Map;

import static io.reactiverse.es4x.codegen.generator.Util.*;

public class IndexDTS extends Generator<ClassModel> {

  public IndexDTS() {
    incremental = true;

    kinds = new HashSet<>();
    kinds.add("class");

    name = "es4x-generator";
  }

  @Override
  public String filename(ClassModel model) {
    return "npm/index.d.ts";
  }

  @Override
  public String render(ClassModel model, int index, int size, Map<String, Object> session) {

    StringWriter sw = new StringWriter();
    PrintWriter writer = new PrintWriter(sw);

    if (index == 0) {
      if (model.getType().getRaw().getModuleName().equals("vertx")) {
        writer.print("export interface AsyncResult<T> {\n");
        writer.print("  succeeded() : boolean;\n");
        writer.print("  failed() : boolean;\n");
        writer.print("  cause() : Error | null;\n");
        writer.print("  result() : T | null;\n");
        writer.print("}\n\n");
      } else {
        writer.print("import { AsyncResult } from '@vertx/core';\n\n");
      }
    } else {
      writer.print("\n");
    }

    boolean imports = false;

    for (ApiTypeInfo referencedType : model.getReferencedTypes()) {
      if (!isImported(referencedType, session)) {
        if (!referencedType.getRaw().getModuleName().equals(model.getType().getRaw().getModuleName())) {
          writer.printf("import { %s } from '%s';\n", referencedType.getSimpleName(), getNPMScope(referencedType.getRaw().getModule()));
          imports = true;
        }
      }
    }
    for (ClassTypeInfo dataObjectType : model.getReferencedDataObjectTypes()) {
      if (!isImported(dataObjectType, session)) {
        if (dataObjectType.getRaw().getModuleName().equals(model.getType().getRaw().getModuleName())) {
          writer.printf("import { %s } from './options';\n", dataObjectType.getSimpleName());
          imports = true;
        } else {
          writer.printf("import { %s } from '%s/options';\n", dataObjectType.getSimpleName(), getNPMScope(dataObjectType.getRaw().getModule()));
          imports = true;
        }
      }
    }
    for (EnumTypeInfo enumType : model.getReferencedEnumTypes()) {
      if (!isImported(enumType, session)) {
        if (enumType.getRaw().getModuleName().equals(model.getType().getRaw().getModuleName())) {
          writer.printf("import { %s } from './enums';\n", enumType.getSimpleName());
          imports = true;
        } else {
          writer.printf("import { %s } from '%s/enums';\n", enumType.getSimpleName(), getNPMScope(enumType.getRaw().getModule()));
          imports = true;
        }
      }
    }

    if (imports) {
      writer.print("\n");
    }

    writer.printf("export %s %s {\n", model.isConcrete() ? "class" : "interface", model.getType().getRaw().getSimpleName());
    boolean moreMethods = false;
    for (MethodInfo method : model.getMethods()) {
      if (moreMethods) {
        writer.print("\n");
      }

      if (method.getDoc() != null) {
        writer.print("  /**\n");
        writer.printf("   *%s\n", method.getDoc().toString().replace("\n", "\n   * "));
        writer.print("   */\n");
      }
      writer.printf("  %s%s(", method.isStaticMethod() ? "static " : "", method.getName());
      boolean more = false;
      for (ParamInfo param : method.getParams()) {
        if (more) {
          writer.print(", ");
        }
        writer.printf("%s: %s%s", param.getName(), genParamType(param.getType()), param.getType().isNullable() ? " | null | undefined" : "");
        more = true;
      }

      writer.printf(") : %s%s;\n", genReturnType(method.getReturnType()), method.getReturnType().isNullable() ? " | null" : "");
      moreMethods = true;
    }
    writer.print("}\n");

    return sw.toString();
  }
}
