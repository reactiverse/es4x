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

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ESModuleIO {

  private static final Logger LOGGER = LoggerFactory.getLogger(ESModuleIO.class);

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

      LOGGER.debug(importMatcher.group(0));

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
      // ensure that the line numbers match
      for (int i = 0; i < exports.length(); i++) {
        if (exports.charAt(i) == '\r' || exports.charAt(i) == '\n') {
          sb.append(exports.charAt(i));
        }
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

  private final FileSystem fs;

  public ESModuleIO(Vertx vertx) {
    this.fs = vertx.fileSystem();
  }

  public String getParent(String uri) throws URISyntaxException {
    switch (uri) {
      case "jar:":
      case "file:":
        throw new RuntimeException("Cannot get parent of root.");
      default:
        return getParent(new URI(uri));
    }
  }

  public String getParent(URI uri) {
    final String path = uri.getPath();
    int last = path.lastIndexOf('/');
    if (path.length() > last) {
      return uri.getScheme() + ':' + path.substring(0, last);
    }
    throw new RuntimeException("Cannot get parent of root.");
  }

  public boolean exists(URI uri) {

    if (uri == null) {
      return false;
    }

    switch (uri.getScheme()) {
      case "jar":
        return fs.existsBlocking(uri.getPath().substring(1));
      case "file":
        return fs.existsBlocking(uri.getPath());
      default:
        return false;
    }
  }

  public boolean isFile(URI uri) {
    switch (uri.getScheme()) {
      case "jar":
        return fs.propsBlocking(uri.getPath().substring(1)).isRegularFile();
      case "file":
        return fs.propsBlocking(uri.getPath()).isRegularFile();
      default:
        return false;
    }
  }

  public String readFile(URI uri) throws IOException {
    return readFile(uri, false);
  }

  public String readFile(URI uri, boolean main) throws IOException {
    Buffer buffer;

    switch (uri.getScheme()) {
      case "jar":
        buffer = fs.readFileBlocking(uri.getPath().substring(1));
        break;
      case "file":
        buffer = fs.readFileBlocking(uri.getPath());
        break;
      default:
        throw new IOException("Cannot handle scheme [" + uri.getScheme() + "]");
    }

    if (main) {
      String content = stripShebang(buffer.toString());
      return adapt(content);
    } else {
      String content = stripBOM(buffer.toString());
      return adapt(content);
    }
  }

  /**
   * Find end of shebang line and slice it off
   * @param content the content to search
   * @return the striped content
   */
  public static String stripShebang(String content) {
    // Remove shebang
    int contLen = content.length();
    if (contLen >= 2) {
      if (content.charAt(0) == '#' && content.charAt(1) == '!') {
        if (contLen == 2) {
          // Exact match
          content = "";
        } else {
          // Find end of shebang line and slice it off
          int i = 2;
          for (; i < contLen; ++i) {
            char code = content.charAt(i);
            if (code == '\n' || code == '\r') {
              break;
            }
          }
          if (i == contLen) {
            content = "";
          } else {
            // Note that this actually includes the newline character(s) in the
            // new output.
            content = content.substring(i);
          }
        }
      }
    }
    return content;
  }

  /**
   * Remove byte order marker. This catches EF BB BF (the UTF-8 BOM)
   * because the buffer-to-string conversion in `fs.readFileSync()`
   * translates it to FEFF, the UTF-16 BOM.
   * @param content the content to search
   * @return the striped content
   */
  public static String stripBOM(String content) {
    if (content.charAt(0) == 0xFEFF) {
      content = content.substring(1);
    }
    return content;
  }
}
