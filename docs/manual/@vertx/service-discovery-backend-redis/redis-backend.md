# Redis backend

The service discovery has a plug-able backend using the
`ServiceDiscoveryBackend` SPI. This is an implementation of the SPI
based on Redis.

## Using the Redis backend

To use the Redis backend, add the following dependency to the
*dependencies* section of your build descriptor:

  - Maven (in your `pom.xml`):

<!-- end list -->

``` xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-service-discovery-backend-redis</artifactId>
 <version>${maven.version}</version>
</dependency>
```

  - Gradle (in your `build.gradle` file):

<!-- end list -->

``` groovy
compile 'io.vertx:vertx-service-discovery-backend-redis:${maven.version}'
```

Be aware that you can have only a single implementation of the SPI in
your *classpath*. If none, the default backend is used.

## Configuration

The backend is based on the
[vertx-redis-client](http://vertx.io/docs/vertx-redis-client/java). The
configuration is the client configuration as well as `key` indicating in
which *key* on Redis the records are stored.

Here is an example:

``` js
import { ServiceDiscovery } from "@vertx/service-discovery"
ServiceDiscovery.create(vertx, new ServiceDiscoveryOptions()
  .setBackendConfiguration({
    "host" : "127.0.0.1",
    "key" : "records"
  }));
```

It’s important to note that the backend configuration is passed in the
`setBackendConfiguration` method (or `backendConfiguration` entry if you
use JSON):

``` js
import { ServiceDiscovery } from "@vertx/service-discovery"
ServiceDiscovery.create(vertx, new ServiceDiscoveryOptions()
  .setBackendConfiguration({
    "host" : "localhost",
    "port" : 1234,
    "key" : "my-records"
  }));
```
