package io.reactiverse.es4x.codegen.generator;

import io.vertx.codegen.EnumModel;
import io.vertx.codegen.EnumValueInfo;
import io.vertx.codegen.Generator;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Map;

public class EnumDTS extends Generator<EnumModel> {

  public EnumDTS() {
    incremental = true;

    kinds = new HashSet<>();
    kinds.add("enum");

    name = "es4x-generator";
  }

  @Override
  public String filename(EnumModel model) {
    return "npm/enums.d.ts";
  }

  @Override
  public String render(EnumModel model, int index, int size, Map<String, Object> session) {

    StringWriter sw = new StringWriter();
    PrintWriter writer = new PrintWriter(sw);

    if (index != 0) {
      writer.print("\n");
    }

    writer.printf("export enum %s {\n", model.getType().getRaw().getSimpleName());
    for (int i = 0; i < model.getValues().size(); i++) {
      EnumValueInfo value = model.getValues().get(i);
      writer.printf("  %s", value.getIdentifier());
      if (i != model.getValues().size() - 1) {
        writer.print(",");
      }
      writer.print("\n");
    }
    writer.print("}\n");

    return sw.toString();
  }
}
