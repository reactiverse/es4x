package io.reactiverse.es4x.commands;

import io.reactiverse.es4x.cli.CmdLineParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

import static io.reactiverse.es4x.cli.Helper.fatal;

public class Versions implements Runnable {

  public static final String NAME = "versions";
  public static final String SUMMARY = "Displays the versions.";

  public Versions() {}

  public Versions(String[] args) {
    CmdLineParser parser = new CmdLineParser();
    CmdLineParser.Option<Boolean> helpOption = parser.addBooleanOption('h', "help");

    try {
      parser.parse(args);
    } catch (CmdLineParser.OptionException e) {
      printUsage();
      System.exit(2);
      return;
    }

    Boolean isHelp = parser.getOptionValue(helpOption, Boolean.FALSE);

    if (isHelp != null && isHelp) {
      printUsage();
      System.exit(0);
      return;
    }
  }

  private void printUsage() {
    System.err.println("Usage: es4x " + NAME + " [OPTIONS] [arg...]");
    System.err.println();
    System.err.println(SUMMARY);
    System.err.println();
  }

  @Override
  public void run() {
    System.out.println("VM:        " + System.getProperty("java.vm.name") + " - " + System.getProperty("java.version"));
    System.out.println("VM Vendor: " + System.getProperty("java.vendor.version"));

    // load the versions from vertx if possible
    try (InputStream is = Versions.class.getClassLoader().getResourceAsStream("META-INF/vertx/vertx-version.txt")) {
      if (is != null) {
        Scanner scanner = (new Scanner(is, "UTF-8")).useDelimiter("\\A");
        if (scanner.hasNext()) {
          System.out.println("Vert.x:    " + scanner.next().trim());
        }
      }
    } catch (IOException e) {
      fatal(e.getMessage());
    }

    // load the versions from es4x
    try (InputStream is = Versions.class.getClassLoader().getResourceAsStream("META-INF/es4x-commands/VERSIONS.properties")) {
      if (is == null) {
        fatal("Cannot find 'META-INF/es4x-commands/VERSIONS.properties' on classpath");
      } else {
        final Properties versions = new Properties();
        versions.load(is);
        System.out.println("ES4X:      " + versions.getProperty("es4x"));
        System.out.println("graaljs:   " + versions.getProperty("graalvm"));
      }
    } catch (IOException e) {
      fatal(e.getMessage());
    }
  }
}
