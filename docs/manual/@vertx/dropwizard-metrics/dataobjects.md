# DropwizardMetricsOptions

Vert.x Dropwizard metrics configuration.

|                                 |                  |                                                                                                                                                                                                                   |
| ------------------------------- | ---------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Name                            | Type             | Description                                                                                                                                                                                                       |
| `@baseName`                     | `String`         | Set a custom baseName for metrics.                                                                                                                                                                                |
| `@configPath`                   | `String`         | Set the path for a config file that contains options in JSON format, to be used to create a new options object. The file will be looked for on the file system first and then on the classpath if it's not found. |
| `@enabled`                      | `Boolean`        | \-                                                                                                                                                                                                                |
| `@jmxDomain`                    | `String`         | Set the JMX domain to use when JMX metrics are enabled.                                                                                                                                                           |
| `@jmxEnabled`                   | `Boolean`        | Set whether JMX will be enabled on the Vert.x instance.                                                                                                                                                           |
| `@monitoredEventBusHandlers`    | `Array of Match` | Add a monitored event bus handler.                                                                                                                                                                                |
| `@monitoredHttpClientEndpoint`  | `Array of Match` |                                                                                                                                                                                                                   |
| `@monitoredHttpClientEndpoints` | `Array of Match` | Add an monitored http client endpoint.                                                                                                                                                                            |
| `@monitoredHttpClientUris`      | `Array of Match` | Add an monitored http client uri.                                                                                                                                                                                 |
| `@monitoredHttpServerUris`      | `Array of Match` | Add an monitored http server uri.                                                                                                                                                                                 |
| `@registryName`                 | `String`         | Set the name used for registering the metrics in the Dropwizard shared registry.                                                                                                                                  |

# Match

A match for a value.

|          |             |                                                                                                                  |
| -------- | ----------- | ---------------------------------------------------------------------------------------------------------------- |
| Name     | Type        | Description                                                                                                      |
| `@alias` | `String`    | Set the alias the human readable name that will be used as a part of registry entry name when the value matches. |
| `@type`  | `MatchType` | Set the type of matching to apply.                                                                               |
| `@value` | `String`    | Set the matched value.                                                                                           |
