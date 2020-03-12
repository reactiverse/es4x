# BitFieldGetCommand

|           |                 |             |
| --------- | --------------- | ----------- |
| Name      | Type            | Description |
| `@offset` | `Number (long)` | \-          |
| `@type`   | `String`        | \-          |

# BitFieldIncrbyCommand

|              |                 |             |
| ------------ | --------------- | ----------- |
| Name         | Type            | Description |
| `@increment` | `Number (long)` | \-          |
| `@offset`    | `Number (long)` | \-          |
| `@type`      | `String`        | \-          |

# BitFieldOptions

|           |                         |             |
| --------- | ----------------------- | ----------- |
| Name      | Type                    | Description |
| `@get`    | `BitFieldGetCommand`    | \-          |
| `@incrby` | `BitFieldIncrbyCommand` | \-          |
| `@set`    | `BitFieldSetCommand`    | \-          |

# BitFieldSetCommand

|           |                 |             |
| --------- | --------------- | ----------- |
| Name      | Type            | Description |
| `@offset` | `Number (long)` | \-          |
| `@type`   | `String`        | \-          |
| `@value`  | `Number (long)` | \-          |

# GeoMember

|              |                   |                                                            |
| ------------ | ----------------- | ---------------------------------------------------------- |
| Name         | Type              | Description                                                |
| `@latitude`  | `Number (Double)` | Set Latitude as per EPSG:900913 / EPSG:3785 / OSGEO:41001  |
| `@longitude` | `Number (Double)` | Set Longitude as per EPSG:900913 / EPSG:3785 / OSGEO:41001 |
| `@member`    | `String`          | Set the member name.                                       |

# GeoRadiusOptions

|              |                 |                                                |
| ------------ | --------------- | ---------------------------------------------- |
| Name         | Type            | Description                                    |
| `@count`     | `Number (Long)` | Set the radius options limit the result count. |
| `@withCoord` | `Boolean`       | Set the radius options to be coordinate based. |
| `@withDist`  | `Boolean`       | Set the radius options to be distance based.   |
| `@withHash`  | `Boolean`       | Set the radius options to be hash based.       |

# KillFilter

|           |           |                   |
| --------- | --------- | ----------------- |
| Name      | Type      | Description       |
| `@addr`   | `String`  | Set ADDR filter   |
| `@id`     | `String`  | Set ID filter     |
| `@skipme` | `Boolean` | Set SKIPME filter |
| `@type`   | `Type`    | Set TYPE filter   |

# LimitOptions

|           |                 |             |
| --------- | --------------- | ----------- |
| Name      | Type            | Description |
| `@count`  | `Number (Long)` | \-          |
| `@offset` | `Number (Long)` | \-          |

# MigrateOptions

|            |           |             |
| ---------- | --------- | ----------- |
| Name       | Type      | Description |
| `@copy`    | `Boolean` | \-          |
| `@replace` | `Boolean` | \-          |

# RangeLimitOptions

|               |                 |             |
| ------------- | --------------- | ----------- |
| Name          | Type            | Description |
| `@count`      | `Number (Long)` | \-          |
| `@offset`     | `Number (Long)` | \-          |
| `@withscores` | `Boolean`       | \-          |

# RedisOptions

Redis Client Configuration options.

|                       |                    |                                                                                                                                                                                                                           |
| --------------------- | ------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Name                  | Type               | Description                                                                                                                                                                                                               |
| `@masterName`         | `String`           | Set the master name (only considered in HA mode).                                                                                                                                                                         |
| `@maxNestedArrays`    | `Number (int)`     | Tune how much nested arrays are allowed on a redis response. This affects the parser performance.                                                                                                                         |
| `@maxWaitingHandlers` | `Number (int)`     | The client will always work on pipeline mode, this means that messages can start queueing. You can control how much backlog you're willing to accept. This methods sets how much handlers is the client willing to queue. |
| `@netClientOptions`   | `NetClientOptions` | Set the net client options to be used while connecting to the redis server. Use this to tune your connection.                                                                                                             |
| `@password`           | `String`           | Set the provided password to be used when establishing a connection to the server.                                                                                                                                        |
| `@role`               | `RedisRole`        | Set the role name (only considered in HA mode).                                                                                                                                                                           |
| `@select`             | `Number (Integer)` | Set the provided database to be selected when establishing a connection to the server.                                                                                                                                    |
| `@type`               | `RedisClientType`  | Set the desired client type to be created.                                                                                                                                                                                |
| `@useSlave`           | `RedisSlaves`      | Set whether or not to use slave nodes (only considered in Cluster mode).                                                                                                                                                  |

# RedisOptions

This object controls the connection setting to the Redis Server. There
is no need to specify most of the settings since it has built the
following sensible defaults:

