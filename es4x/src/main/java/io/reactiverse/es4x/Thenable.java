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
package io.reactiverse.es4x;

import org.graalvm.polyglot.Value;

/**
 * Default interface for thenable objects.
 * By implementing this interface Java objects can be used with JavaScript async/await.
 */
@FunctionalInterface
public interface Thenable {

  /**
   * @param onResolve - A Function called if the Promise is fulfilled.
   *                  This function has one argument, the fulfillment value.
   *                  If it is not a function, it is internally replaced with an "Identity" function
   *                  (it returns the received argument).
   * @param onReject  - A Function called if the Promise is rejected.
   *                  This function has one argument, the rejection reason.
   *                  If it is not a function, it is internally replaced with a "Thrower" function
   *                  (it throws an error it received as argument).
   */
  void then(Value onResolve, Value onReject);
}
