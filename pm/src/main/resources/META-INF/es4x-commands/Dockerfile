ARG BASEIMAGE=oracle/graalvm-ce:${graalvm.version}
# Use official node for build
FROM node:10 AS NPM
ARG ES4X_VERSION=${project.version}
# force es4x maven resolution only to consider production dependencies
ENV ES4X_ENV=production
# Download the ES4X runtime tool
RUN curl -sL https://github.com/reactiverse/es4x/releases/download/${ES4X_VERSION}/es4x-pm-${ES4X_VERSION}-bin.tar.gz | tar zx --strip-components=1 -C /usr/local
# Create app directory
WORKDIR /usr/src/app
# Install app dependencies
# A wildcard is used to ensure both package.json AND package-lock.json are copied
# where available (npm@5+)
COPY package*.json ./
# If you are not building your code for production
# remove the final argument
# npm is run with unsafe permissions because the default docker user is root
RUN if [ -e "package-lock.json" ]; then \
      npm --unsafe-perm ci --only=production ; \
    else ; \
      npm --unsafe-perm update ; \
      # Fetch the java dependencies
      es4x install -f -l app
    fi
# Second stage (build the JVM related code)

FROM $BASEIMAGE AS JVM
# Copy the previous build step
COPY --from=NPM /usr/src/app /usr/src/app
# use the copied workspace
WORKDIR /usr/src/app
# Third stage (contain)
FROM $BASEIMAGE
# Collect the jars from the previous step
COPY --from=JVM /usr/src/app /usr/src/app
# use the copied workspace
WORKDIR /usr/src/app
# Bundle app source
COPY . .
# Define custom java options for containers
ENV JAVA_OPTS="-XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:+UseContainerSupport"
# define the entrypoint
ENTRYPOINT [ "./node_modules/.bin/app" ]
