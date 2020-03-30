# AclToken

Holds properties of Acl token

|          |                |                     |
| -------- | -------------- | ------------------- |
| Name     | Type           | Description         |
| `@id`    | `String`       | Set ID of token     |
| `@name`  | `String`       | Set name of token   |
| `@rules` | `String`       | Set rules for token |
| `@type`  | `AclTokenType` | Set type of token   |

# BlockingQueryOptions

Options used to perform blocking query that used to wait for a potential
change using long polling.

|          |                 |                                                                                                                                                                                                                                                 |
| -------- | --------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Name     | Type            | Description                                                                                                                                                                                                                                     |
| `@index` | `Number (long)` | Set index indicating that the client wishes to wait for any changes subsequent to that index.                                                                                                                                                   |
| `@wait`  | `String`        | Specifying a maximum duration for the blocking request. This is limited to 10 minutes. If not set, the wait time defaults to 5 minutes. This value can be specified in the form of "10s" or "5m" (i.e., 10 seconds or 5 minutes, respectively). |

# Check

Holds check properties

|                |               |                                                          |
| -------------- | ------------- | -------------------------------------------------------- |
| Name           | Type          | Description                                              |
| `@id`          | `String`      | Set the ID of check                                      |
| `@name`        | `String`      | Set the name of check                                    |
| `@nodeName`    | `String`      | Set the name of node                                     |
| `@notes`       | `String`      | Set the human-readable note of check                     |
| `@output`      | `String`      | Set the output of check                                  |
| `@serviceId`   | `String`      | Set the ID of service with which this check associated   |
| `@serviceName` | `String`      | Set the name of service with which this check associated |
| `@status`      | `CheckStatus` | Set the status of check                                  |

# CheckList

Holds result of checks query

|          |                  |                                                                                                      |
| -------- | ---------------- | ---------------------------------------------------------------------------------------------------- |
| Name     | Type             | Description                                                                                          |
| `@index` | `Number (long)`  | Set Consul index, a unique identifier representing the current state of the requested list of checks |
| `@list`  | `Array of Check` | Set list of checks                                                                                   |

# CheckOptions

Options used to register checks in Consul.

|                    |                   |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| ------------------ | ----------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Name               | Type              | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| `@deregisterAfter` | `String`          | Set deregister timeout. This is optional field, which is a timeout in the same time format as Interval and TTL. If a check is associated with a service and has the critical state for more than this configured value, then its associated service (and all of its associated checks) will automatically be deregistered. The minimum timeout is 1 minute, and the process that reaps critical services runs every 30 seconds, so it may take slightly longer than the configured timeout to trigger the deregistration. This should generally be configured with a timeout that's much, much longer than any expected recoverable outage for the given service. |
| `@grpc`            | `String`          | Specifies a gRPC check's endpoint that supports the standard gRPC health checking protocol. The state of the check will be updated at the given Interval by probing the configured endpoint. The endpoint must be represented as `address:port/service`                                                                                                                                                                                                                                                                                                                                                                                                           |
| `@grpcTls`         | `Boolean`         | Specifies whether to use TLS for this gRPC health check. If TLS is enabled, then by default, a valid TLS certificate is expected. Certificate verification can be turned off by setting `TLSSkipVerify` to `true`.                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| `@http`            | `String`          | Set HTTP address to check. Also you should set checking interval                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| `@id`              | `String`          | Set check ID                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| `@interval`        | `String`          | Set checking interval                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| `@name`            | `String`          | Set check name. This is mandatory field                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| `@notes`           | `String`          | Set check notes                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| `@scriptArgs`      | `Array of String` | Set scriptArgs. Also you should set checking interval                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| `@serviceId`       | `String`          | Set the service ID to associate the registered check with an existing service provided by the agent.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| `@status`          | `CheckStatus`     | Set the check status to specify the initial state of the health check.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| `@tcp`             | `String`          | Set TCP address to check. Also you should set checking interval                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| `@tlsSkipVerify`   | `Boolean`         | Specifies if the certificate for an HTTPS check should not be verified.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| `@ttl`             | `String`          | Set Time to Live of check.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |

# CheckQueryOptions

Options used to requesting list of checks

|                    |                        |                                                                                                              |
| ------------------ | ---------------------- | ------------------------------------------------------------------------------------------------------------ |
| Name               | Type                   | Description                                                                                                  |
| `@blockingOptions` | `BlockingQueryOptions` | Set options for blocking query                                                                               |
| `@near`            | `String`               | Set node name for sorting the list in ascending order based on the estimated round trip time from that node. |

# ConsulClientOptions

Options used to create Consul client.

