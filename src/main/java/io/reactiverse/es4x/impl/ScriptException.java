/*
 * Copyright 2018 Red Hat, Inc.
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
package io.reactiverse.es4x.impl;

/**
 * Exception wrapper to avoid depend on Graal exception types
 * when dealing with VMs running on Nashorn only environments
 */
public class ScriptException extends RuntimeException {

  private final boolean isExit;
  private final boolean isIncompleteSource;

  public ScriptException(Throwable other, boolean isIncompleteSource, boolean isExit) {
    super(other.getMessage(), other.getCause());
    this.isIncompleteSource = isIncompleteSource;
    this.isExit = isExit;
  }

  public boolean isIncompleteSource() {
    return isIncompleteSource;
  }

  public boolean isExit() {
    return isExit;
  }
}