\* \`encoding\`: \`UTF-8\` \* \`host\`: \`localhost\` \* \`port\`: 6379
\* \`tcpKeepAlive\`: true \* \`tcpNoDelay\`: true \* \`binary\`: false

However there are two extra properties that have no defaults since they
are optional:

\* \`auth\` \* \`select\`

The usage of this two extra properties is to setup required
authentication and optionally the selection of the active database at
connection time. If you define this extra properties on every connection
to Redis server this client will perform the authentication handshake
and database selection, however if you don't do this and call link
yourself in case of connection failure the client will not be able to
perform the correct authentication handshake.

|                                    |                        |                                                                                                               |
| ---------------------------------- | ---------------------- | ------------------------------------------------------------------------------------------------------------- |
| Name                               | Type                   | Description                                                                                                   |
| `@address`                         | `String`               | Set the eventbus address prefix for \`PUB/SUB\`. \* @param address address prefix.                            |
| `@auth`                            | `String`               | Set the password for authentication at connection time.                                                       |
| `@binary`                          | `Boolean`              | Set the messages to/from redis as binary, default \`false\`. \* @param binary use binary messages             |
| `@connectTimeout`                  | `Number (int)`         | \-                                                                                                            |
| `@crlPaths`                        | `Array of String`      | \-                                                                                                            |
| `@crlValues`                       | `Array of Buffer`      | \-                                                                                                            |
| `@domainSocket`                    | `Boolean`              | Set the domain socket enabled option, default \`false\`.                                                      |
| `@domainSocketAddress`             | `String`               | Set the domain socket address where the Redis server is listening.                                            |
| `@enabledCipherSuites`             | `Array of String`      | \-                                                                                                            |
| `@enabledSecureTransportProtocols` | `Array of String`      | \-                                                                                                            |
| `@encoding`                        | `String`               | Set the user defined character encoding, e.g.: \`iso-8859-1\`. \* @param encoding the user character encoding |
| `@host`                            | `String`               | Set the host name where the Redis server is listening. \* @param host host name                               |
| `@hostnameVerificationAlgorithm`   | `String`               | \-                                                                                                            |
| `@idleTimeout`                     | `Number (int)`         | \-                                                                                                            |
| `@idleTimeoutUnit`                 | `TimeUnit`             | \-                                                                                                            |
| `@jdkSslEngineOptions`             | `JdkSSLEngineOptions`  | \-                                                                                                            |
| `@keyStoreOptions`                 | `JksOptions`           | \-                                                                                                            |
| `@localAddress`                    | `String`               | \-                                                                                                            |
| `@logActivity`                     | `Boolean`              | \-                                                                                                            |
| `@masterName`                      | `String`               | Set name of Redis master (used with Sentinel).                                                                |
| `@metricsName`                     | `String`               | \-                                                                                                            |
| `@openSslEngineOptions`            | `OpenSSLEngineOptions` | \-                                                                                                            |
| `@pemKeyCertOptions`               | `PemKeyCertOptions`    | \-                                                                                                            |
| `@pemTrustOptions`                 | `PemTrustOptions`      | \-                                                                                                            |
| `@pfxKeyCertOptions`               | `PfxOptions`           | \-                                                                                                            |
| `@pfxTrustOptions`                 | `PfxOptions`           | \-                                                                                                            |
| `@port`                            | `Number (int)`         | Set the tcp port where the Redis server is listening.                                                         |
| `@proxyOptions`                    | `ProxyOptions`         | \-                                                                                                            |
| `@receiveBufferSize`               | `Number (int)`         | \-                                                                                                            |
| `@reconnectAttempts`               | `Number (int)`         | \-                                                                                                            |
| `@reconnectInterval`               | `Number (long)`        | \-                                                                                                            |
| `@reuseAddress`                    | `Boolean`              | \-                                                                                                            |
| `@reusePort`                       | `Boolean`              | \-                                                                                                            |
| `@select`                          | `Number (Integer)`     | Set the database to select at connection time. \* @param select database id                                   |
| `@sendBufferSize`                  | `Number (int)`         | \-                                                                                                            |
| `@sentinels`                       | `Array of String`      | Set the list of Sentinels.                                                                                    |
| `@soLinger`                        | `Number (int)`         | \-                                                                                                            |
| `@ssl`                             | `Boolean`              | \-                                                                                                            |
| `@sslHandshakeTimeout`             | `Number (long)`        | \-                                                                                                            |
| `@sslHandshakeTimeoutUnit`         | `TimeUnit`             | \-                                                                                                            |
| `@tcpCork`                         | `Boolean`              | \-                                                                                                            |
| `@tcpFastOpen`                     | `Boolean`              | \-                                                                                                            |
| `@tcpKeepAlive`                    | `Boolean`              | \-                                                                                                            |
| `@tcpNoDelay`                      | `Boolean`              | \-                                                                                                            |
| `@tcpQuickAck`                     | `Boolean`              | \-                                                                                                            |
| `@trafficClass`                    | `Number (int)`         | \-                                                                                                            |
| `@trustAll`                        | `Boolean`              | \-                                                                                                            |
| `@trustStoreOptions`               | `JksOptions`           | \-                                                                                                            |
| `@useAlpn`                         | `Boolean`              | \-                                                                                                            |
| `@usePooledBuffers`                | `Boolean`              | \-                                                                                                            |

# ScanOptions

|          |                |             |
| -------- | -------------- | ----------- |
| Name     | Type           | Description |
| `@count` | `Number (int)` | \-          |
| `@match` | `String`       | \-          |

# SetOptions

|       |                 |             |
| ----- | --------------- | ----------- |
| Name  | Type            | Description |
| `@ex` | `Number (long)` | \-          |
| `@nx` | `Boolean`       | \-          |
| `@px` | `Number (long)` | \-          |
| `@xx` | `Boolean`       | \-          |

# SortOptions

|               |                   |             |
| ------------- | ----------------- | ----------- |
| Name          | Type              | Description |
| `@alpha`      | `Boolean`         | \-          |
| `@by`         | `String`          | \-          |
| `@descending` | `Boolean`         | \-          |
| `@gets`       | `Array of String` | \-          |
| `@store`      | `String`          | \-          |
