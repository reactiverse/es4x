# Code Generation

`es4x` works with vert.x code generation. Vert.x code generation is an annotation processing tool that extracts metadata
from the java source code and allows other projects such as `es4x` to generate other sources.

A typical code generation process will generate the following files:

* `package.json` a module descriptor with references to java artifacts and/or other js dependencies
* `README.md` a simple read me
* `index.js` a commonjs style script exporting all the annotated java interfaces
* `index.mjs` a ES6 module script exporting all the annotated java interfaces
* `index.d.ts` a typescript definition file with all the annotated java interfaces
* `options.js` a commonjs style script exporting all the annotated java data objects
* `options.mjs` a ES6 module script exporting all the annotated java data objects
* `options.d.ts` a typescript definition file with all the annotated data objects
* `enums.js` a commonjs style script exporting all the annotated java enums
* `enums.mjs` a ES6 module script exporting all the annotated java enums
* `enums.d.ts` a typescript definition file with all the annotated java enums
* `mod.js` a single ES6 module re-exporting `index.mjs`, `options.mjs` and `enums.mjs` from a single source

At the moment the current generation of code is driven by a `maven` build. The process can be seen as a sub module of
a maven build defined as usual by a `pom.xml` file.

## Code generating a vert.x module for es4x

For this example, let's consider the module [hot-reload](https://github.com/pmlopes/hot-reload). The module is a vertx
codegen module if the APIs follow the codegen restrictions and interfaces are annotated with `@VertxGen` like in this
interface [HotReload.java](https://github.com/pmlopes/hot-reload/blob/master/src/main/java/xyz/jetdrone/vertx/hot/reload/HotReload.java)

::: warning
Sometimes codegen seem not to be working because a top level `package-info.java` file is missing. Don't forget that file
it is required to get the annotation processor to run.
:::

In order to generate an `es4x` module for this component all we need is a `pom.xml` which should look like this:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">

  <modelVersion>4.0.0</modelVersion>

  <parent>
    <groupId>io.reactiverse.es4x</groupId>
    <artifactId>es4x-generator</artifactId>
    <version>0.16.0</version>
    <relativePath>../..</relativePath>
  </parent>

  <artifactId>hot-reload</artifactId>
  <version>0.15.1-SNAPSHOT</version>

  <packaging>jar</packaging>

  <properties>
    <maven.groupId>xyz.jetdrone</maven.groupId>
    <npm-version>0.0.5</npm-version>
    <npm-skip>false</npm-skip>
    <!-- language=json -->
    <npm-dependencies>
      {
        "@vertx/web": "${stack.version}"
      }
    </npm-dependencies>
  </properties>

  <dependencies>
    <!-- Optional -->
    <dependency>
      <groupId>io.vertx</groupId>
      <artifactId>vertx-web</artifactId>
      <version>${stack.version}</version>
      <optional>true</optional>
    </dependency>
    <!-- SOURCE TO GENERATE -->
    <dependency>
      <groupId>${maven.groupId}</groupId>
      <artifactId>${project.artifactId}</artifactId>
      <version>${npm-version}</version>
    </dependency>
    <dependency>
      <groupId>${maven.groupId}</groupId>
      <artifactId>${project.artifactId}</artifactId>
      <version>${npm-version}</version>
      <scope>provided</scope>
      <classifier>sources</classifier>
    </dependency>
  </dependencies>

</project>
```

The `artifactId` should be the same as the one we want to generate the `es4x` module. In this example the original
module has the following coordinates:

```xml
<groupId>xyz.jetdrone</groupId>
<artifactId>hot-reload</artifactId>
<version>0.0.5</version>
```

From here, there are several properties you need to declare, not all of them are required and some already have been
prefilled here's the list:

```xml
<es4x.version>${project.parent.version}</es4x.version>
<!-- This is the source group id -->
<maven.groupId>io.vertx</maven.groupId>

<npm-skip>true</npm-skip>
<npm-tag>latest</npm-tag>
<maven-deploy-skip>true</maven-deploy-skip>
<maven-gpg-skip>true</maven-gpg-skip>

<!-- language=json -->
<scope-registry>
  [
    {
      "group": "io.vertx",
      "prefix": "vertx-",
      "scope": "vertx",
      "stripPrefix": true
    },
    {
      "group": "io.vertx",
      "module": "vertx",
      "name": "core",
      "scope": "vertx"
    },
    {
      "group": "io.vertx",
      "module": "vertx-jdbc",
      "name": "jdbc-client",
      "scope": "vertx"
    },
    {
      "group": "io.vertx",
      "module": "vertx-mongo",
      "name": "mongo-client",
      "scope": "vertx"
    },
    {
      "group": "io.vertx",
      "module": "vertx-redis",
      "name": "redis-client",
      "scope": "vertx"
    },
    {
      "group": "io.vertx",
      "module": "vertx-sql",
      "name": "jdbc-client",
      "scope": "vertx"
    },
    {
      "group": "io.reactiverse",
      "scope": "reactiverse"
    }
  ]
</scope-registry>

<!-- allow to publish under a custom name -->
<npm-name>${project.artifactId}</npm-name>
<npm-version>${project.version}</npm-version>
<npm-license>Apache-2.0</npm-license>
<!-- allow to publish to a different registry -->
<npm-registry>https://registry.npmjs.org</npm-registry>
<!-- allow override of dependencies -->
<!-- language=json -->
<npm-dependencies>
  {
    "@vertx/core": "${stack.version}"
  }
</npm-dependencies>
<!-- language=json -->
<npm-dev-dependencies>{}</npm-dev-dependencies>
<!-- language=json -->
<package-json>
  {
    "name": "${npm-name}",
    "description": "${project.description}",
    "version": "${npm-version}",
    "license": "${npm-license}",
    "public": true,
    "maven": {
      "groupId": "${maven.groupId}",
      "artifactId": "${project.artifactId}",
      "version": "${npm-version}"
    },
    "dependencies": ${npm-dependencies},
    "devDependencies": ${npm-dev-dependencies}
  }
</package-json>
<!-- language=json -->
<npm-optional-dependencies>[]</npm-optional-dependencies>
<!-- language=json -->
<npm-class-exclusions>[]</npm-class-exclusions>
<!-- language=json -->
<jvm-classes>{}</jvm-classes>
<!-- git -->
<git-url>https://github.com/reactiverse/es4x.git</git-url>
<git-directory>/generator/${maven.groupId}/${project.artifactId}</git-directory>
```

Finally you need to go over the dependencies section. There are 2 dependencies you always need to have:

```xml
<dependency>
  <groupId>${maven.groupId}</groupId>
  <artifactId>${project.artifactId}</artifactId>
  <version>${npm-version}</version>
</dependency>
<dependency>
  <groupId>${maven.groupId}</groupId>
  <artifactId>${project.artifactId}</artifactId>
  <version>${npm-version}</version>
  <scope>provided</scope>
  <classifier>sources</classifier>
</dependency>
```

The final artifact and the artifact source jar. Using the artifact we can compute the dependencies so we can ensure that
the annotation processor can work outside the source project, but there are cases like in this example where optional
dependencies are listed. If such dependencies are used, they need to be re-declared in the pom like in the example
above.

## Running the generation

Running the generation is a typical maven phase (`generate-sources`) so all you need is:

```shell
~/ $ mvn clean generate-sources

[INFO] Scanning for projects...
[INFO]
[INFO] -------------------< io.reactiverse.es4x:hot-reload >-------------------
[INFO] Building hot-reload 0.15.1-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO]
[INFO] --- maven-clean-plugin:2.5:clean (default-clean) @ hot-reload ---
[INFO] Deleting /home/paulo/Projects/reactiverse/es4x/generator/xyz.jetdrone/hot-reload/target
[INFO]
[INFO] --- maven-enforcer-plugin:1.4.1:enforce (enforce-maven) @ hot-reload ---
[INFO]
[INFO] --- properties-maven-plugin:1.0.0:set-system-properties (default) @ hot-reload ---
[INFO] Set 8 system properties
[INFO]
[INFO] --- maven-dependency-plugin:3.2.0:unpack-dependencies (unpack-sources) @ hot-reload ---
[INFO] Unpacking /home/paulo/.m2/repository/xyz/jetdrone/hot-reload/0.0.6/hot-reload-0.0.6-sources.jar to /home/paulo/Projects/reactiverse/es4x/generator/xyz.jetdrone/hot-reload/target/sources/hot-reload with includes "**/*.java" and excludes "**/examples/**/*.*,**/*-examples/**/*.*,**/groovy/**/*.*,**/rxjava/**/*.*,**/reactivex/**/*.*"
[INFO]
[INFO] --- maven-processor-plugin:4.5-jdk8:process (generate-api) @ hot-reload ---
[WARNING] diagnostic: warning: [options] bootstrap class path not set in conjunction with -source 8
[INFO] diagnostic: Note: Loaded es4x-generator (index.js) code generator
[INFO] diagnostic: Note: Loaded es4x-generator (options.js) code generator
[INFO] diagnostic: Note: Loaded es4x-generator (enum.js) code generator
[INFO] diagnostic: Note: Loaded es4x-generator (index.d.ts) code generator
[INFO] diagnostic: Note: Loaded es4x-generator (options.d.ts) code generator
[INFO] diagnostic: Note: Loaded es4x-generator (enum.d.ts) code generator
[INFO] diagnostic: Note: Loaded es4x-generator (index.mjs) code generator
[INFO] diagnostic: Note: Loaded es4x-generator (options.mjs) code generator
[INFO] diagnostic: Note: Loaded es4x-generator (enum.mjs) code generator
[INFO] diagnostic: Note: Loaded es4x-generator (package.json) code generator
[INFO] diagnostic: Note: Loaded es4x-generator (README.md) code generator
[INFO] diagnostic: Note: Loaded es4x-generator (mod.js) code generator
[INFO] diagnostic: Note: Loaded data_object_converters code generator
[INFO] diagnostic: Note: Generated model xyz.jetdrone.vertx.hot.reload.HotReload: npm/index.d.ts
[INFO] diagnostic: Note: Generated model xyz.jetdrone.vertx.hot.reload.HotReload: npm/package.json
[INFO] diagnostic: Note: Generated model xyz.jetdrone.vertx.hot.reload.HotReload: npm/index.js
[INFO] diagnostic: Note: Generated model xyz.jetdrone.vertx.hot.reload.HotReload: npm/mod.js
[INFO] diagnostic: Note: Generated model xyz.jetdrone.vertx.hot.reload.HotReload: npm/index.mjs
[INFO] diagnostic: Note: Generated model xyz.jetdrone.vertx.hot.reload.HotReload: npm/README.md
[WARNING] diagnostic: warning: Unclosed files for the types 'PathForCodeGenProcessor'; these types will not undergo annotation processing
[INFO]
[INFO] --- maven-resources-plugin:3.2.0:copy-resources (copy-extras) @ hot-reload ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] Using 'UTF-8' encoding to copy filtered properties files.
[INFO] skip non existing resourceDirectory /home/paulo/Projects/reactiverse/es4x/generator/xyz.jetdrone/hot-reload/extra
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  2.371 s
[INFO] Finished at: 2021-09-10T21:32:03+02:00
[INFO] ------------------------------------------------------------------------
~/Projects/reactiverse/es4x/generator/xyz.jetdrone/hot-reload (develop)$
```

## Working around limitations

Codegen is a great tool but sometimes is is very hard to get it to work for all cases. For this `es4x` codegen plugin
has several workarounds to allow you to quickly hack your desired output without having to write code.

### Adding code to a generated file

You can either add code to the beginning or to the end of each generated file. At the same level of your `pom.xml` add
a file `{index|options|enums|...}.header.{js|mjs|.d.ts}` and it's content will be prepended to the generated file.

The same can be done to the end of the generated files, use the following format: `{index|options|enums|...}.footer.{js|mjs|.d.ts}`.

For example, vert.x core doesn't annotate the types `JsonObject` and `JsonArray` so in order to expose them in core we
have a file `module.header.mjs` with the following content:

```js
export const AsyncResult = Java.type('io.vertx.core.AsyncResult');
export const JsonObject = Java.type('io.vertx.core.json.JsonObject');
export const JsonArray = Java.type('io.vertx.core.json.JsonArray');
```

The same can be done to a specific type. For example, `HttpMethod` isn't fully annotated so we include the default
methods to right interface during the `index.d.ts` file generation by creating an includes file `HttpMethod.includes.json`.

```json
{
  "d.ts": "  /**\n   * The RFC 2616 {@code OPTIONS} method, this instance is interned and uniquely us..."
}
```

The key identifies the kind of generation template where the inclusion should happen.

::: warning
Exclusions can also be done using `.excludes.json` file name suffix.
:::

## Overriding

Just like inclusion and exclusion, we can also override method parameters or even return types using the file name
suffix `.override.json`, for example, `CompositeFuture` codegen doesn't handle some overloads correctly so we can
define an overrides file as:

```json
{
  "cause": "index?: number",
  "succeeded": "index?: number",
  "failed": "index?: number",
  "isComplete": "index?: number",
  "setHandler": {
    "return": "Future<CompositeFuture>"
  }
}
```

This allows as to define an optional argument `index` to several methods and override the return type for `setHandler`
to comply to typescript restrictions.
