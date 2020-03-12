# Hocon Configuration Format

The Hocon Configuration Format extends the Vert.x Configuration
Retriever and provides the support for the
HOCON(<https://github.com/typesafehub/config/blob/master/HOCON.md>)
format.

It supports includes, json, properties, macros…​

## Using the Hocon Configuration Format

To use the Hocon Configuration Format, add the following dependency to
the *dependencies* section of your build descriptor:

  - Maven (in your `pom.xml`):

<!-- end list -->

``` xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-config-hocon</artifactId>
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
compile 'io.vertx:vertx-config-hocon:${maven.version}'
```

## Configuring the store to use HOCON

Once added to your classpath or dependencies, you need to configure the
`ConfigRetriever` to use this format:

``` js
import { ConfigRetriever } from "@vertx/config"
let store = new ConfigStoreOptions()
  .setType("file")
  .setFormat("hocon")
  .setConfig({
    "path" : "my-config.conf"
  });

let retriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions()
  .setStores([store]));
```

You just need to set `format` to `hocon`.
