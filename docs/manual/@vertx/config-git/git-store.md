# Git Configuration Store

The Git Configuration Store is an extension to the Vert.x Configuration
Retriever to retrieve configuration from a Git repository.

## Using the Git Configuration Store

To use the Git Configuration, add the following dependency to the
*dependencies* section of your build descriptor:

  - Maven (in your `pom.xml`):

<!-- end list -->

``` xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-config-git</artifactId>
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
compile 'io.vertx:vertx-config-git:${maven.version}'
```

## Configuring the store

Once added to your classpath or dependencies, you need to configure the
`ConfigRetriever` to use this store:

``` js
import { ConfigRetriever } from "@vertx/config"

let git = new ConfigStoreOptions()
  .setType("git")
  .setConfig({
    "url" : "https://github.com/cescoffier/vertx-config-test.git",
    "path" : "local",
    "filesets" : [
      {
        "pattern" : "*.json"
      }
    ]
  });

let retriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions()
  .setStores([git]));
```

The configuration requires:

  - the `url` of the repository

  - the `path` where the repository is cloned (local directory)

  - the `user` for private repository (no authentication by default)

  - the `password` of the user

  - the `idRsaKeyPath` for private repository and requires ssh uri

  - at least `fileset` indicating the set of files to read (same
    behavior as the directory configuration store).

You can also configure the `branch` (`master` by default) to use and the
name of the `remote` repository (`origin` by default).

## How does it works

If the local `path` does not exist, the configuration store clones the
repository into this directory. Then it reads the file matching the
different file sets.

It the local `path` exist, it tried to update it (it switches branch if
needed)). If the update failed the configuration retrieval fails.

Periodically, the repositories is updated to check if the configuration
has been updated.
