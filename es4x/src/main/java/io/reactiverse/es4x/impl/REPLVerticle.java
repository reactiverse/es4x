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
package io.reactiverse.es4x.impl;

import io.reactiverse.es4x.Runtime;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.parsetools.RecordParser;
import org.graalvm.polyglot.PolyglotException;

import java.io.*;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicBoolean;

public class REPLVerticle extends AbstractVerticle {

  private static final AtomicBoolean ACTIVE = new AtomicBoolean(false);

  private final Logger log = LoggerFactory.getLogger(REPLVerticle.class);

  private final Runtime runtime;
  private final StringBuilder buffer = new StringBuilder();

  public REPLVerticle(Runtime runtime) {
    this.runtime = runtime;
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

  private RecordParser stdin;
  private boolean cancel = false;

  @Override
  public void start() {
    if (ACTIVE.compareAndSet(false, true)) {
      stdin = RecordParser.newDelimited(System.getProperty("line.separator"), line -> {
        // we really want the system charset to not mess up with user input
        final String statement = updateBuffer(line.toString(Charset.defaultCharset()), true);

        // no-op
        if (statement == null || statement.length() == 0) {
          return;
        }

        try {
          System.out.println("\u001B[1;90m" + runtime.eval(statement, true) + "\u001B[0m");
          System.out.print("js:> ");
          System.out.flush();
        } catch (PolyglotException t) {
          if (t.isIncompleteSource()) {
            updateBuffer(statement, false);
            return;
          }

          System.out.println("\u001B[1m\u001B[31m" + t.getMessage() + "\u001B[0m");

          if (t.isExit()) {
            // polyglot engine is requesting to exit
            // REPLVerticle is cancelled, close the loader
            cancel = true;
            ACTIVE.set(false);
            vertx.close(v -> {
              // force a error code out
              System.exit(1);
            });
          }

          System.out.print("js:> ");
          System.out.flush();
        } catch (Exception ex) {
          log.error(ex.getMessage(), ex);
          System.out.print("js:> ");
          System.out.flush();
        }
      });

      // setup is complete
      String script = System.getProperty("script");

      if (script != null) {
        // eval this script at start
        stdin.handle(Buffer.buffer(script + System.getProperty("line.separator")));
      } else {
        // delay the show of the prompt so it doesn't overlap with the startup logging
        vertx.setTimer(100, t -> {
          System.out.print("js:> ");
          System.out.flush();
        });
      }

      // start the repl cycle
      vertx.setPeriodic(100, l -> {
        if (cancel) {
          vertx.cancelTimer(l);
          return;
        }
        // read stdin
        try {
          int available = System.in.available();
          if (available > 0) {
            byte[] data = new byte[available];
            int bytes = System.in.read(data);
            // end of stream
            if (bytes == -1) {
              cancel = true;
              return;
            }
            // read incomplete
            if (bytes != available) {
              // not all data was read
              byte[] tmp = new byte[bytes];
              System.arraycopy(data, 0, tmp, 0, bytes);
              data = tmp;
            }
            stdin.handle(Buffer.buffer(data));
          }
        } catch (IOException e) {
          e.printStackTrace();
          cancel = true;
        }
      });
    }
  }

  @Override
  public void stop() {
    cancel = true;
    ACTIVE.set(false);
    System.out.println("\u001B[1mShell Terminated.\u001B[0m");
  }
}
