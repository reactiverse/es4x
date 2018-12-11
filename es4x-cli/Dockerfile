# define the build image to use
ARG BUILDIMAGE=openjdk:11-oracle
ARG RUNTIMEIMAGE=debian:stable-slim

FROM $BUILDIMAGE AS build-base
ARG ARTIFACT

ADD target/dist /dist

# setup (avoid guessing file names)
RUN mv /dist/${ARTIFACT?not set!} /dist/main.jar

# run if jlink is present
RUN if [ "$(type jlink)" ]; then \
      # setup (create empty directory)
      # Collect the jmods used in the application
      jdeps --module-path /dist/java-libs --print-module-deps /dist/main.jar > /tmp/jmods.dependencies ; \
      tr -d '\n' < /tmp/jmods.dependencies > /tmp/jmods ; \
    fi

# remove nashorn dependencies (if JVMCI) + jvmci jmod
RUN if [ "$(type jlink)" ] && [ -d /dist/compiler ]; then \
      sed -i -e 's/,jdk.scripting.nashorn,/,/g' /tmp/jmods ; \
      sed -i -e 's/,jdk.dynalink,/,/g' /tmp/jmods ; \
      echo ",jdk.internal.vm.ci" >> /tmp/jmods ; \
      cat /tmp/jmods ; \
    fi

# jlink
RUN if [ "$(type jlink)" ]; then \
      jlink \
        --no-header-files \
        --no-man-pages \
        --compress=2 \
        --strip-debug \
        --add-modules $(cat /tmp/jmods) \
        --output /runtime ; \
      else \
        echo "WARNING: Your build image doesn't have jlink, your runtime image MUST provide it's own JDK" ; \
        mkdir /runtime ; \
      fi

# final image without JVMCI
FROM $RUNTIMEIMAGE AS nojvmci
# collect artifacts
COPY --from=build-base /runtime /usr
COPY --from=build-base /dist /dist
# define entry point
ENTRYPOINT [ "java", "-XX:+IgnoreUnrecognizedVMOptions", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-XX:+UseContainerSupport", "-jar", "/dist/main.jar" ]

# final image with JVMCI
FROM $RUNTIMEIMAGE AS jvmci
# collect artifacts
COPY --from=build-base /runtime /usr
COPY --from=build-base /dist /dist
# define entry point
ENTRYPOINT [ "java", "-XX:+IgnoreUnrecognizedVMOptions", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-XX:+UseContainerSupport", "--module-path=/dist/compiler", "-XX:+EnableJVMCI", "--upgrade-module-path=/dist/compiler/compiler.jar", "-jar", "/dist/main.jar" ]
