This project is an implementation of the Vert.x Metrics Service Provider
Interface (SPI). It uses [Micrometer](http://micrometer.io/) for
managing metrics and reporting to several backends.

# Features

  - Vert.x core tools monitoring: TCP/HTTP client and servers,
    `DatagramSocket` , `EventBus` and pools

  - User defined metrics through Micrometer

  - Reporting to any backend supported by Micrometer

  - Built-in options for [InfluxDB](https://www.influxdata.com/),
    [Prometheus](https://prometheus.io/) and JMX reporting.

# InfluxDB

## Prerequisites

Follow the [instructions to get InfluxDb up and
running](https://docs.influxdata.com/influxdb/latest/introduction/getting_started/).

## Getting started

The modules *vertx-micrometer-metrics* and *micrometer-registry-influx*
must be present in the classpath.

Maven users should add this to their project POM file:

``` xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-micrometer-metrics</artifactId>
 <version>${maven.version}</version>
</dependency>
<dependency>
 <groupId>io.micrometer</groupId>
 <artifactId>micrometer-registry-influx</artifactId>
 <version>${micrometer.version}</version>
</dependency>
```

And Gradle users, to their build file:

``` groovy
compile 'io.vertx:vertx-micrometer-metrics:${maven.version}'
compile 'io.micrometer:micrometer-registry-influx:${micrometer.version}'
```

## Configuration examples

Vert.x does not enable SPI implementations by default. You must enable
metric collection in the Vert.x options.

``` js
import { Vertx } from "@vertx/core"
let vertx = Vertx.vertx(new VertxOptions()
  .setMetricsOptions(new MicrometerMetricsOptions()
    .setInfluxDbOptions(new VertxInfluxDbOptions()
      .setEnabled(true))
    .setEnabled(true)));
```

### Using a specific URI and database name

``` js
import { Vertx } from "@vertx/core"
let vertx = Vertx.vertx(new VertxOptions()
  .setMetricsOptions(new MicrometerMetricsOptions()
    .setInfluxDbOptions(new VertxInfluxDbOptions()
      .setEnabled(true)
      .setUri("http://influxdb.example.com:8888")
      .setDb("sales-department"))
    .setEnabled(true)));
```

### With authentication

``` js
import { Vertx } from "@vertx/core"
let vertx = Vertx.vertx(new VertxOptions()
  .setMetricsOptions(new MicrometerMetricsOptions()
    .setInfluxDbOptions(new VertxInfluxDbOptions()
      .setEnabled(true)
      .setUserName("username")
      .setPassword("password"))
    .setEnabled(true)));
```

# Prometheus

## Prerequisites

Follow the [instructions to get Prometheus up and
running](https://prometheus.io/docs/prometheus/latest/getting_started/).

## Getting started

The modules *vertx-micrometer-metrics* and
*micrometer-registry-prometheus* must be present in the classpath. You
may also probably need *vertx-web*, to expose the metrics.

Maven users should add this to their project POM file:

``` xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-micrometer-metrics</artifactId>
 <version>${maven.version}</version>
</dependency>
<dependency>
 <groupId>io.micrometer</groupId>
 <artifactId>micrometer-registry-prometheus</artifactId>
 <version>${micrometer.version}</version>
</dependency>
```

And Gradle users, to their build file:

``` groovy
compile 'io.vertx:vertx-micrometer-metrics:${maven.version}'
compile 'io.micrometer:micrometer-registry-prometheus:${micrometer.version}'
```

## Configuration examples

Vert.x does not enable SPI implementations by default. You must enable
metric collection in the Vert.x options

``` js
import { Vertx } from "@vertx/core"
let vertx = Vertx.vertx(new VertxOptions()
  .setMetricsOptions(new MicrometerMetricsOptions()
    .setPrometheusOptions(new VertxPrometheusOptions()
      .setEnabled(true))
    .setEnabled(true)));
```

### Using an embedded HTTP server with custom endpoint

``` js
import { Vertx } from "@vertx/core"
let vertx = Vertx.vertx(new VertxOptions()
  .setMetricsOptions(new MicrometerMetricsOptions()
    .setPrometheusOptions(new VertxPrometheusOptions()
      .setEnabled(true)
      .setStartEmbeddedServer(true)
      .setEmbeddedServerOptions(new HttpServerOptions()
        .setPort(8080))
      .setEmbeddedServerEndpoint("/metrics/vertx"))
    .setEnabled(true)));
```

If the embedded server endpoint is not specified, it defaults to
*/metrics*.

### Binding metrics to an existing Vert.x Web router

``` js
import { Vertx } from "@vertx/core"
import { Router } from "@vertx/web"
import { PrometheusScrapingHandler } from "@vertx/micrometer-metrics"
let vertx = Vertx.vertx(new VertxOptions()
  .setMetricsOptions(new MicrometerMetricsOptions()
    .setPrometheusOptions(new VertxPrometheusOptions()
      .setEnabled(true))
    .setEnabled(true)));

// Later on, creating a router
let router = Router.router(vertx);
router.route("/metrics").handler(PrometheusScrapingHandler.create());
vertx.createHttpServer().requestHandler(router).listen(8080);
```

# JMX

## Getting started

The modules *vertx-micrometer-metrics* and *micrometer-registry-jmx*
must be present in the classpath.

Maven users should add this to their project POM file:

``` xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-micrometer-metrics</artifactId>
 <version>${maven.version}</version>
</dependency>
<dependency>
 <groupId>io.micrometer</groupId>
 <artifactId>micrometer-registry-jmx</artifactId>
 <version>${micrometer.version}</version>
</dependency>
```

And Gradle users, to their build file:

``` groovy
compile 'io.vertx:vertx-micrometer-metrics:${maven.version}'
compile 'io.micrometer:micrometer-registry-jmx:${micrometer.version}'
```

## Configuration examples

Vert.x does not enable SPI implementations by default. You must enable
metric collection in the Vert.x options

``` js
import { Vertx } from "@vertx/core"
let vertx = Vertx.vertx(new VertxOptions()
  .setMetricsOptions(new MicrometerMetricsOptions()
    .setJmxMetricsOptions(new VertxJmxMetricsOptions()
      .setEnabled(true))
    .setEnabled(true)));
```

### With step and domain

In Micrometer, `step` refers to the reporting period, in seconds.
`domain` is the JMX domain under which MBeans are registered.

``` js
import { Vertx } from "@vertx/core"
let vertx = Vertx.vertx(new VertxOptions()
  .setMetricsOptions(new MicrometerMetricsOptions()
    .setJmxMetricsOptions(new VertxJmxMetricsOptions()
      .setEnabled(true)
      .setStep(5)
      .setDomain("my.metrics.domain"))
    .setEnabled(true)));
```

# Other backends or combinations

Even if not all backends supported by Micrometer are implemented in
Vert.x options, it is still possible to create any Micrometer registry
and pass it to Vert.x.

The list of available backends includes Graphite, Ganglia, Atlas,
[etc](http://micrometer.io/docs). It also enables the [Micrometer
Composite
Registry](http://micrometer.io/docs/concepts#_composite_registries) in
order to report the same metrics to multiple backends.

In this example, metrics are reported both for JMX and Graphite:

``` js
import { Vertx } from "@vertx/core"
let myRegistry = new (Java.type("io.micrometer.core.instrument.composite.CompositeMeterRegistry"))();
myRegistry.add(new (Java.type("io.micrometer.jmx.JmxMeterRegistry"))((s) => {
  null;
}, Java.type("io.micrometer.core.instrument.Clock").SYSTEM));
myRegistry.add(new (Java.type("io.micrometer.graphite.GraphiteMeterRegistry"))((s) => {
  null;
}, Java.type("io.micrometer.core.instrument.Clock").SYSTEM));

let vertx = Vertx.vertx(new VertxOptions()
  .setMetricsOptions(new MicrometerMetricsOptions()
    .setMicrometerRegistry(myRegistry)
    .setEnabled(true)));
```

# Advanced usage

Please refer to `MicrometerMetricsOptions` for an exhaustive list of
options.

## Averages and quantiles in Prometheus

By default, when using the Prometheus registry, histogram-kind metrics
will not contain averages or quantile stats.

Averages don’t come out of the box but they are typically [computed at
query
time](https://prometheus.io/docs/practices/histograms/#count-and-sum-of-observations),
with `promql`. Example, for HTTP client response time average during the
last 5 minutes:

``` 
 rate(vertx_http_client_responseTime_seconds_sum[5m])
/
 rate(vertx_http_client_responseTime_seconds_count[5m])
```

To compute quantiles, there are two options available. The first is to
activate quantile stats globally and make them usable for Prometheus
function `histogram_quantile`:

``` js
import { Vertx } from "@vertx/core"
let vertx = Vertx.vertx(new VertxOptions()
  .setMetricsOptions(new MicrometerMetricsOptions()
    .setPrometheusOptions(new VertxPrometheusOptions()
      .setEnabled(true)
      .setPublishQuantiles(true))
    .setEnabled(true)));
```

And then, for example the `promql` query for the HTTP client response
time, 99th percentile over the last 5 minutes:

``` 
 histogram_quantile(0.99, sum(rate(vertx_http_client_responseTime_seconds_bucket[5m])) by (le))
```

The advantage of this option is that it can be leveraged in `promql`,
aggregable across dimensions. The downside is that it creates a lot of
timeseries for stats under the hood.

The second option is to create limited stats, non-aggregable across
dimensions. It requires to access directly the Micrometer / Prometheus
registry:

``` js
let registry = Java.type("io.vertx.micrometer.backends.BackendRegistries").getDefaultNow();
registry.config().meterFilter(new (Java.type("io.micrometer.core.instrument.config.MeterFilter"))());
```

See also, more on histograms and percentiles:

  - from [Micrometer
    doc](https://micrometer.io/docs/concepts#_histograms_and_percentiles)

  - from [Prometheus
    doc](https://prometheus.io/docs/prometheus/latest/querying/functions/#histogram_quantile)

Furthermore, you can check some [full working
examples](https://github.com/vert-x3/vertx-examples/tree/master/micrometer-metrics-examples).
They come along with few instructions to setup with Prometheus and view
dashboards in Grafana.

## Disable some metric domains

Restricting the Vert.x modules being monitored can be done using
`disabledMetricsCategories`.

For a full list of domains, see `MetricsDomain`

## User-defined metrics

The Micrometer registries are accessible, in order to create new metrics
or fetch the existing ones. By default, an unique registry is used and
will be shared across the Vert.x instances of the JVM:

``` js
let registry = Java.type("io.vertx.micrometer.backends.BackendRegistries").getDefaultNow();
```

It is also possible to have separate registries per Vertx instance, by
giving a registry name in metrics options. Then it can be retrieved
specifically:

``` js
import { Vertx } from "@vertx/core"
let vertx = Vertx.vertx(new VertxOptions()
  .setMetricsOptions(new MicrometerMetricsOptions()
    .setInfluxDbOptions(new VertxInfluxDbOptions()
      .setEnabled(true))
    .setRegistryName("my registry")
    .setEnabled(true)));

// Later on:
let registry = Java.type("io.vertx.micrometer.backends.BackendRegistries").getNow("my registry");
```

As an example, here is a custom timer that will track the execution time
of a piece of code that is regularly called:

``` js
let registry = Java.type("io.vertx.micrometer.backends.BackendRegistries").getDefaultNow();
let timer = Java.type("io.micrometer.core.instrument.Timer").builder("my.timer").description("a description of what this timer does").register(registry);

vertx.setPeriodic(1000, (l) => {
  timer.record(() => {
    // Running here some operation to monitor
  });
});
```

For more examples, documentation about the Micrometer registry and how
to create metrics, check [Micrometer
doc](http://micrometer.io/docs/concepts#_registry).

## Reusing an existing registry

It is possible to reuse an existing Micrometer registry (or
`CollectorRegistry` from the Prometheus Java client), and inject it into
the Vert.x metrics options:

``` js
import { Vertx } from "@vertx/core"
// This registry might be used to collect metrics other than Vert.x ones
let registry = new (Java.type("io.micrometer.prometheus.PrometheusMeterRegistry"))(Java.type("io.micrometer.prometheus.PrometheusConfig").DEFAULT);

// You could also reuse an existing registry from the Prometheus Java client:
let prometheusClientRegistry = new (Java.type("io.prometheus.client.CollectorRegistry"))();
registry = new (Java.type("io.micrometer.prometheus.PrometheusMeterRegistry"))(Java.type("io.micrometer.prometheus.PrometheusConfig").DEFAULT, prometheusClientRegistry, Java.type("io.micrometer.core.instrument.Clock").SYSTEM);

// It's reused in MicrometerMetricsOptions.
// Prometheus options configured here, such as "setPublishQuantiles(true)", will affect the whole registry.
let vertx = Vertx.vertx(new VertxOptions()
  .setMetricsOptions(new MicrometerMetricsOptions()
    .setPrometheusOptions(new VertxPrometheusOptions()
      .setEnabled(true)
      .setPublishQuantiles(true))
    .setMicrometerRegistry(registry)
    .setEnabled(true)));
```

## JVM or other instrumentations

Since plain access to Micrometer registries is provided, it is possible
to leverage the Micrometer API. For instance, to instrument the JVM:

``` js
let registry = Java.type("io.vertx.micrometer.backends.BackendRegistries").getDefaultNow();

new (Java.type("io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics"))().bindTo(registry);
new (Java.type("io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics"))().bindTo(registry);
new (Java.type("io.micrometer.core.instrument.binder.jvm.JvmGcMetrics"))().bindTo(registry);
new (Java.type("io.micrometer.core.instrument.binder.system.ProcessorMetrics"))().bindTo(registry);
new (Java.type("io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics"))().bindTo(registry);
```

*From [Micrometer documentation](http://micrometer.io/docs/ref/jvm).*

## Labels and matchers

Vert.x Micrometer Metrics defines a set of labels (aka tags or fields)
that are used to provide dimensionality to a metric. For instance,
metrics related to event bus messages have an *address* label, which
allows then to query timeseries for a specific event bus address, or
compare timeseries per address, or perform any kind of aggregation that
the query API allows.

While setting up metrics options, you can specify which labels you want
to enable or not:

``` js
import { Vertx } from "@vertx/core"
let vertx = Vertx.vertx(new VertxOptions()
  .setMetricsOptions(new MicrometerMetricsOptions()
    .setPrometheusOptions(new VertxPrometheusOptions()
      .setEnabled(true))
    .setLabels(Java.type("java.util.EnumSet").of(Label.REMOTE, Label.LOCAL, Label.HTTP_CODE, Label.HTTP_PATH))
    .setEnabled(true)));
```

The full list of labels is detailed here: `Label`.

> **Warning**
> 
> Enabling labels may result in a high cardinality in values, which can
> cause troubles on the metrics backend and affect performances. So it
> must be used with care. In general, it is fine to enable labels when
> the set of possible values is bounded.

For that reason, labels enabled by default are restricted to the ones
with known bounded values.

It is possible to interact with labels further than just
enabling/disabling. There are two ways for that:

### Using Matchers

`Match` objects can be used to filter or rename some label value by
matching it with either an exact string or a regular expression (the
former being more efficient).

Here is an example to restrict HTTP server metrics to those with label
*local=localhost:8080* only:

``` js
import { Vertx } from "@vertx/core"
let vertx = Vertx.vertx(new VertxOptions()
  .setMetricsOptions(new MicrometerMetricsOptions()
    .setPrometheusOptions(new VertxPrometheusOptions()
      .setEnabled(true))
    .setLabelMatchs([new Match()
      .setDomain("HTTP_SERVER")
      .setLabel("local")
      .setValue("localhost:8080")])
    .setEnabled(true)));
```

When an *alias* is specified in the Match, it will be used to rename
value instead of filtering.

Matchers are especially useful to control labelling through
configuration as they are set via `MicrometerMetricsOptions`.

### Using Micrometer’s MeterFilter

Micrometer’s [MeterFilter
API](http://micrometer.io/docs/concepts#_meter_filters) can be accessed
directly in order to define rules on labels. Compared to Matchers, it
offers more features in manipulating the labels, but cannot be defined
from configuration. So both have their advantages.

Here is an example to replace the actual `path` label of HTTP requests
with a generic form using regex:

``` js
let registry = Java.type("io.vertx.micrometer.backends.BackendRegistries").getDefaultNow();
let pattern = Java.type("java.util.regex.Pattern").compile("/foo/bar/.*");

registry.config().meterFilter(Java.type("io.micrometer.core.instrument.config.MeterFilter").replaceTagValues(Label.HTTP_PATH.toString(), (actualPath) => {
  let m = pattern.matcher(actualPath);
  if (m.matches()) {
    return "/foo/bar/:id"
  }
  return actualPath
}, ""));
```

> **Note**
> 
> Matchers use MeterFilters under the hood.

## Snapshots

A `MetricsService` can be created out of a `Measured` object in order to
take a snapshot of its related metrics and measurements. The snapshot is
returned as a `JsonObject`.

A well known *Measured* object is simply `Vertx`:

``` js
import { MetricsService } from "@vertx/micrometer-metrics"
let metricsService = MetricsService.create(vertx);
let metrics = metricsService.getMetricsSnapshot();
console.log(metrics);
```

Other components, such as an `EventBus` or a `HttpServer` are
measurable:

``` js
import { MetricsService } from "@vertx/micrometer-metrics"
let server = vertx.createHttpServer();
let metricsService = MetricsService.create(server);
let metrics = metricsService.getMetricsSnapshot();
console.log(metrics);
```

Finally it is possible to filter the returned metrics from their base
names:

``` js
import { MetricsService } from "@vertx/micrometer-metrics"
let metricsService = MetricsService.create(vertx);
// Client + server
let metrics = metricsService.getMetricsSnapshot("vertx.http");
console.log(metrics);
```

# Vert.x core tools metrics

This section lists all the metrics generated by monitoring the Vert.x
core tools.

> **Note**
> 
> The metric backends may have different conventions or rules for naming
> metrics. The names described below are given with underscore
> separators, but the actual names may vary depending on the backend
> used.

## Net Client

| Metric name                      | Labels                     | Type    | Description                                                |
| -------------------------------- | -------------------------- | ------- | ---------------------------------------------------------- |
| `vertx_net_client_connections`   | `local`, `remote`          | Gauge   | Number of connections to the remote host currently opened. |
| `vertx_net_client_bytesReceived` | `local`, `remote`          | Summary | Number of bytes received from the remote host.             |
| `vertx_net_client_bytesSent`     | `local`, `remote`          | Summary | Number of bytes sent to the remote host.                   |
| `vertx_net_client_errors`        | `local`, `remote`, `class` | Counter | Number of errors.                                          |

## HTTP Client

| Metric name                       | Labels                                      | Type    | Description                                                |
| --------------------------------- | ------------------------------------------- | ------- | ---------------------------------------------------------- |
| `vertx_http_client_connections`   | `local`, `remote`                           | Gauge   | Number of connections to the remote host currently opened. |
| `vertx_http_client_bytesReceived` | `local`, `remote`                           | Summary | Number of bytes received from the remote host.             |
| `vertx_http_client_bytesSent`     | `local`, `remote`                           | Summary | Number of bytes sent to the remote host.                   |
| `vertx_http_client_errors`        | `local`, `remote`, `class`                  | Counter | Number of errors.                                          |
| `vertx_http_client_requests`      | `local`, `remote`, `path`, `method`         | Gauge   | Number of requests waiting for a response.                 |
| `vertx_http_client_requestCount`  | `local`, `remote`, `path`, `method`         | Counter | Number of requests sent.                                   |
| `vertx_http_client_responseTime`  | `local`, `remote`, `path`, `method`, `code` | Timer   | Response time.                                             |
| `vertx_http_client_responseCount` | `local`, `remote`, `path`, `method`, `code` | Counter | Number of received responses.                              |
| `vertx_http_client_wsConnections` | `local`, `remote`                           | Gauge   | Number of websockets currently opened.                     |

## Net Server

| Metric name                      | Labels                     | Type    | Description                                     |
| -------------------------------- | -------------------------- | ------- | ----------------------------------------------- |
| `vertx_net_server_connections`   | `local`, `remote`          | Gauge   | Number of opened connections to the Net Server. |
| `vertx_net_server_bytesReceived` | `local`, `remote`          | Summary | Number of bytes received by the Net Server.     |
| `vertx_net_server_bytesSent`     | `local`, `remote`          | Summary | Number of bytes sent by the Net Server.         |
| `vertx_net_server_errors`        | `local`, `remote`, `class` | Counter | Number of errors.                               |

## HTTP Server

| Metric name                           | Labels                                      | Type    | Description                                      |
| ------------------------------------- | ------------------------------------------- | ------- | ------------------------------------------------ |
| `vertx_http_server_connections`       | `local`, `remote`                           | Gauge   | Number of opened connections to the HTTP Server. |
| `vertx_http_server_bytesReceived`     | `local`, `remote`                           | Summary | Number of bytes received by the HTTP Server.     |
| `vertx_http_server_bytesSent`         | `local`, `remote`                           | Summary | Number of bytes sent by the HTTP Server.         |
| `vertx_http_server_errors`            | `local`, `remote`, `class`                  | Counter | Number of errors.                                |
| `vertx_http_server_requests`          | `local`, `remote`, `path`, `method`         | Gauge   | Number of requests being processed.              |
| `vertx_http_server_requestCount`      | `local`, `remote`, `path`, `method`, `code` | Counter | Number of processed requests.                    |
| `vertx_http_server_requestResetCount` | `local`, `remote`, `path`, `method`         | Counter | Number of requests reset.                        |
| `vertx_http_server_processingTime`    | `local`, `remote`, `path`, `method`, `code` | Timer   | Request processing time.                         |
| `vertx_http_client_wsConnections`     | `local`, `remote`                           | Gauge   | Number of websockets currently opened.           |

## Datagram socket

| Metric name                    | Labels  | Type    | Description                                                              |
| ------------------------------ | ------- | ------- | ------------------------------------------------------------------------ |
| `vertx_datagram_bytesReceived` | `local` | Summary | Total number of bytes received on the `<host>:<port>` listening address. |
| `vertx_datagram_bytesSent`     | (none)  | Summary | Total number of bytes sent to the remote host.                           |
| `vertx_datagram_errors`        | `class` | Counter | Total number of errors.                                                  |

## Event Bus

| Metric name                     | Labels                          | Type    | Description                                                                                                                                         |
| ------------------------------- | ------------------------------- | ------- | --------------------------------------------------------------------------------------------------------------------------------------------------- |
| `vertx_eventbus_handlers`       | `address`                       | Gauge   | Number of event bus handlers in use.                                                                                                                |
| `vertx_eventbus_errors`         | `address`,`class`               | Counter | Number of errors.                                                                                                                                   |
| `vertx_eventbus_bytesWritten`   | `address`                       | Summary | Total number of bytes sent while sending messages to event bus cluster peers.                                                                       |
| `vertx_eventbus_bytesRead`      | `address`                       | Summary | Total number of bytes received while reading messages from event bus cluster peers.                                                                 |
| `vertx_eventbus_pending`        | `address`,`side` (local/remote) | Gauge   | Number of messages not processed yet. One message published will count for `N` pending if `N` handlers are registered to the corresponding address. |
| `vertx_eventbus_published`      | `address`,`side` (local/remote) | Counter | Number of messages published (publish / subscribe).                                                                                                 |
| `vertx_eventbus_discarded`      | `address`,`side` (local/remote) | Counter | Number of discarded messages (e.g. still pending messages while handler is unregistered, or overflowing messages).                                  |
| `vertx_eventbus_sent`           | `address`,`side` (local/remote) | Counter | Number of messages sent (point-to-point).                                                                                                           |
| `vertx_eventbus_received`       | `address`,`side` (local/remote) | Counter | Number of messages received.                                                                                                                        |
| `vertx_eventbus_delivered`      | `address`,`side` (local/remote) | Counter | Number of messages delivered to handlers.                                                                                                           |
| `vertx_eventbus_replyFailures`  | `address`,`failure`             | Counter | Number of message reply failures.                                                                                                                   |
| `vertx_eventbus_processingTime` | `address`                       | Timer   | Processing time for handlers listening to the `address`.                                                                                            |

# Vert.x pool metrics

This section lists all the metrics generated by monitoring Vert.x pools.

There are two types currently supported:

  - *worker* (see `WorkerExecutor`)

  - *datasource* (created with Vert.x JDBC client)

> **Note**
> 
> Vert.x creates two worker pools upfront, *worker-thread* and
> *internal-blocking*.

| Metric name              | Labels                  | Type    | Description                                                                                       |
| ------------------------ | ----------------------- | ------- | ------------------------------------------------------------------------------------------------- |
| `vertx_pool_queue_delay` | `pool_type`,`pool_name` | Timer   | Time waiting for a resource (queue time).                                                         |
| `vertx_pool_queue_size`  | `pool_type`,`pool_name` | Gauge   | Number of elements waiting for a resource.                                                        |
| `vertx_pool_usage`       | `pool_type`,`pool_name` | Timer   | Time using a resource (i.e. processing time for worker pools).                                    |
| `vertx_pool_inUse`       | `pool_type`,`pool_name` | Gauge   | Number of resources used.                                                                         |
| `vertx_pool_completed`   | `pool_type`,`pool_name` | Counter | Number of elements done with the resource (i.e. total number of tasks executed for worker pools). |
| `vertx_pool_ratio`       | `pool_type`,`pool_name` | Gauge   | Pool usage ratio, only present if maximum pool size could be determined.                          |

# Verticle metrics

| Metric name               | Labels | Type  | Description                            |
| ------------------------- | ------ | ----- | -------------------------------------- |
| `vertx_verticle_deployed` | `name` | Gauge | Number of verticle instances deployed. |
