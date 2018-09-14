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

import io.vertx.codegen.ModuleInfo;
import io.vertx.codegen.type.*;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

public final class Util {

  private Util () {
    throw new RuntimeException("Static Class");
  }

  private final static JsonArray registry;

  static {
    /* parse the registry from the system property */
    registry = new JsonArray(System.getProperty("scope-registry", "[]"));
  }

  public static String genReturnType(TypeInfo type) {

    switch (type.getKind()) {
      case STRING:
        return "string";
      case PRIMITIVE:
      case BOXED_PRIMITIVE:
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
      case VOID:
        return "void";
      case THROWABLE:
        return "Error";
      case JSON_OBJECT:
        return "any";
      case JSON_ARRAY:
        return "any[]";
      case DATA_OBJECT:
        return type.getErased().getSimpleName();
      case LIST:
      case SET:
        return genReturnType(((ParameterizedTypeInfo) type).getArg(0)) + "[]";
      case MAP:
        return "{ [key:string]: " + genReturnType(((ParameterizedTypeInfo) type).getArg(1)) + "; }";
      case HANDLER:
        return "(result: " + genReturnType(((ParameterizedTypeInfo) type).getArg(0)) + ") => void";
      default:
        if (type.isParameterized()) {
          return type.getRaw().getSimpleName();
        } else {
          return type.getErased().getSimpleName();
        }
    }
  }

  public static String genParamType(TypeInfo type) {

    switch (type.getKind()) {
      case STRING:
        return "string";
      case PRIMITIVE:
      case BOXED_PRIMITIVE:
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
      case VOID:
        return "void";
      case THROWABLE:
        return "Error";
      case JSON_OBJECT:
        return "any";
      case JSON_ARRAY:
        return "any[]";
      case OBJECT:
        return "any";
      case HANDLER:
        return "(result: " + genParamType(((ParameterizedTypeInfo) type).getArg(0)) + ") => void";
      case ASYNC_RESULT:
        return "AsyncResult<" + genParamType(((ParameterizedTypeInfo) type).getArg(0)) + ">";
      case LIST:
      case SET:
       return genParamType(((ParameterizedTypeInfo) type).getArg(0)) + "[]";
      case MAP:
        return "{ [key:string]: " + genParamType(((ParameterizedTypeInfo) type).getArg(1)) + "; }";
      case FUNCTION:
        return "(t: " + genParamType(((ParameterizedTypeInfo) type).getArg(0)) + ") => " + genParamType(((ParameterizedTypeInfo) type).getArg(1));
      default:
        if (type.isParameterized()) {
          return type.getRaw().getSimpleName();
        } else {
          return type.getErased().getSimpleName();
        }
      }
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

}
