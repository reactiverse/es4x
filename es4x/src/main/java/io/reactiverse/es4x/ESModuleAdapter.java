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

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ESModuleAdapter {

  private static final boolean DEBUG = Boolean.getBoolean("es2cjs.debug");

  private static final Pattern importDef = Pattern.compile("import (\\* as [a-zA-Z_$][0-9a-zA-Z_$]*|\\{.+?}) from ['\"]([0-9a-zA-Z_$@./\\- ]+)['\"];?", Pattern.DOTALL);
  private static final Pattern exportDef = Pattern.compile("\\{(.+?)}", Pattern.DOTALL);
  private static final Pattern aliasDef = Pattern.compile("(\\*|[a-zA-Z_$][0-9a-zA-Z_$]*) as ([a-zA-Z_$][0-9a-zA-Z_$]*)", Pattern.DOTALL);

  private static String replace(String source, Pattern pattern, Function<Matcher, String> fn) {
    final Matcher m = pattern.matcher(source);
    boolean result = m.find();
    if (result) {
      StringBuilder sb = new StringBuilder(source.length());
      int p = 0;
      do {
        sb.append(source, p, m.start());
        sb.append(fn.apply(m));
        p = m.end();
      } while (m.find());
      sb.append(source, p, source.length());
      return sb.toString();
    }
    return source;
  }

  public static String adapt(String statement) {
    return replace(statement, importDef, importMatcher -> {

      if (DEBUG) {
        System.out.println(importMatcher.group(0));
      }

      final String exports = importMatcher.group(1);
      final String module = importMatcher.group(2);
      // is it single or multiple
      final Matcher exportMatcher = exportDef.matcher(exports);
      final StringBuilder sb = new StringBuilder();

      if (exportMatcher.find()) {
        final String[] multi = exportMatcher.group(1).split("\\s*,\\s*");
        for (String single : multi) {
          sb.append(adaptImport(single.trim(), module));
        }
      } else {
        sb.append(adaptImport(exports.trim(), module));
      }

      return sb.toString();
    });
  }

  private static String adaptImport(final String exports, final String module) {
    final Matcher aliasMatcher = aliasDef.matcher(exports);
    if (aliasMatcher.find()) {
      final String base = aliasMatcher.group(1).trim();
      final String alias = aliasMatcher.group(2).trim();

      if ("*".equals(base)) {
        return "const " + alias + " = require('" + module + "');";
      } else {
        return "const " + alias + " = require('" + module + "')." + base + ";";
      }
    } else {
      return "const " + exports + " = require('" + module + "')." + exports + ";";
    }
  }
}
