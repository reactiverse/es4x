package io.reactiverse.es4x.commands;

import io.reactiverse.es4x.commands.proxies.JsonObjectProxy;
import org.eclipse.aether.artifact.Artifact;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.util.Collections;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import static junit.framework.TestCase.assertNotNull;

public class ProxyGeneratorTest {

  private static File coreJar = null;

  @BeforeClass
  public static void beforeClass() throws Exception {
    Resolver resolver = new Resolver();

    for (Artifact a : resolver.resolve("io.vertx:vertx-core:3.8.1", Collections.emptyList())) {
      if ("io.vertx".equals(a.getGroupId()) && "vertx-core".equals(a.getArtifactId())) {
        coreJar = a.getFile();
        break;
      }
    }

    if (coreJar == null) {
      throw new RuntimeException("Failed to locate the core jar");
    }
  }

  @Test
  public void testGenerateJsonObject() throws Exception {

    try (JarInputStream jar = new JarInputStream(new FileInputStream(coreJar))) {
      JarEntry je;
      while ((je = jar.getNextJarEntry()) != null) {
        if ("io/vertx/core/json/JsonObject.class".equals(je.getName())) {
          assertNotNull(new JsonObjectProxy().rewrite(jar));
          break;
        }
        if ("io/vertx/core/json/JsonArray.class".equals(je.getName())) {
          // TODO:!!!
          break;
        }
      }
    }
  }
}