<table>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 50%" />
</colgroup>
<tbody>
<tr class="odd">
<td><p>Name</p></td>
<td><p>Type</p></td>
<td><p>Description</p></td>
</tr>
<tr class="even">
<td><p><code>@aclToken</code></p></td>
<td><p><code>String</code></p></td>
<td><p>Set the ACL token. When provided, the client will use this token when making requests to the Consul by providing the "?token" query parameter. When not provided, the empty token, which maps to the 'anonymous' ACL policy, is used.</p></td>
</tr>
<tr class="odd">
<td><p><code>@alpnVersions</code></p></td>
<td><p><code>Array of HttpVersion</code></p></td>
<td><p>Set the list of protocol versions to provide to the server during the Application-Layer Protocol Negotiation. When the list is empty, the client provides a best effort list according to link:</p>
<p>: [ "h2", "http/1.1" ] otherwise: [link]</p></td>
</tr>
<tr class="even">
<td><p><code>@connectTimeout</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the connect timeout</p></td>
</tr>
<tr class="odd">
<td><p><code>@crlPaths</code></p></td>
<td><p><code>Array of String</code></p></td>
<td><p>Add a CRL path</p></td>
</tr>
<tr class="even">
<td><p><code>@crlValues</code></p></td>
<td><p><code>Array of Buffer</code></p></td>
<td><p>Add a CRL value</p></td>
</tr>
<tr class="odd">
<td><p><code>@dc</code></p></td>
<td><p><code>String</code></p></td>
<td><p>Set the datacenter name. When provided, the client will use it when making requests to the Consul by providing the "?dc" query parameter. When not provided, the datacenter of the consul agent is queried.</p></td>
</tr>
<tr class="even">
<td><p><code>@decoderInitialBufferSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>set to <code>initialBufferSizeHttpDecoder</code> the initial buffer of the HttpDecoder.</p></td>
</tr>
<tr class="odd">
<td><p><code>@defaultHost</code></p></td>
<td><p><code>String</code></p></td>
<td><p>Set the default host name to be used by this client in requests if none is provided when making the request.</p></td>
</tr>
<tr class="even">
<td><p><code>@defaultPort</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the default port to be used by this client in requests if none is provided when making the request.</p></td>
</tr>
<tr class="odd">
<td><p><code>@enabledCipherSuites</code></p></td>
<td><p><code>Array of String</code></p></td>
<td><p>Add an enabled cipher suite, appended to the ordered suites.</p></td>
</tr>
<tr class="even">
<td><p><code>@enabledSecureTransportProtocols</code></p></td>
<td><p><code>Array of String</code></p></td>
<td><p>Add an enabled SSL/TLS protocols, appended to the ordered protocols.</p></td>
</tr>
<tr class="odd">
<td><p><code>@followRedirects</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Configure the default behavior of the client to follow HTTP <code>30x</code> redirections.</p></td>
</tr>
<tr class="even">
<td><p><code>@forceSni</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>By default, the server name is only sent for Fully Qualified Domain Name (FQDN), setting this property to <code>true</code> forces the server name to be always sent.</p></td>
</tr>
<tr class="odd">
<td><p><code>@http2ClearTextUpgrade</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set to <code>true</code> when an h2c connection is established using an HTTP/1.1 upgrade request, and <code>false</code> when an h2c connection is established directly (with prior knowledge).</p></td>
</tr>
<tr class="even">
<td><p><code>@http2ConnectionWindowSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the default HTTP/2 connection window size. It overrides the initial window size set by , so the connection window size is greater than for its streams, in order the data throughput. A value of <code>-1</code> reuses the initial window size setting.</p></td>
</tr>
<tr class="odd">
<td><p><code>@http2KeepAliveTimeout</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@http2MaxPoolSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the maximum pool size for HTTP/2 connections</p></td>
</tr>
<tr class="odd">
<td><p><code>@http2MultiplexingLimit</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set a client limit of the number concurrent streams for each HTTP/2 connection, this limits the number of streams the client can create for a connection. The effective number of streams for a connection is the min of this value and the server's initial settings. Setting the value to <code>-1</code> means to use the value sent by the server's initial settings. <code>-1</code> is the default value.</p></td>
</tr>
<tr class="even">
<td><p><code>@idleTimeout</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the idle timeout, in seconds. zero means don't timeout. This determines if a connection will timeout and be closed if no data is received within the timeout.</p></td>
</tr>
<tr class="odd">
<td><p><code>@idleTimeoutUnit</code></p></td>
<td><p><code>TimeUnit</code></p></td>
<td><p>Set the idle timeout unit. If not specified, default is seconds.</p></td>
</tr>
<tr class="even">
<td><p><code>@initialSettings</code></p></td>
<td><p><code>Http2Settings</code></p></td>
<td><p>Set the HTTP/2 connection settings immediately sent by to the server when the client connects.</p></td>
</tr>
<tr class="odd">
<td><p><code>@jdkSslEngineOptions</code></p></td>
<td><p><code>JdkSSLEngineOptions</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@keepAlive</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether keep alive is enabled on the client</p></td>
</tr>
<tr class="odd">
<td><p><code>@keepAliveTimeout</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@keyStoreOptions</code></p></td>
<td><p><code>JksOptions</code></p></td>
<td><p>Set the key/cert options in jks format, aka Java keystore.</p></td>
</tr>
<tr class="odd">
<td><p><code>@localAddress</code></p></td>
<td><p><code>String</code></p></td>
<td><p>Set the local interface to bind for network connections. When the local address is null, it will pick any local address, the default local address is null.</p></td>
</tr>
<tr class="even">
<td><p><code>@logActivity</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set to true to enabled network activity logging: Netty's pipeline is configured for logging on Netty's logger.</p></td>
</tr>
<tr class="odd">
<td><p><code>@maxChunkSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the maximum HTTP chunk size</p></td>
</tr>
<tr class="even">
<td><p><code>@maxHeaderSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the maximum length of all headers for HTTP/1.x .</p></td>
</tr>
<tr class="odd">
<td><p><code>@maxInitialLineLength</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the maximum length of the initial line for HTTP/1.x (e.g. <code>"HTTP/1.1 200 OK"</code>)</p></td>
</tr>
<tr class="even">
<td><p><code>@maxPoolSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the maximum pool size for connections</p></td>
</tr>
<tr class="odd">
<td><p><code>@maxRedirects</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set to <code>maxRedirects</code> the maximum number of redirection a request can follow.</p></td>
</tr>
<tr class="even">
<td><p><code>@maxWaitQueueSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the maximum requests allowed in the wait queue, any requests beyond the max size will result in a ConnectionPoolTooBusyException. If the value is set to a negative number then the queue will be unbounded.</p></td>
</tr>
<tr class="odd">
<td><p><code>@maxWebSocketFrameSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@maxWebSocketMessageSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@maxWebsocketFrameSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the max websocket frame size</p></td>
</tr>
<tr class="even">
<td><p><code>@maxWebsocketMessageSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the max websocket message size</p></td>
</tr>
<tr class="odd">
<td><p><code>@metricsName</code></p></td>
<td><p><code>String</code></p></td>
<td><p>Set the metrics name identifying the reported metrics, useful for grouping metrics with the same name.</p></td>
</tr>
<tr class="even">
<td><p><code>@openSslEngineOptions</code></p></td>
<td><p><code>OpenSSLEngineOptions</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@pemKeyCertOptions</code></p></td>
<td><p><code>PemKeyCertOptions</code></p></td>
<td><p>Set the key/cert store options in pem format.</p></td>
</tr>
<tr class="even">
<td><p><code>@pemTrustOptions</code></p></td>
<td><p><code>PemTrustOptions</code></p></td>
<td><p>Set the trust options.</p></td>
</tr>
<tr class="odd">
<td><p><code>@pfxKeyCertOptions</code></p></td>
<td><p><code>PfxOptions</code></p></td>
<td><p>Set the key/cert options in pfx format.</p></td>
</tr>
<tr class="even">
<td><p><code>@pfxTrustOptions</code></p></td>
<td><p><code>PfxOptions</code></p></td>
<td><p>Set the trust options in pfx format</p></td>
</tr>
<tr class="odd">
<td><p><code>@pipelining</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether pipe-lining is enabled on the client</p></td>
</tr>
<tr class="even">
<td><p><code>@pipeliningLimit</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the limit of pending requests a pipe-lined HTTP/1 connection can send.</p></td>
</tr>
<tr class="odd">
<td><p><code>@poolCleanerPeriod</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@protocolVersion</code></p></td>
<td><p><code>HttpVersion</code></p></td>
<td><p>Set the protocol version.</p></td>
</tr>
<tr class="odd">
<td><p><code>@proxyOptions</code></p></td>
<td><p><code>ProxyOptions</code></p></td>
<td><p>Set proxy options for connections via CONNECT proxy (e.g. Squid) or a SOCKS proxy.</p></td>
</tr>
<tr class="even">
<td><p><code>@receiveBufferSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the TCP receive buffer size</p></td>
</tr>
<tr class="odd">
<td><p><code>@reuseAddress</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set the value of reuse address</p></td>
</tr>
<tr class="even">
<td><p><code>@reusePort</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set the value of reuse port. This is only supported by native transports.</p></td>
</tr>
<tr class="odd">
<td><p><code>@sendBufferSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the TCP send buffer size</p></td>
</tr>
<tr class="even">
<td><p><code>@sendUnmaskedFrames</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set true when the client wants to skip frame masking. You may want to set it true on server by server websocket communication: In this case you are by passing RFC6455 protocol. It's false as default.</p></td>
</tr>
<tr class="odd">
<td><p><code>@soLinger</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set whether SO_linger keep alive is enabled</p></td>
</tr>
<tr class="even">
<td><p><code>@ssl</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether SSL/TLS is enabled</p></td>
</tr>
<tr class="odd">
<td><p><code>@sslHandshakeTimeout</code></p></td>
<td><p><code>Number (long)</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@sslHandshakeTimeoutUnit</code></p></td>
<td><p><code>TimeUnit</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@tcpCork</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Enable the <code>TCP_CORK</code> option - only with linux native transport.</p></td>
</tr>
<tr class="even">
<td><p><code>@tcpFastOpen</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Enable the <code>TCP_FASTOPEN</code> option - only with linux native transport.</p></td>
</tr>
<tr class="odd">
<td><p><code>@tcpKeepAlive</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether TCP keep alive is enabled</p></td>
</tr>
<tr class="even">
<td><p><code>@tcpNoDelay</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether TCP no delay is enabled</p></td>
</tr>
<tr class="odd">
<td><p><code>@tcpQuickAck</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Enable the <code>TCP_QUICKACK</code> option - only with linux native transport.</p></td>
</tr>
<tr class="even">
<td><p><code>@timeout</code></p></td>
<td><p><code>Number (long)</code></p></td>
<td><p>Sets the amount of time (in milliseconds) after which if the request does not return any data within the timeout period an failure will be passed to the handler and the request will be closed.</p></td>
</tr>
<tr class="odd">
<td><p><code>@trafficClass</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the value of traffic class</p></td>
</tr>
<tr class="even">
<td><p><code>@trustAll</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether all server certificates should be trusted</p></td>
</tr>
<tr class="odd">
<td><p><code>@trustStoreOptions</code></p></td>
<td><p><code>JksOptions</code></p></td>
<td><p>Set the trust options in jks format, aka Java truststore</p></td>
</tr>
<tr class="even">
<td><p><code>@tryUseCompression</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether compression is enabled</p></td>
</tr>
<tr class="odd">
<td><p><code>@tryUsePerFrameWebSocketCompression</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@tryUsePerFrameWebsocketCompression</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@tryUsePerMessageWebSocketCompression</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@tryUsePerMessageWebsocketCompression</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@tryWebSocketDeflateFrameCompression</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@tryWebsocketDeflateFrameCompression</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@useAlpn</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set the ALPN usage.</p></td>
</tr>
<tr class="even">
<td><p><code>@usePooledBuffers</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether Netty pooled buffers are enabled</p></td>
</tr>
<tr class="odd">
<td><p><code>@userAgent</code></p></td>
<td><p><code>String</code></p></td>
<td><p>Sets the Web Client user agent header. Defaults to Vert.x-WebClient/&lt;version&gt;.</p></td>
</tr>
<tr class="even">
<td><p><code>@userAgentEnabled</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Sets whether the Web Client should send a user agent header. Defaults to true.</p></td>
</tr>
<tr class="odd">
<td><p><code>@verifyHost</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether hostname verification is enabled</p></td>
</tr>
<tr class="even">
<td><p><code>@webSocketCompressionAllowClientNoContext</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@webSocketCompressionLevel</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@webSocketCompressionRequestServerNoContext</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@websocketCompressionAllowClientNoContext</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@websocketCompressionLevel</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@websocketCompressionRequestServerNoContext</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>-</p></td>
</tr>
</tbody>
</table>

