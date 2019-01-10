/*
 * Copyright 2018 Paulo Lopes.
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
package io.reactiverse.es4x.codegen;

import io.reactiverse.es4x.codegen.generator.*;
import io.vertx.codegen.Generator;
import io.vertx.codegen.GeneratorLoader;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class ES4XGeneratorLoader implements GeneratorLoader {
  @Override
  public Stream<Generator<?>> loadGenerators(ProcessingEnvironment processingEnv) {

    final List<Generator<?>> generators = new LinkedList<>();

    // JS core
    generators.add(new IndexJS());
    generators.add(new OptionsJS());
    generators.add(new EnumJS());
    // d.ts
    generators.add(new IndexDTS());
    generators.add(new OptionsDTS());
    generators.add(new EnumDTS());
    // ES6 modules (for transpilers/optimizers)
    generators.add(new ModuleMJS());
    generators.add(new OptionsMJS());
    generators.add(new EnumMJS());
    // package
    generators.add(new PackageJSON());
    generators.add(new ReadmeMD());

    return generators.stream();

  }
}
