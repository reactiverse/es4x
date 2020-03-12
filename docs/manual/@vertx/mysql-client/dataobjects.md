# MySQLAuthOptions

Authentication options for MySQL authentication which can be used for
CHANGE\_USER command.

|                            |          |                                                                                                                                                |
| -------------------------- | -------- | ---------------------------------------------------------------------------------------------------------------------------------------------- |
| Name                       | Type     | Description                                                                                                                                    |
| `@charset`                 | `String` | Set the charset for the connection.                                                                                                            |
| `@collation`               | `String` | Set the collation for the connection.                                                                                                          |
| `@database`                | `String` | Specify the default database for the re-authentication.                                                                                        |
| `@password`                | `String` | Specify the user password to be used for the authentication.                                                                                   |
| `@properties`              | `String` | Set connection attributes which will be sent to server at the re-authentication.                                                               |
| `@serverRsaPublicKeyPath`  | `String` | Set the path of server RSA public key which is mostly used for encrypting password under insecure connections when performing authentication.  |
| `@serverRsaPublicKeyValue` | `Buffer` | Set the value of server RSA public key which is mostly used for encrypting password under insecure connections when performing authentication. |
| `@user`                    | `String` | Specify the user account to be used for the authentication.                                                                                    |

# MySQLConnectOptions

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
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@characterEncoding</code></p></td>
<td><p><code>String</code></p></td>
<td><p>Set the Java charset for encoding string values, this value is UTF-8 by default.</p></td>
</tr>
<tr class="even">
<td><p><code>@charset</code></p></td>
<td><p><code>String</code></p></td>
<td><p>Set the charset for the connection.</p></td>
</tr>
<tr class="odd">
<td><p><code>@collation</code></p></td>
<td><p><code>String</code></p></td>
<td><p>Set the collation for the connection.</p></td>
</tr>
<tr class="even">
<td><p><code>@connectTimeout</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@crlPaths</code></p></td>
<td><p><code>Array of String</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@crlValues</code></p></td>
<td><p><code>Array of Buffer</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@database</code></p></td>
<td><p><code>String</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@enabledCipherSuites</code></p></td>
<td><p><code>Array of String</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@enabledSecureTransportProtocols</code></p></td>
<td><p><code>Array of String</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@host</code></p></td>
<td><p><code>String</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@hostnameVerificationAlgorithm</code></p></td>
<td><p><code>String</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@idleTimeout</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@idleTimeoutUnit</code></p></td>
<td><p><code>TimeUnit</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@jdkSslEngineOptions</code></p></td>
<td><p><code>JdkSSLEngineOptions</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@keyStoreOptions</code></p></td>
<td><p><code>JksOptions</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@localAddress</code></p></td>
<td><p><code>String</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@logActivity</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@metricsName</code></p></td>
<td><p><code>String</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@openSslEngineOptions</code></p></td>
<td><p><code>OpenSSLEngineOptions</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@password</code></p></td>
<td><p><code>String</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@pemKeyCertOptions</code></p></td>
<td><p><code>PemKeyCertOptions</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@pemTrustOptions</code></p></td>
<td><p><code>PemTrustOptions</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@pfxKeyCertOptions</code></p></td>
<td><p><code>PfxOptions</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@pfxTrustOptions</code></p></td>
<td><p><code>PfxOptions</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@port</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@preparedStatementCacheMaxSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@preparedStatementCacheSqlLimit</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@properties</code></p></td>
<td><p><code>String</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@proxyOptions</code></p></td>
<td><p><code>ProxyOptions</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@receiveBufferSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@reconnectAttempts</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@reconnectInterval</code></p></td>
<td><p><code>Number (long)</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@reuseAddress</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@reusePort</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@sendBufferSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@serverRsaPublicKeyPath</code></p></td>
<td><p><code>String</code></p></td>
<td><p>Set the path of server RSA public key which is mostly used for encrypting password under insecure connections when performing authentication.</p></td>
</tr>
<tr class="odd">
<td><p><code>@serverRsaPublicKeyValue</code></p></td>
<td><p><code>Buffer</code></p></td>
<td><p>Set the value of server RSA public key which is mostly used for encrypting password under insecure connections when performing authentication.</p></td>
</tr>
<tr class="even">
<td><p><code>@soLinger</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@ssl</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@sslHandshakeTimeout</code></p></td>
<td><p><code>Number (long)</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@sslHandshakeTimeoutUnit</code></p></td>
<td><p><code>TimeUnit</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@sslMode</code></p></td>
<td><p><code>SslMode</code></p></td>
<td><p>Set the link for the client, this option can be used to specify the desired security state of the connection to the server.</p></td>
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
<td><p><code>@useAffectedRows</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Sets how affected rows are calculated on update/delete/insert, if set to <code>true</code> an update that effectively does not change any data returns zero affected rows.</p>
<p>See mysql-affected-rows for details.</p></td>
</tr>
<tr class="even">
<td><p><code>@useAlpn</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@usePooledBuffers</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@user</code></p></td>
<td><p><code>String</code></p></td>
<td><p>-</p></td>
</tr>
</tbody>
</table>
