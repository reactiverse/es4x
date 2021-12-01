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

import io.reactiverse.es4x.sourcemap.SourceMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.SourceSection;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.logging.*;

import static java.util.logging.Level.*;

public class ANSIFormatter extends Formatter {

  // are ANSI colors allowed?
  private static final boolean colors;
//  private static final SourceMap sourceMap;

  static {
    if (Boolean.getBoolean("noTTY")) {
      // in this case rely on the system property to DISABLE the colors.
      colors = false;
    } else {
      String term = System.getenv("TERM");
      if (term != null) {
        term = term.toLowerCase();
        colors =
          // this is where the most common config will be on unices
          term.equals("xterm-color")
            // however as there are lots of terminal emulators, it seems
            // safer to look up for the suffix "-256color" as it covers:
            // vte, linux, tmux, screen, putty, rxvt, nsterm, ...
            || term.endsWith("-256color");
      } else {
        // there's no env variable (we're running either embedded (no shell)
        // or on an OS that doesn't set the TERM variable (Windows maybe)
        colors = System.console() != null;
      }
    }
//
//    // handle source maps
//    String sourceMapFile = System.getProperty("sourceMap");
//    if (sourceMapFile != null) {
//      sourceMap = new SourceMap(Buffer.buffer(Files.readAllBytes(Paths.get(sourceMapFile))));
//    } else {
//      sourceMap = null;
//    }
  }

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

    if (colors) {
      sb.append(prefix(record.getLevel()));
    }
    sb.append(message);
    if (thrownMessage != null) {
      sb.append(" caused by ");
      sb.append(thrownMessage);
    }
    if (colors) {
      sb.append(suffix(record.getLevel()));
    }
    if (thrownTrace != null) {
      sb.append(thrownTrace);
    } else {
      sb.append(System.lineSeparator());
    }

    return sb.toString();
  }

  private static String prefix(Level l) {
    if (SEVERE.equals(l)) {
      return "\u001B[1m\u001B[31m";
    }
    if (WARNING.equals(l)) {
      return "\u001B[1m\u001B[33m";
    }
    if (INFO.equals(l)) {
      return "";
    }
    if (CONFIG.equals(l)) {
      return "\u001B[1m\u001B[34m";
    }
    if (FINE.equals(l)) {
      return "\u001B[1m\u001B[32m";
    }
    if (FINER.equals(l)) {
      return "\u001B[1m\u001B[94m";
    }
    if (FINEST.equals(l)) {
      return "\u001B[94m";
    }

    return "[" + l.getName().toUpperCase() + "] ";
  }

  private static String suffix(Level l) {
    if (SEVERE.equals(l)) {
      return "\u001B[0m";
    }
    if (WARNING.equals(l)) {
      return "\u001B[0m";
    }
    if (INFO.equals(l)) {
      return "";
    }
    if (CONFIG.equals(l)) {
      return "\u001B[0m";
    }
    if (FINE.equals(l)) {
      return "\u001B[0m";
    }
    if (FINER.equals(l)) {
      return "\u001B[0m";
    }
    if (FINEST.equals(l)) {
      return "\u001B[0m";
    }

    return "";
  }


  private static final String CAUSE_CAPTION = "Caused by: ";
  private static final String SUPPRESSED_CAPTION = "Suppressed: ";

  public static void printStackTrace(Throwable self, PrintWriter s) {
    // Guard against malicious overrides of Throwable.equals by
    // using a Set with identity equality semantics.
    Set<Throwable> dejaVu = Collections.newSetFromMap(new IdentityHashMap<>());
    dejaVu.add(self);

    // Print our stack trace
    s.println(self);
    StackTraceElement[] trace = self.getStackTrace();
    printTrace(self, trace, trace.length, s);

    // Print suppressed exceptions, if any
    for (Throwable se : self.getSuppressed())
      printEnclosedStackTrace(se, s, trace, SUPPRESSED_CAPTION, "\t", dejaVu);

    // Print cause, if any
    Throwable ourCause = self.getCause();
    if (ourCause != null)
      printEnclosedStackTrace(ourCause, s, trace, CAUSE_CAPTION, "", dejaVu);
  }

  private static void printEnclosedStackTrace(
    Throwable self,
    PrintWriter s,
    StackTraceElement[] enclosingTrace,
    String caption,
    String prefix,
    Set<Throwable> dejaVu) {

    if (dejaVu.contains(self)) {
      s.println(prefix + caption + "[CIRCULAR REFERENCE: " + self + "]");
    } else {
      dejaVu.add(self);
      // Compute number of frames in common between this and enclosing trace
      StackTraceElement[] trace = self.getStackTrace();
      int m = trace.length - 1;
      int n = enclosingTrace.length - 1;
      while (m >= 0 && n >= 0 && trace[m].equals(enclosingTrace[n])) {
        m--;
        n--;
      }
      int framesInCommon = trace.length - 1 - m;

      // Print our stack trace
      s.println(prefix + caption + self);
      printTrace(self, trace, m, s);

      if (framesInCommon != 0)
        s.println(prefix + "\t... " + framesInCommon + " more");

      // Print suppressed exceptions, if any
      for (Throwable se : self.getSuppressed())
        printEnclosedStackTrace(se, s, trace, SUPPRESSED_CAPTION,
          prefix + "\t", dejaVu);

      // Print cause, if any
      Throwable ourCause = self.getCause();
      if (ourCause != null)
        printEnclosedStackTrace(ourCause, s, trace, CAUSE_CAPTION, prefix, dejaVu);
    }
  }

  private static void printTrace(Throwable self, StackTraceElement[] trace, int limit, PrintWriter s) {
    if (self instanceof PolyglotException) {
      int i = 0;
      for (PolyglotException.StackFrame stackFrame : ((PolyglotException) self).getPolyglotStackTrace()) {
        if (i++ == limit) {
          break;
        }
        if (stackFrame.isHostFrame()) {
          s.println("\tat " + stackFrame);
        } else {
          SourceSection sourceSection = stackFrame.getSourceLocation();
          if (sourceSection != null) {
            URI uri = sourceSection.getSource().getURI();

            // TODO: rewrite with sourcemap is available

            s.println("\tat <js> " + stackFrame.getRootName()
              + "(" +
              ("file".equals(uri.getScheme()) ? uri.getPath() : uri) +
              (sourceSection.hasLines() ? ":" + sourceSection.getStartLine() : "") +
              (sourceSection.hasColumns() ? ":" + sourceSection.getStartColumn() : "") +
              ")");
          } else {
            s.println("\tat " + stackFrame);
          }
        }
      }
    } else {
      for (int i = 0; i <= limit; i++) {
        s.println("\tat " + trace[i]);
      }
    }
  }
}
