# HttpTermOptions

The web term configuration options.

|                                            |                        |                                                                       |
| ------------------------------------------ | ---------------------- | --------------------------------------------------------------------- |
| Name                                       | Type                   | Description                                                           |
| `@acceptBacklog`                           | `Number (int)`         | \-                                                                    |
| `@acceptUnmaskedFrames`                    | `Boolean`              | \-                                                                    |
| `@alpnVersions`                            | `Array of HttpVersion` | \-                                                                    |
| `@authOptions`                             | `AuthOptions`          |                                                                       |
| `@charset`                                 | `String`               | Set the charset used for encoding / decoding text data from/to SockJS |
| `@clientAuth`                              | `ClientAuth`           | \-                                                                    |
| `@clientAuthRequired`                      | `Boolean`              | \-                                                                    |
| `@compressionLevel`                        | `Number (int)`         | \-                                                                    |
| `@compressionSupported`                    | `Boolean`              | \-                                                                    |
| `@crlPaths`                                | `Array of String`      | \-                                                                    |
| `@crlValues`                               | `Array of Buffer`      | \-                                                                    |
| `@decoderInitialBufferSize`                | `Number (int)`         | \-                                                                    |
| `@decompressionSupported`                  | `Boolean`              | \-                                                                    |
| `@enabledCipherSuites`                     | `Array of String`      | \-                                                                    |
| `@enabledSecureTransportProtocols`         | `Array of String`      | \-                                                                    |
| `@handle100ContinueAutomatically`          | `Boolean`              | \-                                                                    |
| `@host`                                    | `String`               | \-                                                                    |
| `@http2ConnectionWindowSize`               | `Number (int)`         | \-                                                                    |
| `@idleTimeout`                             | `Number (int)`         | \-                                                                    |
| `@idleTimeoutUnit`                         | `TimeUnit`             | \-                                                                    |
| `@initialSettings`                         | `Http2Settings`        | \-                                                                    |
| `@intputrc`                                | `String`               | The path of the inputrc config.                                       |
| `@jdkSslEngineOptions`                     | `JdkSSLEngineOptions`  | \-                                                                    |
| `@keyStoreOptions`                         | `JksOptions`           | \-                                                                    |
| `@logActivity`                             | `Boolean`              | \-                                                                    |
| `@maxChunkSize`                            | `Number (int)`         | \-                                                                    |
| `@maxHeaderSize`                           | `Number (int)`         | \-                                                                    |
| `@maxInitialLineLength`                    | `Number (int)`         | \-                                                                    |
| `@maxWebSocketFrameSize`                   | `Number (int)`         | \-                                                                    |
| `@maxWebSocketMessageSize`                 | `Number (int)`         | \-                                                                    |
| `@maxWebsocketFrameSize`                   | `Number (int)`         | \-                                                                    |
| `@maxWebsocketMessageSize`                 | `Number (int)`         | \-                                                                    |
| `@openSslEngineOptions`                    | `OpenSSLEngineOptions` | \-                                                                    |
| `@pemKeyCertOptions`                       | `PemKeyCertOptions`    | \-                                                                    |
| `@pemTrustOptions`                         | `PemTrustOptions`      | \-                                                                    |
| `@perFrameWebSocketCompressionSupported`   | `Boolean`              | \-                                                                    |
| `@perFrameWebsocketCompressionSupported`   | `Boolean`              | \-                                                                    |
| `@perMessageWebSocketCompressionSupported` | `Boolean`              | \-                                                                    |
| `@perMessageWebsocketCompressionSupported` | `Boolean`              | \-                                                                    |
| `@pfxKeyCertOptions`                       | `PfxOptions`           | \-                                                                    |
| `@pfxTrustOptions`                         | `PfxOptions`           | \-                                                                    |
| `@port`                                    | `Number (int)`         | \-                                                                    |
| `@receiveBufferSize`                       | `Number (int)`         | \-                                                                    |
| `@reuseAddress`                            | `Boolean`              | \-                                                                    |
| `@reusePort`                               | `Boolean`              | \-                                                                    |
| `@sendBufferSize`                          | `Number (int)`         | \-                                                                    |
| `@shellHtmlResource`                       | `Buffer`               | Set `shell.html` resource to use.                                     |
| `@sni`                                     | `Boolean`              | \-                                                                    |
| `@soLinger`                                | `Number (int)`         | \-                                                                    |
| `@sockJSHandlerOptions`                    | `SockJSHandlerOptions` | The SockJS handler options.                                           |
| `@sockJSPath`                              | `String`               | Configure the SockJS path, the default value is `/term/*`.            |
| `@ssl`                                     | `Boolean`              | \-                                                                    |
| `@sslHandshakeTimeout`                     | `Number (long)`        | \-                                                                    |
| `@sslHandshakeTimeoutUnit`                 | `TimeUnit`             | \-                                                                    |
| `@tcpCork`                                 | `Boolean`              | \-                                                                    |
| `@tcpFastOpen`                             | `Boolean`              | \-                                                                    |
| `@tcpKeepAlive`                            | `Boolean`              | \-                                                                    |
| `@tcpNoDelay`                              | `Boolean`              | \-                                                                    |
| `@tcpQuickAck`                             | `Boolean`              | \-                                                                    |
| `@termJsResource`                          | `Buffer`               | Set `term.js` resource to use.                                        |
| `@trafficClass`                            | `Number (int)`         | \-                                                                    |
| `@trustStoreOptions`                       | `JksOptions`           | \-                                                                    |
| `@useAlpn`                                 | `Boolean`              | \-                                                                    |
| `@usePooledBuffers`                        | `Boolean`              | \-                                                                    |
| `@vertsShellJsResource`                    | `Buffer`               | Set `vertxshell.js` resource to use.                                  |
| `@webSocketAllowServerNoContext`           | `Boolean`              | \-                                                                    |
| `@webSocketCompressionLevel`               | `Number (int)`         | \-                                                                    |
| `@webSocketPreferredClientNoContext`       | `Boolean`              | \-                                                                    |
| `@webSocketSubProtocols`                   | `Array of String`      | \-                                                                    |
| `@websocketAllowServerNoContext`           | `Boolean`              | \-                                                                    |
| `@websocketCompressionLevel`               | `Number (int)`         | \-                                                                    |
| `@websocketPreferredClientNoContext`       | `Boolean`              | \-                                                                    |
| `@websocketSubProtocols`                   | `String`               | \-                                                                    |

