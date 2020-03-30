# PoolOptions

The options for configuring a connection pool.

|                     |                |                                                                                                                                                                                                      |
| ------------------- | -------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Name                | Type           | Description                                                                                                                                                                                          |
| `@maxSize`          | `Number (int)` | Set the maximum pool size                                                                                                                                                                            |
| `@maxWaitQueueSize` | `Number (int)` | Set the maximum connection request allowed in the wait queue, any requests beyond the max size will result in an failure. If the value is set to a negative number then the queue will be unbounded. |

# SqlConnectOptions

Connect options for configuring link or link.

|                                    |                        |                                                                                         |
| ---------------------------------- | ---------------------- | --------------------------------------------------------------------------------------- |
| Name                               | Type                   | Description                                                                             |
| `@cachePreparedStatements`         | `Boolean`              | Set whether prepared statements cache should be enabled.                                |
| `@connectTimeout`                  | `Number (int)`         | \-                                                                                      |
| `@crlPaths`                        | `Array of String`      | \-                                                                                      |
| `@crlValues`                       | `Array of Buffer`      | \-                                                                                      |
| `@database`                        | `String`               | Specify the default database for the connection.                                        |
| `@enabledCipherSuites`             | `Array of String`      | \-                                                                                      |
| `@enabledSecureTransportProtocols` | `Array of String`      | \-                                                                                      |
| `@host`                            | `String`               | Specify the host for connecting to the server.                                          |
| `@hostnameVerificationAlgorithm`   | `String`               | \-                                                                                      |
| `@idleTimeout`                     | `Number (int)`         | \-                                                                                      |
| `@idleTimeoutUnit`                 | `TimeUnit`             | \-                                                                                      |
| `@jdkSslEngineOptions`             | `JdkSSLEngineOptions`  | \-                                                                                      |
| `@keyStoreOptions`                 | `JksOptions`           | \-                                                                                      |
| `@localAddress`                    | `String`               | \-                                                                                      |
| `@logActivity`                     | `Boolean`              | \-                                                                                      |
| `@metricsName`                     | `String`               | \-                                                                                      |
| `@openSslEngineOptions`            | `OpenSSLEngineOptions` | \-                                                                                      |
| `@password`                        | `String`               | Specify the user password to be used for the authentication.                            |
| `@pemKeyCertOptions`               | `PemKeyCertOptions`    | \-                                                                                      |
| `@pemTrustOptions`                 | `PemTrustOptions`      | \-                                                                                      |
| `@pfxKeyCertOptions`               | `PfxOptions`           | \-                                                                                      |
| `@pfxTrustOptions`                 | `PfxOptions`           | \-                                                                                      |
| `@port`                            | `Number (int)`         | Specify the port for connecting to the server.                                          |
| `@preparedStatementCacheMaxSize`   | `Number (int)`         | Set the maximum number of prepared statements that the connection will cache.           |
| `@preparedStatementCacheSqlLimit`  | `Number (int)`         | Set the maximum length of prepared statement SQL string that the connection will cache. |
| `@properties`                      | `String`               | Set properties for this client, which will be sent to server at the connection start.   |
| `@proxyOptions`                    | `ProxyOptions`         | \-                                                                                      |
| `@receiveBufferSize`               | `Number (int)`         | \-                                                                                      |
| `@reconnectAttempts`               | `Number (int)`         | \-                                                                                      |
| `@reconnectInterval`               | `Number (long)`        | \-                                                                                      |
| `@reuseAddress`                    | `Boolean`              | \-                                                                                      |
| `@reusePort`                       | `Boolean`              | \-                                                                                      |
| `@sendBufferSize`                  | `Number (int)`         | \-                                                                                      |
| `@soLinger`                        | `Number (int)`         | \-                                                                                      |
| `@ssl`                             | `Boolean`              | \-                                                                                      |
| `@sslHandshakeTimeout`             | `Number (long)`        | \-                                                                                      |
| `@sslHandshakeTimeoutUnit`         | `TimeUnit`             | \-                                                                                      |
| `@tcpCork`                         | `Boolean`              | \-                                                                                      |
| `@tcpFastOpen`                     | `Boolean`              | \-                                                                                      |
| `@tcpKeepAlive`                    | `Boolean`              | \-                                                                                      |
| `@tcpNoDelay`                      | `Boolean`              | \-                                                                                      |
| `@tcpQuickAck`                     | `Boolean`              | \-                                                                                      |
| `@trafficClass`                    | `Number (int)`         | \-                                                                                      |
| `@trustAll`                        | `Boolean`              | \-                                                                                      |
| `@trustStoreOptions`               | `JksOptions`           | \-                                                                                      |
| `@useAlpn`                         | `Boolean`              | \-                                                                                      |
| `@usePooledBuffers`                | `Boolean`              | \-                                                                                      |
| `@user`                            | `String`               | Specify the user account to be used for the authentication.                             |
