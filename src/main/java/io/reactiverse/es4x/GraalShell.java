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
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

    final JsonObject options = new JsonObject();

    // parse the arguments
    if (args != null) {
      String lastArg = null;

      for (String arg : args) {
        if (arg != null && arg.length() > 0 && arg.charAt(0) == '-') {
          if ("-cluster".equals(arg)) {
            // adapt argument to match vertx options
            arg = "-clustered";
          }

          lastArg = arg;

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
          if (lastArg == null) {
            throw new IllegalArgumentException("Invalid argument: " + arg);
          }
          options.put(lastArg, arg);
          lastArg = null;
        }
      }
    }

    // Graal mode
    System.setProperty("es4x.engine", "GraalVM");

    final VertxOptions vertxOptions = new VertxOptions(options);
    final Context context = Context
      .newBuilder("js")
      .allowHostAccess(options.getBoolean("allowHostAccess", true))
      .allowCreateThread(options.getBoolean("allowCreateThread", true))
      .allowAllAccess(options.getBoolean("allowAllAccess", false))
      .allowHostClassLoading(options.getBoolean("allowHostClassLoading", false))
      .allowIO(options.getBoolean("allowIO", false))
      .allowNativeAccess(options.getBoolean("allowNativeAccess", false))
      .build();

    if (vertxOptions.isClustered()) {
      Vertx.clusteredVertx(vertxOptions, res -> {
        if (res.failed()) {
          res.cause().printStackTrace();
          System.exit(1);
        } else {
          try {
            bootstrap(res.result(), context);
          } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
          }
        }
      });
    } else {
      bootstrap(Vertx.vertx(vertxOptions), context);
    }
  }

  private static void bootstrap(Vertx vertx, Context context) throws IOException {

    final GraalLoader loader = new GraalLoader(vertx, context);

    try (BufferedReader input = new BufferedReader(new InputStreamReader(System.in))) {
      for (;;) {
        try {
          System.out.print("> ");
          String line = input.readLine();
          if (line == null) {
            break;
          }
          Source source = Source
            .newBuilder("js", line, "<shell>")
            .interactive(true)
            .buildLiteral();

          loader.eval(source);
        } catch (PolyglotException t) {
          if(t.isExit()) {
            break;
          }
          t.printStackTrace();
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
