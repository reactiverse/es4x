ARG BASEIMAGE=oracle/graalvm-ce:${graalvm.version}
# Use official node for build
FROM node:lts AS NPM
# Create app directory
WORKDIR /usr/src/app
# Add the application to the container
COPY . .
# Install app dependencies
ENV NODE_ENV=production
# npm is run with unsafe permissions because the default docker user is root
RUN npm --unsafe-perm ci

# Second stage (build the JVM related code)
FROM $BASEIMAGE AS JVM
ARG ES4X_VERSION=${project.version}
# Download the ES4X runtime tool
RUN curl -sL https://github.com/reactiverse/es4x/releases/download/${ES4X_VERSION}/es4x-pm-${ES4X_VERSION}-bin.tar.gz | tar zx --strip-components=1 -C /usr/local
# force es4x maven resolution only to consider production dependencies
ENV ES4X_ENV=production
# Copy the previous build step
COPY --from=NPM /usr/src/app /usr/src/app
# use the copied workspace
WORKDIR /usr/src/app
# Install the maven dependencies
RUN es4x install

# Third stage (contain)
FROM $BASEIMAGE
# Collect the jars from the previous step
COPY --from=JVM /usr/src/app /usr/src/app
# use the copied workspace
WORKDIR /usr/src/app
# Define custom java options for containers
ENV JAVA_OPTS="-XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:+UseContainerSupport"
# define the entrypoint
ENTRYPOINT [ "./node_modules/.bin/es4x-launcher" ]
