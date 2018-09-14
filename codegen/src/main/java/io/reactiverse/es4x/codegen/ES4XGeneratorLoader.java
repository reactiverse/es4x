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

    generators.add(new IndexJS());
    generators.add(new IndexDTS());
    generators.add(new OptionsJS());
    generators.add(new OptionsDTS());
    generators.add(new EnumJS());
    generators.add(new EnumDTS());
    generators.add(new ReadmeMD());
    generators.add(new PackageJSON());

    return generators.stream();

  }
}
