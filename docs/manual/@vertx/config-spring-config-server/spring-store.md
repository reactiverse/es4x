# Spring Config Server Store

The Spring Config Server Store extends the Vert.x Configuration
Retriever and provides the a way to retrieve configuration from a Spring
Server.

## Using the Spring Config Server Store

To use the Spring Config Server Store, add the following dependency to
the *dependencies* section of your build descriptor:

  - Maven (in your `pom.xml`):

<!-- end list -->

``` xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-config-spring-config-server</artifactId>
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
compile 'io.vertx:vertx-config-spring-config-server:${maven.version}'
```

## Configuring the store

Once added to your classpath or dependencies, you need to configure the
`ConfigRetriever` to use this store:

``` js
import { ConfigRetriever } from "@vertx/config"
let store = new ConfigStoreOptions()
  .setType("spring-config-server")
  .setConfig({
    "url" : "http://localhost:8888/foo/development"
  });

let retriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions()
  .setStores([store]));
```

Configurable attributes are:

  - `url` - the `url` to retrieve the configuration (mandatory),
    supports two type of formats:
    
      - `/{application}/{environment}` which produces response with
        separated propertySources
    
      - `/{application}-{environment}.json` which produces response as
        JSON with unique fields and resolved Spring placeholders

  - `timeout` - the timeout (in milliseconds) to retrieve the
    configuration, 3000 by default

  - `user` - the `user` (no authentication by default)

  - `password` - the `password`

  - `httpClientConfiguration` - the configuration of the underlying HTTP
    client
