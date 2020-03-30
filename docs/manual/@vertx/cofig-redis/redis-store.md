# Redis Configuration Store

The Redis Configuration Store extends the Vert.x Configuration Retriever
and provides the way to retrieve configuration from a Redis server.

## Using the Redis Configuration Store

To use the Redis Configuration Store, add the following dependency to
the *dependencies* section of your build descriptor:

  - Maven (in your `pom.xml`):

<!-- end list -->

``` xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-config-redis</artifactId>
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
compile 'io.vertx:vertx-config-redis:${maven.version}'
```

## Configuring the store

Once added to your classpath or dependencies, you need to configure the
`ConfigRetriever` to use this store:

``` js
import { ConfigRetriever } from "@vertx/config"
let store = new ConfigStoreOptions()
  .setType("redis")
  .setConfig({
    "host" : "localhost",
    "port" : 6379,
    "key" : "my-configuration"
  });

let retriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions()
  .setStores([store]));
```

The store configuration is used to create an instance of `RedisClient`.
check the documentation of the Vert.x Redis Client for further details.

In addition, you can set the `key` instructing the store in which
*field* the configuration is stored. `configuration` is used by default.

The created Redis client retrieves the configuration using the `HGETALL`
configuration.
