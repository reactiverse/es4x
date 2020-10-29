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

import io.reactiverse.es4x.commands.Install;
import io.reactiverse.es4x.commands.Project;
import io.reactiverse.es4x.commands.SecurityPolicy;
import io.reactiverse.es4x.commands.Versions;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static io.reactiverse.es4x.cli.Helper.*;

public class PM {

  private static void printUsage() {
    System.err.println("Usage: es4x [COMMAND] [OPTIONS] [arg...]");
    System.err.println();
    System.err.println("Commands:");
    System.err.println(pad(Project.NAME + "/app", 16) + Project.SUMMARY);
    System.err.println(pad(Install.NAME, 16) + Install.SUMMARY);
    System.err.println(pad(SecurityPolicy.NAME, 16) + SecurityPolicy.SUMMARY);
    System.err.println(pad(Versions.NAME, 16) + Versions.SUMMARY);
    System.err.println();
    System.err.println("Current VM:");
    System.out.println("Name:   " + System.getProperty("java.vm.name") + " - " + System.getProperty("java.version"));
    String vendor = System.getProperty("java.vendor.version");
    if (vendor != null && vendor.length() > 0) {
      System.out.println("Vendor: " + vendor);
    }
    System.err.println();
    System.err.println("Run 'es4x COMMAND --help' for more information on a command.");
  }

  private static void verifyRuntime(boolean fatal) {
    if (GraalVMVersion.isGraalVM()) {
      // graalvm version should be aligned with the dependencies
      // used on the application, otherwise it introduces some
      // unwanted side effects
      try (InputStream is = PM.class.getClassLoader().getResourceAsStream("META-INF/es4x-commands/VERSIONS.properties")) {
        if (is != null) {
          final Properties versions = new Properties();
          versions.load(is);
          String wanted = versions.getProperty("graalvm");
          if (!GraalVMVersion.isGreaterOrEqual(wanted)) {
            String msg = "Runtime GraalVM version mismatch { wanted: [%s], provided: [%s] }%sFor installation help see: https://www.graalvm.org/docs/getting-started-with-graalvm/";
            if (fatal) {
              fatal(String.format(msg, wanted, GraalVMVersion.version(), System.lineSeparator()));
            } else {
              warn(String.format(msg, wanted, GraalVMVersion.version(), System.lineSeparator()));
            }
          }
        }
      } catch (IOException e) {
        fatal(e.getMessage());
      }
    }
  }

  private static final String[] EMPTY_ARGS = new String[]{""};
  private static final String[] EMPTY = new String[]{};

  public static void main(String[] args) {
    if (args == null || args.length == 0) {
      args = EMPTY_ARGS;
    }

    String command = args[0];
    String[] cmdArgs;
    // strip the command out of the arguments
    if (args.length > 1) {
      cmdArgs = new String[args.length - 1];
      System.arraycopy(args, 1, cmdArgs, 0, cmdArgs.length);
    } else {
      cmdArgs = EMPTY;
    }

    switch (command) {
      case "app":
      case Project.NAME:
        verifyRuntime(true);
        new Project(cmdArgs).run();
        System.exit(0);
        return;
      case Install.NAME:
        verifyRuntime(true);
        new Install(cmdArgs).run();
        System.exit(0);
        return;
      case SecurityPolicy.NAME:
        verifyRuntime(true);
        new SecurityPolicy(cmdArgs).run();
        System.exit(0);
        return;
      case Versions.NAME:
        verifyRuntime(true);
        new Versions(cmdArgs).run();
        System.exit(0);
        return;
      case "-h":
      case "--help":
        verifyRuntime(false);
        printUsage();
        System.exit(0);
        return;
      default:
        // if the user is requesting a unknown command, but silent install is active
        // then perform the silent install and let the control flow from the script
        if (System.getProperty("silent-install") != null) {
          verifyRuntime(true);
          new Install(cmdArgs).run();
          System.exit(0);
        } else {
          verifyRuntime(false);
          printUsage();
          System.exit(2);
        }
    }
  }
}
