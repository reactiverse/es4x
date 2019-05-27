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
import io.vertx.codegen.doc.Doc;
import io.vertx.codegen.type.ApiTypeInfo;
import io.vertx.codegen.type.ClassTypeInfo;
import io.vertx.codegen.type.EnumTypeInfo;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

import static io.reactiverse.es4x.codegen.generator.Util.*;

public class IndexDTS extends Generator<ClassModel> {

  public IndexDTS() {
    incremental = true;

    kinds = new HashSet<>();
    kinds.add("class");

    name = "es4x-generator (index.d.ts)";
  }

  @Override
  public String filename(ClassModel model) {
    return "npm/index.d.ts";
  }

  @Override
  public String render(ClassModel model, int index, int size, Map<String, Object> session) {

    StringWriter sw = new StringWriter();
    PrintWriter writer = new PrintWriter(sw);

    ClassTypeInfo type = model.getType();

    if (index == 0) {
      Util.generateLicense(writer);

      if (type.getModuleName().equals("vertx")) {
        writer.print("export interface Handler<T> {\n");
        writer.print("  handle(arg0: T) : void;\n");
        writer.print("}\n\n");
        writer.print("export interface AsyncResult<T> {\n");
        writer.print("  succeeded() : boolean;\n");
        writer.print("  failed() : boolean;\n");
        writer.print("  cause() : Error | null;\n");
        writer.print("  result() : T | null;\n");
        writer.print("}\n\n");
      } else {
        writer.print("import { Handler, AsyncResult } from '@vertx/core';\n\n");
      }
    } else {
      writer.print("\n");
    }

    boolean imports = false;

    for (ApiTypeInfo referencedType : model.getReferencedTypes()) {
      if (!isImported(referencedType, session)) {
        if (!referencedType.getRaw().getModuleName().equals(type.getModuleName())) {
          writer.printf("import { %s } from '%s';\n", referencedType.getSimpleName(), getNPMScope(referencedType.getRaw().getModule()));
          imports = true;
        }
      }
    }
    for (ClassTypeInfo dataObjectType : model.getReferencedDataObjectTypes()) {
      if (!isImported(dataObjectType, session)) {
        if (dataObjectType.getRaw().getModuleName().equals(type.getModuleName())) {
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
        if (enumType.getRaw().getModuleName().equals(type.getModuleName())) {
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

    final Set<String> superTypes = new HashSet<>();
    model.getAbstractSuperTypes().forEach(ti -> superTypes.add(genType(ti)));

    if (model.isHandler()) {
      if (model.isConcrete()) {
        superTypes.add("Handler<" + genType(model.getHandlerArg()) + ">");
      }
    }

    writer.printf("export %s %s%s", model.isConcrete() ? "abstract class" : "interface", type.getSimpleName(), genGeneric(type.getParams()));

    if (model.isConcrete()) {
      if (model.getConcreteSuperType() != null) {
        writer.printf(" extends %s", genType(model.getConcreteSuperType()));
      }
      if (!superTypes.isEmpty()) {
        writer.printf(" implements %s", String.join(", ", superTypes));
      }
    } else {
      if (model.isHandler()) {
        writer.printf(" extends Handler<%s>", genType(model.getHandlerArg()));
        if (!superTypes.isEmpty()) {
          writer.printf(", %s", String.join(", ", superTypes));
        }
      } else {
        if (!superTypes.isEmpty()) {
          writer.printf(" extends %s", String.join(", ", superTypes));
        }
      }
    }

    writer.print(" {\n");

    boolean moreConstants = false;
    boolean hasConstantInInterface = !model.isConcrete() && model.getConstants().size() > 0;

    // this looks awkward (and it is) but TS does not allow static constants in interfaces
    // so they get listed on the abstract classes.
    if (model.isConcrete()) {
      for (ConstantInfo constant : model.getConstants()) {
        if (moreConstants) {
          writer.print("\n");
        }

        generateDoc(writer, constant.getDoc());

        writer.printf("  static readonly %s : %s;\n", constant.getName(), genType(constant.getType()));
        moreConstants = true;
      }
    }

    boolean moreMethods = false;
    boolean hasStaticMethodsInInterface = false;

    for (MethodInfo method : model.getMethods()) {
      if (!model.isConcrete() && method.isStaticMethod()) {
        hasStaticMethodsInInterface = true;
        continue;
      }

      if (moreMethods || moreConstants) {
        writer.print("\n");
      }

      generateMethod(writer, type, method);
      moreMethods = true;
    }

    // BEGIN of non polyglot methods...

    for (MethodInfo method : model.getAnyJavaTypeMethods()) {
      if (moreMethods || moreConstants) {
        writer.print("\n");
      }

      generateMethod(writer, type, method);
      moreMethods = true;
    }
    writer.print("}\n");


    // if the model is not concrete (interface) we need to merge types to allow declaring the constants
    // from the java interface

    if (hasConstantInInterface || hasStaticMethodsInInterface) {
      writer.print("\n");
      writer.printf("export abstract class %s%s implements %s%s {\n", type.getSimpleName(), genGeneric(type.getParams()), type.getSimpleName(), genGeneric(type.getParams()));

      moreConstants = false;
      for (ConstantInfo constant : model.getConstants()) {
        if (moreConstants) {
          writer.print("\n");
        }

        generateDoc(writer, constant.getDoc());

        writer.printf("  static readonly %s : %s;\n", constant.getName(), genType(constant.getType()));
        moreConstants = true;
      }

      moreMethods = false;
      for (MethodInfo method : model.getMethods()) {
        if (!method.isStaticMethod()) {
          continue;
        }

        if (moreMethods || moreConstants) {
          writer.print("\n");
        }

        generateMethod(writer, type, method);
        moreMethods = true;
      }

      writer.print("}\n");
    }

    return sw.toString();
  }


  private void generateDoc(PrintWriter writer, Doc doc) {
    if (doc != null) {
      writer.print("  /**\n");
      writer.printf("   *%s\n", doc.toString().replace("\n", "\n   * "));
      writer.print("   */\n");
    }
  }

  private void generateMethod(PrintWriter writer, ClassTypeInfo type, MethodInfo method) {

    generateDoc(writer, method.getDoc());

    if (getOverrideArgs(type.getSimpleName(), method.getName()) != null) {
      writer.printf("  %s%s%s(%s", method.isStaticMethod() ? "static " : "", method.getName(), genGeneric(method.getTypeParams()), getOverrideArgs(type.getSimpleName(), method.getName()));
    } else {
      writer.printf("  %s%s%s(", method.isStaticMethod() ? "static " : "", method.getName(), genGeneric(method.getTypeParams()));
      boolean more = false;
      for (ParamInfo param : method.getParams()) {
        if (more) {
          writer.print(", ");
        }
        writer.printf("%s: %s%s", cleanReserved(param.getName()), genType(param.getType()), param.getType().isNullable() ? " | null | undefined" : "");
        more = true;
      }
    }

    if (getOverrideReturn(type.getSimpleName(), method.getName()) != null) {
      writer.printf(") : %s%s;\n", getOverrideReturn(type.getSimpleName(), method.getName()), method.getReturnType().isNullable() ? " | null" : "");
    } else {
      writer.printf(") : %s%s;\n", genType(method.getReturnType()), method.getReturnType().isNullable() ? " | null" : "");
    }
  }
}
