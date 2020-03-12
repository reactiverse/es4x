# Kubernetes ConfigMap Store

The Kubernetes ConfigMap Store extends the Vert.x Configuration
Retriever and provides support Kubernetes Config Map and Secrets. So,
configuration is retrieved by reading the config map or the secrets.

## Using the Kubernetes ConfigMap Store

To use the Kubernetes ConfigMap Store, add the following dependency to
the *dependencies* section of your build descriptor:

  - Maven (in your `pom.xml`):

<!-- end list -->

``` xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-config-kubernetes-configmap</artifactId>
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
compile 'io.vertx:vertx-config-kubernetes-configmap:${maven.version}'
```

## Configuring the store

Once added to your classpath or dependencies, you need to configure the
`ConfigRetriever` to use this store:

``` js
import { ConfigRetriever } from "@vertx/config"
let store = new ConfigStoreOptions()
  .setType("configmap")
  .setConfig({
    "namespace" : "my-project-namespace",
    "name" : "configmap-name"
  });

let retriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions()
  .setStores([store]));
```

You need to configure the store to find the right configmap. this is
done with:

  - `namespace` - the project namespace, `default` by default. If the
    `KUBERNETES_NAMESPACE` ENV variable is set, it uses this value.

  - `name` - the name of the config map

  - `optional` - whether or not the config map is optional (`true` by
    default)

If the config map is composed by several element, you can use the `key`
parameter to tell which `key` is read

The application must have the permissions to read the config map.

To read data from a secret, just configure the `secret` property to
`true`:

``` js
import { ConfigRetriever } from "@vertx/config"
let store = new ConfigStoreOptions()
  .setType("configmap")
  .setConfig({
    "namespace" : "my-project-namespace",
    "name" : "my-secret",
    "secret" : true
  });

let retriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions()
  .setStores([store]));
```

If the config map is not available, an empty JSON object is passed as
configuration chunk. To disable this behavior and explicitly fail, you
can set the `optional` configuration to `false`.
