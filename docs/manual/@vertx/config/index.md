Vert.x Config provides a way to configure your Vert.x application. It:

  - offers multiple configuration syntaxes (JSON, properties, Yaml
    (extension), Hocon (extension)…​

  - offers multiple configuration stores such as files, directories,
    HTTP, git (extension), Redis (extension), system properties and
    environment properties.

  - lets you define the processing order and overloading

  - supports runtime reconfiguration

# Concepts

The library is structured around:

  - a\*Config Retriever**instantiated and used by the Vert.x
    application. It configures a set of configuration
    store**Configuration store\*\* defines a location from where the
    configuration data is read and also a format (JSON by default)

The configuration is retrieved as a JSON Object.

# Using the Config Retriever

To use the Config Retriever, add the following dependency to the
*dependencies* section of your build descriptor:

  - Maven (in your `pom.xml`):

<!-- end list -->

``` xml
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
----
```

Once done, you first need to instantiate the `ConfigRetriever`:

    import { ConfigRetriever } from "@vertx/config"
    let retriever = ConfigRetriever.create(vertx);

By default, the Config Retriever is configured with the following stores
(in this order):

  - The Vert.x verticle `config()`

  - The system properties

  - The environment variables

  - A `conf/config.json` file. This path can be overridden using the
    `vertx-config-path` system property or `VERTX_CONFIG_PATH`
    environment variable.

You can configure your own stores:

    import { ConfigRetriever } from "@vertx/config"
    let httpStore = new ConfigStoreOptions()
      .setType("http")
      .setConfig({
        "host" : "localhost",
        "port" : 8080,
        "path" : "/conf"
      });

    let fileStore = new ConfigStoreOptions()
      .setType("file")
      .setConfig({
        "path" : "my-config.json"
      });

    let sysPropsStore = new ConfigStoreOptions()
      .setType("sys");


    let options = new ConfigRetrieverOptions()
      .setStores([httpStore, fileStore, sysPropsStore]);

    let retriever = ConfigRetriever.create(vertx, options);

More details about the overloading rules and available stores are
available below. Each store can be marked as `optional`. If a failure is
caught while retrieving (or processing) the configuration from an
optional store, the failure is logged, but the processing does not fail.
Instead, an empty JSON object is returned (`{}`). To mark a store as
optional, use the `optional` attribute:

    import { ConfigRetriever } from "@vertx/config"
    let fileStore = new ConfigStoreOptions()
      .setType("file")
      .setOptional(true)
      .setConfig({
        "path" : "my-config.json"
      });
    let sysPropsStore = new ConfigStoreOptions()
      .setType("sys");

    let options = new ConfigRetrieverOptions()
      .setStores([fileStore, sysPropsStore]);

    let retriever = ConfigRetriever.create(vertx, options);

Once you have the instance of the Config Retriever, *retrieve* the
configuration as follows:

    retriever.getConfig((ar) => {
      if (ar.failed()) {
        // Failed to retrieve the configuration
      } else {
        let config = ar.result();
      }
    });

# Overloading rules

The declaration order of the configuration store is important as it
defines the overloading. For conflicting key, configuration stores
arriving *last* overloads the value provided by the previous
configuration stores. Let’s take an example. We have 2 configuration
stores:

  - `A` provides `{a:value, b:1}`

  - `B` provides `{a:value2, c:2}`

Declared in this order (A, B), the resulting configuration would be:
`{a:value2, b:1, c:2}`.

If you declare them in the reverse order (B, A), you will get:
`{a:value, b:1, c:2}`.

# Using the retrieve configuration

The retrieve configuration allows:

  - configuring verticles,

  - configure ports, clients, locations and so on,

  - configuring Vert.x itself

This section gives a few examples of usage.

## Configuring a single verticle

The following example can be placed in the `start` method of a verticle.
It retrieves the configuration (using the default stores), and configure
an HTTP server with the content of the configuration.

    import { ConfigRetriever } from "@vertx/config"
    let retriever = ConfigRetriever.create(vertx);
    retriever.getConfig((json) => {
      let result = json.result();

      vertx.createHttpServer().requestHandler((req) => {
        result.message;
      }).listen(result.port);
    });

## Configuring a set of verticles

The following example configures 2 verticles using the configurations
contained in the `verticles.json` file:

    import { ConfigRetriever } from "@vertx/config"
    let retriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions()
      .setStores([new ConfigStoreOptions()
        .setType("file")
        .setConfig({
          "path" : "verticles.json"
        })]));

    retriever.getConfig((json) => {
      let a = json.result().a;
      let b = json.result().b;
      vertx.deployVerticle(Java.type("examples.GreetingVerticle").class.getName(), new DeploymentOptions()
        .setConfig(a));
      vertx.deployVerticle(Java.type("examples.GreetingVerticle").class.getName(), new DeploymentOptions()
        .setConfig(b));
    });

