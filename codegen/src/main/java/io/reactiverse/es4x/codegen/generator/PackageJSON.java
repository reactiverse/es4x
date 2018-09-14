package io.reactiverse.es4x.codegen.generator;

import io.vertx.codegen.Generator;
import io.vertx.codegen.ModuleModel;
import io.vertx.core.json.JsonObject;

import java.util.HashSet;
import java.util.Map;

import static io.reactiverse.es4x.codegen.generator.Util.getNPMScope;

public class PackageJSON extends Generator<ModuleModel> {

  public PackageJSON() {
    kinds = new HashSet<>();
    kinds.add("module");

    name = "es4x-generator";
  }

  @Override
  public String filename(ModuleModel model) {
    return "npm/package.json";
  }

  @Override
  public String render(ModuleModel model, int index, int size, Map<String, Object> session) {

    /* attempt to merge from the environment config */
    JsonObject json = new JsonObject(System.getProperty("package-json", "{}"));

    if (json.getString("name") == null || json.getString("name").equals("")) {
      json.put("name", getNPMScope(model.getModule()));
    }

    if (!Boolean.getBoolean("npm-meta-package")) {
      /* always overwritten */
      json.put("main", "index.js");
      json.put("types", "index.d.ts");
    }

    // extras
    json.put("sideEffects", false);

    return json.encodePrettily();
  }
}
