package io.reactiverse.es4x;

import io.reactiverse.es4x.asm.FutureBaseVisitor;
import io.reactiverse.es4x.asm.JsonArrayVisitor;
import io.reactiverse.es4x.asm.JsonObjectVisitor;
import io.reactiverse.es4x.commands.Resolver;
import org.eclipse.aether.artifact.Artifact;

import java.io.*;
import java.util.Collections;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class VertxPatch {

  public static void main(String[] args) throws IOException {

    System.err.println(args[0]);
    System.err.println(args[1]);

    Resolver resolver = new Resolver();
    File coreJar = null;

    for (Artifact a : resolver.resolve("io.vertx:vertx-core:" + args[0], Collections.emptyList())) {
      if ("io.vertx".equals(a.getGroupId()) && "vertx-core".equals(a.getArtifactId())) {
        coreJar = a.getFile();
        break;
      }
    }

    if (coreJar == null) {
      throw new RuntimeException("Failed to locate the core jar");
    }

    try (JarInputStream jar = new JarInputStream(new FileInputStream(coreJar))) {
      JarEntry je;
      byte[] bytes;
      File target;
      while ((je = jar.getNextJarEntry()) != null) {
        switch (je.getName()) {
          case "io/vertx/core/json/JsonObject.class":
            bytes = new JsonObjectVisitor().rewrite(jar);
            target = new File(args[1], "io/vertx/core/json");
            target.mkdirs();
            try (OutputStream writer = new FileOutputStream(new File(target, "JsonObject.class"))) {
              writer.write(bytes);
            }
            break;

          case "io/vertx/core/json/JsonArray.class":
            bytes = new JsonArrayVisitor().rewrite(jar);
            target = new File(args[1], "io/vertx/core/json");
            target.mkdirs();
            try (OutputStream writer = new FileOutputStream(new File(target, "JsonArray.class"))) {
              writer.write(bytes);
            }
            break;
          case "io/vertx/core/impl/future/FutureBase.class":
            bytes = new FutureBaseVisitor().rewrite(jar);
            target = new File(args[1], "io/vertx/core/impl/future");
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
