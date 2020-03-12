# Box

Rectangular box data type in Postgres represented by pairs of links that
are opposite corners of the box.

|                     |         |             |
| ------------------- | ------- | ----------- |
| Name                | Type    | Description |
| `@lowerLeftCorner`  | `Point` | \-          |
| `@upperRightCorner` | `Point` | \-          |

# Circle

Circle data type in Postgres represented by a center link and radius.

|                |                   |             |
| -------------- | ----------------- | ----------- |
| Name           | Type              | Description |
| `@centerPoint` | `Point`           | \-          |
| `@radius`      | `Number (double)` | \-          |

# Interval

Postgres Interval is date and time based such as 120 years 3 months 332
days 20 hours 20 minutes 20.999999 seconds

|                 |                |             |
| --------------- | -------------- | ----------- |
| Name            | Type           | Description |
| `@days`         | `Number (int)` | \-          |
| `@hours`        | `Number (int)` | \-          |
| `@microseconds` | `Number (int)` | \-          |
| `@minutes`      | `Number (int)` | \-          |
| `@months`       | `Number (int)` | \-          |
| `@seconds`      | `Number (int)` | \-          |
| `@years`        | `Number (int)` | \-          |

# Line

Line data type in Postgres represented by the linear equation Ax + By +
C = 0, where A and B are not both zero.

|      |                   |             |
| ---- | ----------------- | ----------- |
| Name | Type              | Description |
| `@a` | `Number (double)` | \-          |
| `@b` | `Number (double)` | \-          |
| `@c` | `Number (double)` | \-          |

# LineSegment

Finite line segment data type in Postgres represented by pairs of links
that are the endpoints of the segment.

|       |         |             |
| ----- | ------- | ----------- |
| Name  | Type    | Description |
| `@p1` | `Point` | \-          |
| `@p2` | `Point` | \-          |

# Path

Path data type in Postgres represented by lists of connected points.
Paths can be open, where the first and last points in the list are
considered not connected, or closed, where the first and last points are
considered connected.

|           |                  |             |
| --------- | ---------------- | ----------- |
| Name      | Type             | Description |
| `@open`   | `Boolean`        | \-          |
| `@points` | `Array of Point` | \-          |

# PgConnectOptions

|                                    |                        |                                                                                                    |
| ---------------------------------- | ---------------------- | -------------------------------------------------------------------------------------------------- |
| Name                               | Type                   | Description                                                                                        |
| `@cachePreparedStatements`         | `Boolean`              | \-                                                                                                 |
| `@connectTimeout`                  | `Number (int)`         | \-                                                                                                 |
| `@crlPaths`                        | `Array of String`      | \-                                                                                                 |
| `@crlValues`                       | `Array of Buffer`      | \-                                                                                                 |
| `@database`                        | `String`               | \-                                                                                                 |
| `@enabledCipherSuites`             | `Array of String`      | \-                                                                                                 |
| `@enabledSecureTransportProtocols` | `Array of String`      | \-                                                                                                 |
| `@host`                            | `String`               | \-                                                                                                 |
| `@hostnameVerificationAlgorithm`   | `String`               | \-                                                                                                 |
| `@idleTimeout`                     | `Number (int)`         | \-                                                                                                 |
| `@idleTimeoutUnit`                 | `TimeUnit`             | \-                                                                                                 |
| `@jdkSslEngineOptions`             | `JdkSSLEngineOptions`  | \-                                                                                                 |
| `@keyStoreOptions`                 | `JksOptions`           | \-                                                                                                 |
| `@localAddress`                    | `String`               | \-                                                                                                 |
| `@logActivity`                     | `Boolean`              | \-                                                                                                 |
| `@metricsName`                     | `String`               | \-                                                                                                 |
| `@openSslEngineOptions`            | `OpenSSLEngineOptions` | \-                                                                                                 |
| `@password`                        | `String`               | \-                                                                                                 |
| `@pemKeyCertOptions`               | `PemKeyCertOptions`    | \-                                                                                                 |
| `@pemTrustOptions`                 | `PemTrustOptions`      | \-                                                                                                 |
| `@pfxKeyCertOptions`               | `PfxOptions`           | \-                                                                                                 |
| `@pfxTrustOptions`                 | `PfxOptions`           | \-                                                                                                 |
| `@pipeliningLimit`                 | `Number (int)`         | \-                                                                                                 |
| `@port`                            | `Number (int)`         | \-                                                                                                 |
| `@preparedStatementCacheMaxSize`   | `Number (int)`         | \-                                                                                                 |
| `@preparedStatementCacheSqlLimit`  | `Number (int)`         | \-                                                                                                 |
| `@properties`                      | `String`               | \-                                                                                                 |
| `@proxyOptions`                    | `ProxyOptions`         | \-                                                                                                 |
| `@receiveBufferSize`               | `Number (int)`         | \-                                                                                                 |
| `@reconnectAttempts`               | `Number (int)`         | \-                                                                                                 |
| `@reconnectInterval`               | `Number (long)`        | \-                                                                                                 |
| `@reuseAddress`                    | `Boolean`              | \-                                                                                                 |
| `@reusePort`                       | `Boolean`              | \-                                                                                                 |
| `@sendBufferSize`                  | `Number (int)`         | \-                                                                                                 |
| `@soLinger`                        | `Number (int)`         | \-                                                                                                 |
| `@ssl`                             | `Boolean`              | \-                                                                                                 |
| `@sslHandshakeTimeout`             | `Number (long)`        | \-                                                                                                 |
| `@sslHandshakeTimeoutUnit`         | `TimeUnit`             | \-                                                                                                 |
| `@sslMode`                         | `SslMode`              | Set link for the client, this option can be used to provide different levels of secure protection. |
| `@tcpCork`                         | `Boolean`              | \-                                                                                                 |
| `@tcpFastOpen`                     | `Boolean`              | \-                                                                                                 |
| `@tcpKeepAlive`                    | `Boolean`              | \-                                                                                                 |
| `@tcpNoDelay`                      | `Boolean`              | \-                                                                                                 |
| `@tcpQuickAck`                     | `Boolean`              | \-                                                                                                 |
| `@trafficClass`                    | `Number (int)`         | \-                                                                                                 |
| `@trustAll`                        | `Boolean`              | \-                                                                                                 |
| `@trustStoreOptions`               | `JksOptions`           | \-                                                                                                 |
| `@useAlpn`                         | `Boolean`              | \-                                                                                                 |
| `@usePooledBuffers`                | `Boolean`              | \-                                                                                                 |
| `@user`                            | `String`               | \-                                                                                                 |
| `@usingDomainSocket`               | `Boolean`              | \-                                                                                                 |

# PgNotification

A notification emited by Postgres.

|              |                |                        |
| ------------ | -------------- | ---------------------- |
| Name         | Type           | Description            |
| `@channel`   | `String`       | Set the channel value. |
| `@payload`   | `String`       | Set the payload value. |
| `@processId` | `Number (int)` | Set the process id.    |

# Point

A Postgresql point.

|      |                   |             |
| ---- | ----------------- | ----------- |
| Name | Type              | Description |
| `@x` | `Number (double)` | \-          |
| `@y` | `Number (double)` | \-          |

# Polygon

Polygon data type in Postgres represented by lists of points (the
vertexes of the polygon). Polygons are very similar to closed paths, but
are stored differently and have their own set of support routines.

|           |                  |             |
| --------- | ---------------- | ----------- |
| Name      | Type             | Description |
| `@points` | `Array of Point` | \-          |
