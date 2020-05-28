package io.reactiverse.es4x.commands;

import io.reactiverse.es4x.cli.CmdLineParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import static io.reactiverse.es4x.commands.Helper.fatal;
import static io.reactiverse.es4x.commands.Helper.warn;

public class SecurityPolicy implements Runnable {

  public static final String NAME = "security-policy";
  public static final String SUMMARY = "Initializes a secure by default VM 'security.policy' to work with ES4X.";

  private File cwd;

  public SecurityPolicy() {
  }

  public SecurityPolicy(String[] args) {
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

    setCwd(new File("."));
  }

  private void printUsage() {
    System.err.println("Usage: es4x " + NAME + " [OPTIONS] [arg...]");
    System.err.println();
    System.err.println(SUMMARY);
    System.err.println();
  }

  public SecurityPolicy setCwd(File cwd) {
    this.cwd = cwd;
    return this;
  }

  @Override
  public void run() {
    final File file = new File(cwd, "security.policy");

    if (!file.exists()) {
      // Load the file from the class path
      try (InputStream in = SecurityPolicy.class.getClassLoader().getResourceAsStream("META-INF/es4x-commands/security.policy")) {
        if (in == null) {
          fatal("Cannot load security.policy template.");
        } else {
          Files.copy(in, file.toPath());
        }
        warn("Creating a new 'security.policy' with full network access and read-only IO access to the working directory.");
      } catch (IOException e) {
        fatal(e.getMessage());
      }
    } else {
      warn("'security.policy' already exists, not over writing.");
    }
  }
}
