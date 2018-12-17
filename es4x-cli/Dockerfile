ARG BASEIMAGE=oracle/graalvm-ce:1.0.0-rc10
FROM $BASEIMAGE
ARG JAR

COPY target/dist/java-libs /dist/java-libs
COPY target/dist/${JAR} /dist/main.jar

# define entry point
ENTRYPOINT [ "java", "-XX:+IgnoreUnrecognizedVMOptions", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-XX:+UseContainerSupport", "-jar", "/dist/main.jar" ]
