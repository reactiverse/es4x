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

import io.vertx.codegen.ModuleInfo;
import io.vertx.codegen.TypeParamInfo;
import io.vertx.codegen.type.*;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public final class Util {

  private Util () {
    throw new RuntimeException("Static Class");
  }

  private final static JsonArray registry;
  private final static int year;

  static {
    /* parse the registry from the system property */
    registry = new JsonArray(System.getProperty("scope-registry", "[]"));
    year = Calendar.getInstance().get(Calendar.YEAR);
  }

  public static String genType(TypeInfo type) {

    switch (type.getKind()) {
      case STRING:
        return "string";
      case BOXED_PRIMITIVE:
      case PRIMITIVE:
        switch (type.getSimpleName()) {
          case "boolean":
          case "Boolean":
            return "boolean";
          case "char":
          case "Character":
            return "string";
          default:
            return "number";
        }
      case ENUM:
        if (type.getRaw().getModule() != null) {
          return type.getSimpleName();
        } else {
          return "any";
        }
      case OBJECT:
        if (type.isVariable()) {
          return type.getName();
        } else {
          return "any";
        }
      case JSON_OBJECT:
        return "{ [key: string]: any }";
      case JSON_ARRAY:
        return "any[]";
      case THROWABLE:
        return "Error";
      case VOID:
        return "void";
      case LIST:
      case SET:
        if (type.isParameterized()) {
          return genType(((ParameterizedTypeInfo) type).getArg(0)) + "[]";
        } else {
          return "any[]";
        }
      case MAP:
        if (type.isParameterized()) {
          return "{ [key: " + genType(((ParameterizedTypeInfo) type).getArg(0)) + "]: " + genType(((ParameterizedTypeInfo) type).getArg(1)) + "; }";
        } else {
          return "{ [key: string]: any }";
        }
      case API:
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        if (type.isParameterized()) {
          for (TypeInfo t : ((ParameterizedTypeInfo) type).getArgs()) {
            if (!first) {
              sb.append(", ");
            }
            sb.append(genType(t));
            first = false;
          }
          return type.getRaw().getSimpleName() + "<" + sb.toString() + ">";
        } else {
          // TS is strict with generics, you can't define/use a generic type with out its generic <T>
          if (type.getRaw() != null && type.getRaw().getParams().size() > 0) {
            for (TypeParamInfo t : type.getRaw().getParams()) {
              if (!first) {
                sb.append(", ");
              }
              sb.append("any");
              first = false;
            }
            return type.getSimpleName() + "<" + sb.toString() + ">";
          } else {
            return type.getErased().getSimpleName();
          }
        }
      case DATA_OBJECT:
        return type.getErased().getSimpleName();
      case HANDLER:
        if (type.isParameterized()) {
          return "(res: " + genType(((ParameterizedTypeInfo) type).getArg(0)) + ") => void";
        } else {
          return "(res: any) => void";
        }
      case FUNCTION:
        if (type.isParameterized()) {
          return "(arg: " + genType(((ParameterizedTypeInfo) type).getArg(0)) + ") => " + genType(((ParameterizedTypeInfo) type).getArg(1));
        } else {
          return "(arg: any) => any";
        }
      case ASYNC_RESULT:
        if (type.isParameterized()) {
          return "AsyncResult<" + genType(((ParameterizedTypeInfo) type).getArg(0)) + ">";
        } else {
          return "AsyncResult<any>";
        }
      case CLASS_TYPE:
        return "any /* TODO: class */";
      case OTHER:
        return "any /* TODO: other */";
    }

    System.out.println("!!! " + type + " - " + type.getKind());
    return "";
  }

  public static String genGeneric(List<? extends TypeParamInfo> params) {
    StringBuilder sb = new StringBuilder();

    if (params.size() > 0) {
      sb.append("<");
      boolean firstParam = true;
      for (TypeParamInfo p : params) {
        if (!firstParam) {
          sb.append(", ");
        }
        sb.append(p.getName());
        firstParam = false;
      }
      sb.append(">");
    }

    return sb.toString();
  }

  public static boolean isImported(TypeInfo ref, Map<String, Object> session) {
    if (ref.getRaw().getModuleName() == null) {
      return true;
    }

    final String key = ref.getRaw().getModuleName() + "/" + ref.getSimpleName();

    if (!session.containsKey(key)) {
      session.put(key, ref);
      return false;
    }

    return true;
  }

  public static String getNPMScope(ModuleInfo module) {

    String scope = "";
    String name = "";

    /* get from registry */
    for (Object el : registry) {
      JsonObject entry = (JsonObject) el;

      if (entry.getString("group").equals(module.getGroupPackage())) {
        scope = entry.getString("scope", "");
        if (scope.charAt(0) != '@') {
          scope = "@" + scope;
        }
        if (scope.charAt(scope.length() - 1) != '/') {
          scope += "/";
        }
        if (entry.containsKey("prefix")) {
          if (module.getName().startsWith(entry.getString("prefix"))) {
            if (entry.getBoolean("stripPrefix")) {
              name = module.getName().substring(entry.getString("prefix").length());
            } else {
              name = module.getName();
            }
          }
        }
        if (entry.containsKey("module")) {
          if (module.getName().equals(entry.getString("module"))) {
            name = entry.getString("name");
          }
        }
      }
    }

    if (name.equals("")) {
      name = module.getName();
    }

    return scope + name;
  }

  public static String includeFileIfPresent(String file) {
    final File path = new File(System.getProperty("basedir"), file);
    if (path.exists()) {
      try {
        byte[] bytes = Files.readAllBytes(path.toPath());
        String md = new String(bytes, StandardCharsets.UTF_8);
        if (md.length() > 0) {
          if (md.charAt(md.length() - 1) != '\n') {
            return md + "\n\n";
          } else {
            return md + "\n";
          }
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    return "";
  }

  public static void generateLicense(PrintWriter writer) {
    writer.println("/*");
    writer.println(" * Copyright " + year + " ES4X");
    writer.println(" *");
    writer.println(" * ES4X licenses this file to you under the Apache License, version 2.0");
    writer.println(" * (the \"License\"); you may not use this file except in compliance with the");
    writer.println(" * License.  You may obtain a copy of the License at:");
    writer.println(" *");
    writer.println(" * http://www.apache.org/licenses/LICENSE-2.0");
    writer.println(" *");
    writer.println(" * Unless required by applicable law or agreed to in writing, software");
    writer.println(" * distributed under the License is distributed on an \"AS IS\" BASIS, WITHOUT");
    writer.println(" * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the");
    writer.println(" * License for the specific language governing permissions and limitations");
    writer.println(" * under the License.");
    writer.println(" */");
    writer.println();
  }
}
