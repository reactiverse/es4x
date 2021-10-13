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
package io.reactiverse.es4x.impl;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.List;

public class SetContainer<T> extends AbstractSet<T> {

  private final List<T> holder;

  public SetContainer(List<T> holder) {
    this.holder = holder;
  }

  @Override
  public boolean add(T item) {
    if (holder.contains(item)) {
      return false;
    }

    holder.add(item);
    return true;
  }

  @Override
  public Iterator<T> iterator() {
    return holder.iterator();
  }

  @Override
  public int size() {
    return holder.size();
  }
}
