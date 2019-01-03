package io.reactiverse.es4x.runtime.commands;

import io.vertx.core.cli.CLIException;
import io.vertx.core.cli.annotations.Description;
import io.vertx.core.cli.annotations.Name;
import io.vertx.core.cli.annotations.Summary;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.launcher.DefaultCommand;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Comment to create a pom.xml from a package.json.
 *
 * @author Paulo Lopes
 */
@Name("pom")
@Summary("Create a pom.xml.")
@Description("Create a pom.xml from a package.json in the current working directory.")
public class PomCommand extends DefaultCommand {

  @Override
  public void run() throws CLIException {
    File packageJson = new File("package.json");

    if (packageJson.exists()) {
      final JsonObject npm = readJSON(packageJson);

      if (!npm.containsKey("files")) {
        npm.put("files", new JsonArray());
      }

      @SuppressWarnings("unchecked")
      List<String> files = npm.getJsonArray("files").getList();
      Map<String, Artifact> dependencies = new HashMap<>();

      // we must include 2 files by default: `package.json` and npm.main
      if (!files.contains("package.json")) {
        files.add("package.json");
      }
      if (npm.containsKey("main")) {
        if (!files.contains(npm.getString("main"))) {
          files.add(npm.getString("main"));
        }
      }

      // extension to the default package.json to describe pure maven dependencies
      toMavenDep(npm, dependencies);
      // standard dependencies
      find(npm, false, dependencies, files);
      find(npm, true, dependencies, files);

      // mark all files as dirs
      for (int i = 0; i < files.size(); i++) {
        String el = files.get(i);
        // if marked as dir, adapt to ant pattern
        if (el.charAt(el.length() - 1) == '/') {
          files.set(i, el + "**.*");
        }
        // remove the "./" if present (maven does not like it)
        if (el.startsWith("./")) {
          files.set(i, el.substring(2));
        }
      }

      final Map<String, Object> data = new HashMap<>();

      data.put("groupId", npm.getString("groupId", npm.getString("name")));
      data.put("artifactId", npm.getString("artifactId", npm.getString("name")));
      data.put("version", npm.getString("version"));
      data.put("name", npm.getString("name"));
      data.put("description", npm.getString("description", npm.getString("name")));
      data.put("main", npm.getString("main"));

      data.put("files", files);
      data.put("dependencies", dependencies.values());
      data.put("packageJson", npm.getMap());

      File nodeModules = new File("node_modules");
      if (!nodeModules.exists()) {
        if (!nodeModules.mkdir()) {
          throw new IllegalStateException("Cannot create 'node_modules' directory.");
        }
      }



      try (FileOutputStream pom = new FileOutputStream(new File(nodeModules, "pom.xml"))) {
        pom.write(render(data).getBytes(StandardCharsets.UTF_8));
      } catch (IOException ioe) {
        throw new CLIException(ioe.getMessage(), ioe);
      }
    }
  }

  private JsonObject readJSON(File file) throws CLIException {
    try (InputStream is = new FileInputStream(file)) {
      try (Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A")) {
        return new JsonObject(scanner.hasNext() ? scanner.next() : "");
      }
    } catch (IOException ioe) {
      throw new CLIException(ioe.getMessage(), ioe);
    }
  }

  private void find(JsonObject npm, boolean dev, Map<String, Artifact> dependencies, List<String> files) {
    // locate dependencies
    final JsonObject jsonDependencies = npm.getJsonObject(dev ? "devDependencies" : "dependencies", new JsonObject());

    for (Map.Entry<String, Object> kv : jsonDependencies) {
      // skip if we already visited this dependency
      if (!dependencies.containsKey(kv.getKey())) {
        final File dir = new File(System.getProperty("user.dir"), "node_modules/" + kv.getKey());
        if (dir.exists()) {
          if (!dev) {
            if (!files.contains("node_modules/" + kv.getKey() + "/")) {
              files.add("node_modules/" + kv.getKey() + "/");
            }
          }

          final File file = new File(new File(System.getProperty("user.dir"), "node_modules/" + kv.getKey()), "package.json");
          if (file.exists()) {
            final JsonObject json = readJSON(file);
            if (json.containsKey("maven")) {
              final JsonObject maven = json.getJsonObject("maven");
              // add this dependency
              dependencies.put(kv.getKey(), new Artifact()
                .setGroupId(maven.getString("groupId"))
                .setArtifactId(maven.getString("artifactId"))
                .setVersion(maven.getString("version"))
                .setScope(dev ? "test" : maven.getString("scope"))
                .setClassifier(maven.getString("classifier")));

              // recurse...
              find(json, false, dependencies, files);
            }
          }
        }
      }
    }
  }

  private void toMavenDep(JsonObject npm, Map<String, Artifact> dependencies) {
    final JsonObject jsonDependencies = npm.getJsonObject("mvnDependencies", new JsonObject());
    // locate dependencies
    for (Map.Entry<String, Object> kv : jsonDependencies) {
      final String[] ga = kv.getKey().split(":");
      final String[] vsc = npm.getJsonObject("mvnDependencies").getString(kv.getKey()).split(":");
      // add this dependency
      dependencies.put(kv.getKey(), new Artifact()
        .setGroupId(ga[0])
        .setArtifactId(ga.length > 1 ? ga[1] : null)
        .setVersion(vsc[0])
        .setScope(vsc.length > 1 ? vsc[1] : null)
        .setClassifier(vsc.length > 2 ? vsc[2] : null));
    }
  }

