# OAuth2ClientOptions

Options describing how an OAuth2 will make connections.

|                                               |                             |                                                                       |
| --------------------------------------------- | --------------------------- | --------------------------------------------------------------------- |
| Name                                          | Type                        | Description                                                           |
| `@alpnVersions`                               | `Array of HttpVersion`      | \-                                                                    |
| `@authorizationPath`                          | `String`                    | Get the Oauth2 authorization resource path. e.g.: /oauth/authorize    |
| `@clientID`                                   | `String`                    | Set the provider client id                                            |
| `@clientSecret`                               | `String`                    | Set the provider client secret                                        |
| `@clientSecretParameterName`                  | `String`                    | Override the HTTP form field name for client secret                   |
| `@connectTimeout`                             | `Number (int)`              | \-                                                                    |
| `@crlPaths`                                   | `Array of String`           | \-                                                                    |
| `@crlValues`                                  | `Array of Buffer`           | \-                                                                    |
| `@decoderInitialBufferSize`                   | `Number (int)`              | \-                                                                    |
| `@defaultHost`                                | `String`                    | \-                                                                    |
| `@defaultPort`                                | `Number (int)`              | \-                                                                    |
| `@enabledCipherSuites`                        | `Array of String`           | \-                                                                    |
| `@enabledSecureTransportProtocols`            | `Array of String`           | \-                                                                    |
| `@extraParameters`                            | `Json object`               | Set extra parameters to be sent to the provider on each request       |
| `@flow`                                       | `OAuth2FlowType`            | \-                                                                    |
| `@forceSni`                                   | `Boolean`                   | \-                                                                    |
| `@headers`                                    | `Json object`               | Set custom headers to be sent with every request to the provider      |
| `@http2ClearTextUpgrade`                      | `Boolean`                   | \-                                                                    |
| `@http2ConnectionWindowSize`                  | `Number (int)`              | \-                                                                    |
| `@http2KeepAliveTimeout`                      | `Number (int)`              | \-                                                                    |
| `@http2MaxPoolSize`                           | `Number (int)`              | \-                                                                    |
| `@http2MultiplexingLimit`                     | `Number (int)`              | \-                                                                    |
| `@idleTimeout`                                | `Number (int)`              | \-                                                                    |
| `@idleTimeoutUnit`                            | `TimeUnit`                  | \-                                                                    |
| `@initialSettings`                            | `Http2Settings`             | \-                                                                    |
| `@introspectionPath`                          | `String`                    | Set the provider token introspection resource path                    |
| `@jdkSslEngineOptions`                        | `JdkSSLEngineOptions`       | \-                                                                    |
| `@jwkPath`                                    | `String`                    | \-                                                                    |
| `@jwtOptions`                                 | `JWTOptions`                | \-                                                                    |
| `@keepAlive`                                  | `Boolean`                   | \-                                                                    |
| `@keepAliveTimeout`                           | `Number (int)`              | \-                                                                    |
| `@keyStoreOptions`                            | `JksOptions`                | \-                                                                    |
| `@localAddress`                               | `String`                    | \-                                                                    |
| `@logActivity`                                | `Boolean`                   | \-                                                                    |
| `@logoutPath`                                 | `String`                    | Set the provider logout path                                          |
| `@maxChunkSize`                               | `Number (int)`              | \-                                                                    |
| `@maxHeaderSize`                              | `Number (int)`              | \-                                                                    |
| `@maxInitialLineLength`                       | `Number (int)`              | \-                                                                    |
| `@maxPoolSize`                                | `Number (int)`              | \-                                                                    |
| `@maxRedirects`                               | `Number (int)`              | \-                                                                    |
| `@maxWaitQueueSize`                           | `Number (int)`              | \-                                                                    |
| `@maxWebSocketFrameSize`                      | `Number (int)`              | \-                                                                    |
| `@maxWebSocketMessageSize`                    | `Number (int)`              | \-                                                                    |
| `@maxWebsocketFrameSize`                      | `Number (int)`              | \-                                                                    |
| `@maxWebsocketMessageSize`                    | `Number (int)`              | \-                                                                    |
| `@metricsName`                                | `String`                    | \-                                                                    |
| `@openSslEngineOptions`                       | `OpenSSLEngineOptions`      | \-                                                                    |
| `@pemKeyCertOptions`                          | `PemKeyCertOptions`         | \-                                                                    |
| `@pemTrustOptions`                            | `PemTrustOptions`           | \-                                                                    |
| `@pfxKeyCertOptions`                          | `PfxOptions`                | \-                                                                    |
| `@pfxTrustOptions`                            | `PfxOptions`                | \-                                                                    |
| `@pipelining`                                 | `Boolean`                   | \-                                                                    |
| `@pipeliningLimit`                            | `Number (int)`              | \-                                                                    |
| `@poolCleanerPeriod`                          | `Number (int)`              | \-                                                                    |
| `@protocolVersion`                            | `HttpVersion`               | \-                                                                    |
| `@proxyOptions`                               | `ProxyOptions`              | \-                                                                    |
| `@pubSecKeys`                                 | `Array of PubSecKeyOptions` | The provider PubSec key options                                       |
| `@receiveBufferSize`                          | `Number (int)`              | \-                                                                    |
| `@reuseAddress`                               | `Boolean`                   | \-                                                                    |
| `@reusePort`                                  | `Boolean`                   | \-                                                                    |
| `@revocationPath`                             | `String`                    | Set the Oauth2 revocation resource path. e.g.: /oauth/revoke          |
| `@scopeSeparator`                             | `String`                    | Set the provider scope separator                                      |
| `@sendBufferSize`                             | `Number (int)`              | \-                                                                    |
| `@sendUnmaskedFrames`                         | `Boolean`                   | \-                                                                    |
| `@site`                                       | `String`                    | Root URL for the provider                                             |
| `@soLinger`                                   | `Number (int)`              | \-                                                                    |
| `@ssl`                                        | `Boolean`                   | \-                                                                    |
| `@sslHandshakeTimeout`                        | `Number (long)`             | \-                                                                    |
| `@sslHandshakeTimeoutUnit`                    | `TimeUnit`                  | \-                                                                    |
| `@tcpCork`                                    | `Boolean`                   | \-                                                                    |
| `@tcpFastOpen`                                | `Boolean`                   | \-                                                                    |
| `@tcpKeepAlive`                               | `Boolean`                   | \-                                                                    |
| `@tcpNoDelay`                                 | `Boolean`                   | \-                                                                    |
| `@tcpQuickAck`                                | `Boolean`                   | \-                                                                    |
| `@tokenPath`                                  | `String`                    | Get the Oauth2 token resource path. e.g.: /oauth/token                |
| `@trafficClass`                               | `Number (int)`              | \-                                                                    |
| `@trustAll`                                   | `Boolean`                   | \-                                                                    |
| `@trustStoreOptions`                          | `JksOptions`                | \-                                                                    |
| `@tryUseCompression`                          | `Boolean`                   | \-                                                                    |
| `@tryUsePerFrameWebSocketCompression`         | `Boolean`                   | \-                                                                    |
| `@tryUsePerFrameWebsocketCompression`         | `Boolean`                   | \-                                                                    |
| `@tryUsePerMessageWebSocketCompression`       | `Boolean`                   | \-                                                                    |
| `@tryUsePerMessageWebsocketCompression`       | `Boolean`                   | \-                                                                    |
| `@tryWebSocketDeflateFrameCompression`        | `Boolean`                   | \-                                                                    |
| `@tryWebsocketDeflateFrameCompression`        | `Boolean`                   | \-                                                                    |
| `@useAlpn`                                    | `Boolean`                   | \-                                                                    |
| `@useBasicAuthorizationHeader`                | `Boolean`                   | Flag to use HTTP basic auth header with client id, client secret.     |
| `@usePooledBuffers`                           | `Boolean`                   | \-                                                                    |
| `@userAgent`                                  | `String`                    | Set a custom user agent to use when communicating to a provider       |
| `@userInfoParameters`                         | `Json object`               | Set custom parameters to be sent during the userInfo resource request |
| `@userInfoPath`                               | `String`                    | Set the provider userInfo resource path                               |
| `@validateIssuer`                             | `Boolean`                   | \-                                                                    |
| `@verifyHost`                                 | `Boolean`                   | \-                                                                    |
| `@webSocketCompressionAllowClientNoContext`   | `Boolean`                   | \-                                                                    |
| `@webSocketCompressionLevel`                  | `Number (int)`              | \-                                                                    |
| `@webSocketCompressionRequestServerNoContext` | `Boolean`                   | \-                                                                    |
| `@websocketCompressionAllowClientNoContext`   | `Boolean`                   | \-                                                                    |
| `@websocketCompressionLevel`                  | `Number (int)`              | \-                                                                    |
| `@websocketCompressionRequestServerNoContext` | `Boolean`                   | \-                                                                    |
