# Kubernetes bridge

This discovery bridge imports services from Kubernetes (or Openshift v3)
into the Vert.x service discovery. Kubernetes services are mapped to
`Record`. This bridge only supports the importation of services from
kubernetes in vert.x (and not the opposite).

`Record` are created from Kubernetes Service. The service type is
deduced from the `service-type` label or from the port exposed by the
service.

## Using the bridge

To use this Vert.x discovery bridge, add the following dependency to the
*dependencies* section of your build descriptor:

  - Maven (in your `pom.xml`):

<!-- end list -->

``` xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-service-discovery-bridge-kubernetes</artifactId>
 <version>${maven.version}</version>
</dependency>
```

  - Gradle (in your `build.gradle` file):

<!-- end list -->

``` groovy
compile 'io.vertx:vertx-service-discovery-bridge-kubernetes:${maven.version}'
```

## Configuring the bridge

The bridge is configured using:

  - the oauth token (using the content of
    `/var/run/secrets/kubernetes.io/serviceaccount/token` by default)

  - the namespace in which the service are searched (defaults to
    `default`).

Be aware that the application must have access to Kubernetes and must be
able to read the chosen namespace.

## The Service to Record mapping

The record is created as follows:

  - the service type is deduced from the `service.type` label. If this
    label is not set the service type is set to `unknown`

  - the record’s name is the service’s name

  - the labels of the service are mapped to metadata

  - in addition are added: `kubernetes.uuid`, `kubernetes.namespace`,
    `kubernetes.name`

  - the location is deduced from the\*first\*\* port of the service

For HTTP endpoints, the `ssl` (`https`) attribute is set to `true` if
the service has the `ssl` label set to `true`.

## Dynamics

The bridge imports all services on `start` and removes them on `stop`.
In between it watches the Kubernetes services and add the new ones and
removes the deleted ones.

## Supported types

The bridge uses the `service-type` label to induce the type. In addition
it checks the port of the service. Are supported:

  - ports 80, 443 and from 8080 to 9000: HTTP endpoint

  - ports 5432 and 5433: JDBC data source (PostGreSQL)

  - ports 3306 and 13306: JDBC data source (MySQL)

  - port 6379: Redis data source

  - ports 27017, 27018 and 27019: MongoDB data source

If present, the `service-type` overrides the port-based deduction.
