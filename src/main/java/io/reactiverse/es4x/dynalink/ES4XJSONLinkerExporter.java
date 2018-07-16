///*
// * Copyright 2018 Red Hat, Inc.
// *
// *  All rights reserved. This program and the accompanying materials
// *  are made available under the terms of the Eclipse Public License v1.0
// *  and Apache License v2.0 which accompanies this distribution.
// *
// *  The Eclipse Public License is available at
// *  http://www.eclipse.org/legal/epl-v10.html
// *
// *  The Apache License v2.0 is available at
// *  http://www.opensource.org/licenses/apache2.0.php
// *
// *  You may elect to redistribute this code under either of these licenses.
// */
//package io.reactiverse.es4x.dynalink;
//
//import jdk.dynalink.linker.*;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ES4XJSONLinkerExporter extends GuardingDynamicLinkerExporter {
//
//  @Override
//  public List<GuardingDynamicLinker> get() {
//    final List<GuardingDynamicLinker> linkers = new ArrayList<>();
//    linkers.add(new ES4XJSONLinker());
//    return linkers;
//  }
//}
