# PoolOptions

The options for configuring a connection pool.

|                     |                |                                                                                                                                                                                                      |
| ------------------- | -------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Name                | Type           | Description                                                                                                                                                                                          |
| `@maxSize`          | `Number (int)` | Set the maximum pool size                                                                                                                                                                            |
| `@maxWaitQueueSize` | `Number (int)` | Set the maximum connection request allowed in the wait queue, any requests beyond the max size will result in an failure. If the value is set to a negative number then the queue will be unbounded. |

# SqlConnectOptions

Connect options for configuring link or link.

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
<td><p><code>@cachePreparedStatements</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether prepared statements cache should be enabled.</p></td>
</tr>
<tr class="odd">
<td><p><code>@connectTimeout</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@crlPaths</code></p></td>
<td><p><code>Array of String</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@crlValues</code></p></td>
<td><p><code>Array of Buffer</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@database</code></p></td>
<td><p><code>String</code></p></td>
<td><p>Specify the default database for the connection.</p></td>
</tr>
<tr class="odd">
<td><p><code>@enabledCipherSuites</code></p></td>
<td><p><code>Array of String</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@enabledSecureTransportProtocols</code></p></td>
<td><p><code>Array of String</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@host</code></p></td>
<td><p><code>String</code></p></td>
<td><p>Specify the host for connecting to the server.</p></td>
</tr>
<tr class="even">
<td><p><code>@hostnameVerificationAlgorithm</code></p></td>
<td><p><code>String</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@idleTimeout</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@idleTimeoutUnit</code></p></td>
<td><p><code>TimeUnit</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@jdkSslEngineOptions</code></p></td>
<td><p><code>JdkSSLEngineOptions</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@keyStoreOptions</code></p></td>
<td><p><code>JksOptions</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@localAddress</code></p></td>
<td><p><code>String</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@logActivity</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@metricsName</code></p></td>
<td><p><code>String</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@openSslEngineOptions</code></p></td>
<td><p><code>OpenSSLEngineOptions</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@password</code></p></td>
<td><p><code>String</code></p></td>
<td><p>Specify the user password to be used for the authentication.</p></td>
</tr>
<tr class="even">
<td><p><code>@pemKeyCertOptions</code></p></td>
<td><p><code>PemKeyCertOptions</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@pemTrustOptions</code></p></td>
<td><p><code>PemTrustOptions</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@pfxKeyCertOptions</code></p></td>
<td><p><code>PfxOptions</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@pfxTrustOptions</code></p></td>
<td><p><code>PfxOptions</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@port</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Specify the port for connecting to the server.</p></td>
</tr>
<tr class="odd">
<td><p><code>@preparedStatementCacheMaxSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the maximum number of prepared statements that the connection will cache.</p></td>
</tr>
<tr class="even">
<td><p><code>@preparedStatementCacheSqlLimit</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the maximum length of prepared statement SQL string that the connection will cache.</p>
<p>This is an helper setting the link.</p></td>
</tr>
<tr class="odd">
<td><p><code>@properties</code></p></td>
<td><p><code>String</code></p></td>
<td><p>Set properties for this client, which will be sent to server at the connection start.</p></td>
</tr>
<tr class="even">
<td><p><code>@proxyOptions</code></p></td>
<td><p><code>ProxyOptions</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@receiveBufferSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@reconnectAttempts</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@reconnectInterval</code></p></td>
<td><p><code>Number (long)</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@reuseAddress</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@reusePort</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@sendBufferSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@soLinger</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@ssl</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>-</p></td>
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
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@tcpFastOpen</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@tcpKeepAlive</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@tcpNoDelay</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@tcpQuickAck</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@trafficClass</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@trustAll</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@trustStoreOptions</code></p></td>
<td><p><code>JksOptions</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@useAlpn</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@usePooledBuffers</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@user</code></p></td>
<td><p><code>String</code></p></td>
<td><p>Specify the user account to be used for the authentication.</p></td>
</tr>
</tbody>
</table>
