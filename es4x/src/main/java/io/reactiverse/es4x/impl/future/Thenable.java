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
package io.reactiverse.es4x.impl.future;

import org.graalvm.polyglot.Value;

/**
 * Thenable is an object or that defines a then method. It follows the JavaScript Promise conventions.
 */
@FunctionalInterface
public interface Thenable {

  /**
   * After some event "then" call one of the argument functions.
   * @param onResolve an optional function to be executed on success (resolve)
   * @param onReject an optional function to be executed on failure (reject)
   */
  void then(Value onResolve, Value onReject);
}
