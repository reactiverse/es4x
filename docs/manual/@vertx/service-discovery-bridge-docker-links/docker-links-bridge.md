# Docker Links bridge

This discovery bridge imports services from Docker Links into the Vert.x
service discovery. When you link a Docker container to another Docker
container, Docker injects a set of environment variables. This bridge
analyzes these environment variables and imports service record for each
link. The service type is deduced from the `service.type` label. If not
set, the service is imported as `unknown`. Only `http-endpoint` are
supported for now.

As the links are created when the container starts, the imported records
are created when the bridge starts and do not change afterwards.

## Using the bridge

To use this Vert.x discovery bridge, add the following dependency to the
*dependencies* section of your build descriptor:

  - Maven (in your `pom.xml`):

<!-- end list -->

``` xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-service-discovery-bridge-docker</artifactId>
 <version>${maven.version}</version>
</dependency>
```

  - Gradle (in your `build.gradle` file):

<!-- end list -->

``` groovy
compile 'io.vertx:vertx-service-discovery-bridge-docker:${maven.version}'
```

Then, when creating the service discovery, registers this bridge as
follows:

``` js
import { ServiceDiscovery } from "@vertx/service-discovery"
ServiceDiscovery.create(vertx).registerServiceImporter(new (Java.type("io.vertx.servicediscovery.docker.DockerLinksServiceImporter"))(), {
});
```

The bridge does not need any further configuration.
