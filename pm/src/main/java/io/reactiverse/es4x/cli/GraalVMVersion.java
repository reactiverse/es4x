package io.reactiverse.es4x.cli;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GraalVMVersion {

  /* graalvm version format: year.release.bugfix */
  private final boolean graalvm;
  private final int year;
  private final int release;
  private final int bugfix;

  public GraalVMVersion() {

    final String VM_NAME = System.getProperty("java.vm.name", "").toLowerCase();
    final String VENDOR_VERSION = System.getProperty("java.vendor.version", "").toLowerCase();

    graalvm =
      // from graal 20.0.0 the vm name doesn't contain graalvm in the name
      // but it is now part of the vendor version
      VENDOR_VERSION.contains("graalvm") || VM_NAME.contains("graalvm");

    Pattern p;
    Matcher m;

    if (graalvm) {
      p = Pattern.compile("graalvm .+? (\\d+)\\.(\\d+)\\.(\\d+)");
      m = p.matcher(VENDOR_VERSION);
      if (m.matches()) {
        year = Integer.parseInt(m.group(1));
        release = Integer.parseInt(m.group(2));
        bugfix = Integer.parseInt(m.group(3));
      } else {
        year = 0;
        release = 0;
        bugfix = 0;
      }
    } else {
      year = 0;
      release = 0;
      bugfix = 0;
    }
  }

  public boolean isGraalVM() {
    return graalvm;
  }

  public boolean isGreaterOrEqual(String version) {
    if (version == null) {
      return false;
    }

    final String[] parts = version.split("\\.");

    int year = parseInt(parts, 0);
    int release = parseInt(parts, 1);
    int bugfix = parseInt(parts, 2);

    if (this.year != year) return this.year - year >= 0;
    if (this.release != release) return this.release - release >= 0;
    return this.bugfix - bugfix >= 0;
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

  @Override
  public String toString() {
    return String.format("%d.%d.%d", year, release, bugfix);
  }
}
