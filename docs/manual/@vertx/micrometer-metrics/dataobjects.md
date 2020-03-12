# Match

A match for a value.

|           |                 |                                                                             |
| --------- | --------------- | --------------------------------------------------------------------------- |
| Name      | Type            | Description                                                                 |
| `@alias`  | `String`        | Set an alias that would replace the label value when it matches.            |
| `@domain` | `MetricsDomain` | Set the label domain, restricting this rule to a single domain.             |
| `@label`  | `String`        | Set the label name. The match will apply to the values related to this key. |
| `@type`   | `MatchType`     | Set the type of matching to apply.                                          |
| `@value`  | `String`        | Set the matched value.                                                      |

# MicrometerMetricsOptions

Vert.x micrometer configuration.

It is required to set either

influxDbOptions

,

prometheusOptions

or

jmxMetricsOptions

(or, programmatically,

micrometerRegistry

) in order to actually report metrics.

|                              |                          |                                                                                                                                                                                                                                                                                                          |
| ---------------------------- | ------------------------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Name                         | Type                     | Description                                                                                                                                                                                                                                                                                              |
| `@disabledMetricsCategories` | `Array of MetricsDomain` | Sets metrics types that are disabled.                                                                                                                                                                                                                                                                    |
| `@enabled`                   | `Boolean`                | Set whether metrics will be enabled on the Vert.x instance. Metrics are not enabled by default.                                                                                                                                                                                                          |
| `@influxDbOptions`           | `VertxInfluxDbOptions`   | Set InfluxDB options. Setting a registry backend option is mandatory in order to effectively report metrics.                                                                                                                                                                                             |
| `@jmxMetricsOptions`         | `VertxJmxMetricsOptions` | Set JMX metrics options. Setting a registry backend option is mandatory in order to effectively report metrics.                                                                                                                                                                                          |
| `@jvmMetricsEnabled`         | `Boolean`                | Whether JVM metrics should be collected. Defaults to `false`.                                                                                                                                                                                                                                            |
| `@labelMatches`              | `Array of Match`         | Set a list of rules for label matching.                                                                                                                                                                                                                                                                  |
| `@labelMatchs`               | `Array of Match`         | Add a rule for label matching.                                                                                                                                                                                                                                                                           |
| `@labels`                    | `Array of Label`         | Sets enabled labels. These labels can be fine-tuned later on using Micrometer's Meter filters (see http://micrometer.io/docs/concepts\#\_meter\_filters)                                                                                                                                                 |
| `@prometheusOptions`         | `VertxPrometheusOptions` | Set Prometheus options. Setting a registry backend option is mandatory in order to effectively report metrics.                                                                                                                                                                                           |
| `@registryName`              | `String`                 | Set a name for the metrics registry, so that a new registry will be created and associated with this name. If `registryName` is not provided (or null), a default registry will be used. If the same name is given to several Vert.x instances (within the same JVM), they will share the same registry. |

# VertxInfluxDbOptions

Vert.x InfluxDb micrometer configuration.

|                    |                |                                                                                                                                                   |
| ------------------ | -------------- | ------------------------------------------------------------------------------------------------------------------------------------------------- |
| Name               | Type           | Description                                                                                                                                       |
| `@batchSize`       | `Number (int)` | Maximum number of measurements sent per request to the InfluxDB server. When the maximum is reached, several requests are made. Default is 10000. |
| `@compressed`      | `Boolean`      | Activate or deactivate GZIP compression. It is activated by default.                                                                              |
| `@connectTimeout`  | `Number (int)` | Connection timeout for InfluxDB server connections, in seconds. Default is 1 second.                                                              |
| `@db`              | `String`       | Database name used to store metrics. Default is "default".                                                                                        |
| `@enabled`         | `Boolean`      | Set true to enable InfluxDB reporting                                                                                                             |
| `@numThreads`      | `Number (int)` | Number of threads to use by the push scheduler. Default is 2.                                                                                     |
| `@password`        | `String`       | Password used for authenticated connections                                                                                                       |
| `@readTimeout`     | `Number (int)` | Read timeout for InfluxDB server connections, in seconds. Default is 10 seconds.                                                                  |
| `@retentionPolicy` | `String`       | InfluxDB retention policy                                                                                                                         |
| `@step`            | `Number (int)` | Push interval steps, in seconds. Default is 10 seconds.                                                                                           |
| `@uri`             | `String`       | URI of the InfluxDB server. Example: http://influx:8086.                                                                                          |
| `@userName`        | `String`       | Username used for authenticated connections                                                                                                       |

# VertxJmxMetricsOptions

Options for Prometheus metrics backend.

|            |                |                                                         |
| ---------- | -------------- | ------------------------------------------------------- |
| Name       | Type           | Description                                             |
| `@domain`  | `String`       | Set the JMX domain under which to publish metrics       |
| `@enabled` | `Boolean`      | Set true to enable Prometheus reporting                 |
| `@step`    | `Number (int)` | Push interval steps, in seconds. Default is 10 seconds. |

# VertxPrometheusOptions

Options for Prometheus metrics backend.

|                           |                     |                                                                                                                                                                       |
| ------------------------- | ------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Name                      | Type                | Description                                                                                                                                                           |
| `@embeddedServerEndpoint` | `String`            | Set metrics endpoint. Use conjointly with the embedded server options. Defaults to /metrics.                                                                          |
| `@embeddedServerOptions`  | `HttpServerOptions` | HTTP server options for the embedded server                                                                                                                           |
| `@enabled`                | `Boolean`           | Set true to enable Prometheus reporting                                                                                                                               |
| `@publishQuantiles`       | `Boolean`           | Set true to publish histogram stats, necessary to compute quantiles. Note that it generates many new timeseries for stats, which is why it is deactivated by default. |
| `@startEmbeddedServer`    | `Boolean`           | When true, an embedded server will init to expose metrics with Prometheus format.                                                                                     |