  private String render(Map<String, Object> data) {
    /// @language=xml
    return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
      "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
      "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
      "\n" +
      "  <modelVersion>4.0.0</modelVersion>\n" +
      "\n" +
      "  <!-- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN BY THE NEXT RUN -->\n" +
      "  <!-- WARNING: IF YOU NEED HAVE A CUSTOM POM.XML DETACH THE postinstall SCRIPT -->\n" +
      "\n" +
      "  <groupId>" + data.get("groupId") + "</groupId>\n" +
      "  <artifactId>" + data.get("artifactId") + "</artifactId>\n" +
      "  <version>" + data.get("version") + "</version>\n" +
      "\n" +
      "  <name>" + data.get("name") + "</name>\n" +
      "  <description>" + data.get("description") + "</description>\n" +
      "\n" +
      "  <properties>\n" +
      "    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>\n" +
      "    <maven.compiler.target>1.8</maven.compiler.target>\n" +
      "    <maven.compiler.source>1.8</maven.compiler.source>\n" +
      "    <maven.compiler.testSource>1.8</maven.compiler.testSource>\n" +
      "    <maven.compiler.testTarget>1.8</maven.compiler.testTarget>\n" +
      "  </properties>\n" +
      "\n" +
      "  <dependencies>\n" +
      renderArtifacts((Collection<Artifact>) data.get("dependencies")) +
      "    <!-- Always required -->\n" +
      "    <dependency>\n" +
      "      <groupId>io.reactiverse</groupId>\n" +
      "      <artifactId>es4x</artifactId>\n" +
      "      <version>[0.6.1,)</version>\n" +
      "    </dependency>\n" +
      "  </dependencies>\n" +
      "  <build>\n" +
      "    <plugins>\n" +
      "      <plugin>\n" +
      "        <artifactId>maven-resources-plugin</artifactId>\n" +
      "        <version>3.1.0</version>\n" +
      "        <executions>\n" +
      "          <execution>\n" +
      "            <id>package-resources</id>\n" +
      "            <phase>prepare-package</phase>\n" +
      "            <goals>\n" +
      "              <goal>copy-resources</goal>\n" +
      "            </goals>\n" +
      "            <configuration>\n" +
      "              <outputDirectory>${project.build.directory}/classes/node_modules/" + data.get("name") + "</outputDirectory>\n" +
      "              <resources>\n" +
      "                <resource>\n" +
      "                  <directory>${project.basedir}</directory>\n" +
      "                  <includes>\n" +
      renderFiles((Collection<String>) data.get("files")) +
      "                  </includes>\n" +
      "                  <filtering>false</filtering>\n" +
      "                </resource>\n" +
      "              </resources>\n" +
      "            </configuration>\n" +
      "          </execution>\n" +
      "        </executions>\n" +
      "      </plugin>\n" +
      "      <plugin>\n" +
      "        <groupId>org.apache.maven.plugins</groupId>\n" +
      "        <artifactId>maven-jar-plugin</artifactId>\n" +
      "        <version>2.4</version>\n" +
      "        <configuration>\n" +
      "          <archive>\n" +
      "            <manifest>\n" +
      "              <addClasspath>true</addClasspath>\n" +
      "              <mainClass>io.vertx.core.Launcher</mainClass>\n" +
      "              <classpathPrefix>node-modules/java-libs/</classpathPrefix>\n" +
      "              <useUniqueVersions>false</useUniqueVersions>\n" +
      "            </manifest>\n" +
      "            <manifestEntries>\n" +
      "              <Main-Verticle>js:./node_modules/" + data.get("name") + "</Main-Verticle>\n" +
      "            </manifestEntries>\n" +
      "          </archive>\n" +
      "          <outputDirectory>${project.basedir}/..</outputDirectory>\n" +
      "        </configuration>\n" +
      "      </plugin>\n" +
      "    </plugins>\n" +
      "  </build>\n" +
      "</project>\n";
  }

  private String renderFiles(Collection<String> files) {
    StringBuilder sb = new StringBuilder();
    for (String file : files) {
      sb.append("                    <include>").append(file).append("</include>\n");
    }

    return sb.toString();
  }

  private String renderArtifacts(Collection<Artifact> artifacts) {
    StringBuilder sb = new StringBuilder();
    for (Artifact artifact : artifacts) {
      sb.append("    <dependency>\n" + "      <groupId>").append(artifact.getGroupId()).append("</groupId>\n").append("      <artifactId>").append(artifact.getArtifactId()).append("</artifactId>\n").append("      <version>").append(artifact.getVersion()).append("</version>\n").append("      <scope>").append(artifact.getScope()).append("</scope>\n");

      if (artifact.getClassifier() != null) {
        sb.append("      {{#classifier}}\n" + "      <classifier>").append(artifact.getClassifier()).append("</classifier>\n").append("      {{/classifier}}\n");
      }

      sb.append("    </dependency>\n");
    }

    return sb.toString();
  }
}