## Configuring Vert.x itself

You can also configure Vert.x directly. For this, you need a temporary
Vert.x instance used to retrieve the configuration. Then the actual
instance is created:

    import { Vertx } from "@vertx/core"
    import { ConfigRetriever } from "@vertx/config"
    // Create a first instance of Vert.x
    let vertx = Vertx.vertx();
    // Create the config retriever
    let retriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions()
      .setStores([new ConfigStoreOptions()
        .setType("file")
        .setConfig({
          "path" : "vertx.json"
        })]));

    // Retrieve the configuration
    retriever.getConfig((json) => {
      let result = json.result();
      // Close the vert.x instance, we don't need it anymore.
      vertx.close();

      // Create a new Vert.x instance using the retrieve configuration
      let options = result;
      let newVertx = Vertx.vertx(options);

      // Deploy your verticle
      newVertx.deployVerticle(Java.type("examples.GreetingVerticle").class.getName(), new DeploymentOptions()
        .setConfig(result.a));
    });

## Propagating configuration changes to the event bus

Vert.x Config notifies you when the configuration changes. If you want
to react to this event, you need to implement the reaction yourself. For
instance, you can un-deploy and redeploy verticle or send the new
configuration on the event bus. The following example shows this latter
case. It sends the new configuration on the event bus. Interested
verticles can listen for this address and update themselves:

    import { ConfigRetriever } from "@vertx/config"
    let retriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions()
      .setStores([new ConfigStoreOptions()
        .setType("file")
        .setConfig({
          "path" : "verticles.json"
        })]));

    retriever.getConfig((json) => {
      //...
    });

    retriever.listen((change) => {
      let json = change.newConfiguration;
      vertx.eventBus().publish("new-configuration", json);
    });

# Available configuration stores

The Config Retriever provides a set of configuration stores and formats.
More are available as extensions, and you can also implement your own.

## Structure of the configuration

Each declared data store must specify the `type`. It can also define the
`format`. If not set JSON is used.

Some configurations tore requires additional configuration (such a
path…​). This configuration is passed as a Json Object using
`setConfig`

## File

This configuration store just read the configuration from a file. It
supports all supported formats.

``` js
let file = new ConfigStoreOptions()
  .setType("file")
  .setFormat("properties")
  .setConfig({
    "path" : "path-to-file.properties"
  });
```

The `path` configuration is required.

## JSON

The JSON configuration store serves the given JSON config as it is.

``` js
let json = new ConfigStoreOptions()
  .setType("json")
  .setConfig({
    "key" : "value"
  });
```

The only supported format for this configuration store is JSON.

## Environment Variables

This configuration store transforms environment variables to a JSON
Object contributed to the global configuration.

``` js
let json = new ConfigStoreOptions()
  .setType("env");
```

This configuration store does not support the `format` configuration. By
default, the retrieved value is transformed into JSON compatible
structures (number, string, boolean, JSON object and JSON array). To
avoid this conversion, configure the `raw-data` attribute:

``` js
let json = new ConfigStoreOptions()
  .setType("env")
  .setConfig({
    "raw-data" : true
  });
```

You can configure the `raw-data` attribute (`false` by default). If
`raw-data` is `true` no attempts to convert values is made, and you’ll
be able to get raw values using `config.getString(key)`. It is useful
when manipulating large integers.