# SSHTermOptions

The SSH term configuration options.

|                      |                     |                                                                        |
| -------------------- | ------------------- | ---------------------------------------------------------------------- |
| Name                 | Type                | Description                                                            |
| `@authOptions`       | `AuthOptions`       |                                                                        |
| `@defaultCharset`    | `String`            | Set the default charset to use when the client does not specifies one. |
| `@host`              | `String`            | Set the host                                                           |
| `@intputrc`          | `String`            | The path of the inputrc config.                                        |
| `@keyPairOptions`    | `JksOptions`        | Set the key pair options in jks format, aka Java keystore.             |
| `@pemKeyPairOptions` | `PemKeyCertOptions` | Set the key pair store options in pem format.                          |
| `@pfxKeyPairOptions` | `PfxOptions`        | Set the key pair options in pfx format.                                |
| `@port`              | `Number (int)`      | Set the port                                                           |

# ShellServerOptions

The configurations options for the shell server.

|                   |                 |                                                                                                             |
| ----------------- | --------------- | ----------------------------------------------------------------------------------------------------------- |
| Name              | Type            | Description                                                                                                 |
| `@reaperInterval` | `Number (long)` | Set the repear interval, i.e the period at which session eviction is performed.                             |
| `@sessionTimeout` | `Number (long)` | Set the session timeout.                                                                                    |
| `@welcomeMessage` | `String`        | Set the shell welcome message, i.e the message displayed in the user console when he connects to the shell. |

# ShellServiceOptions

The configurations options for the shell service, the shell connectors
can be configured with , and .

|                   |                     |                                                                                                             |
| ----------------- | ------------------- | ----------------------------------------------------------------------------------------------------------- |
| Name              | Type                | Description                                                                                                 |
| `@httpOptions`    | `HttpTermOptions`   | \-                                                                                                          |
| `@reaperInterval` | `Number (long)`     | Set the repear interval, i.e the period at which session eviction is performed.                             |
| `@sessionTimeout` | `Number (long)`     | Set the session timeout.                                                                                    |
| `@sshOptions`     | `SSHTermOptions`    | Set the SSH options, if the option is null, SSH will not be started.                                        |
| `@telnetOptions`  | `TelnetTermOptions` | Set the Telnet options, if the option is null, Telnet will not be started.                                  |
| `@welcomeMessage` | `String`            | Set the shell welcome message, i.e the message displayed in the user console when he connects to the shell. |

