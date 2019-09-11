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

import io.vertx.codegen.*;
import io.vertx.codegen.type.ClassKind;
import io.vertx.codegen.type.ClassTypeInfo;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

import static io.reactiverse.es4x.codegen.generator.Util.*;

public class OptionsDTS extends Generator<DataObjectModel> {

  public OptionsDTS() {
    incremental = true;

    kinds = new HashSet<>();
    kinds.add("dataObject");

    name = "es4x-generator (options.d.ts)";
  }

  @Override
  public String filename(DataObjectModel model) {
    return "npm/options.d.ts";
  }

  private Collection<ClassTypeInfo> filterImports(Map<String, PropertyInfo> properties) {
    Set<ClassTypeInfo> result = new HashSet<>();
    for (PropertyInfo p : properties.values()) {
      p.getType().collectImports(result);
    }

    return result;
  }
  @Override
  public String render(DataObjectModel model, int index, int size, Map<String, Object> session) {

    if (isBlacklistedClass(model.getType().getName())) {
      return null;
    }

    StringWriter sw = new StringWriter();
    PrintWriter writer = new PrintWriter(sw);

    boolean imports = false;

    if (index == 0) {
      Util.generateLicense(writer);
    } else {
      writer.print("\n");
    }

    for (ClassTypeInfo referencedType : filterImports(model.getPropertyMap())) {
      if (!isImported(referencedType, session)) {
        if (referencedType.getKind() == ClassKind.ENUM) {
          if (referencedType.getRaw().getModuleName().equals(model.getType().getRaw().getModuleName())) {
            writer.printf("import { %s } from './enums';\n", referencedType.getSimpleName());
            imports = true;
          } else {
            // ignore missing imports
            if (isOptionalModule(getNPMScope(referencedType.getRaw().getModule()))) {
              writer.println("// @ts-ignore");
            }
            writer.printf("import { %s } from '%s/enums';\n", referencedType.getSimpleName(), getNPMScope(referencedType.getRaw().getModule()));
            imports = true;
          }
        }
        if (referencedType.getKind() == ClassKind.DATA_OBJECT) {
          if (!referencedType.getRaw().getModuleName().equals(model.getType().getRaw().getModuleName())) {
            // ignore missing imports
            if (isOptionalModule(getNPMScope(referencedType.getRaw().getModule()))) {
              writer.println("// @ts-ignore");
            }
            writer.printf("import { %s } from '%s/options';\n", referencedType.getSimpleName(), getNPMScope(referencedType.getRaw().getModule()));
            imports = true;
          }
        }
        if (referencedType.getKind() == ClassKind.API) {
          if (referencedType.getRaw().getModuleName().equals(model.getType().getRaw().getModuleName())) {
            writer.printf("import { %s } from './index';\n", referencedType.getSimpleName());
            imports = true;
          } else {
            // ignore missing imports
            if (isOptionalModule(getNPMScope(referencedType.getRaw().getModule()))) {
              writer.println("// @ts-ignore");
            }
            writer.printf("import { %s } from '%s';\n", referencedType.getSimpleName(), getNPMScope(referencedType.getRaw().getModule()));
            imports = true;
          }
        }
      }
    }

    if (imports) {
      writer.print("\n");
    }

    if (model.getDoc() != null) {
      writer.print("/**\n");
      writer.printf(" *%s\n", model.getDoc().toString().replace("\n", "\n * "));
      writer.print("*/\n");
    }

    writer.printf("export %sclass %s {\n", model.isConcrete() ? "" : "abstract ", model.getType().getRaw().getSimpleName());

    // TODO: handle extends/implements

    writer.print("\n");
    writer.print("  constructor();\n");
    // copy constructor
    writer.printf("  constructor(obj: %s);\n", model.getType().getRaw().getSimpleName());

    boolean more = false;

    for (Map.Entry<String, PropertyInfo> entry : model.getPropertyMap().entrySet()) {

      final PropertyInfo property = entry.getValue();

      if (more) {
        writer.print("\n");
      }

      // write getter
      if (property.getDoc() != null) {
        writer.print("  /**\n");
        writer.printf("   *%s\n", property.getDoc().toString().replace("\n", "\n   * "));
        writer.print("   */\n");
      }
      // custom vert.x style
      String getter = property.getGetterMethod();
      writer.printf("  %s(): %s;\n", getter == null ? entry.getKey() : getter, genType(property.getType()));


      if (property.isSetter()) {
        // write setter
        writer.print("\n");

        if (property.getDoc() != null) {
          writer.print("  /**\n");
          writer.printf("   *%s\n", property.getDoc().toString().replace("\n", "\n   * "));
          writer.print("   */\n");
        }
        writer.printf("  %s(%s: %s): %s;\n", property.getSetterMethod(), cleanReserved(entry.getKey()), genType(property.getType()), model.getType().getRaw().getSimpleName());
      }

      if (property.isAdder()) {
        // write adder
        writer.print("\n");

        if (property.getDoc() != null) {
          writer.print("  /**\n");
          writer.printf("   *%s\n", property.getDoc().toString().replace("\n", "\n   * "));
          writer.print("   */\n");
        }
        writer.printf("  %s(%s: %s): %s;\n", property.getAdderMethod(), cleanReserved(entry.getKey()), genType(property.getType()), model.getType().getRaw().getSimpleName());
      }

      more = true;
    }

    writer.print("}\n");

    return sw.toString();
  }
}
