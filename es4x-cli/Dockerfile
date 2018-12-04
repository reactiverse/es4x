# define the build image to use
ARG BUILDIMAGE=openjdk:11-oracle
ARG RUNTIMEIMAGE=debian:stable-slim

FROM $BUILDIMAGE AS build
# enable JMVCI
ARG JVMCI=1
# enable JLINK
ARG JLINK=1
ARG ARTIFACT

ADD target/dist /dist

# setup (avoid guessing file names)
RUN mv /dist/${ARTIFACT?not set!} /dist/main.jar
# setup (create empty directory)
RUN if [ "x$JLINK" == "x" ]; then \
      mkdir /runtime ; \
    fi

# Collect the jmods used in the application
RUN if [ "x$JLINK" != "x" ]; then \
      jdeps --module-path /dist/java-libs --print-module-deps /dist/main.jar > jmods.dependencies ; \
      tr -d '\n' < jmods.dependencies > jmods ; \
    fi

# remove nashorn dependencies (if JVMCI) + jvmci jmod
RUN if [ "x$JLINK" != "x" -a "x$JVMCI" != "x" ]; then \
      sed -i -e 's/,jdk.scripting.nashorn,/,/g' jmods ; \
      sed -i -e 's/,jdk.dynalink,/,/g' jmods ; \
      echo ",jdk.internal.vm.ci" >> jmods ; \
    fi

# jlink
RUN if [ "x$JLINK" != "" ]; then \
      cat jmods ; \
      jlink \
        --no-header-files \
        --no-man-pages \
        --compress=2 \
        --strip-debug \
        --add-modules $(cat jmods) \
        --output /runtime ; \
    fi

# remove the compiler bits if not jvmci
RUN if [ "x$JVMCI" != "x" ]; then \
      rm -Rf /dist/compiler ; \
    fi

# final image
FROM $RUNTIMEIMAGE
# enable JMVCI
ARG JVMCI="--module-path=/dist/compiler -XX:+UnlockExperimentalVMOptions -XX:+EnableJVMCI --upgrade-module-path=/dist/compiler/compiler.jar"
# collect artifacts
COPY --from=build /runtime /usr
COPY --from=build /dist /dist
# ENV vars
ENV JAVA_TOOL_OPTIONS="${JVMCI}"
# define entry point
ENTRYPOINT ["java", "-XX:+IgnoreUnrecognizedVMOptions", "-jar", "/dist/main.jar"]
