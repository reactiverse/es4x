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
package io.reactiverse.es4x;

import io.reactiverse.es4x.impl.graal.GraalLoader;
import io.vertx.core.Vertx;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class GraalShell {

  private static String toCamelCase(String arg) {
    StringBuilder sb = new StringBuilder();
    boolean upperNext = false;

    for (int i = 0; i < arg.length(); i++) {
      char ch = arg.charAt(i);

      if (ch == '-') {
        upperNext = true;
        continue;
      }

      if (upperNext) {
        upperNext = false;
        if (i != 1) {
          ch = Character.toUpperCase(ch);
        }
      }
      sb.append(ch);
    }
    return sb.toString();
  }

  public static void main(String[] args) throws IOException {

    // Graal mode
    System.setProperty("es4x.engine", "GraalJS");
    final Runtime runtime = Runtime.getCurrent();

    final Map<String, Object> options = new HashMap<>();
    String script = null;

    // parse the arguments
    if (args != null) {
      for (String arg : args) {
        if (arg != null && arg.length() > 0 && arg.charAt(0) == '-') {
          if ("-cluster".equals(arg)) {
            // adapt argument to match vertx options
            arg = "-clustered";
          }

          int idx = arg.indexOf('=');
          if (idx != -1) {
            final String fragment = arg.substring(idx + 1);
            Object value = fragment;

            // if the fragment is a boolean value cast it
            if ("true".equalsIgnoreCase(fragment) || "false".equalsIgnoreCase(fragment)) {
              value = Boolean.parseBoolean(fragment);
            } else {
              // attempt to cast numeric values to number
              try {
                value = Integer.parseInt(fragment);
              } catch (NumberFormatException e) {
                // not a numeric value, ignore!
              }
            }

            options.put(toCamelCase(arg.substring(0, idx)), value);
          } else {
            options.put(toCamelCase(arg), true);
          }
        } else {
          if (script == null) {
            script = arg;
          } else {
            throw new IllegalArgumentException("Invalid argument: " + arg);
          }
        }
      }
    }

    final Vertx vertx = runtime.vertx(options);

    final Context context = Context
      .newBuilder("js")
      .allowHostAccess((Boolean) options.getOrDefault("allowHostAccess", true))
      .allowCreateThread((Boolean) options.getOrDefault("allowCreateThread", true))
      .allowAllAccess((Boolean) options.getOrDefault("allowAllAccess", false))
      .allowHostClassLoading((Boolean) options.getOrDefault("allowHostClassLoading", false))
      .allowIO((Boolean) options.getOrDefault("allowIO", false))
      .allowNativeAccess((Boolean) options.getOrDefault("allowNativeAccess", false))
      .build();

    runtime.registerCodec(vertx);

    final GraalLoader loader = new GraalLoader(vertx, context);

    if (script != null) {
      loader.main(script);
    }

    try (BufferedReader input = new BufferedReader(new InputStreamReader(System.in))) {
      final StringBuilder buffer = new StringBuilder();
      for (; ; ) {
        try {
          if (buffer.length() == 0) {
            System.out.print("> ");
          } else {
            System.out.print("| ");
          }

          String line = input.readLine();
          if (line == null) {
            break;
          }
          buffer.append(line);

          Source source = Source
            .newBuilder("js", buffer, "<shell>")
            .interactive(true)
            .buildLiteral();

          System.out.println("\u001B[1;90m" + loader.eval(source) + "\u001B[0m");
          // reset the buffer
          buffer.setLength(0);
        } catch (PolyglotException t) {
          // incomplete source, do not handle as error and
          // continue appending to the previous buffer
          if (t.isIncompleteSource()) {
            continue;
          }

          // polyglot engine is requesting to exit
          if (t.isExit()) {
            break;
          }

          // reset the buffer as the source is complete
          // but there's an error anyway
          buffer.setLength(0);

          // collect the trace back to a string
          try (StringWriter sw = new StringWriter()) {
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            String sStackTrace = sw.toString(); // stack trace as a string
            int idx = sStackTrace.indexOf("\n\tat");
            if (idx != -1) {
              System.out.print("\u001B[1m\u001B[31m" + sStackTrace.substring(0, idx) + "\u001B[0m");
              System.out.println(sStackTrace.substring(idx));
            } else {
              System.out.println(sStackTrace);
            }
          }
        }
      }
      // REPL is cancelled, close the loader
      try {
        loader.close();
      } catch (RuntimeException e) {
        // ignore...
      }
    }
  }
}
