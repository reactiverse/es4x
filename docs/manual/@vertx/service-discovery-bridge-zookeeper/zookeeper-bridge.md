# Zookeeper bridge

This discovery bridge imports services from [Apache
Zookeeper](https://zookeeper.apache.org/) into the Vert.x service
discovery. The bridge uses the [Curator extension for service
discovery](http://curator.apache.org/curator-x-discovery/).

Service description are read as JSON Object (merged in the Vert.x
service record metadata). The service type is deduced from this
description by reading the `service-type`.

## Using the bridge

To use this Vert.x discovery bridge, add the following dependency to the
*dependencies* section of your build descriptor:

  - Maven (in your `pom.xml`):

<!-- end list -->

``` xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-service-discovery-bridge-zookeeper</artifactId>
 <version>${maven.version}</version>
</dependency>
```

  - Gradle (in your `build.gradle` file):

<!-- end list -->

``` groovy
compile 'io.vertx:vertx-service-discovery-bridge-zookeeper:${maven.version}'
```

Then, when creating the service discovery registers this bridge as
follows:

``` js
import { ServiceDiscovery } from "@vertx/service-discovery"
ServiceDiscovery.create(vertx).registerServiceImporter(new (Java.type("io.vertx.servicediscovery.zookeeper.ZookeeperServiceImporter"))(), {
  "connection" : "127.0.0.1:2181"
});
```

Only the `connection` configuration is mandatory. It’s the connection
*string* of the Zookeeper server.

In addition you can configure:

  - `maxRetries`: the number of connection attempt, 3 by default

  - `baseSleepTimeBetweenRetries`: the amount of milliseconds to wait
    between retries (exponential backoff strategy). 1000 ms by default.

  - `basePath`: the Zookeeper path in which the service are stored.
    Default to `/discovery`.

  - `connectionTimeoutMs`: the connection timeout in milliseconds.
    Defaults to 1000.

  - `canBeReadOnly` : whether or not the backend support the *read-only*
    mode (defaults to true)

<!-- end list -->

``` js
import { ServiceDiscovery } from "@vertx/service-discovery"
ServiceDiscovery.create(vertx).registerServiceImporter(new (Java.type("io.vertx.servicediscovery.zookeeper.ZookeeperServiceImporter"))(), {
  "connection" : "127.0.0.1:2181",
  "maxRetries" : 5,
  "baseSleepTimeBetweenRetries" : 2000,
  "basePath" : "/services"
});
```
