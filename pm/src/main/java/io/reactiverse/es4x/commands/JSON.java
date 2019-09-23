/*
 * Copyright 2019 Red Hat, Inc.
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
package io.reactiverse.es4x.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.Map;

/**
 * Utility class to handle JSON read/write
 */
public class JSON {

  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  static <T> T parse(File in, Class<T> type) throws IOException {
    try (Reader reader = new FileReader(in)) {
      return GSON.fromJson(reader, type);
    }
  }

  static void encode(File file, Map json) throws IOException {
    try (Writer writer = new FileWriter(file)) {
      GSON.toJson(json, writer);
      writer.flush();
    }
  }
}
