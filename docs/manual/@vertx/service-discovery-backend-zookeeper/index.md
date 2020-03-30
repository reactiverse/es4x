# Vert.x Discovery Backend - Zookeeper

The service discovery has a plug-able backend using the
`ServiceDiscoveryBackend` SPI. This is an implementation of the SPI
based on Apache Zookeeper.

# Using the Zookeeper backend

To use the Zookeeper backend, add the following dependency to the
*dependencies* section of your build descriptor:

  - Maven (in your `pom.xml`):

<!-- end list -->

``` xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-service-discovery-backend-zookeeper</artifactId>
 <version>${maven.version}</version>
</dependency>
```

  - Gradle (in your `build.gradle` file):

<!-- end list -->

``` groovy
compile 'io.vertx:vertx-service-discovery-backend-zookeeper:${maven.version}'
```

Be aware that you can have only a single implementation of the SPI in
your *classpath*. If none, the default backend is used.

# Configuration

There is a single mandatory configuration attribute: `connection`.
Connection is the Zookeeper connection *string*.

Here is an example:

``` js
import { ServiceDiscovery } from "@vertx/service-discovery"
ServiceDiscovery.create(vertx, new ServiceDiscoveryOptions()
  .setBackendConfiguration({
    "connection" : "127.0.0.1:2181"
  }));
```

Additionally you can configure:

  - `maxRetries`: the number of connection attempt, 3 by default

  - `baseSleepTimeBetweenRetries`: the amount of milliseconds to wait
    between retries (exponential backoff strategy). 1000 ms by default.

  - `connectionTimeoutMs`: the connection timeout in milliseconds.
    Defaults to 1000.

  - `canBeReadOnly` : whether or not the backend support the *read-only*
    mode (defaults to false)

  - `basePath`: the Zookeeper path in which the service records are
    stored. Default to `/services`.

  - `ephemeral`: whether or not the created nodes are ephemeral nodes
    (see
    <https://zookeeper.apache.org/doc/r3.4.5/zookeeperOver.html#Nodes+and+ephemeral+nodes>).
    `false` by default

  - `guaranteed`: whether or not to guarantee the node deletion even in
    case of failure. `false` by default

<!-- end list -->

``` js
import { ServiceDiscovery } from "@vertx/service-discovery"
ServiceDiscovery.create(vertx, new ServiceDiscoveryOptions()
  .setBackendConfiguration({
    "connection" : "127.0.0.1:2181",
    "ephemeral" : true,
    "guaranteed" : true,
    "basePath" : "/services/my-backend"
  }));
```

# How are stored the records

The records are stored in individual nodes structured as follows:

    basepath (/services/)
      |
      |- record 1 registration id => the record 1 is the data of this node
      |- record 2 registration id => the record 2 is the data of this node
