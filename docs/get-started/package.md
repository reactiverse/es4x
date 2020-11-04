# Package

Packaging applications should follow the `NPM` style:

```sh
npm pack
```

[npm pack](https://docs.npmjs.com/cli/pack) will produce a `TGZ` with your application which you can move to other
location. However applications can also be [published](https://docs.npmjs.com/cli/publish) to a NPM registry.

It is important to notice that in order to work with `published/packed` the target environment needs to have access to
the package [@es4x/create](https://www.npmjs.com/package/@es4x/create) as it will be required to install the `java` bits.


## Docker

Docker images can also be created for you for.

```bash
es4x dockerfile
```

This will produce a simple `dockerfile` that you can customize to your needs, by default the file will be a 3 stage
build.

1. On the first stage `node` is used to install all `NPM` dependencies
2. On the second stage `java` is used to install the `Maven` dependencies
3. On the final stage the GraalVM image is used to run the application

By default [oracle/graalvm-ce](https://hub.docker.com/r/oracle/graalvm-ce) docker image is used, but it can be replace
with any other JDK image (please prefer versions 11 or above) with support for JVMCI.

```bash
docker build . --build-arg BASEIMAGE=openjdk:11
```

## JLink

Java 11 supports [jlink](https://docs.oracle.com/en/java/javase/11/tools/jlink.html). You can use the jlink tool to
assemble and optimize a set of modules and their dependencies into a custom runtime image.

```bash
es4x jlink
```

This will produce a **optimized** runtime, which means it can be used instead of relying on a full JDK installation.
As comparision, a hello world application will produce a runtime weighting about **80Mb**, while a full JDK installation
requires around **200Mb**.

This feature can be using in collaboration with `Dockerfile`. Instead of using the graal base image, use the `OpenJDK`
base image. Then on the second stage, run jlink:

```dockerfile
# Second stage (build the JVM related code)
FROM openjdk:11 AS JVM
ARG ES4X_VERSION=${project.version}
# Copy the previous build step
COPY --from=NPM /usr/src/app /usr/src/app
# use the copied workspace
WORKDIR /usr/src/app
# Download the ES4X runtime tool
RUN curl -sL https://github.com/reactiverse/es4x/releases/download/${ES4X_VERSION}/es4x-pm-${ES4X_VERSION}-bin.tar.gz | \
    tar zx --strip-components=1 -C /usr/local
# Install the Java Dependencies
# force es4x maven resolution only to consider production dependencies
RUN es4x install --only=prod
# Create the optimized runtime
RUN es4x jlink -t /usr/local
```

This will produce the optimized runtime to jre, which can be used with a small base image for the final stage:

```dockerfile
FROM debian:slim
# Collect the jars from the previous step
COPY --from=JVM /usr/local /usr/local
COPY --from=JVM /usr/src/app /usr/src/app
# use the copied workspace
WORKDIR /usr/src/app
# Bundle app source
COPY . .
# Define custom java options for containers
ENV JAVA_OPTS="-XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:+UseContainerSupport"
# define the entrypoint
ENTRYPOINT [ "./node_modules/.bin/es4x-launcher" ]
```

This will produce a small final image, but a larger layer as you're packaging the optimized runtime too.