If you want to select the set of keys to import, use the `keys`
attributes. It filters out all non selected keys. Keys must be listed
individually:

``` js
let json = new ConfigStoreOptions()
  .setType("env")
  .setConfig({
    "keys" : [
      "SERVICE1_HOST",
      "SERVICE2_HOST"
    ]
  });
```

## System Properties

This configuration store transforms system properties to a JSON Object
contributed to the global configuration.

``` js
let json = new ConfigStoreOptions()
  .setType("sys")
  .setConfig({
    "cache" : "false"
  });
```

This configuration store does not support the `format` configuration.

You can configure the `cache` attribute (`true` by default) let you
decide whether or not it caches the system properties on the first
access and does not reload them.

You can also configure the `raw-data` attribute (`false` by default). If
`raw-data` is `true` no attempts to convert values is made, and you’ll
be able to get raw values using `config.getString(key)`. It is useful
when manipulating large integers.

## HTTP

This configuration store retrieves the configuration from an HTTP
location. It can use any supported format.

``` js
let http = new ConfigStoreOptions()
  .setType("http")
  .setConfig({
    "host" : "localhost",
    "port" : 8080,
    "path" : "/A"
  });
```

It creates a Vert.x HTTP Client with the store configuration (see next
snippet). To ease the configuration; you can also configure the `host`,
`port` and `path` with the `host`, `port` and `path` properties.

``` js
let http = new ConfigStoreOptions()
  .setType("http")
  .setConfig({
    "defaultHost" : "localhost",
    "defaultPort" : 8080,
    "ssl" : true,
    "path" : "/A"
  });
```

## Event Bus

This event bus configuration store receives the configuration from the
event bus. This stores let you distribute your configuration among your
local and distributed components.

``` js
let eb = new ConfigStoreOptions()
  .setType("event-bus")
  .setConfig({
    "address" : "address-getting-the-conf"
  });
```

This configuration store supports any format.

## Directory

This configuration store is similar to the `file` configuration store,
but instead of reading a single file, read several files from a
directory.

This configuration store configuration requires:

  - a `path` - the root directory in which files are located

  - at least one `fileset` - an object to select the files

  - for properties file, you can indicate if you want to disable the
    type conversion using the `raw-data` attribute

Each `fileset` contains: \* a `pattern` : a Ant-style pattern to select
files. The pattern is applied on the relative path of the files from the
current working directory. \* an optional `format` indicating the format
of the files (each fileset can use a different format, BUT files in a
fileset must share the same format).

``` js
let dir = new ConfigStoreOptions()
  .setType("directory")
  .setConfig({
    "path" : "config",
    "filesets" : [
      {
        "pattern" : "dir/*json"
      },
      {
        "pattern" : "dir/*.properties",
        "format" : "properties"
      }
    ]
  });

let dirWithRawData = new ConfigStoreOptions()
  .setType("directory")
  .setConfig({
    "path" : "config",
    "filesets" : [
      {
        "pattern" : "dir/*json"
      },
      {
        "pattern" : "dir/*.properties",
        "format" : "properties",
        "raw-data" : true
      }
    ]
  });
```

## Properties file and raw data

Vert.x Config can read properties file. When reading such a file, you
can pass the `raw-data` attribute to indicate to Vert.x to not attempt
to convert values. It is useful when manipulating large integers. Values
can be retrieved using `config.getString(key)`.

``` js
let propertyWithRawData = new ConfigStoreOptions()
  .setFormat("properties")
  .setType("file")
  .setConfig({
    "path" : "raw.properties",
    "raw-data" : true
  });
```

Some properties configuration maybe is hierarchical in nature. When
reading such a file, you can pass the `hierarchical` attribute to
indicate to Vert.x to convert the configuration to a json object while
maintaining this hierarchy, in contrast to the previous method with a
flat structure.

Example:

    server.host=localhost
    server.port=8080
    multiple.values=1,2,3

Get values:

``` js
import { Vertx } from "@vertx/core"
import { ConfigRetriever } from "@vertx/config"
let propertyWitHierarchical = new ConfigStoreOptions()
  .setFormat("properties")
  .setType("file")
  .setConfig({
    "path" : "hierarchical.properties",
    "hierarchical" : true
  });
let options = new ConfigRetrieverOptions()
  .setStores([propertyWitHierarchical]);

let configRetriever = ConfigRetriever.create(Vertx.vertx(), options);

configRetriever.configStream().handler((config) => {
  let host = config.server.host;
  let port = config.server.port;
  let multiple = config.multiple.values;
  for (let i = 0;i < multiple.length;i++) {
    let value = multiple[i];
  }
});
```

# Listening for configuration changes

The Configuration Retriever periodically retrieve the configuration, and
if the outcome is different from the current one, your application can
be reconfigured. By default, the configuration is reloaded every 5
seconds.

``` js
import { Vertx } from "@vertx/core"
import { ConfigRetriever } from "@vertx/config"
let options = new ConfigRetrieverOptions()
  .setScanPeriod(2000)
  .setStores([store1, store2]);

let retriever = ConfigRetriever.create(Vertx.vertx(), options);
retriever.getConfig((json) => {
  // Initial retrieval of the configuration
});

retriever.listen((change) => {
  // Previous configuration
  let previous = change.previousConfiguration;
  // New configuration
  let conf = change.newConfiguration;
});
```

# Retrieving the last retrieved configuration

You can retrieve the last retrieved configuration without "waiting" to
be retrieved using:

``` js
let last = retriever.getCachedConfig();
```

# Reading configuration as a stream

The `ConfigRetriever` provide a way to access the stream of
configuration. It’s a `ReadStream` of `JsonObject`. By registering the
right set of handlers you are notified:

  - when a new configuration is retrieved

  - when an error occur while retrieving a configuration

  - when the configuration retriever is closed (the `endHandler` is
    called).

<!-- end list -->

``` js
import { Vertx } from "@vertx/core"
import { ConfigRetriever } from "@vertx/config"
let options = new ConfigRetrieverOptions()
  .setScanPeriod(2000)
  .setStores([store1, store2]);

let retriever = ConfigRetriever.create(Vertx.vertx(), options);
retriever.configStream().endHandler((v) => {
  // retriever closed
}).exceptionHandler((t) => {
  // an error has been caught while retrieving the configuration
}).handler((conf) => {
  // the configuration
});
```

# Processing the configuration

You can configure a *processor* that can validate and update the
configuration. This is done using the `setConfigurationProcessor`
method.

The prcessing must not return `null`. It takes the retrieved
configuration and returns the processed one. If the processor does not
update the configuration, it must return the input configuration. If the
processor can throw an exception (for example for validation purpose).

# Retrieving the configuration as a Future

The `ConfigRetriever` provide a way to retrieve the configuration as a
`Future`:

``` js
import { ConfigRetriever } from "@vertx/config"
let future = ConfigRetriever.getConfigAsFuture(retriever);
future.setHandler((ar) => {
  if (ar.failed()) {
    // Failed to retrieve the configuration
  } else {
    let config = ar.result();
  }
});
```

# Extending the Config Retriever

You can extend the configuration by implementing:

  - the `ConfigProcessor` SPI to add support for a format

  - the `ConfigStoreFactory` SPI to add support for configuration store
    (place from where the configuration data is retrieved)

# Additional formats

Besides of the out of the box format supported by this library, Vert.x
Config provides additional formats you can use in your application.

Unresolved directive in index.adoc - include::hocon-format.adoc\[\]

Unresolved directive in index.adoc - include::yaml-format.adoc\[\]

# Additional stores

Besides of the out of the box stores supported by this library, Vert.x
Config provides additional stores you can use in your application.

Unresolved directive in index.adoc - include::git-store.adoc\[\]

Unresolved directive in index.adoc - include::kubernetes-store.adoc\[\]

Unresolved directive in index.adoc - include::redis-store.adoc\[\]

Unresolved directive in index.adoc - include::zookeeper-store.adoc\[\]

Unresolved directive in index.adoc - include::consul-store.adoc\[\]

Unresolved directive in index.adoc - include::spring-store.adoc\[\]

Unresolved directive in index.adoc - include::vault-store.adoc\[\]
