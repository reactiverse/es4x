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
package io.reactiverse.es4x.jul;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.*;

import static java.util.logging.Level.*;

public class ES4XFormatter extends Formatter {

  private static final boolean ansi = System.console() != null;

  @Override
  public synchronized String format(LogRecord record) {

    Throwable thrown = record.getThrown();
    String message = record.getMessage();

    String thrownMessage = null;
    String thrownTrace = null;

    if (thrown != null) {
      // collect the trace back to a string
      try (StringWriter sw = new StringWriter()) {
        PrintWriter pw = new PrintWriter(sw);
        // print the thrown to String
        thrown.printStackTrace(pw);
        String sStackTrace = sw.toString(); // stack trace as a string
        int idx = sStackTrace.indexOf("\n\tat");
        if (idx != -1) {
          thrownMessage = sStackTrace.substring(0, idx);
          thrownTrace = sStackTrace.substring(idx);
        } else {
          thrownTrace = sStackTrace;
        }
      } catch (IOException e) {
        // ignore...
      }
    }

    StringBuilder sb = new StringBuilder();

    sb.append(prefix(record.getLevel()));
    sb.append(message);
    if (thrownMessage != null) {
      sb.append(" caused by ");
      sb.append(thrownMessage);
    }
    sb.append(suffix(record.getLevel()));
    if (thrownTrace != null) {
      sb.append(thrownTrace);
    } else {
      sb.append(System.lineSeparator());
    }

    return sb.toString();
  }

  private static String prefix(Level l) {
    if (SEVERE.equals(l)) {
      if (ansi) {
        return "\u001B[1m\u001B[31m";
      } else {
        return "[SEVERE] ";
      }
    }
    if (WARNING.equals(l)) {
      if (ansi) {
        return "\u001B[1m\u001B[33m";
      } else {
        return "[WARNING] ";
      }
    }
    if (INFO.equals(l)) {
      if (ansi) {
        return "\u001B[1m\u001B[31m";
      } else {
        return "[INFO] ";
      }
    }
    if (CONFIG.equals(l)) {
      if (ansi) {
        return "\u001B[1m\u001B[36m";
      } else {
        return "[CONFIG] ";
      }
    }
    if (FINE.equals(l)) {
      if (ansi) {
        return "\u001B[1m\u001B[94m";
      } else {
        return "[FINE] ";
      }
    }
    if (FINER.equals(l)) {
      if (ansi) {
        return "\u001B[94m";
      } else {
        return "[FINER] ";
      }
    }
    if (FINEST.equals(l)) {
      if (ansi) {
        return "\u001B[94m";
      } else {
        return "[FINEST] ";
      }
    }

    return "[" + l.getName().toUpperCase() + "]";
  }

  private static String suffix(Level l) {
    if (ansi) {
      return "\u001B[0m";
    } else {
      return "";
    }
  }
}
