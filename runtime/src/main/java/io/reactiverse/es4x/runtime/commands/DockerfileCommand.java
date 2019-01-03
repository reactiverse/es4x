package io.reactiverse.es4x.runtime.commands;

import io.vertx.core.cli.CLIException;
import io.vertx.core.cli.annotations.Description;
import io.vertx.core.cli.annotations.Name;
import io.vertx.core.cli.annotations.Option;
import io.vertx.core.cli.annotations.Summary;
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
@Name("dockerfile")
@Summary("Create a Dockerfile.")
@Description("Create a Dockerfile from a package.json in the current working directory.")
public class DockerfileCommand extends DefaultCommand {

  private boolean jvmci;
  private String jar;

  /**
   * Enables / disables the high-availability.
   *
   * @param jvmci whether or not to enable the HA.
   */
  @Option(longName = "jvmci", acceptValue = false, flag = true)
  @Description("If specified the Dockerfile will have the correct JVMCI flags set in the entrypoint.")
  public void setJvmci(boolean jvmci) {
    this.jvmci = jvmci;
  }

  @Override
  public void run() throws CLIException {
    File packageJson = new File("package.json");

    if (packageJson.exists()) {
      final JsonObject npm = readJSON(packageJson);

      jar = npm.getString("artifactId", npm.getString("name")) + "-" + npm.getString("version") + ".jar";
    }

    try (FileOutputStream dockerfile = new FileOutputStream(new File("Dockerfile"))) {
      dockerfile.write(render().getBytes(StandardCharsets.UTF_8));
    } catch (IOException ioe) {
      throw new CLIException(ioe.getMessage(), ioe);
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

  private String render() {
    return "ARG BASEIMAGE=" + (jvmci ? "openjdk:11-oracle" : "oracle/graalvm-ce:1.0.0-rc10") + "\n" +
      "FROM $BASEIMAGE\n" +
      "ARG JAR" + (jar != null ? "=" + jar : "") + "\n" +
      (jvmci ? "COPY target/dist/compiler /dist/compiler\n" : "") +
      "COPY target/dist/java-libs /dist/java-libs\n" +
      "COPY target/dist/${JAR} /dist/main.jar\n" +

      "ENTRYPOINT [ \"java\", \"-XX:+IgnoreUnrecognizedVMOptions\", \"-XX:+UnlockExperimentalVMOptions\", \"-XX:+UseCGroupMemoryLimitForHeap\", \"-XX:+UseContainerSupport\", " + (jvmci ? "ENTRYPOINT [ \"java\", \"-XX:+IgnoreUnrecognizedVMOptions\", \"-XX:+UnlockExperimentalVMOptions\", \"-XX:+UseCGroupMemoryLimitForHeap\", \"-XX:+UseContainerSupport\", \"-jar\", \"/dist/main.jar\" ]\n" : "") + "\"-jar\", \"/dist/main.jar\" ]\n";
  }
}