# Coordinate

Holds network coordinates of node

|           |                           |                  |
| --------- | ------------------------- | ---------------- |
| Name      | Type                      | Description      |
| `@adj`    | `Number (float)`          | Set adjustment   |
| `@err`    | `Number (float)`          | Set error        |
| `@height` | `Number (float)`          | Set height       |
| `@node`   | `String`                  | Set name of node |
| `@vec`    | `Array of Number (Float)` | Set vector       |

# CoordinateList

Holds result of network coordinates query

|          |                       |                                                                                                   |
| -------- | --------------------- | ------------------------------------------------------------------------------------------------- |
| Name     | Type                  | Description                                                                                       |
| `@index` | `Number (long)`       | Set Consul index, a unique identifier representing the current state of the requested coordinates |
| `@list`  | `Array of Coordinate` | Set list of coordinates                                                                           |

# DcCoordinates

Holds coordinates of servers in datacenter

|               |                       |                                   |
| ------------- | --------------------- | --------------------------------- |
| Name          | Type                  | Description                       |
| `@datacenter` | `String`              | Set datacenter                    |
| `@servers`    | `Array of Coordinate` | Set list of servers in datacenter |

# Event

Holds properties of Consul event

|            |                |                                               |
| ---------- | -------------- | --------------------------------------------- |
| Name       | Type           | Description                                   |
| `@id`      | `String`       | Set ID of event                               |
| `@lTime`   | `Number (int)` | Set the Lamport clock time                    |
| `@name`    | `String`       | Set name of event                             |
| `@node`    | `String`       | Set regular expression to filter by node name |
| `@payload` | `String`       | Set payload of event                          |
| `@service` | `String`       | Set regular expression to filter by service   |
| `@tag`     | `String`       | Set regular expression to filter by tag       |
| `@version` | `Number (int)` | Set version                                   |

