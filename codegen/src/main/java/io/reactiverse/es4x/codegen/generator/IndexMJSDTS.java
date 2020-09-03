/*
 * Copyright 2020 Frank Lemanschik.
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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Map;

import static io.reactiverse.es4x.codegen.generator.Util.*;

public class IndexMJSDTS extends Generator<Model> {

  public IndexMJSDTS() {
    kinds = new HashSet<>();
    kinds.add("class");
    kinds.add("enum");
    kinds.add("dataObject");
    kinds.add("module");

    name = "es4x-generator (README.md)";
    incremental = true;
  }

  @Override
  public String filename(Model model) {
    return "npm/index.mjs.d.ts";
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

    StringWriter sw = new StringWriter();
    PrintWriter writer = new PrintWriter(sw);

    
    writer.print("import commonjs from \"../types/index\";export = commonjs;\n");


    return sw.toString();
  }
}
