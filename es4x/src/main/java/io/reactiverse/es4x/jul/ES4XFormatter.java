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

import jdk.nashorn.api.scripting.NashornException;

import java.util.Collections;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class ES4XFormatter extends Formatter {

  private static final String format = "%5$s %6$s%n";

  private final Date dat = new Date();

  public synchronized String format(LogRecord record) {
    dat.setTime(record.getMillis());
    String source;
    if (record.getSourceClassName() != null) {
      source = record.getSourceClassName();
      if (record.getSourceMethodName() != null) {
        source += " " + record.getSourceMethodName();
      }
    } else {
      source = record.getLoggerName();
    }
    String message = formatMessage(record);
    CharSequence throwable = "";
    if (record.getThrown() != null) {
      throwable = formatStackTrace(record.getThrown());
    }

    return String.format(format,
      dat,
      source,
      record.getLoggerName(),
      record.getLevel(),
      message,
      throwable);
  }

  private static CharSequence formatStackTrace(Throwable self) {
    final StringBuffer buffer = new StringBuffer();

    Set<Throwable> dejaVu = Collections.newSetFromMap(new IdentityHashMap<>());
    dejaVu.add(self);

    StackTraceElement[] trace;

    if (self instanceof NashornException) {
      buffer
        .append(self.getLocalizedMessage())
        .append(System.lineSeparator());

      buffer
        .append("\tat (")
        .append(((NashornException) self).getFileName())
        .append(":")
        .append(((NashornException) self).getLineNumber())
        .append(":")
        .append(((NashornException) self).getColumnNumber())
        .append(")")
        .append(System.lineSeparator());

      trace = NashornException.getScriptFrames(self);

      for (StackTraceElement traceElement : trace) {
        buffer
          .append("\tat ")
          .append(traceElement.getMethodName())
          .append(" (")
          .append(traceElement.getFileName())
          .append(":")
          .append(traceElement.getLineNumber())
          .append(")")
          .append(System.lineSeparator());
      }
    } else {
      buffer
        .append(self)
        .append(System.lineSeparator());

      trace = self.getStackTrace();
      for (StackTraceElement traceElement : trace) {
        buffer
          .append("\tat ")
          .append(traceElement)
          .append(System.lineSeparator());
      }
    }

    Throwable[] suppressed = self.getSuppressed();

    for (Throwable se : suppressed) {
      printEnclosedStackTrace(se, buffer, trace, "Suppressed: ", "\t", dejaVu);
    }

    Throwable ourCause = self.getCause();
    if (ourCause != null) {
      printEnclosedStackTrace(ourCause, buffer, trace, "Caused by: ", "", dejaVu);
    }

    return buffer;
  }

  private static void printEnclosedStackTrace(Throwable self, StringBuffer buffer, StackTraceElement[] enclosingTrace, String caption, String prefix, Set<Throwable> dejaVu) {
    if (dejaVu.contains(self)) {
      buffer
        .append("\t[CIRCULAR REFERENCE:")
        .append(self)
        .append("]")
        .append(System.lineSeparator());
    } else {

      dejaVu.add(self);

      StackTraceElement[] trace;

      if (self instanceof NashornException) {
        trace = NashornException.getScriptFrames(self);
      } else {
        trace = self.getStackTrace();
      }

      int m = trace.length - 1;

      for (int n = enclosingTrace.length - 1; m >= 0 && n >= 0 && trace[m].equals(enclosingTrace[n]); --n) {
        --m;
      }

      int framesInCommon = trace.length - 1 - m;
      buffer
        .append(prefix)
        .append(caption)
        .append(self)
        .append(System.lineSeparator());

      for (int i = 0; i <= m; ++i) {
        buffer
          .append(prefix)
          .append("\tat ")
          .append(trace[i])
          .append(System.lineSeparator());
      }

      if (framesInCommon != 0) {
        buffer
          .append(prefix)
          .append("\t... ")
          .append(framesInCommon)
          .append(" more")
          .append(System.lineSeparator());
      }

      Throwable[] suppressed = self.getSuppressed();

      for (Throwable se : suppressed) {
        printEnclosedStackTrace(se, buffer, trace, "Suppressed: ", prefix + "\t", dejaVu);
      }

      Throwable ourCause = self.getCause();
      if (ourCause != null) {
        printEnclosedStackTrace(ourCause, buffer, trace, "Caused by: ", prefix, dejaVu);
      }
    }
  }
}