# EventList

Holds result of events query

|          |                  |                                                                                              |
| -------- | ---------------- | -------------------------------------------------------------------------------------------- |
| Name     | Type             | Description                                                                                  |
| `@index` | `Number (long)`  | Set Consul index, a unique identifier representing the current state of the requested events |
| `@list`  | `Array of Event` | Set list of events                                                                           |

# EventListOptions

Holds options for events list request

|                    |                        |                                        |
| ------------------ | ---------------------- | -------------------------------------- |
| Name               | Type                   | Description                            |
| `@blockingOptions` | `BlockingQueryOptions` | Set options for blocking query         |
| `@name`            | `String`               | Set event name for filtering on events |

# EventOptions

Options used to trigger a new user event.

|            |          |                                               |
| ---------- | -------- | --------------------------------------------- |
| Name       | Type     | Description                                   |
| `@node`    | `String` | Set regular expression to filter by node name |
| `@payload` | `String` | Set payload of event                          |
| `@service` | `String` | Set regular expression to filter by service   |
| `@tag`     | `String` | Set regular expression to filter by tag       |

# KeyValue

Represents key/value pair stored in Consul

|                |                 |                                                                                                                 |
| -------------- | --------------- | --------------------------------------------------------------------------------------------------------------- |
| Name           | Type            | Description                                                                                                     |
| `@createIndex` | `Number (long)` | Set the internal index value that represents when the entry was created.                                        |
| `@flags`       | `Number (long)` | Set the flags attached to this entry. Clients can choose to use this however makes sense for their application. |
| `@key`         | `String`        | Set the key                                                                                                     |
| `@lockIndex`   | `Number (long)` | Set the number of times this key has successfully been acquired in a lock.                                      |
| `@modifyIndex` | `Number (long)` | Set the last index that modified this key.                                                                      |
| `@session`     | `String`        | Set the session that owns the lock                                                                              |
| `@value`       | `String`        | Set the value                                                                                                   |

