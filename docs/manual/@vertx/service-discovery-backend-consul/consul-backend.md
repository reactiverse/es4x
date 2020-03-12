# Consul backend

\<p\> The service discovery has a plug-able backend using the
`ServiceDiscoveryBackend` SPI. This is an implementation of the SPI
based on Consul. \<p\> ==== Using the Consul backend \<p\> To use the
Consul backend, add the following dependency to the *dependencies*
section of your build descriptor: \<p\> \* Maven (in your `pom.xml`):
\<p\>

``` xml
<dependency>
<groupId>io.vertx</groupId>
<artifactId>vertx-service-discovery-backend-consul</artifactId>
<version>${maven.version}</version>
</dependency>
```

\<p\> \* Gradle (in your `build.gradle` file): \<p\>

``` groovy
compile 'io.vertx:vertx-service-discovery-backend-consul:${maven.version}'
```

\<p\> Be aware that you can have only a single implementation of the SPI
in your *classpath*. If none, the default backend is used. \<p\> ====
Configuration \<p\> The backend is based on the
[vertx-consul-client](http://vertx.io/docs/vertx-consul-client/java).
The configuration is the client configuration. \<p\> Here is an example:
\<p\>

``` js
import { ServiceDiscovery } from "@vertx/service-discovery"
ServiceDiscovery.create(vertx, new ServiceDiscoveryOptions()
  .setBackendConfiguration({
    "defaultHost" : "127.0.0.1",
    "dc" : "my-dc"
  }));
```
