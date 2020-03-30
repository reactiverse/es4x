# Consul Configuration Store

The Consul Configuration Store extends the Vert.x Configuration
Retriever and provides the way to retrieve configuration from a
[Consul](https://www.consul.io).

## Using the Consul Configuration Store

To use the Consul Configuration Store, add the following dependency to
the *dependencies* section of your build descriptor:

  - Maven (in your `pom.xml`):

<!-- end list -->

``` xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-config-consul</artifactId>
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
compile 'io.vertx:vertx-config-consul:${maven.version}'
```

## Configuring the store

Once added to your classpath or dependencies, you need to configure the
`ConfigRetriever` to use this store:

``` js
import { ConfigRetriever } from "@vertx/config"
let store = new ConfigStoreOptions()
  .setType("consul")
  .setConfig({
    "host" : "localhost",
    "port" : 8500,
    "prefix" : "foo"
  });

let retriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions()
  .setStores([store]));
```

The store configuration is used to create an instance of `ConsulClient`.
Check the documentation of the Vert.x Consul Client for further details.
And this is the parameters specific to the Consul Configuration Store:

  - `prefix`  
    A prefix that will not be taken into account when building the
    configuration tree. Defaults to empty.

  - `delimiter`  
    Symbol that used to split keys in the Consul storage to obtain
    levels in the configuration tree. Defaults to "/".
