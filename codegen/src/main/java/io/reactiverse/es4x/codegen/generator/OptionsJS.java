package io.reactiverse.es4x.codegen.generator;

import io.vertx.codegen.DataObjectModel;
import io.vertx.codegen.EnumModel;
import io.vertx.codegen.Generator;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Map;

import static io.reactiverse.es4x.codegen.generator.Util.getNPMScope;

public class OptionsJS extends Generator<DataObjectModel> {

  public OptionsJS() {
    incremental = true;

    kinds = new HashSet<>();
    kinds.add("dataObject");

    name = "es4x-generator";
  }

  @Override
  public String filename(DataObjectModel model) {
    return "npm/options.js";
  }

  @Override
  public String render(DataObjectModel model, int index, int size, Map<String, Object> session) {

    StringWriter sw = new StringWriter();
    PrintWriter writer = new PrintWriter(sw);

    if (index == 0) {
      writer.printf("/// <reference types=\"%s/options\" />\n", getNPMScope(model.getType().getRaw().getModule()));
      writer.print("module.exports = {\n");
    }

    writer.printf("  %s: Java.type('%s')", model.getType().getRaw().getSimpleName(), model.getType().getName());

    if (index != size - 1) {
      writer.print(',');
    }

    writer.print('\n');

    if (index == size - 1) {
      writer.print("};\n");
    }

    return sw.toString();
  }
}
