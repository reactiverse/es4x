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

import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.Jsoner;

import java.io.*;

/**
 * Utility class to handle JSON read/write
 */
public class JSON {

  @SuppressWarnings("unchecked")
  static <T> T parse(File in) throws IOException {
    try (Reader fileReader = new FileReader(in)) {
      return (T) Jsoner.deserialize(fileReader);
    } catch (JsonException | ClassCastException e) {
      throw new IOException(e);
    }
  }

  static void encode(File file, Object json) throws IOException {
    try (Writer writer = new FileWriter(file)) {
      Jsoner.prettyPrint(new StringReader(Jsoner.serialize(json)), writer, "  ", System.lineSeparator());
      writer.flush();
    } catch (JsonException e) {
      throw new IOException(e);
    }
  }
}
