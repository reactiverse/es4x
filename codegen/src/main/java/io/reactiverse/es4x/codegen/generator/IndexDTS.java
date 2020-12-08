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
import io.vertx.codegen.type.*;

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

    if (isExcludedClass(model.getType().getName())) {
      return null;
    }

    StringWriter sw = new StringWriter();
    PrintWriter writer = new PrintWriter(sw);

    ClassTypeInfo type = model.getType();

    if (index == 0) {
      Util.generateLicense(writer);
      registerJvmClasses();
      for (Object fqcn : jvmClasses("api")) {
        JVMClass.generateDTS(writer, fqcn.toString());
      }

      // include a file if present
      writer.print(includeFileIfPresent("index.header.d.ts"));

      if (!type.getModuleName().equals("vertx")) {
        if (isOptionalModule("@vertx/core")) {
          writer.println("// @ts-ignore");
        }
        // hard coded imports for non codegen types
        writer.print("import { Handler, AsyncResult } from '@vertx/core';\n\n");
      }
    } else {
      writer.print("\n");
    }

    boolean imports = false;

    @SuppressWarnings("unchecked")
    Map<String, String> aliasMap = (Map<String, String>) session.computeIfAbsent("aliasMap", (a) -> new HashMap<String, String>());
    for (ApiTypeInfo referencedType : model.getReferencedTypes()) {
      if (!sameModule(type, referencedType.getRaw())) {
        String simpleName = referencedType.getSimpleName();
        if (simpleName.equals(model.getIfaceSimpleName())) {
          String aliasName = simpleName + "Super";
          simpleName = simpleName + " as " + aliasName;
          aliasMap.put(referencedType.getName(), aliasName);
        }
        importType(writer, session, referencedType, simpleName, getNPMScope(referencedType.getRaw().getModule()));
        imports = true;
      }
    }
    for (ClassTypeInfo dataObjectType : model.getReferencedDataObjectTypes()) {
      if (sameModule(type, dataObjectType.getRaw())) {
        importType(writer, session, dataObjectType, dataObjectType.getSimpleName(), "./options");
        imports = true;
      } else {
        importType(writer, session, dataObjectType, dataObjectType.getSimpleName(), getNPMScope(dataObjectType.getRaw().getModule()) + "/options");
        imports = true;
      }
    }
    for (EnumTypeInfo enumType : model.getReferencedEnumTypes()) {
      if (enumType.getRaw().getModuleName() == null) {
        System.err.println("@@@ Missing module for ENUM: " + enumType);
        continue;
      }
      if (sameModule(type, enumType.getRaw())) {
        importType(writer, session, enumType, enumType.getSimpleName(), "./enums");
        imports = true;
      } else {
        importType(writer, session, enumType, enumType.getSimpleName(), getNPMScope(enumType.getRaw().getModule()) + "/enums");
        imports = true;
      }
    }

    final Set<String> superTypes = new HashSet<>();
    // ensure that all super types are also imported
    model.getAbstractSuperTypes().forEach(ti -> {
      if (!sameModule(type, ti.getRaw())) {
        importType(writer, session, ti, ti.getSimpleName(), getNPMScope(ti.getRaw().getModule()));
      }
      superTypes.add(genType(ti));
    });

    imports |= superTypes.size() > 0;

    if (model.isHandler()) {
      if (model.isConcrete()) {
        TypeInfo ti = model.getHandlerArg();
        if (!sameModule(type, ti.getRaw())) {
          importType(writer, session, ti, ti.getSimpleName(), getNPMScope(ti.getRaw().getModule()) + (ti.isDataObjectHolder() ? "/options" : ""));
          imports = true;
        }
        superTypes.add("Handler<" + genType(ti) + ">");
      }
    }

    if (imports) {
      writer.print("\n");
    }

    generateDoc(writer, model.getDoc(), "");

    writer.printf("export %s %s%s", model.isConcrete() ? "abstract class" : "interface", type.getSimpleName(), genGeneric(type.getParams()));

    if (model.isConcrete()) {
      if (model.getConcreteSuperType() != null) {
        String simpleName = aliasMap.get(model.getConcreteSuperType().getName());
        writer.printf(" extends %s", simpleName != null ? simpleName : genType(model.getConcreteSuperType()));
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

        generateDoc(writer, constant.getDoc(), "  ");

        writer.printf("  static readonly %s : %s;\n", constant.getName(), genType(constant.getType()));
        moreConstants = true;
      }
    }

    boolean moreMethods = false;
    boolean hasStaticMethodsInInterface = false;

    for (MethodInfo method : model.getMethods()) {
      if (isExcluded(type.getSimpleName(), method.getName(), method.getParams())) {
        continue;
      }

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
      if (isExcluded(type.getSimpleName(), method.getName(), method.getParams())) {
        continue;
      }

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

        generateDoc(writer, constant.getDoc(), "  ");

        writer.printf("  static readonly %s : %s;\n", constant.getName(), genType(constant.getType()));
        moreConstants = true;
      }

      moreMethods = false;
      for (MethodInfo method : model.getMethods()) {
        if (isExcluded(type.getSimpleName(), method.getName(), method.getParams())) {
          continue;
        }

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

    if (index == size - 1) {
      // include a file if present
      writer.print(includeFileIfPresent("index.footer.d.ts"));
    }

    return sw.toString();
  }

  private void generateMethod(PrintWriter writer, ClassTypeInfo type, MethodInfo method) {
    if (method.getKind() == MethodKind.FUTURE) {
      // slice the last element
      List<ParamInfo> params = new ArrayList<>(method.getParams());
      ParamInfo lastParam = params.remove(params.size() - 1);
      // extract the generic param out of the Future
      TypeInfo arg = ((ParameterizedTypeInfo) lastParam.getType()).getArg(0);
      generateMethod(writer, type, method, params, lastParam.isNullable(), "PromiseLike<" + (arg.isParameterized() ? genType(((ParameterizedTypeInfo) arg).getArg(0)) : "any") + ">");
      writer.print("\n");
    }
    generateMethod(writer, type, method, method.getParams(), method.getReturnType().isNullable(), genType(method.getReturnType()));
  }

  private void generateMethod(PrintWriter writer, ClassTypeInfo type, MethodInfo method, List<ParamInfo> params, boolean returnTypeNullable, String returnOverride) {

    generateDoc(writer, method.getDoc(), "  ");

    if (getOverrideArgs(type.getSimpleName(), method.getName()) != null) {
      writer.printf("  %s%s%s(%s", method.isStaticMethod() ? "static " : "", method.getName(), genGeneric(method.getTypeParams()), getOverrideArgs(type.getSimpleName(), method.getName()));
    } else {
      writer.printf("  %s%s%s(", method.isStaticMethod() ? "static " : "", method.getName(), genGeneric(method.getTypeParams()));
      boolean more = false;
      for (ParamInfo param : params) {
        if (more) {
          writer.print(", ");
        }
        writer.printf("%s: %s%s", cleanReserved(param.getName()), genType(param.getType(), true), param.getType().isNullable() ? " | null | undefined" : "");
        more = true;
      }
    }

    if (getOverrideReturn(type.getSimpleName(), method.getName()) != null) {
      writer.printf(") : %s%s;\n", getOverrideReturn(type.getSimpleName(), method.getName()), returnTypeNullable ? " | null" : "");
    } else {
      writer.printf(") : %s%s;\n", returnOverride, returnTypeNullable ? " | null" : "");
    }
  }
}
