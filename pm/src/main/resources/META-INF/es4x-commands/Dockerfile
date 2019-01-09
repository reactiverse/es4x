ARG BASEIMAGE=oracle/graalvm-ce:${graalvm.version}
# Use official node for build
FROM node:10 AS NPM
# Create app directory
WORKDIR /usr/src/app
# Install app dependencies
# A wildcard is used to ensure both package.json AND package-lock.json are copied
# where available (npm@5+)
COPY package*.json ./
# If you are not building your code for production
# remove the argument
RUN npm install --only=production
# Second stage (build the JVM related code)
FROM $BASEIMAGE AS JVM
ARG ES4X_VERSION=${project.version}
# Download the ES4X runtime tool
RUN curl -sL https://github.com/reactiverse/es4x/releases/download/${ES4X_VERSION}/es4x-pm-${ES4X_VERSION}-bin.tar.gz | tar zx --strip-components=1 -C /usr/local
# Copy the previous build step
COPY --from=NPM /usr/src/app /usr/src/app
# use the copied workspace
WORKDIR /usr/src/app
# Fetch the java dependencies
RUN es4x-pm install -f
# Third stage (contain)
FROM $BASEIMAGE
# Copy the previous build step
COPY --from=JVM /usr/local/bin/es4x /usr/local/bin/es4x
# Should not be needed, but in case a script call happens always assume success
RUN ln -s /usr/local/bin/es4x-pm /usr/bin/true
COPY --from=JVM /usr/src/app /usr/src/app
# use the copied workspace
WORKDIR /usr/src/app
# Bundle app source
COPY . .
# Define custom java options for containers
ENV JAVA_OPTS="-XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:+UseContainerSupport"
# define the entrypoint
ENTRYPOINT [ "es4x" ]
CMD [ "run", "./" ]
