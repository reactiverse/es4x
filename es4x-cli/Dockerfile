FROM openjdk:12-alpine AS build
ADD target/dist /dist
# collect the required modules
RUN jdeps --module-path /dist/java-libs --print-module-deps /dist/*.jar > jmods.dependencies
# remove nashorn dependencies + desktop
RUN sed -i -e 's/,jdk.scripting.nashorn,/,/g' jmods.dependencies && \
    sed -i -e 's/,jdk.dynalink,/,/g' jmods.dependencies && \
    sed -i -e 's/,jdk.desktop,/,/g' jmods.dependencies && \
    cat jmods.dependencies
# create the runtime
RUN jlink \
  --no-header-files \
  --no-man-pages \
  --compress=2 \
  --strip-debug \
  --add-modules $(cat jmods.dependencies),jdk.internal.vm.ci \
  --output /runtime
# final image
FROM alpine:3.8
# collect artifacts
COPY --from=build /runtime /runtime
COPY --from=build /dist /dist
# define entry point
ENTRYPOINT ["/runtime/bin/java", "--module-path=/dist/compiler", "-XX:+UnlockExperimentalVMOptions", "-XX:+EnableJVMCI", "--upgrade-module-path=/dist/compiler/compiler.jar", "-jar", "/dist/empty-project-1.0.0.jar"]
