package io.reactiverse.es4x.dynalink;

import jdk.dynalink.linker.*;

import java.util.ArrayList;
import java.util.List;

public class ES4XJSONLinkerExporter extends GuardingDynamicLinkerExporter {

  @Override
  public List<GuardingDynamicLinker> get() {
    final List<GuardingDynamicLinker> linkers = new ArrayList<>();
    linkers.add(new ES4XJSONLinker());
    return linkers;
  }
}
