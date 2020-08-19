package io.reactiverse.es4x.commands;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.PrintStream;

import org.eclipse.aether.AbstractRepositoryListener;
import org.eclipse.aether.RepositoryEvent;

/**
 * A simplistic repository listener that logs events to the console.
 */
final class ConsoleRepositoryListener extends AbstractRepositoryListener {

  private final PrintStream out;

  public ConsoleRepositoryListener() {
    this(null);
  }

  public ConsoleRepositoryListener(PrintStream out) {
    this.out = (out != null) ? out : System.out;
  }

  public void artifactDescriptorInvalid(RepositoryEvent event) {
    out.println("Invalid artifact descriptor for " + event.getArtifact() + ": "
      + event.getException().getMessage());
  }

  public void artifactDescriptorMissing(RepositoryEvent event) {
    out.println("Missing artifact descriptor for " + event.getArtifact());
  }

  public void metadataInvalid(RepositoryEvent event) {
    out.println("Invalid metadata " + event.getMetadata());
  }
}