# KeyValueList

Holds result of key/value pairs query

|          |                     |                             |
| -------- | ------------------- | --------------------------- |
| Name     | Type                | Description                 |
| `@index` | `Number (long)`     | Set Consul index            |
| `@list`  | `Array of KeyValue` | Set list of key/value pairs |

# KeyValueOptions

Options used to put key/value pair to Consul.

|                   |                 |                                                                                                                                                                                                              |
| ----------------- | --------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Name              | Type            | Description                                                                                                                                                                                                  |
| `@acquireSession` | `String`        | Set session ID for lock acquisition operation.                                                                                                                                                               |
| `@casIndex`       | `Number (long)` | Set the Check-And-Set index. If the index is `0`, Consul will only put the key if it does not already exist. If the index is non-zero, the key is only set if the index matches the ModifyIndex of that key. |
| `@flags`          | `Number (long)` | Set the flags. Flags is an value between `0` and 264-1 that can be attached to each entry. Clients can choose to use this however makes sense for their application.                                         |
| `@releaseSession` | `String`        | Set session ID for lock release operation.                                                                                                                                                                   |

# MaintenanceOptions

Options used to placing a given service into "maintenance mode". During
maintenance mode, the service will be marked as unavailable and will not
be present in DNS or API queries. Maintenance mode is persistent and
will be automatically restored on agent restart.

