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
package io.reactiverse.es4x.cli;

import io.reactiverse.es4x.asm.FutureBaseVisitor;
import io.reactiverse.es4x.asm.JsonArrayVisitor;
import io.reactiverse.es4x.asm.JsonObjectVisitor;
import io.reactiverse.es4x.commands.Resolver;
import io.reactiverse.es4x.commands.Versions;
import org.eclipse.aether.artifact.Artifact;

import java.io.*;
import java.util.Collections;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * Internal tooling to ASM patch the vert.x core classes in order to allow JS interop. The patch involves:
 *
 * <ul>
 *   <li>JsonObject - to allow using the Java type as a pure JSON object in JavaScript</li>
 *   <li>JsonArray - to allow using the Java type as a pure JSON object in JavaScript</li>
 *   <li>FutureBase - to allow using the Java type as a pure Thenable (async/await and Promise APIs) in JavaScript</li>
 * </ul>
 *
 * Usage: {@code java io.reactiverse.es4x.cli.VertxPatch [vertx version] [target]}
 */
public class VertxPatch {

  public static void main(String[] args) throws IOException {

    String _version;
    String _target;

    switch (args.length) {
      case 0:
        // load the versions from vertx if possible
        try (InputStream is = Versions.class.getClassLoader().getResourceAsStream("META-INF/vertx/vertx-version.txt")) {
          if (is != null) {
            Scanner scanner = (new Scanner(is, "UTF-8")).useDelimiter("\\A");
            if (scanner.hasNext()) {
              _version = scanner.next().trim();
              _target = "target";
              break;
            }
          }
        }
        throw new RuntimeException("Missing 'vert-core' from the classpath (cannot guess the right version)");
      case 1:
        if (args[0] == null) {
          throw new RuntimeException("Missing vertx-core version (e.g.: 4.0.0)");
        }
        _version = args[0];
        _target = "target";
        break;
      case 2:
        if (args[0] == null) {
          throw new RuntimeException("Missing vertx-core version (e.g.: 4.0.0)");
        }
        _version = args[0];
        if (args[1] == null) {
          throw new RuntimeException("Missing target (e.g.: 'target')");
        }
        _target = args[1];
        break;
      default:
        throw new RuntimeException("Invalid number of arguments (e.g.: '4.0.0' 'target')");
    }

    Resolver resolver = new Resolver();
    File coreJar = null;

    for (Artifact a : resolver.resolve("io.vertx:vertx-core:" + _version, Collections.emptyList())) {
      if ("io.vertx".equals(a.getGroupId()) && "vertx-core".equals(a.getArtifactId())) {
        coreJar = a.getFile();
        break;
      }
    }

    if (coreJar == null) {
      throw new RuntimeException("Failed to locate the core jar");
    }

    try (InputStream is = new FileInputStream(coreJar)) {
      try (JarInputStream jar = new JarInputStream(is)) {
        JarEntry je;
        byte[] bytes;
        File target;
        while ((je = jar.getNextJarEntry()) != null) {
          switch (je.getName()) {
            case "io/vertx/core/json/JsonObject.class":
              bytes = new JsonObjectVisitor().rewrite(jar);
              target = new File(_target, "io/vertx/core/json");
              target.mkdirs();
              try (OutputStream writer = new FileOutputStream(new File(target, "JsonObject.class"))) {
                writer.write(bytes);
              }
              break;

            case "io/vertx/core/json/JsonArray.class":
              bytes = new JsonArrayVisitor().rewrite(jar);
              target = new File(_target, "io/vertx/core/json");
              target.mkdirs();
              try (OutputStream writer = new FileOutputStream(new File(target, "JsonArray.class"))) {
                writer.write(bytes);
              }
              break;
            case "io/vertx/core/impl/future/FutureBase.class":
              bytes = new FutureBaseVisitor().rewrite(jar);
              target = new File(_target, "io/vertx/core/impl/future");
              target.mkdirs();
              try (OutputStream writer = new FileOutputStream(new File(target, "FutureBase.class"))) {
                writer.write(bytes);
              }
              break;
          }
        }
      }
    }
  }
}
