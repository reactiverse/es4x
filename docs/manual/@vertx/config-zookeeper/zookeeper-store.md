# Zookeeper Configuration Store

The Zookeeper Configuration Store extends the Vert.x Configuration
Retriever and provides the way to retrieve configuration from a
Zookeeper server. It uses Apache Curator as client.

## Using the Zookeeper Configuration Store

To use the Redis Configuration Store, add the following dependency to
the *dependencies* section of your build descriptor:

  - Maven (in your `pom.xml`):

<!-- end list -->

``` xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-config-zookeeper</artifactId>
 <version>${maven.version}</version>
</dependency>
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-config</artifactId>
 <version>${maven.version}</version>
</dependency>
```

  - Gradle (in your `build.gradle` file):

<!-- end list -->

``` groovy
compile 'io.vertx:vertx-config:${maven.version}'
compile 'io.vertx:vertx-config-zookeeper:${maven.version}'
```

## Configuring the store

Once added to your classpath or dependencies, you need to configure the
`ConfigRetriever` to use this store:

``` js
import { ConfigRetriever } from "@vertx/config"
let store = new ConfigStoreOptions()
  .setType("zookeeper")
  .setConfig({
    "connection" : "localhost:2181",
    "path" : "/path/to/my/conf"
  });

let retriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions()
  .setStores([store]));
```

The store configuration is used to configure the Apache Curator client
and the *path* of the Zookeeper node containing the configuration.
Notice that the format of the configuration can be JSON, or any
supported format.

The configuration requires the `configuration` attribute indicating the
connection *string* of the Zookeeper server, and the `path` attribute
indicating the path of the node containing the configuration.

In addition you can configure:

  - `maxRetries`: the number of connection attempt, 3 by default

  - `baseSleepTimeBetweenRetries`: the amount of milliseconds to wait
    between retries (exponential backoff strategy). 1000 ms by default.