|           |           |                                                                                                                                                                                    |
| --------- | --------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Name      | Type      | Description                                                                                                                                                                        |
| `@enable` | `Boolean` | Set maintenance mode to enabled: `true` to enter maintenance mode or `false` to resume normal operation. This flag is required.                                                    |
| `@id`     | `String`  | Set the ID of service. This field is required.                                                                                                                                     |
| `@reason` | `String`  | Set the reason message. If provided, its value should be a text string explaining the reason for placing the service into maintenance mode. This is simply to aid human operators. |

# Node

|               |          |                      |
| ------------- | -------- | -------------------- |
| Name          | Type     | Description          |
| `@address`    | `String` | Set node address     |
| `@lanAddress` | `String` | Set node lan address |
| `@name`       | `String` | Set node name        |
| `@wanAddress` | `String` | Set node wan address |

# NodeList

Holds result of nodes query

|          |                 |                                                                                                     |
| -------- | --------------- | --------------------------------------------------------------------------------------------------- |
| Name     | Type            | Description                                                                                         |
| `@index` | `Number (long)` | Set Consul index, a unique identifier representing the current state of the requested list of nodes |
| `@list`  | `Array of Node` | Set list of nodes                                                                                   |

# NodeQueryOptions

Options used to requesting list of nodes

|                    |                        |                                                                                                              |
| ------------------ | ---------------------- | ------------------------------------------------------------------------------------------------------------ |
| Name               | Type                   | Description                                                                                                  |
| `@blockingOptions` | `BlockingQueryOptions` | Set options for blocking query                                                                               |
| `@near`            | `String`               | Set node name for sorting the list in ascending order based on the estimated round trip time from that node. |

# PreparedQueryDefinition

Defines a prepared query.