# TelnetTermOptions

Telnet terminal options configuration, extends link.

|                                    |                        |                                                                                                                                                                                            |
| ---------------------------------- | ---------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Name                               | Type                   | Description                                                                                                                                                                                |
| `@acceptBacklog`                   | `Number (int)`         | \-                                                                                                                                                                                         |
| `@charset`                         | `String`               | Set the charset to use when binary mode is active, see link and link.                                                                                                                      |
| `@clientAuth`                      | `ClientAuth`           | \-                                                                                                                                                                                         |
| `@clientAuthRequired`              | `Boolean`              | \-                                                                                                                                                                                         |
| `@crlPaths`                        | `Array of String`      | \-                                                                                                                                                                                         |
| `@crlValues`                       | `Array of Buffer`      | \-                                                                                                                                                                                         |
| `@enabledCipherSuites`             | `Array of String`      | \-                                                                                                                                                                                         |
| `@enabledSecureTransportProtocols` | `Array of String`      | \-                                                                                                                                                                                         |
| `@host`                            | `String`               | \-                                                                                                                                                                                         |
| `@idleTimeout`                     | `Number (int)`         | \-                                                                                                                                                                                         |
| `@idleTimeoutUnit`                 | `TimeUnit`             | \-                                                                                                                                                                                         |
| `@inBinary`                        | `Boolean`              | Set the telnet connection to negociate binary data format when receiving from the client, the default value is true. This allows to send data in 8 bit format and thus charset like UTF-8. |
| `@intputrc`                        | `String`               | The path of the inputrc config.                                                                                                                                                            |
| `@jdkSslEngineOptions`             | `JdkSSLEngineOptions`  | \-                                                                                                                                                                                         |
| `@keyStoreOptions`                 | `JksOptions`           | \-                                                                                                                                                                                         |
| `@logActivity`                     | `Boolean`              | \-                                                                                                                                                                                         |
| `@openSslEngineOptions`            | `OpenSSLEngineOptions` | \-                                                                                                                                                                                         |
| `@outBinary`                       | `Boolean`              | Set the telnet connection to negociate binary data format when sending to the client, the default value is true. This allows to send data in 8 bit format and thus charset like UTF-8.     |
| `@pemKeyCertOptions`               | `PemKeyCertOptions`    | \-                                                                                                                                                                                         |
| `@pemTrustOptions`                 | `PemTrustOptions`      | \-                                                                                                                                                                                         |
| `@pfxKeyCertOptions`               | `PfxOptions`           | \-                                                                                                                                                                                         |
| `@pfxTrustOptions`                 | `PfxOptions`           | \-                                                                                                                                                                                         |
| `@port`                            | `Number (int)`         | \-                                                                                                                                                                                         |
| `@receiveBufferSize`               | `Number (int)`         | \-                                                                                                                                                                                         |
| `@reuseAddress`                    | `Boolean`              | \-                                                                                                                                                                                         |
| `@reusePort`                       | `Boolean`              | \-                                                                                                                                                                                         |
| `@sendBufferSize`                  | `Number (int)`         | \-                                                                                                                                                                                         |
| `@sni`                             | `Boolean`              | \-                                                                                                                                                                                         |
| `@soLinger`                        | `Number (int)`         | \-                                                                                                                                                                                         |
| `@ssl`                             | `Boolean`              | \-                                                                                                                                                                                         |
| `@sslHandshakeTimeout`             | `Number (long)`        | \-                                                                                                                                                                                         |
| `@sslHandshakeTimeoutUnit`         | `TimeUnit`             | \-                                                                                                                                                                                         |
| `@tcpCork`                         | `Boolean`              | \-                                                                                                                                                                                         |
| `@tcpFastOpen`                     | `Boolean`              | \-                                                                                                                                                                                         |
| `@tcpKeepAlive`                    | `Boolean`              | \-                                                                                                                                                                                         |
| `@tcpNoDelay`                      | `Boolean`              | \-                                                                                                                                                                                         |
| `@tcpQuickAck`                     | `Boolean`              | \-                                                                                                                                                                                         |
| `@trafficClass`                    | `Number (int)`         | \-                                                                                                                                                                                         |
| `@trustStoreOptions`               | `JksOptions`           | \-                                                                                                                                                                                         |
| `@useAlpn`                         | `Boolean`              | \-                                                                                                                                                                                         |
| `@usePooledBuffers`                | `Boolean`              | \-                                                                                                                                                                                         |
