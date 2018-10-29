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

import io.vertx.core.Context;
import io.vertx.core.Vertx;
import org.graalvm.polyglot.PolyglotException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Shell {

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

  public static void main(String[] args) {

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

    // create the vertx instance that will boostrap the whole process
    final Vertx vertx = runtime.vertx(options);
    final String main = script;

    if (main == null && System.console() == null) {
      // invalid state, no script and no console
      throw new RuntimeException("No Script provided in non interactive shell!");
    }

    // move the context to the event loop
    vertx.runOnContext(v -> {
      runtime.registerCodec(vertx);

      final Loader loader = runtime.loader(vertx);

      if (main != null) {
        loader.main(main);
      } else {
        new REPL(vertx.getOrCreateContext(), loader).start();
      }
    });
  }

  private static class REPL extends Thread {

    final StringBuilder buffer = new StringBuilder();

    final Context context;
    final Loader loader;

    private REPL(Context context, Loader loader) {
      this.context = context;
      this.loader = loader;
    }

    private synchronized String updateBuffer(String line, boolean resetOrPrepend) {
      if (resetOrPrepend) {
        buffer.append(line);
        final String statement = buffer.toString();
        // reset the buffer
        buffer.setLength(0);
        return statement;
      } else {
        // incomplete source, do not handle as error and
        // continue appending to the previous buffer
        buffer.insert(0, line);
        return null;
      }
    }

    @Override
    public void run() {
      try (BufferedReader input = new BufferedReader(new InputStreamReader(System.in))) {
        System.out.print("> ");
        System.out.flush();

        for (; ; ) {
          String line = input.readLine();
          if (line == null) {
            break;
          }

          final String statement = updateBuffer(line, true);

          // ensure the statement is run on the right context
          context.runOnContext(v -> {
            try {
              System.out.println("\u001B[1;90m" + loader.evalLiteral(statement) + "\u001B[0m");
              System.out.print("> ");
              System.out.flush();
            } catch (PolyglotException t) {
              if (t.isIncompleteSource()) {
                updateBuffer(statement, false);
                return;
              }

              System.out.println("\u001B[1m\u001B[31m" + t.getMessage() + "\u001B[0m\n");

              if (t.isExit()) {
                // polyglot engine is requesting to exit
                // REPL is cancelled, close the loader
                try {
                  loader.close();
                  System.exit(1);
                } catch (RuntimeException e) {
                  // ignore...
                }
                // force a error code out
                System.exit(1);
              }

              System.out.print("> ");
              System.out.flush();
            } catch (Throwable t) {
              String message = null;
              String trace = null;

              // collect the trace back to a string
              try (StringWriter sw = new StringWriter()) {
                PrintWriter pw = new PrintWriter(sw);
                t.printStackTrace(pw);
                String sStackTrace = sw.toString(); // stack trace as a string
                int idx = sStackTrace.indexOf("\n\tat");
                if (idx != -1) {
                  message = sStackTrace.substring(0, idx);
                  trace = sStackTrace.substring(idx);
                } else {
                  trace = sStackTrace;
                }
              } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
              }

              if (message != null) {
                System.out.print("\u001B[1m\u001B[31m" + message + "\u001B[0m");
              }

              System.out.println(trace);
              System.out.print("> ");
              System.out.flush();
            }
          });
        }
      } catch (IOException e) {
        e.printStackTrace();
        System.exit(1);
      }
    }
  }
}