|                   |                   |                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| ----------------- | ----------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Name              | Type              | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| `@dcs`            | `Array of String` | Specifies a fixed list of remote datacenters to forward the query to if there are no healthy nodes in the local datacenter. Datacenters are queried in the order given in the list. If this option is combined with NearestN, then the NearestN queries will be performed first, followed by the list given by Datacenters. A given datacenter will only be queried one time during a failover, even if it is selected by both NearestN and is listed in Datacenters. |
| `@dnsTtl`         | `String`          | Set the TTL duration when query results are served over DNS. If this is specified, it will take precedence over any Consul agent-specific configuration.                                                                                                                                                                                                                                                                                                              |
| `@id`             | `String`          | Set ID of the query, always generated by Consul                                                                                                                                                                                                                                                                                                                                                                                                                       |
| `@meta`           | `String`          | Set a list of user-defined key/value pairs that will be used for filtering the query results to nodes with the given metadata values present.                                                                                                                                                                                                                                                                                                                         |
| `@name`           | `String`          | Set an optional friendly name that can be used to execute a query instead of using its ID                                                                                                                                                                                                                                                                                                                                                                             |
| `@nearestN`       | `Number (int)`    | Specifies that the query will be forwarded to up to NearestN other datacenters based on their estimated network round trip time using Network Coordinates from the WAN gossip pool. The median round trip time from the server handling the query to the servers in the remote datacenter is used to determine the priority.                                                                                                                                          |
| `@passing`        | `Boolean`         | Specifies the behavior of the query's health check filtering. If this is set to false, the results will include nodes with checks in the passing as well as the warning states. If this is set to true, only nodes with checks in the passing state will be returned.                                                                                                                                                                                                 |
| `@service`        | `String`          | Set the name of the service to query                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| `@session`        | `String`          | Set the ID of an existing session. This provides a way to automatically remove a prepared query when the given session is invalidated. If not given the prepared query must be manually removed when no longer needed.                                                                                                                                                                                                                                                |
| `@tags`           | `Array of String` | Set a list of service tags to filter the query results. For a service to pass the tag filter it must have all of the required tags, and none of the excluded tags (prefixed with \`\!\`).                                                                                                                                                                                                                                                                             |
| `@templateRegexp` | `String`          | Set regular expression which is used to extract fields from the entire name, once this template is selected.                                                                                                                                                                                                                                                                                                                                                          |
| `@templateType`   | `String`          | The template type, which must be `name_prefix_match`. This means that the template will apply to any query lookup with a name whose prefix matches the Name field of the template.                                                                                                                                                                                                                                                                                    |
| `@token`          | `String`          | Set the ACL token to use each time the query is executed. This allows queries to be executed by clients with lesser or even no ACL Token, so this should be used with care.                                                                                                                                                                                                                                                                                           |

# PreparedQueryExecuteOptions

Options used to execute prepared query

|          |                |                                                                                                                                                                                                                                                                                     |
| -------- | -------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Name     | Type           | Description                                                                                                                                                                                                                                                                         |
| `@limit` | `Number (int)` | Set the size of the list to the given number of nodes. This is applied after any sorting or shuffling.                                                                                                                                                                              |
| `@near`  | `String`       | Set node name for sorting the list in ascending order based on the estimated round trip time from that node. Passing `_agent` will use the agent's node for the sort. If this is not present, the default behavior will shuffle the nodes randomly each time the query is executed. |

# PreparedQueryExecuteResponse

The results of executing prepared query

|              |                         |                                                                                                                                                          |
| ------------ | ----------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Name         | Type                    | Description                                                                                                                                              |
| `@dc`        | `String`                | Set the datacenter that ultimately provided the list of nodes                                                                                            |
| `@dnsTtl`    | `String`                | Set the TTL duration when query results are served over DNS. If this is specified, it will take precedence over any Consul agent-specific configuration. |
| `@failovers` | `Number (int)`          | Set the number of remote datacenters that were queried while executing the query.                                                                        |
| `@nodes`     | `Array of ServiceEntry` | Set the list of healthy nodes providing the given service, as specified by the constraints of the prepared query.                                        |
| `@service`   | `String`                | Set the service name that the query was selecting. This is useful for context in case an empty list of nodes is returned.                                |

# Service

Holds properties of service and node that its containing

|                |                   |                                                                 |
| -------------- | ----------------- | --------------------------------------------------------------- |
| Name           | Type              | Description                                                     |
| `@address`     | `String`          | Set service address                                             |
| `@id`          | `String`          | Set ID of service                                               |
| `@meta`        | `String`          | Specifies arbitrary KV metadata linked to the service instance. |
| `@name`        | `String`          | Set service name                                                |
| `@node`        | `String`          | Set node name                                                   |
| `@nodeAddress` | `String`          | Set node address                                                |
| `@port`        | `Number (int)`    | Set service port                                                |
| `@tags`        | `Array of String` | Set list of service tags                                        |

# ServiceEntry

Holds properties of service, node and related checks

|            |                  |                    |
| ---------- | ---------------- | ------------------ |
| Name       | Type             | Description        |
| `@checks`  | `Array of Check` | Set list of checks |
| `@node`    | `Node`           | Set node           |
| `@service` | `Service`        | Set service        |

# ServiceEntryList

Holds list of services, nodes and related checks

|          |                         |                                                                                                        |
| -------- | ----------------------- | ------------------------------------------------------------------------------------------------------ |
| Name     | Type                    | Description                                                                                            |
| `@index` | `Number (long)`         | Set Consul index, a unique identifier representing the current state of the requested list of services |
| `@list`  | `Array of ServiceEntry` | Set list of services                                                                                   |

# ServiceList

Holds result of services query

|          |                    |                                                                                                        |
| -------- | ------------------ | ------------------------------------------------------------------------------------------------------ |
| Name     | Type               | Description                                                                                            |
| `@index` | `Number (long)`    | Set Consul index, a unique identifier representing the current state of the requested list of services |
| `@list`  | `Array of Service` | Set list of services                                                                                   |

# ServiceOptions

Options used to register service.

|                     |                         |                                                                 |
| ------------------- | ----------------------- | --------------------------------------------------------------- |
| Name                | Type                    | Description                                                     |
| `@address`          | `String`                | Set service address                                             |
| `@checkListOptions` | `Array of CheckOptions` | Set checks options of service                                   |
| `@checkOptions`     | `CheckOptions`          | Set check options of service                                    |
| `@id`               | `String`                | Set the ID of session                                           |
| `@meta`             | `String`                | Specifies arbitrary KV metadata linked to the service instance. |
| `@name`             | `String`                | Set service name                                                |
| `@port`             | `Number (int)`          | Set service port                                                |
| `@tags`             | `Array of String`       | Set list of tags associated with service                        |

# ServiceQueryOptions

Options used to requesting list of services

|                    |                        |                                                                                                              |
| ------------------ | ---------------------- | ------------------------------------------------------------------------------------------------------------ |
| Name               | Type                   | Description                                                                                                  |
| `@blockingOptions` | `BlockingQueryOptions` | Set options for blocking query                                                                               |
| `@near`            | `String`               | Set node name for sorting the list in ascending order based on the estimated round trip time from that node. |
| `@tag`             | `String`               | Set tag for filtering request results                                                                        |

# Session

Holds properties of Consul sessions

|                |                   |                                          |
| -------------- | ----------------- | ---------------------------------------- |
| Name           | Type              | Description                              |
| `@checks`      | `Array of String` | Set the list of associated health checks |
| `@createIndex` | `Number (long)`   | Set the create index of session          |
| `@id`          | `String`          | Set the ID of node                       |
| `@index`       | `Number (long)`   | Set Consul index                         |
| `@lockDelay`   | `Number (long)`   | Set the Lock delay of session            |
| `@node`        | `String`          | Set the ID of node                       |

# SessionList

Holds result of sessions query

|          |                    |                                                                                                        |
| -------- | ------------------ | ------------------------------------------------------------------------------------------------------ |
| Name     | Type               | Description                                                                                            |
| `@index` | `Number (long)`    | Set Consul index, a unique identifier representing the current state of the requested list of sessions |
| `@list`  | `Array of Session` | Set list of sessions                                                                                   |

# SessionOptions

Options used to create session.

<table>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 50%" />
</colgroup>
<tbody>
<tr class="odd">
<td><p>Name</p></td>
<td><p>Type</p></td>
<td><p>Description</p></td>
</tr>
<tr class="even">
<td><p><code>@behavior</code></p></td>
<td><p><code>SessionBehavior</code></p></td>
<td><p>Set the behavior when a session is invalidated. The release behavior is the default if none is specified.</p></td>
</tr>
<tr class="odd">
<td><p><code>@checks</code></p></td>
<td><p><code>Array of String</code></p></td>
<td><p>Set a list of associated health checks. It is highly recommended that, if you override this list, you include the default "serfHealth"</p></td>
</tr>
<tr class="even">
<td><p><code>@lockDelay</code></p></td>
<td><p><code>Number (long)</code></p></td>
<td><p>Set the lock-delay period.</p></td>
</tr>
<tr class="odd">
<td><p><code>@name</code></p></td>
<td><p><code>String</code></p></td>
<td><p>Set the human-readable name for the Session</p></td>
</tr>
<tr class="even">
<td><p><code>@node</code></p></td>
<td><p><code>String</code></p></td>
<td><p>Set the node to which the session will be assigned</p></td>
</tr>
<tr class="odd">
<td><p><code>@ttl</code></p></td>
<td><p><code>Number (long)</code></p></td>
<td><p>Set the TTL interval. When TTL interval expires without being renewed, the session has expired and an invalidation is triggered. If specified, it must be between <code>10s</code> and <code>86400s</code> currently.</p>
<p>The contract of a TTL is that it represents a lower bound for invalidation; that is, Consul will not expire the session before the TTL is reached, but it is allowed to delay the expiration past the TTL.</p>
<p>The lowest practical TTL should be used to keep the number of managed sessions low. When locks are forcibly expired, such as during a leader election, sessions may not be reaped for up to double this TTL, so long TTL values (&gt; 1 hour) should be avoided.</p></td>
</tr>
</tbody>
</table>

# TxnError

Holds information describing which operations failed if the transaction
was rolled back.

|            |                |                                                          |
| ---------- | -------------- | -------------------------------------------------------- |
| Name       | Type           | Description                                              |
| `@opIndex` | `Number (int)` | Set the index of the failed operation in the transaction |
| `@what`    | `String`       | Set error message about why that operation failed.       |

# TxnKVOperation

Holds operation to apply to the key/value store inside a transaction

|            |                 |                                                                                                                 |
| ---------- | --------------- | --------------------------------------------------------------------------------------------------------------- |
| Name       | Type            | Description                                                                                                     |
| `@flags`   | `Number (long)` | Set the flags attached to this entry. Clients can choose to use this however makes sense for their application. |
| `@index`   | `Number (long)` | Set the index used for locking, unlocking, and check-and-set operations.                                        |
| `@key`     | `String`        | Set the key                                                                                                     |
| `@session` | `String`        | Set the session used for locking, unlocking, and check-and-set operations.                                      |
| `@type`    | `TxnKVVerb`     | Set the type of operation to perform                                                                            |
| `@value`   | `String`        | Set the value                                                                                                   |

# TxnRequest

Holds list of operations in transaction

|                   |                |                                                  |
| ----------------- | -------------- | ------------------------------------------------ |
| Name              | Type           | Description                                      |
| `@operationsSize` | `Number (int)` | Returns the number of operations in this request |

# TxnResponse

Holds results of transaction

|                |                     |                                                |
| -------------- | ------------------- | ---------------------------------------------- |
| Name           | Type                | Description                                    |
| `@errors`      | `Array of TxnError` | Adds error to this response                    |
| `@errorsSize`  | `Number (int)`      | Returns the number of errors in this response  |
| `@resultsSize` | `Number (int)`      | Returns the number of results in this response |
