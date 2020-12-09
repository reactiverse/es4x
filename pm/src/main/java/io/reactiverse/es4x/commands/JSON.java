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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Utility class to handle JSON read/write
 */
public class JSON {

  static JSONObject parseObject(File in) throws IOException {
    return new JSONObject(new String(Files.readAllBytes(in.toPath()), StandardCharsets.UTF_8));
  }

  static JSONArray parseArray(File in) throws IOException {
    return new JSONArray(new String(Files.readAllBytes(in.toPath()), StandardCharsets.UTF_8));
  }

  static void encodeObject(File file, JSONObject json) throws IOException {
    try (OutputStream out = new FileOutputStream(file)) {
      out.write(json.toString(2).getBytes(StandardCharsets.UTF_8));
    }
  }

  static void encodeArray(File file, JSONArray json) throws IOException {
    try (OutputStream out = new FileOutputStream(file)) {
      out.write(json.toString(2).getBytes(StandardCharsets.UTF_8));
    }
  }
}
