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
Running: mvnw ... package
Run your application with:

  java \
  -jar target/dist/empty-project-1.0.0-bin.jar
```

Note that if your environment supports `JVMCI` an extra directory will exist on the `target/dist` with the JVMCI
compiler bits. This will allow the usage of `graaljs` on `JDK >=11`. In this case the startup command is slighter
longer:

```bash
Running: mvnw ... package
Run your application with:

  java \
  --module-path=target/dist/compiler \
  -XX:+UnlockExperimentalVMOptions \
  -XX:+EnableJVMCI \
  --upgrade-module-path=target/dist/compiler/compiler.jar \
  -jar target/dist/empty-project-1.0.0-bin-jvmci.jar
```

To distribute just the application binaries all you need lives in `target/dist`.

## Docker

Docker images can also be created for you for this just pass the `-d` flag to the command:

```bash
npm run package -- -d
```

If your system is running on JVMCI enabled environment `JDK>=11` then a docker image with JMVCI will be created for you
using the base image: `openjdk:11-oracle`. If you prefer to use a different base image for JVMCI then just specify the
base image to use after the flag.

```bash
npm run package -- -d adoptopenjdk/openjdk11-openj9
```

### GraalVM

If your environment was running on graalvm, then there isn't a JVMCI `compiler` in your `dist` directory, in this case
the docker command will create a docker image based on `oracle/graalvm-ce:1.0.0-rc10`. The same rules apply for
switching the base image.

```bash
# assuming there's a EE image in one of your
# configured docker registries
npm run package -- -d oracle/graalvm-ee:1.0.0-rc10
```
