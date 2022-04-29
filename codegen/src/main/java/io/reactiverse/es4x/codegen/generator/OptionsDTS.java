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
import io.vertx.codegen.type.TypeInfo;
import io.vertx.core.json.JsonObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

import static io.reactiverse.es4x.codegen.generator.Util.*;
import static io.reactiverse.es4x.codegen.generator.Util.getNPMScope;

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

    if (isExcludedClass(model.getType().getName())) {
      return null;
    }

    StringWriter sw = new StringWriter();
    PrintWriter writer = new PrintWriter(sw);

    if (index == 0) {
      Util.generateLicense(writer);
      registerJvmClasses();
      for (Object fqcn : jvmClasses("dataObject")) {
        JVMClass.generateDTS(writer, fqcn.toString());
      }

      // include a file if present
      writer.print(includeFileIfPresent("options.header.d.ts"));
    } else {
      writer.print("\n");
    }

    boolean imports = false;

    JsonObject includes = getIncludes(model.getType().getSimpleName());

    if (includes.containsKey("import<d.ts>")) {
      writer.printf("%s\n", includes.getString("import<d.ts>"));
    }

    for (ClassTypeInfo referencedType : filterImports(model.getPropertyMap())) {
      if (referencedType.getKind() == ClassKind.ENUM) {
        if (referencedType.getRaw().getModuleName() == null) {
          System.err.println("@@@ Missing module for ENUM: " + referencedType);
          continue;
        }
        if (sameModule(model.getType(), referencedType.getRaw())) {
          importType(writer, session, referencedType, referencedType.getSimpleName(), "./enums");
          imports = true;
        } else {
          importType(writer, session, referencedType, referencedType.getSimpleName(), getNPMScope(referencedType.getRaw().getModule()) + "/enums");
          imports = true;
        }
      }
      if (referencedType.getKind() == ClassKind.OTHER && referencedType.getDataObject() != null) {
        if (!sameModule(model.getType(), referencedType.getRaw())) {
          importType(writer, session, referencedType, referencedType.getSimpleName(), getNPMScope(referencedType.getRaw().getModule()) + "/options");
          imports = true;
        }
      }
      if (referencedType.getKind() == ClassKind.API) {
        if (sameModule(model.getType(), referencedType.getRaw())) {
          importType(writer, session, referencedType, referencedType.getSimpleName(), "./index");
          imports = true;
        } else {
          importType(writer, session, referencedType, referencedType.getSimpleName(), getNPMScope(referencedType.getRaw().getModule()));
          imports = true;
        }
      }
    }

    // address extends outside the module
    if (model.getSuperType() != null) {
      String selfScope = getNPMScope(model.getModule());
      String superScope = getNPMScope(model.getSuperType().getModule());
      if (!selfScope.equals(superScope)) {
        TypeInfo referencedType = model.getSuperType();
        String suffix = "";
        // take care of the suffixes
        if (referencedType.getKind() == ClassKind.ENUM) {
          suffix = "/enums";
        }
        if (referencedType.getKind() == ClassKind.OTHER && referencedType.getDataObject() != null) {
          suffix = "/options";
        }
        importType(writer, session, referencedType, referencedType.getSimpleName(), getNPMScope(referencedType.getRaw().getModule()) + suffix);
        imports = true;
      }
    }

    if (imports) {
      writer.print("\n");
    }

    generateDoc(writer, model.getDoc(), "");

    writer.printf("export %sclass %s%s%s {\n\n",
      model.isConcrete() ? "" : "abstract ",
      model.getType().getRaw().getSimpleName(),
      model.getSuperType() != null ? " extends " + model.getSuperType().getRaw().getSimpleName() : "",
      includes.containsKey("dataObjectImplements<d.ts>") ? " implements " + includes.getString("dataObjectImplements<d.ts>") : "");

    if (model.hasEmptyConstructor()) {
      writer.print("  constructor();\n\n");
    }

    // constructor
    writer.printf("  constructor(obj: %s%s%s);\n\n",
      model.getType().getRaw().getSimpleName(),
      model.hasStringConstructor() ? " | string" : "",
      model.hasJsonConstructor() ? " | { [key: string]: any }" : "");

    for (Map.Entry<String, PropertyInfo> entry : model.getPropertyMap().entrySet()) {

      final PropertyInfo property = entry.getValue();

      if (property.getGetterMethod() != null) {
        // write getter
        generateDoc(writer, property.getDoc(), "  ");
        writer.printf("  %s%s(): %s;\n\n", getOverride(property), property.getGetterMethod(), genCollectionAwareType(property, false));
      }

      if (property.isSetter()) {
        // write setter
        generateDoc(writer, property.getDoc(), "  ");
        writer.printf("  %s%s(%s: %s%s): %s;\n\n", getOverride(property), property.getSetterMethod(), cleanReserved(property.getName()), genCollectionAwareType(property, true), property.getType().isNullable() ? " | null | undefined" : "", model.getType().getRaw().getSimpleName());
      }

      if (property.isAdder()) {
        // write adder
        generateDoc(writer, property.getDoc(), "  ");
        if (property.getKind() == PropertyKind.MAP) {
          writer.printf("  %s%s(key: string, %s: %s%s): %s;\n\n", getOverride(property), property.getAdderMethod(), cleanReserved(property.getName()), genType(property.getType(), true), property.getType().isNullable() ? " | null | undefined" : "", model.getType().getRaw().getSimpleName());
        } else {
          writer.printf("  %s%s(%s: %s%s): %s;\n\n", getOverride(property), property.getAdderMethod(), cleanReserved(property.getName()), genType(property.getType(), true), property.getType().isNullable() ? " | null | undefined" : "", model.getType().getRaw().getSimpleName());
        }
      }
    }

    if (model.hasToJsonMethod()) {
      writer.print("\n  toJson(): { [key: string]: any };\n");
    }

    if (includes.containsKey("d.ts")) {
      writer.printf("%s\n", includes.getString("d.ts"));
    }

    writer.print("}\n");

    if (index == size - 1) {
      // include a file if present
      writer.print(includeFileIfPresent("options.footer.d.ts"));
    }

    return sw.toString();
  }

  private String getOverride(PropertyInfo property) {
    if (property.getAnnotation("java.lang.Override") != null) {
      return "/* override */ ";
    }
    return "";
  }

  private static String genCollectionAwareType(PropertyInfo propertyInfo, boolean parameter) {
    if (propertyInfo.isList() || propertyInfo.isSet()) {
      return genType(propertyInfo.getType(), parameter) + "[]";
    }

    if (propertyInfo.isMap()) {
      return "{ [key: string]: " + genType(propertyInfo.getType(), parameter) + " }";
    }

    return genType(propertyInfo.getType(), parameter);
  }
}
