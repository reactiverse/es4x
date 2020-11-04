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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GraalVMVersion {

  /* graalvm version format: year.release.bugfix */
  private static final boolean GRAALVM;
  private static final int YEAR;
  private static final int RELEASE;
  private static final int BUGFIX;

  static {

    final String VM_NAME = System.getProperty("java.vm.name", "").toLowerCase();
    final String VENDOR_VERSION = System.getProperty("java.vendor.version", "").toLowerCase();

    GRAALVM =
      // from graal 20.0.0 the vm name doesn't contain graalvm in the name
      // but it is now part of the vendor version
      VENDOR_VERSION.contains("graalvm") || VM_NAME.contains("graalvm");

    Pattern p;
    Matcher m;

    if (GRAALVM) {
      p = Pattern.compile("graalvm .+? (\\d+)\\.(\\d+)\\.(\\d+)");
      // fallback for jdk8 graal, which seems to have a empty vendor
      m = p.matcher("".equals(VENDOR_VERSION) ? VM_NAME : VENDOR_VERSION);
      if (m.find()) {
        YEAR = Integer.parseInt(m.group(1));
        RELEASE = Integer.parseInt(m.group(2));
        BUGFIX = Integer.parseInt(m.group(3));
      } else {
        YEAR = 0;
        RELEASE = 0;
        BUGFIX = 0;
      }
    } else {
      YEAR = 0;
      RELEASE = 0;
      BUGFIX = 0;
    }
  }

  public static boolean isGraalVM() {
    return GRAALVM;
  }

  public static boolean isGreaterOrEqual(String version) {
    if (version == null) {
      return false;
    }

    final String[] parts = version.split("\\.");

    int year = parseInt(parts, 0);
    int release = parseInt(parts, 1);
    int bugfix = parseInt(parts, 2);

    if (YEAR != year) return YEAR - year >= 0;
    if (RELEASE != release) return RELEASE - release >= 0;
    return BUGFIX - bugfix >= 0;
  }

  private static int parseInt(String[] parts, int idx) {
    if (parts.length > idx) {
      try {
        return Integer.parseInt(parts[idx]);
      } catch (NumberFormatException e) {
        return -1;
      }
    } else {
      return 0;
    }
  }

  public static String version() {
    return String.format("%d.%d.%d", YEAR, RELEASE, BUGFIX);
  }

  @Override
  public String toString() {
    return version();
  }
}
