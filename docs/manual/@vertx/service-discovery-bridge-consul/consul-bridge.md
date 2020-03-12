# Consul bridge

This discovery bridge imports services from [Consul](http://consul.io)
into the Vert.x service discovery. The bridge connects to a Consul agent
(server) and periodically scan for services:

  - new services are imported

  - services in maintenance mode or that has been removed from consul
    are removed

This bridge uses the HTTP API for Consul. It does not export to Consul
and does not support service modification.

The service type is deduced from `tags`. If a `tag` matches a known
service type, this service type will be used. If not, the service is
imported as `unknown`. Only `http-endpoint` is supported for now.

## Using the bridge

To use this Vert.x discovery bridge, add the following dependency to the
*dependencies* section of your build descriptor:

  - Maven (in your `pom.xml`):

<!-- end list -->

``` xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-service-discovery-bridge-consul</artifactId>
 <version>${maven.version}</version>
</dependency>
```

  - Gradle (in your `build.gradle` file):

<!-- end list -->

``` groovy
compile 'io.vertx:vertx-service-discovery-bridge-consul:${maven.version}'
```

Then, when creating the service discovery registers this bridge as
follows:

``` js
import { ServiceDiscovery } from "@vertx/service-discovery"
ServiceDiscovery.create(vertx).registerServiceImporter(new (Java.type("io.vertx.servicediscovery.consul.ConsulServiceImporter"))(), {
  "host" : "localhost",
  "port" : 8500,
  "scan-period" : 2000
});
```

You can configure the:

  - agent host using the `host` property, it defaults to `localhost`

  - agent port using the `port` property, it defaults to 8500

  - acl token using the `acl_token` property, it defaults to null

  - scan period using the `scan-period` property. The time is set in ms,
    and is 2000 ms by default
