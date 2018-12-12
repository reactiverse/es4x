It is common to package JVM applications as runnable `JAR` files, the `es4x-cli` creates a `pom.xml` with the
`maven-jar-plugin` configured for this:

```sh
npm run package
```

And a new `JAR` file should be built in your `target/dist` directory.

Packaging will re-arrange your application code to be moved to the directory `node_modules/your-module-name` so
it can be used from other JARs. In order for this to work correctly, the current `node_modules` are also
bundled in the jar as well as all files listed under the `files` property of your `package.json`.

When running this script you will see also the output command you need to use to run your application e.g.:

```bash
# Running on a GraalVM JVM
Running: /home/plopes/Projects/reactiverse/es4x/examples/empty-project/mvnw ... package
Run your application with:

  java \
  -jar target/dist/empty-project-1.0.0-bin.jar
```

Note that if you run with the environment flag `JVMCI` then you can run your app on JDK11 too but the start command
will be a little more complex, this is what the script tells you e.g.:

```bash
Running: /home/plopes/Projects/reactiverse/es4x/examples/empty-project/mvnw ... package
Run your application with:

  java \
  --module-path=target/dist/compiler \
  -XX:+UnlockExperimentalVMOptions \
  -XX:+EnableJVMCI \
  --upgrade-module-path=target/dist/compiler/compiler.jar \
  -jar target/dist/empty-project-1.0.0-bin-jvmci.jar
```

To distribute just the application binaries all you need lives in `target/dist`.
