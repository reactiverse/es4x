# WebClientOptions

|                                               |                        |                                                                                  |
| --------------------------------------------- | ---------------------- | -------------------------------------------------------------------------------- |
| Name                                          | Type                   | Description                                                                      |
| `@alpnVersions`                               | `Array of HttpVersion` | \-                                                                               |
| `@connectTimeout`                             | `Number (int)`         | \-                                                                               |
| `@crlPaths`                                   | `Array of String`      | \-                                                                               |
| `@crlValues`                                  | `Array of Buffer`      | \-                                                                               |
| `@decoderInitialBufferSize`                   | `Number (int)`         | \-                                                                               |
| `@defaultHost`                                | `String`               | \-                                                                               |
| `@defaultPort`                                | `Number (int)`         | \-                                                                               |
| `@enabledCipherSuites`                        | `Array of String`      | \-                                                                               |
| `@enabledSecureTransportProtocols`            | `Array of String`      | \-                                                                               |
| `@followRedirects`                            | `Boolean`              | Configure the default behavior of the client to follow HTTP `30x` redirections.  |
| `@forceSni`                                   | `Boolean`              | \-                                                                               |
| `@http2ClearTextUpgrade`                      | `Boolean`              | \-                                                                               |
| `@http2ConnectionWindowSize`                  | `Number (int)`         | \-                                                                               |
| `@http2KeepAliveTimeout`                      | `Number (int)`         | \-                                                                               |
| `@http2MaxPoolSize`                           | `Number (int)`         | \-                                                                               |
| `@http2MultiplexingLimit`                     | `Number (int)`         | \-                                                                               |
| `@idleTimeout`                                | `Number (int)`         | \-                                                                               |
| `@idleTimeoutUnit`                            | `TimeUnit`             | \-                                                                               |
| `@initialSettings`                            | `Http2Settings`        | \-                                                                               |
| `@jdkSslEngineOptions`                        | `JdkSSLEngineOptions`  | \-                                                                               |
| `@keepAlive`                                  | `Boolean`              | \-                                                                               |
| `@keepAliveTimeout`                           | `Number (int)`         | \-                                                                               |
| `@keyStoreOptions`                            | `JksOptions`           | \-                                                                               |
| `@localAddress`                               | `String`               | \-                                                                               |
| `@logActivity`                                | `Boolean`              | \-                                                                               |
| `@maxChunkSize`                               | `Number (int)`         | \-                                                                               |
| `@maxHeaderSize`                              | `Number (int)`         | \-                                                                               |
| `@maxInitialLineLength`                       | `Number (int)`         | \-                                                                               |
| `@maxPoolSize`                                | `Number (int)`         | \-                                                                               |
| `@maxRedirects`                               | `Number (int)`         | \-                                                                               |
| `@maxWaitQueueSize`                           | `Number (int)`         | \-                                                                               |
| `@maxWebSocketFrameSize`                      | `Number (int)`         | \-                                                                               |
| `@maxWebSocketMessageSize`                    | `Number (int)`         | \-                                                                               |
| `@maxWebsocketFrameSize`                      | `Number (int)`         | \-                                                                               |
| `@maxWebsocketMessageSize`                    | `Number (int)`         | \-                                                                               |
| `@metricsName`                                | `String`               | \-                                                                               |
| `@openSslEngineOptions`                       | `OpenSSLEngineOptions` | \-                                                                               |
| `@pemKeyCertOptions`                          | `PemKeyCertOptions`    | \-                                                                               |
| `@pemTrustOptions`                            | `PemTrustOptions`      | \-                                                                               |
| `@pfxKeyCertOptions`                          | `PfxOptions`           | \-                                                                               |
| `@pfxTrustOptions`                            | `PfxOptions`           | \-                                                                               |
| `@pipelining`                                 | `Boolean`              | \-                                                                               |
| `@pipeliningLimit`                            | `Number (int)`         | \-                                                                               |
| `@poolCleanerPeriod`                          | `Number (int)`         | \-                                                                               |
| `@protocolVersion`                            | `HttpVersion`          | \-                                                                               |
| `@proxyOptions`                               | `ProxyOptions`         | \-                                                                               |
| `@receiveBufferSize`                          | `Number (int)`         | \-                                                                               |
| `@reuseAddress`                               | `Boolean`              | \-                                                                               |
| `@reusePort`                                  | `Boolean`              | \-                                                                               |
| `@sendBufferSize`                             | `Number (int)`         | \-                                                                               |
| `@sendUnmaskedFrames`                         | `Boolean`              | \-                                                                               |
| `@soLinger`                                   | `Number (int)`         | \-                                                                               |
| `@ssl`                                        | `Boolean`              | \-                                                                               |
| `@sslHandshakeTimeout`                        | `Number (long)`        | \-                                                                               |
| `@sslHandshakeTimeoutUnit`                    | `TimeUnit`             | \-                                                                               |
| `@tcpCork`                                    | `Boolean`              | \-                                                                               |
| `@tcpFastOpen`                                | `Boolean`              | \-                                                                               |
| `@tcpKeepAlive`                               | `Boolean`              | \-                                                                               |
| `@tcpNoDelay`                                 | `Boolean`              | \-                                                                               |
| `@tcpQuickAck`                                | `Boolean`              | \-                                                                               |
| `@trafficClass`                               | `Number (int)`         | \-                                                                               |
| `@trustAll`                                   | `Boolean`              | \-                                                                               |
| `@trustStoreOptions`                          | `JksOptions`           | \-                                                                               |
| `@tryUseCompression`                          | `Boolean`              | \-                                                                               |
| `@tryUsePerFrameWebSocketCompression`         | `Boolean`              | \-                                                                               |
| `@tryUsePerFrameWebsocketCompression`         | `Boolean`              | \-                                                                               |
| `@tryUsePerMessageWebSocketCompression`       | `Boolean`              | \-                                                                               |
| `@tryUsePerMessageWebsocketCompression`       | `Boolean`              | \-                                                                               |
| `@tryWebSocketDeflateFrameCompression`        | `Boolean`              | \-                                                                               |
| `@tryWebsocketDeflateFrameCompression`        | `Boolean`              | \-                                                                               |
| `@useAlpn`                                    | `Boolean`              | \-                                                                               |
| `@usePooledBuffers`                           | `Boolean`              | \-                                                                               |
| `@userAgent`                                  | `String`               | Sets the Web Client user agent header. Defaults to Vert.x-WebClient/\<version\>. |
| `@userAgentEnabled`                           | `Boolean`              | Sets whether the Web Client should send a user agent header. Defaults to true.   |
| `@verifyHost`                                 | `Boolean`              | \-                                                                               |
| `@webSocketCompressionAllowClientNoContext`   | `Boolean`              | \-                                                                               |
| `@webSocketCompressionLevel`                  | `Number (int)`         | \-                                                                               |
| `@webSocketCompressionRequestServerNoContext` | `Boolean`              | \-                                                                               |
| `@websocketCompressionAllowClientNoContext`   | `Boolean`              | \-                                                                               |
| `@websocketCompressionLevel`                  | `Number (int)`         | \-                                                                               |
| `@websocketCompressionRequestServerNoContext` | `Boolean`              | \-                                                                               |
