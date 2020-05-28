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
import io.vertx.core.json.JsonObject;

import java.util.HashSet;
import java.util.Map;

import static io.reactiverse.es4x.codegen.generator.Util.getNPMScope;

public class PackageJSON extends Generator<Model> {

  private final String build = System.getenv("PRERELEASE");

  public PackageJSON() {
    kinds = new HashSet<>();
    kinds.add("class");
    kinds.add("enum");
    kinds.add("dataObject");
    kinds.add("module");

    name = "es4x-generator (package.json)";
    incremental = true;
  }

  @Override
  public String filename(Model model) {
    return "npm/package.json";
  }

  @Override
  public String render(Model model, int index, int size, Map<String, Object> session) {

    if (model instanceof EnumModel) {
      session.putIfAbsent("enum", "seen");
    }

    if (model instanceof ClassModel) {
      session.putIfAbsent("index", "seen");
    }

    if (model instanceof DataObjectModel) {
      session.putIfAbsent("options", "seen");
    }

    if (index != size - 1) {
      // wait for the last run
      return "";
    }

    /* attempt to merge from the environment config */
    JsonObject json = new JsonObject(System.getProperty("package-json", "{\"version\": \"0.0.0\", \"private\": true, \"name\": \"noname\"}"));

    if (json.getString("name") == null || json.getString("name").equals("")) {
      json.put("name", getNPMScope(model.getModule()));
    }

    if (session.containsKey("index")) {
      /* always overwritten */
      json.put("main", "index.js");
      json.put("module", "index.mjs");
      json.put("types", "index.d.ts");
    }

    // extras
    json.put("sideEffects", false);

    // repository
    String url = System.getProperty("git-url");
    if (url != null && url.length() > 0) {
      String directory = System.getProperty("git-directory");
      if (directory != null && directory.length() > 0) {
        json.put("repository",
          new JsonObject()
            .put("type", "git")
            .put("url", url)
            .put("directory", directory));
      } else {
        json.put("repository",
          new JsonObject()
            .put("type", "git")
            .put("url", url));
      }
    }

    // fix version(s)
    json.put("version", toSemVer(json.getString("version")));
    if (json.containsKey("dependencies")) {
      for (Map.Entry<String, Object> kv : json.getJsonObject("dependencies")) {
        kv.setValue(toSemVer((String) kv.getValue()));
      }
    }
    if (json.containsKey("devDependencies")) {
      if (json.getJsonObject("devDependencies") != null) {
        JsonObject deps = json.getJsonObject("devDependencies");
        if (deps.size() == 0) {
          // cleanup
          json.remove("devDependencies");
        } else {
          for (Map.Entry<String, Object> kv : deps) {
            kv.setValue(toSemVer((String) kv.getValue()));
          }
        }
      } else {
        // cleanup
        json.remove("devDependencies");
      }
    }

    return json.encodePrettily();
  }

  private String toSemVer(String string) {
    String base = "0.0.0";
    char[] version = string.toCharArray();
    int dots = 0;
    for (int i = 0; i < version.length; i++) {
      if (version[i] == '-') {
        // start of prerelease
        break;
      }
      if (version[i] == '.') {
        dots++;
        if (dots > 2) {
          version[i] = '-';
        }
      }
    }

    String semver;

    if (dots == 2) {
      semver = string;
    } else if (dots > 2) {
      semver = new String(version);
    } else {
      semver = base.substring(0, 2 * dots) + new String(version);
    }

    return build != null ? semver + "-" + build : semver;
  }
}
