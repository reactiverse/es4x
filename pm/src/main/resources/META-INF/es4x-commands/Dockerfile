# Use official node for build
FROM node:lts AS NPM
# Disable calls to es4x command (this container has no JVM)
RUN ln -s /bin/true /usr/bin/es4x
# Create app directory
WORKDIR /usr/src/app
# Add the application to the container
COPY . .
# npm is run with unsafe permissions because the default docker user is root
# also all dev packages are not installed
RUN npm --unsafe-perm install --only=prod

# Second stage (build the JVM related code)
FROM adoptopenjdk/openjdk11 AS JVM
# Download the ES4X runtime tool
RUN curl -sL https://github.com/reactiverse/es4x/releases/download/${project.version}/es4x-pm-${project.version}-bin.tar.gz | tar zx --strip-components=1 -C /usr/local
# Copy the previous build step
COPY --from=NPM /usr/src/app /usr/src/app
# use the copied workspace
WORKDIR /usr/src/app
# force es4x maven resolution only to consider production dependencies
RUN es4x install --only=prod

# Third stage (contain)
FROM gcr.io/distroless/java:11
# Collect the jars from the previous step
COPY --from=JVM /usr/src/app /usr/src/app
# use the copied workspace
WORKDIR /usr/src/app
# define the entrypoint
ENTRYPOINT [  "/usr/bin/java", "--module-path=node_modules/.jvmci", "-XX:+UnlockExperimentalVMOptions", "-XX:+EnableJVMCI", "--upgrade-module-path=node_modules/.jvmci/compiler.jar", "-XX:+UseContainerSupport", "-jar", "./node_modules/.bin/es4x-launcher.jar" ]
