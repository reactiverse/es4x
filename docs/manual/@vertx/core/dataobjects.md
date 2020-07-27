# AddressResolverOptions

Configuration options for Vert.x hostname resolver. The resolver uses
the local

hosts

file and performs DNS

A

and

AAAA

queries.

|                            |                   |                                                                                                                                                                                                                                                                                                                                                                                          |
| -------------------------- | ----------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Name                       | Type              | Description                                                                                                                                                                                                                                                                                                                                                                              |
| `@cacheMaxTimeToLive`      | `Number (int)`    | Set the cache maximum TTL value in seconds. After successful resolution IP addresses are cached with their DNS response TTL, use this to set a maximum value to all responses TTL.                                                                                                                                                                                                       |
| `@cacheMinTimeToLive`      | `Number (int)`    | Set the cache minimum TTL value in seconds. After resolution successful IP addresses are cached with their DNS response TTL, use this to set a minimum value to all responses TTL.                                                                                                                                                                                                       |
| `@cacheNegativeTimeToLive` | `Number (int)`    | Set the negative cache TTL value in seconds. After a failed hostname resolution, DNS queries won't be retried for a period of time equals to the negative TTL. This allows to reduce the response time of negative replies and reduce the amount of messages to DNS servers.                                                                                                             |
| `@hostsPath`               | `String`          | Set the path of an alternate hosts configuration file to use instead of the one provided by the os. The default value is null, so the operating system hosts config is used.                                                                                                                                                                                                             |
| `@hostsValue`              | `Buffer`          | Set an alternate hosts configuration file to use instead of the one provided by the os. The value should contain the hosts content literaly, for instance 127.0.0.1 localhost The default value is null, so the operating system hosts config is used.                                                                                                                                   |
| `@maxQueries`              | `Number (int)`    | Set the maximum number of queries when an hostname is resolved.                                                                                                                                                                                                                                                                                                                          |
| `@ndots`                   | `Number (int)`    | Set the ndots value used when resolving using search domains, the default value is `-1` which determines the value from the OS on Linux or uses the value `1`.                                                                                                                                                                                                                           |
| `@optResourceEnabled`      | `Boolean`         | Set to true to enable the automatic inclusion in DNS queries of an optional record that hints the remote DNS server about how much data the resolver can read per response.                                                                                                                                                                                                              |
| `@queryTimeout`            | `Number (long)`   | Set the query timeout in milliseconds, i.e the amount of time after a query is considered to be failed.                                                                                                                                                                                                                                                                                  |
| `@rdFlag`                  | `Boolean`         | Set the DNS queries Recursion Desired flag value.                                                                                                                                                                                                                                                                                                                                        |
| `@rotateServers`           | `Boolean`         | Set to `true` to enable round-robin selection of the dns server to use. It spreads the query load among the servers and avoids all lookup to hit the first server of the list.                                                                                                                                                                                                           |
| `@searchDomains`           | `Array of String` | Set the lists of DNS search domains. When the search domain list is null, the effective search domain list will be populated using the system DNS search domains.                                                                                                                                                                                                                        |
| `@servers`                 | `Array of String` | Set the list of DNS server addresses, an address is the IP of the dns server, followed by an optional colon and a port, e.g `8.8.8.8` or {code 192.168.0.1:40000}. When the list is empty, the resolver will use the list of the system DNS server addresses from the environment, if that list cannot be retrieved it will use Google's public DNS servers `"8.8.8.8"` and `"8.8.4.4"`. |

# Argument

Defines a command line argument. Unlike options, argument don't have
names and are identified using an index. The first index is 0 (because
we are in the computer world).

|                 |                |                                                                                                                 |
| --------------- | -------------- | --------------------------------------------------------------------------------------------------------------- |
| Name            | Type           | Description                                                                                                     |
| `@argName`      | `String`       | Sets the argument name of this link.                                                                            |
| `@defaultValue` | `String`       | Sets the default value of this link.                                                                            |
| `@description`  | `String`       | Sets the description of the link.                                                                               |
| `@hidden`       | `Boolean`      | Sets whether or not the current link is hidden.                                                                 |
| `@index`        | `Number (int)` | Sets the argument index.                                                                                        |
| `@multiValued`  | `Boolean`      | Sets whether or not the argument can receive several values. Only the last argument can receive several values. |
| `@required`     | `Boolean`      | Sets whether or not the current link is required.                                                               |

# ClientOptionsBase

Base class for Client options

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
<td><p><code>@enabledCipherSuites</code></p></td>
<td><p><code>Array of String</code></p></td>
<td><p>Add an enabled cipher suite, appended to the ordered suites.</p></td>
</tr>
<tr class="even">
<td><p><code>@enabledSecureTransportProtocols</code></p></td>
<td><p><code>Array of String</code></p></td>
<td><p>Sets the list of enabled SSL/TLS protocols.</p></td>
</tr>
<tr class="odd">
<td><p><code>@idleTimeout</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the idle timeout, default time unit is seconds. Zero means don't timeout. This determines if a connection will timeout and be closed if no data is received within the timeout.</p>
<p>If you want change default time unit, use link</p></td>
</tr>
<tr class="even">
<td><p><code>@idleTimeoutUnit</code></p></td>
<td><p><code>TimeUnit</code></p></td>
<td><p>Set the idle timeout unit. If not specified, default is seconds.</p></td>
</tr>
<tr class="odd">
<td><p><code>@jdkSslEngineOptions</code></p></td>
<td><p><code>JdkSSLEngineOptions</code></p></td>
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
<td><p>Set the trust options in pem format</p></td>
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
<td><p><code>@soLinger</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set whether SO_linger keep alive is enabled</p></td>
</tr>
<tr class="odd">
<td><p><code>@ssl</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether SSL/TLS is enabled</p></td>
</tr>
<tr class="even">
<td><p><code>@sslHandshakeTimeout</code></p></td>
<td><p><code>Number (long)</code></p></td>
<td><p>Set the SSL handshake timeout, default time unit is seconds.</p></td>
</tr>
<tr class="odd">
<td><p><code>@sslHandshakeTimeoutUnit</code></p></td>
<td><p><code>TimeUnit</code></p></td>
<td><p>Set the SSL handshake timeout unit. If not specified, default is seconds.</p></td>
</tr>
<tr class="even">
<td><p><code>@tcpCork</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Enable the <code>TCP_CORK</code> option - only with linux native transport.</p></td>
</tr>
<tr class="odd">
<td><p><code>@tcpFastOpen</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Enable the <code>TCP_FASTOPEN</code> option - only with linux native transport.</p></td>
</tr>
<tr class="even">
<td><p><code>@tcpKeepAlive</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether TCP keep alive is enabled</p></td>
</tr>
<tr class="odd">
<td><p><code>@tcpNoDelay</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether TCP no delay is enabled</p></td>
</tr>
<tr class="even">
<td><p><code>@tcpQuickAck</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Enable the <code>TCP_QUICKACK</code> option - only with linux native transport.</p></td>
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
<td><p><code>@useAlpn</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set the ALPN usage.</p></td>
</tr>
<tr class="odd">
<td><p><code>@usePooledBuffers</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether Netty pooled buffers are enabled</p></td>
</tr>
</tbody>
</table>

# CopyOptions

Describes the copy (and move) options.

|                    |           |                                                                                                    |
| ------------------ | --------- | -------------------------------------------------------------------------------------------------- |
| Name               | Type      | Description                                                                                        |
| `@atomicMove`      | `Boolean` | Whether move should be performed as an atomic filesystem operation. Defaults to `false`.           |
| `@copyAttributes`  | `Boolean` | Whether the file attributes should be copied. Defaults to `false`.                                 |
| `@nofollowLinks`   | `Boolean` | Whether symbolic links should not be followed during copy or move operations. Defaults to `false`. |
| `@replaceExisting` | `Boolean` | Whether an existing file, empty directory, or link should be replaced. Defaults to `false`.        |

# DatagramSocketOptions

Options used to configure a datagram socket.

|                              |                |                                                                                                                |
| ---------------------------- | -------------- | -------------------------------------------------------------------------------------------------------------- |
| Name                         | Type           | Description                                                                                                    |
| `@broadcast`                 | `Boolean`      | Set if the socket can send or receive broadcast packets                                                        |
| `@ipV6`                      | `Boolean`      | Set if IP v6 should be used                                                                                    |
| `@logActivity`               | `Boolean`      | Set to true to enabled network activity logging: Netty's pipeline is configured for logging on Netty's logger. |
| `@loopbackModeDisabled`      | `Boolean`      | Set if loopback mode is disabled                                                                               |
| `@multicastNetworkInterface` | `String`       | Set the multicast network interface address                                                                    |
| `@multicastTimeToLive`       | `Number (int)` | Set the multicast ttl value                                                                                    |
| `@receiveBufferSize`         | `Number (int)` | Set the TCP receive buffer size                                                                                |
| `@reuseAddress`              | `Boolean`      | Set the value of reuse address                                                                                 |
| `@reusePort`                 | `Boolean`      | Set the value of reuse port. This is only supported by native transports.                                      |
| `@sendBufferSize`            | `Number (int)` | Set the TCP send buffer size                                                                                   |
| `@trafficClass`              | `Number (int)` | Set the value of traffic class                                                                                 |

# DeliveryOptions

Delivery options are used to configure message delivery.

Delivery options allow to configure delivery timeout and message codec
name, and to provide any headers that you wish to send with the message.

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
<td><p><code>@codecName</code></p></td>
<td><p><code>String</code></p></td>
<td><p>Set the codec name.</p></td>
</tr>
<tr class="odd">
<td><p><code>@headers</code></p></td>
<td><p><code>String</code></p></td>
<td><p>Add a message header. Message headers can be sent with any message and will be accessible with link at the recipient.</p></td>
</tr>
<tr class="even">
<td><p><code>@localOnly</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Whether a message should be delivered to local consumers only. Defaults to <code>false</code>.</p>
<p>This option is effective in clustered mode only and does not apply to reply messages.</p></td>
</tr>
<tr class="odd">
<td><p><code>@sendTimeout</code></p></td>
<td><p><code>Number (long)</code></p></td>
<td><p>Set the send timeout.</p></td>
</tr>
</tbody>
</table>

# DeploymentOptions

Options for configuring a verticle deployment.

|                             |                   |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| --------------------------- | ----------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Name                        | Type              | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| `@config`                   | `Json object`     | Set the JSON configuration that will be passed to the verticle(s) when it's deployed                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| `@extraClasspath`           | `Array of String` | Set any extra classpath to be used when deploying the verticle. Ignored if no isolation group is set.                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| `@ha`                       | `Boolean`         | Set whether the verticle(s) will be deployed as HA.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| `@instances`                | `Number (int)`    | Set the number of instances that should be deployed.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| `@isolatedClasses`          | `Array of String` | Set the isolated class names.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| `@isolationGroup`           | `String`          | Set the isolation group that will be used when deploying the verticle(s)                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| `@maxWorkerExecuteTime`     | `Number (long)`   | Sets the value of max worker execute time, in link. The default value of link is                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| `@maxWorkerExecuteTimeUnit` | `TimeUnit`        | Set the time unit of `maxWorkerExecuteTime`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| `@multiThreaded`            | `Boolean`         | Set whether the verticle(s) should be deployed as a multi-threaded worker verticle. WARNING: Multi-threaded worker verticles are a deprecated feature. Most applications will have no need for them. Because of the concurrency in these verticles you have to be very careful to keep the verticle in a consistent state using standard Java techniques for multi-threaded programming. You can read the documentation that explains how you can replace this feature by the usage of custom worker pools or `executeBlocking` calls. |
| `@worker`                   | `Boolean`         | Set whether the verticle(s) should be deployed as a worker verticle                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| `@workerPoolName`           | `String`          | Set the worker pool name to use for this verticle. When no name is set, the Vert.x worker pool will be used, when a name is set, the verticle will use a named worker pool.                                                                                                                                                                                                                                                                                                                                                            |
| `@workerPoolSize`           | `Number (int)`    | Set the maximum number of worker threads to be used by the Vert.x instance.                                                                                                                                                                                                                                                                                                                                                                                                                                                            |

# DnsClientOptions

Configuration options for Vert.x DNS client.

|                     |                 |                                                                                                                |
| ------------------- | --------------- | -------------------------------------------------------------------------------------------------------------- |
| Name                | Type            | Description                                                                                                    |
| `@host`             | `String`        | Set the host name to be used by this client in requests.                                                       |
| `@logActivity`      | `Boolean`       | Set to true to enabled network activity logging: Netty's pipeline is configured for logging on Netty's logger. |
| `@port`             | `Number (int)`  | Set the port to be used by this client in requests.                                                            |
| `@queryTimeout`     | `Number (long)` | Set the query timeout in milliseconds, i.e the amount of time after a query is considered to be failed.        |
| `@recursionDesired` | `Boolean`       | Set whether or not recursion is desired                                                                        |

# EventBusOptions

Options to configure the event bus.

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
<td><p><code>@acceptBacklog</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the accept back log.</p></td>
</tr>
<tr class="odd">
<td><p><code>@clientAuth</code></p></td>
<td><p><code>ClientAuth</code></p></td>
<td><p>Set whether client auth is required</p></td>
</tr>
<tr class="even">
<td><p><code>@clusterPingInterval</code></p></td>
<td><p><code>Number (long)</code></p></td>
<td><p>Set the value of cluster ping interval, in ms.</p></td>
</tr>
<tr class="odd">
<td><p><code>@clusterPingReplyInterval</code></p></td>
<td><p><code>Number (long)</code></p></td>
<td><p>Set the value of cluster ping reply interval, in ms.</p></td>
</tr>
<tr class="even">
<td><p><code>@clusterPublicHost</code></p></td>
<td><p><code>String</code></p></td>
<td><p>Set the public facing hostname to be used for clustering. Sometimes, e.g. when running on certain clouds, the local address the server listens on for clustering is not the same address that other nodes connect to it at, as the OS / cloud infrastructure does some kind of proxying. If this is the case you can specify a public hostname which is different from the hostname the server listens at. The default value is null which means use the same as the cluster hostname.</p></td>
</tr>
<tr class="odd">
<td><p><code>@clusterPublicPort</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>See link for an explanation.</p></td>
</tr>
<tr class="even">
<td><p><code>@clustered</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Sets whether or not the event bus is clustered.</p></td>
</tr>
<tr class="odd">
<td><p><code>@connectTimeout</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Sets the connect timeout</p></td>
</tr>
<tr class="even">
<td><p><code>@crlPaths</code></p></td>
<td><p><code>Array of String</code></p></td>
<td><p>Add a CRL path</p></td>
</tr>
<tr class="odd">
<td><p><code>@crlValues</code></p></td>
<td><p><code>Array of Buffer</code></p></td>
<td><p>Add a CRL value</p></td>
</tr>
<tr class="even">
<td><p><code>@enabledCipherSuites</code></p></td>
<td><p><code>Array of String</code></p></td>
<td><p>Add an enabled cipher suite, appended to the ordered suites.</p></td>
</tr>
<tr class="odd">
<td><p><code>@enabledSecureTransportProtocols</code></p></td>
<td><p><code>Array of String</code></p></td>
<td><p>Sets the list of enabled SSL/TLS protocols.</p></td>
</tr>
<tr class="even">
<td><p><code>@host</code></p></td>
<td><p><code>String</code></p></td>
<td><p>Sets the host.</p></td>
</tr>
<tr class="odd">
<td><p><code>@idleTimeout</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the idle timeout, default time unit is seconds. Zero means don't timeout. This determines if a connection will timeout and be closed if no data is received within the timeout.</p>
<p>If you want change default time unit, use link</p></td>
</tr>
<tr class="even">
<td><p><code>@idleTimeoutUnit</code></p></td>
<td><p><code>TimeUnit</code></p></td>
<td><p>Set the idle timeout unit. If not specified, default is seconds.</p></td>
</tr>
<tr class="odd">
<td><p><code>@jdkSslEngineOptions</code></p></td>
<td><p><code>JdkSSLEngineOptions</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@keyStoreOptions</code></p></td>
<td><p><code>JksOptions</code></p></td>
<td><p>Set the key/cert options in jks format, aka Java keystore.</p></td>
</tr>
<tr class="odd">
<td><p><code>@logActivity</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set to true to enabled network activity logging: Netty's pipeline is configured for logging on Netty's logger.</p></td>
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
<td><p>Set the trust options in pem format</p></td>
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
<td><p><code>@port</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Sets the port.</p></td>
</tr>
<tr class="even">
<td><p><code>@receiveBufferSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the TCP receive buffer size</p></td>
</tr>
<tr class="odd">
<td><p><code>@reconnectAttempts</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Sets the value of reconnect attempts.</p></td>
</tr>
<tr class="even">
<td><p><code>@reconnectInterval</code></p></td>
<td><p><code>Number (long)</code></p></td>
<td><p>Set the reconnect interval.</p></td>
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
<td><p><code>@soLinger</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set whether SO_linger keep alive is enabled</p></td>
</tr>
<tr class="odd">
<td><p><code>@ssl</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether SSL/TLS is enabled</p></td>
</tr>
<tr class="even">
<td><p><code>@sslHandshakeTimeout</code></p></td>
<td><p><code>Number (long)</code></p></td>
<td><p>Set the SSL handshake timeout, default time unit is seconds.</p></td>
</tr>
<tr class="odd">
<td><p><code>@sslHandshakeTimeoutUnit</code></p></td>
<td><p><code>TimeUnit</code></p></td>
<td><p>Set the SSL handshake timeout unit. If not specified, default is seconds.</p></td>
</tr>
<tr class="even">
<td><p><code>@tcpCork</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Enable the <code>TCP_CORK</code> option - only with linux native transport.</p></td>
</tr>
<tr class="odd">
<td><p><code>@tcpFastOpen</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Enable the <code>TCP_FASTOPEN</code> option - only with linux native transport.</p></td>
</tr>
<tr class="even">
<td><p><code>@tcpKeepAlive</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether TCP keep alive is enabled</p></td>
</tr>
<tr class="odd">
<td><p><code>@tcpNoDelay</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether TCP no delay is enabled</p></td>
</tr>
<tr class="even">
<td><p><code>@tcpQuickAck</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Enable the <code>TCP_QUICKACK</code> option - only with linux native transport.</p></td>
</tr>
<tr class="odd">
<td><p><code>@trafficClass</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the value of traffic class</p></td>
</tr>
<tr class="even">
<td><p><code>@trustAll</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether all server certificates should be trusted.</p></td>
</tr>
<tr class="odd">
<td><p><code>@trustStoreOptions</code></p></td>
<td><p><code>JksOptions</code></p></td>
<td><p>Set the trust options in jks format, aka Java truststore</p></td>
</tr>
<tr class="even">
<td><p><code>@useAlpn</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set the ALPN usage.</p></td>
</tr>
<tr class="odd">
<td><p><code>@usePooledBuffers</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether Netty pooled buffers are enabled</p></td>
</tr>
</tbody>
</table>

# FileSystemOptions

Vert.x file system base configuration, this class can be extended by
provider implementations to configure those specific implementations.

|                              |           |                                                                                                                                                                                         |
| ---------------------------- | --------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Name                         | Type      | Description                                                                                                                                                                             |
| `@classPathResolvingEnabled` | `Boolean` | When vert.x cannot find the file on the filesystem it tries to resolve the file from the class path when this is set to `true`.                                                         |
| `@fileCacheDir`              | `String`  | When vert.x reads a file that is packaged with the application it gets extracted to this directory first and subsequent reads will use the extracted file to get better IO performance. |
| `@fileCachingEnabled`        | `Boolean` | Set to `true` to cache files on the real file system when the filesystem performs class path resolving.                                                                                 |

# GoAway

A frame.

|                 |                 |                               |
| --------------- | --------------- | ----------------------------- |
| Name            | Type            | Description                   |
| `@debugData`    | `Buffer`        | Set the additional debug data |
| `@errorCode`    | `Number (long)` |                               |
| `@lastStreamId` | `Number (int)`  | Set the last stream id.       |

# Http2Settings

HTTP2 settings, the settings is initialized with the default HTTP/2
values.

The settings expose the parameters defined by the HTTP/2 specification,
as well as extra settings for protocol extensions.

|                         |                 |                        |
| ----------------------- | --------------- | ---------------------- |
| Name                    | Type            | Description            |
| `@headerTableSize`      | `Number (long)` | Set HTTP/2 setting.    |
| `@initialWindowSize`    | `Number (int)`  | Set the HTTP/2 setting |
| `@maxConcurrentStreams` | `Number (long)` | Set the HTTP/2 setting |
| `@maxFrameSize`         | `Number (int)`  | Set the HTTP/2 setting |
| `@maxHeaderListSize`    | `Number (long)` | Set the HTTP/2 setting |
| `@pushEnabled`          | `Boolean`       | Set the HTTP/2 setting |

# HttpClientOptions

Options describing how an link will make connections.

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
<td><p><code>@alpnVersions</code></p></td>
<td><p><code>Array of HttpVersion</code></p></td>
<td><p>Set the list of protocol versions to provide to the server during the Application-Layer Protocol Negotiation. When the list is empty, the client provides a best effort list according to link:</p>
<p>: [ "h2", "http/1.1" ] otherwise: [link]</p></td>
</tr>
<tr class="odd">
<td><p><code>@connectTimeout</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the connect timeout</p></td>
</tr>
<tr class="even">
<td><p><code>@crlPaths</code></p></td>
<td><p><code>Array of String</code></p></td>
<td><p>Add a CRL path</p></td>
</tr>
<tr class="odd">
<td><p><code>@crlValues</code></p></td>
<td><p><code>Array of Buffer</code></p></td>
<td><p>Add a CRL value</p></td>
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
<td><p>Sets the list of enabled SSL/TLS protocols.</p></td>
</tr>
<tr class="odd">
<td><p><code>@forceSni</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>By default, the server name is only sent for Fully Qualified Domain Name (FQDN), setting this property to <code>true</code> forces the server name to be always sent.</p></td>
</tr>
<tr class="even">
<td><p><code>@http2ClearTextUpgrade</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set to <code>true</code> when an h2c connection is established using an HTTP/1.1 upgrade request, and <code>false</code> when an h2c connection is established directly (with prior knowledge).</p></td>
</tr>
<tr class="odd">
<td><p><code>@http2ConnectionWindowSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the default HTTP/2 connection window size. It overrides the initial window size set by link, so the connection window size is greater than for its streams, in order the data throughput. A value of <code>-1</code> reuses the initial window size setting.</p></td>
</tr>
<tr class="even">
<td><p><code>@http2KeepAliveTimeout</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the keep alive timeout for HTTP/2 connections, in seconds. This value determines how long a connection remains unused in the pool before being evicted and closed. A timeout of <code>0</code> means there is no timeout and the connection can remain indefinitely in the pool.</p></td>
</tr>
<tr class="odd">
<td><p><code>@http2MaxPoolSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the maximum pool size for HTTP/2 connections</p></td>
</tr>
<tr class="even">
<td><p><code>@http2MultiplexingLimit</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set a client limit of the number concurrent streams for each HTTP/2 connection, this limits the number of streams the client can create for a connection. The effective number of streams for a connection is the min of this value and the server's initial settings. Setting the value to <code>-1</code> means to use the value sent by the server's initial settings. <code>-1</code> is the default value.</p></td>
</tr>
<tr class="odd">
<td><p><code>@idleTimeout</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the idle timeout, default time unit is seconds. Zero means don't timeout. This determines if a connection will timeout and be closed if no data is received within the timeout.</p>
<p>If you want change default time unit, use link</p></td>
</tr>
<tr class="even">
<td><p><code>@idleTimeoutUnit</code></p></td>
<td><p><code>TimeUnit</code></p></td>
<td><p>Set the idle timeout unit. If not specified, default is seconds.</p></td>
</tr>
<tr class="odd">
<td><p><code>@initialSettings</code></p></td>
<td><p><code>Http2Settings</code></p></td>
<td><p>Set the HTTP/2 connection settings immediately sent by to the server when the client connects.</p></td>
</tr>
<tr class="even">
<td><p><code>@jdkSslEngineOptions</code></p></td>
<td><p><code>JdkSSLEngineOptions</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@keepAlive</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether keep alive is enabled on the client</p></td>
</tr>
<tr class="even">
<td><p><code>@keepAliveTimeout</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the keep alive timeout for HTTP/1.x, in seconds. This value determines how long a connection remains unused in the pool before being evicted and closed. A timeout of <code>0</code> means there is no timeout and the connection can remain indefinitely in the pool.</p></td>
</tr>
<tr class="odd">
<td><p><code>@keyStoreOptions</code></p></td>
<td><p><code>JksOptions</code></p></td>
<td><p>Set the key/cert options in jks format, aka Java keystore.</p></td>
</tr>
<tr class="even">
<td><p><code>@localAddress</code></p></td>
<td><p><code>String</code></p></td>
<td><p>Set the local interface to bind for network connections. When the local address is null, it will pick any local address, the default local address is null.</p></td>
</tr>
<tr class="odd">
<td><p><code>@logActivity</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set to true to enabled network activity logging: Netty's pipeline is configured for logging on Netty's logger.</p></td>
</tr>
<tr class="even">
<td><p><code>@maxChunkSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the maximum HTTP chunk size</p></td>
</tr>
<tr class="odd">
<td><p><code>@maxHeaderSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the maximum length of all headers for HTTP/1.x .</p></td>
</tr>
<tr class="even">
<td><p><code>@maxInitialLineLength</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the maximum length of the initial line for HTTP/1.x (e.g. <code>"HTTP/1.1 200 OK"</code>)</p></td>
</tr>
<tr class="odd">
<td><p><code>@maxPoolSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the maximum pool size for connections</p></td>
</tr>
<tr class="even">
<td><p><code>@maxRedirects</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set to <code>maxRedirects</code> the maximum number of redirection a request can follow.</p></td>
</tr>
<tr class="odd">
<td><p><code>@maxWaitQueueSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the maximum requests allowed in the wait queue, any requests beyond the max size will result in a ConnectionPoolTooBusyException. If the value is set to a negative number then the queue will be unbounded.</p></td>
</tr>
<tr class="even">
<td><p><code>@maxWebSocketFrameSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the max WebSocket frame size</p></td>
</tr>
<tr class="odd">
<td><p><code>@maxWebSocketMessageSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the max WebSocket message size</p></td>
</tr>
<tr class="even">
<td><p><code>@maxWebsocketFrameSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the max WebSocket frame size</p></td>
</tr>
<tr class="odd">
<td><p><code>@maxWebsocketMessageSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the max WebSocket message size</p></td>
</tr>
<tr class="even">
<td><p><code>@metricsName</code></p></td>
<td><p><code>String</code></p></td>
<td><p>Set the metrics name identifying the reported metrics, useful for grouping metrics with the same name.</p></td>
</tr>
<tr class="odd">
<td><p><code>@openSslEngineOptions</code></p></td>
<td><p><code>OpenSSLEngineOptions</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@pemKeyCertOptions</code></p></td>
<td><p><code>PemKeyCertOptions</code></p></td>
<td><p>Set the key/cert store options in pem format.</p></td>
</tr>
<tr class="odd">
<td><p><code>@pemTrustOptions</code></p></td>
<td><p><code>PemTrustOptions</code></p></td>
<td><p>Set the trust options in pem format</p></td>
</tr>
<tr class="even">
<td><p><code>@pfxKeyCertOptions</code></p></td>
<td><p><code>PfxOptions</code></p></td>
<td><p>Set the key/cert options in pfx format.</p></td>
</tr>
<tr class="odd">
<td><p><code>@pfxTrustOptions</code></p></td>
<td><p><code>PfxOptions</code></p></td>
<td><p>Set the trust options in pfx format</p></td>
</tr>
<tr class="even">
<td><p><code>@pipelining</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether pipe-lining is enabled on the client</p></td>
</tr>
<tr class="odd">
<td><p><code>@pipeliningLimit</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the limit of pending requests a pipe-lined HTTP/1 connection can send.</p></td>
</tr>
<tr class="even">
<td><p><code>@poolCleanerPeriod</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the connection pool cleaner period in milli seconds, a non positive value disables expiration checks and connections will remain in the pool until they are closed.</p></td>
</tr>
<tr class="odd">
<td><p><code>@protocolVersion</code></p></td>
<td><p><code>HttpVersion</code></p></td>
<td><p>Set the protocol version.</p></td>
</tr>
<tr class="even">
<td><p><code>@proxyOptions</code></p></td>
<td><p><code>ProxyOptions</code></p></td>
<td><p>Set proxy options for connections via CONNECT proxy (e.g. Squid) or a SOCKS proxy.</p></td>
</tr>
<tr class="odd">
<td><p><code>@receiveBufferSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the TCP receive buffer size</p></td>
</tr>
<tr class="even">
<td><p><code>@reuseAddress</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set the value of reuse address</p></td>
</tr>
<tr class="odd">
<td><p><code>@reusePort</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set the value of reuse port. This is only supported by native transports.</p></td>
</tr>
<tr class="even">
<td><p><code>@sendBufferSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the TCP send buffer size</p></td>
</tr>
<tr class="odd">
<td><p><code>@sendUnmaskedFrames</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set <code>true</code> when the client wants to skip frame masking. You may want to set it <code>true</code> on server by server WebSocket communication: in this case you are by passing RFC6455 protocol. It's <code>false</code> as default.</p></td>
</tr>
<tr class="even">
<td><p><code>@soLinger</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set whether SO_linger keep alive is enabled</p></td>
</tr>
<tr class="odd">
<td><p><code>@ssl</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether SSL/TLS is enabled</p></td>
</tr>
<tr class="even">
<td><p><code>@sslHandshakeTimeout</code></p></td>
<td><p><code>Number (long)</code></p></td>
<td><p>Set the SSL handshake timeout, default time unit is seconds.</p></td>
</tr>
<tr class="odd">
<td><p><code>@sslHandshakeTimeoutUnit</code></p></td>
<td><p><code>TimeUnit</code></p></td>
<td><p>Set the SSL handshake timeout unit. If not specified, default is seconds.</p></td>
</tr>
<tr class="even">
<td><p><code>@tcpCork</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Enable the <code>TCP_CORK</code> option - only with linux native transport.</p></td>
</tr>
<tr class="odd">
<td><p><code>@tcpFastOpen</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Enable the <code>TCP_FASTOPEN</code> option - only with linux native transport.</p></td>
</tr>
<tr class="even">
<td><p><code>@tcpKeepAlive</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether TCP keep alive is enabled</p></td>
</tr>
<tr class="odd">
<td><p><code>@tcpNoDelay</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether TCP no delay is enabled</p></td>
</tr>
<tr class="even">
<td><p><code>@tcpQuickAck</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Enable the <code>TCP_QUICKACK</code> option - only with linux native transport.</p></td>
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
<td><p>Set whether the client will offer the WebSocket per-frame deflate compression extension.</p></td>
</tr>
<tr class="even">
<td><p><code>@tryUsePerFrameWebsocketCompression</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether the client will offer the WebSocket per-frame deflate compression extension.</p></td>
</tr>
<tr class="odd">
<td><p><code>@tryUsePerMessageWebSocketCompression</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether the client will offer the WebSocket per-message deflate compression extension.</p></td>
</tr>
<tr class="even">
<td><p><code>@tryUsePerMessageWebsocketCompression</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether the client will offer the WebSocket per-message deflate compression extension.</p></td>
</tr>
<tr class="odd">
<td><p><code>@tryWebSocketDeflateFrameCompression</code></p></td>
<td><p><code>Boolean</code></p></td>
<td></td>
</tr>
<tr class="even">
<td><p><code>@tryWebsocketDeflateFrameCompression</code></p></td>
<td><p><code>Boolean</code></p></td>
<td></td>
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
<td><p><code>@verifyHost</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether hostname verification is enabled</p></td>
</tr>
<tr class="even">
<td><p><code>@webSocketCompressionAllowClientNoContext</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether the <code>client_no_context_takeover</code> parameter of the WebSocket per-message deflate compression extension will be offered.</p></td>
</tr>
<tr class="odd">
<td><p><code>@webSocketCompressionLevel</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the WebSocket deflate compression level.</p></td>
</tr>
<tr class="even">
<td><p><code>@webSocketCompressionRequestServerNoContext</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether the <code>server_no_context_takeover</code> parameter of the WebSocket per-message deflate compression extension will be offered.</p></td>
</tr>
<tr class="odd">
<td><p><code>@websocketCompressionAllowClientNoContext</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether the <code>client_no_context_takeover</code> parameter of the WebSocket per-message deflate compression extension will be offered.</p></td>
</tr>
<tr class="even">
<td><p><code>@websocketCompressionLevel</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the WebSocket deflate compression level.</p></td>
</tr>
<tr class="odd">
<td><p><code>@websocketCompressionRequestServerNoContext</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether the <code>server_no_context_takeover</code> parameter of the WebSocket per-message deflate compression extension will be offered.</p></td>
</tr>
</tbody>
</table>

# HttpServerOptions

Represents options used by an link instance

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
<td><p><code>@acceptBacklog</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the accept back log</p></td>
</tr>
<tr class="odd">
<td><p><code>@acceptUnmaskedFrames</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set <code>true</code> when the server accepts unmasked frame. As default Server doesn't accept unmasked frame, you can bypass this behaviour (RFC 6455) setting <code>true</code>. It's set to <code>false</code> as default.</p></td>
</tr>
<tr class="even">
<td><p><code>@alpnVersions</code></p></td>
<td><p><code>Array of HttpVersion</code></p></td>
<td><p>Set the list of protocol versions to provide to the server during the Application-Layer Protocol Negotiatiation.</p></td>
</tr>
<tr class="odd">
<td><p><code>@clientAuth</code></p></td>
<td><p><code>ClientAuth</code></p></td>
<td><p>Set whether client auth is required</p></td>
</tr>
<tr class="even">
<td><p><code>@clientAuthRequired</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether client auth is required</p></td>
</tr>
<tr class="odd">
<td><p><code>@compressionLevel</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>This method allows to set the compression level to be used in http1.x/2 response bodies when compression support is turned on (@see setCompressionSupported) and the client advertises to support <code>deflate/gzip</code> compression in the <code>Accept-Encoding</code> header</p>
<p>default value is : 6 (Netty legacy)</p>
<p>The compression level determines how much the data is compressed on a scale from 1 to 9, where '9' is trying to achieve the maximum compression ratio while '1' instead is giving priority to speed instead of compression ratio using some algorithm optimizations and skipping pedantic loops that usually gives just little improvements</p>
<p>While one can think that best value is always the maximum compression ratio, there's a trade-off to consider: the most compressed level requires the most computational work to compress/decompress data, e.g. more dictionary lookups and loops.</p>
<p>E.g. you have it set fairly high on a high-volume website, you may experience performance degradation and latency on resource serving due to CPU overload, and, however - as the computational work is required also client side while decompressing - setting an higher compression level can result in an overall higher page load time especially nowadays when many clients are handled mobile devices with a low CPU profile.</p>
<p>see also: http://www.gzip.org/algorithm.txt</p></td>
</tr>
<tr class="even">
<td><p><code>@compressionSupported</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether the server should support gzip/deflate compression (serving compressed responses to clients advertising support for them with Accept-Encoding header)</p></td>
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
<td><p><code>@decoderInitialBufferSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the initial buffer size for the HTTP decoder</p></td>
</tr>
<tr class="even">
<td><p><code>@decompressionSupported</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether the server supports decompression</p></td>
</tr>
<tr class="odd">
<td><p><code>@enabledCipherSuites</code></p></td>
<td><p><code>Array of String</code></p></td>
<td><p>Add an enabled cipher suite, appended to the ordered suites.</p></td>
</tr>
<tr class="even">
<td><p><code>@enabledSecureTransportProtocols</code></p></td>
<td><p><code>Array of String</code></p></td>
<td><p>Sets the list of enabled SSL/TLS protocols.</p></td>
</tr>
<tr class="odd">
<td><p><code>@handle100ContinueAutomatically</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether 100 Continue should be handled automatically</p></td>
</tr>
<tr class="even">
<td><p><code>@host</code></p></td>
<td><p><code>String</code></p></td>
<td><p>Set the host</p></td>
</tr>
<tr class="odd">
<td><p><code>@http2ConnectionWindowSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the default HTTP/2 connection window size. It overrides the initial window size set by link, so the connection window size is greater than for its streams, in order the data throughput. A value of <code>-1</code> reuses the initial window size setting.</p></td>
</tr>
<tr class="even">
<td><p><code>@idleTimeout</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the idle timeout, default time unit is seconds. Zero means don't timeout. This determines if a connection will timeout and be closed if no data is received within the timeout.</p>
<p>If you want change default time unit, use link</p></td>
</tr>
<tr class="odd">
<td><p><code>@idleTimeoutUnit</code></p></td>
<td><p><code>TimeUnit</code></p></td>
<td><p>Set the idle timeout unit. If not specified, default is seconds.</p></td>
</tr>
<tr class="even">
<td><p><code>@initialSettings</code></p></td>
<td><p><code>Http2Settings</code></p></td>
<td><p>Set the HTTP/2 connection settings immediatly sent by the server when a client connects.</p></td>
</tr>
<tr class="odd">
<td><p><code>@jdkSslEngineOptions</code></p></td>
<td><p><code>JdkSSLEngineOptions</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@keyStoreOptions</code></p></td>
<td><p><code>JksOptions</code></p></td>
<td><p>Set the key/cert options in jks format, aka Java keystore.</p></td>
</tr>
<tr class="odd">
<td><p><code>@logActivity</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set to true to enabled network activity logging: Netty's pipeline is configured for logging on Netty's logger.</p></td>
</tr>
<tr class="even">
<td><p><code>@maxChunkSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the maximum HTTP chunk size that link will receive</p></td>
</tr>
<tr class="odd">
<td><p><code>@maxHeaderSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the maximum length of all headers for HTTP/1.x .</p></td>
</tr>
<tr class="even">
<td><p><code>@maxInitialLineLength</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the maximum length of the initial line for HTTP/1.x (e.g. <code>"GET / HTTP/1.0"</code>)</p></td>
</tr>
<tr class="odd">
<td><p><code>@maxWebSocketFrameSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the maximum WebSocket frames size</p></td>
</tr>
<tr class="even">
<td><p><code>@maxWebSocketMessageSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the maximum WebSocket message size</p></td>
</tr>
<tr class="odd">
<td><p><code>@maxWebsocketFrameSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the maximum WebSocket frames size</p></td>
</tr>
<tr class="even">
<td><p><code>@maxWebsocketMessageSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the maximum WebSocket message size</p></td>
</tr>
<tr class="odd">
<td><p><code>@openSslEngineOptions</code></p></td>
<td><p><code>OpenSSLEngineOptions</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@pemKeyCertOptions</code></p></td>
<td><p><code>PemKeyCertOptions</code></p></td>
<td><p>Set the key/cert store options in pem format.</p></td>
</tr>
<tr class="odd">
<td><p><code>@pemTrustOptions</code></p></td>
<td><p><code>PemTrustOptions</code></p></td>
<td><p>Set the trust options in pem format</p></td>
</tr>
<tr class="even">
<td><p><code>@perFrameWebSocketCompressionSupported</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Enable or disable support for the WebSocket per-frame deflate compression extension.</p></td>
</tr>
<tr class="odd">
<td><p><code>@perFrameWebsocketCompressionSupported</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Enable or disable support for the WebSocket per-frame deflate compression extension.</p></td>
</tr>
<tr class="even">
<td><p><code>@perMessageWebSocketCompressionSupported</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Enable or disable support for WebSocket per-message deflate compression extension.</p></td>
</tr>
<tr class="odd">
<td><p><code>@perMessageWebsocketCompressionSupported</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Enable or disable support for WebSocket per-message deflate compression extension.</p></td>
</tr>
<tr class="even">
<td><p><code>@pfxKeyCertOptions</code></p></td>
<td><p><code>PfxOptions</code></p></td>
<td><p>Set the key/cert options in pfx format.</p></td>
</tr>
<tr class="odd">
<td><p><code>@pfxTrustOptions</code></p></td>
<td><p><code>PfxOptions</code></p></td>
<td><p>Set the trust options in pfx format</p></td>
</tr>
<tr class="even">
<td><p><code>@port</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the port</p></td>
</tr>
<tr class="odd">
<td><p><code>@receiveBufferSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the TCP receive buffer size</p></td>
</tr>
<tr class="even">
<td><p><code>@reuseAddress</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set the value of reuse address</p></td>
</tr>
<tr class="odd">
<td><p><code>@reusePort</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set the value of reuse port. This is only supported by native transports.</p></td>
</tr>
<tr class="even">
<td><p><code>@sendBufferSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the TCP send buffer size</p></td>
</tr>
<tr class="odd">
<td><p><code>@sni</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether the server supports Server Name Indiciation</p></td>
</tr>
<tr class="even">
<td><p><code>@soLinger</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set whether SO_linger keep alive is enabled</p></td>
</tr>
<tr class="odd">
<td><p><code>@ssl</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether SSL/TLS is enabled</p></td>
</tr>
<tr class="even">
<td><p><code>@sslHandshakeTimeout</code></p></td>
<td><p><code>Number (long)</code></p></td>
<td><p>Set the SSL handshake timeout, default time unit is seconds.</p></td>
</tr>
<tr class="odd">
<td><p><code>@sslHandshakeTimeoutUnit</code></p></td>
<td><p><code>TimeUnit</code></p></td>
<td><p>Set the SSL handshake timeout unit. If not specified, default is seconds.</p></td>
</tr>
<tr class="even">
<td><p><code>@tcpCork</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Enable the <code>TCP_CORK</code> option - only with linux native transport.</p></td>
</tr>
<tr class="odd">
<td><p><code>@tcpFastOpen</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Enable the <code>TCP_FASTOPEN</code> option - only with linux native transport.</p></td>
</tr>
<tr class="even">
<td><p><code>@tcpKeepAlive</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether TCP keep alive is enabled</p></td>
</tr>
<tr class="odd">
<td><p><code>@tcpNoDelay</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether TCP no delay is enabled</p></td>
</tr>
<tr class="even">
<td><p><code>@tcpQuickAck</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Enable the <code>TCP_QUICKACK</code> option - only with linux native transport.</p></td>
</tr>
<tr class="odd">
<td><p><code>@trafficClass</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the value of traffic class</p></td>
</tr>
<tr class="even">
<td><p><code>@trustStoreOptions</code></p></td>
<td><p><code>JksOptions</code></p></td>
<td><p>Set the trust options in jks format, aka Java truststore</p></td>
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
<td><p><code>@webSocketAllowServerNoContext</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether the WebSocket server will accept the <code>server_no_context_takeover</code> parameter of the per-message deflate compression extension offered by the client.</p></td>
</tr>
<tr class="even">
<td><p><code>@webSocketCompressionLevel</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the WebSocket compression level.</p></td>
</tr>
<tr class="odd">
<td><p><code>@webSocketPreferredClientNoContext</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether the WebSocket server will accept the <code>client_no_context_takeover</code> parameter of the per-message deflate compression extension offered by the client.</p></td>
</tr>
<tr class="even">
<td><p><code>@webSocketSubProtocols</code></p></td>
<td><p><code>Array of String</code></p></td>
<td><p>Set the WebSocket list of sub-protocol supported by the server.</p></td>
</tr>
<tr class="odd">
<td><p><code>@websocketAllowServerNoContext</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether the WebSocket server will accept the <code>server_no_context_takeover</code> parameter of the per-message deflate compression extension offered by the client.</p></td>
</tr>
<tr class="even">
<td><p><code>@websocketCompressionLevel</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the WebSocket compression level.</p></td>
</tr>
<tr class="odd">
<td><p><code>@websocketPreferredClientNoContext</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether the WebSocket server will accept the <code>client_no_context_takeover</code> parameter of the per-message deflate compression extension offered by the client.</p></td>
</tr>
<tr class="even">
<td><p><code>@websocketSubProtocols</code></p></td>
<td><p><code>String</code></p></td>
<td><p>Set the WebSocket sub-protocols supported by the server.</p></td>
</tr>
</tbody>
</table>

# JdkSSLEngineOptions

Configures a link to use the JDK ssl engine implementation.

|      |      |             |
| ---- | ---- | ----------- |
| Name | Type | Description |

# JksOptions

Key or trust store options configuring private key and/or certificates
based on Java Keystore files.

When used as a key store, it should point to a store containing a
private key and its certificate. When used as a trust store, it should
point to a store containing a list of trusted certificates.

The store can either be loaded by Vert.x from the filesystem:

HttpServerOptions options = HttpServerOptions.httpServerOptions();
options.setKeyStore(new
JKSOptions().setPath("/mykeystore.jks").setPassword("foo"));

Or directly provided as a buffer:

Buffer store = vertx.fileSystem().readFileBlocking("/mykeystore.jks");
options.setKeyStore(new
JKSOptions().setValue(store).setPassword("foo"));

|             |          |                                    |
| ----------- | -------- | ---------------------------------- |
| Name        | Type     | Description                        |
| `@password` | `String` | Set the password for the key store |
| `@path`     | `String` | Set the path to the key store      |
| `@value`    | `Buffer` | Set the key store as a buffer      |

# MetricsOptions

Vert.x metrics base configuration, this class can be extended by
provider implementations to configure those specific implementations.

|            |           |                                                             |
| ---------- | --------- | ----------------------------------------------------------- |
| Name       | Type      | Description                                                 |
| `@enabled` | `Boolean` | Set whether metrics will be enabled on the Vert.x instance. |

# NetClientOptions

Options for configuring a link.

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
<td><p><code>@enabledCipherSuites</code></p></td>
<td><p><code>Array of String</code></p></td>
<td><p>Add an enabled cipher suite, appended to the ordered suites.</p></td>
</tr>
<tr class="even">
<td><p><code>@enabledSecureTransportProtocols</code></p></td>
<td><p><code>Array of String</code></p></td>
<td><p>Sets the list of enabled SSL/TLS protocols.</p></td>
</tr>
<tr class="odd">
<td><p><code>@hostnameVerificationAlgorithm</code></p></td>
<td><p><code>String</code></p></td>
<td><p>Set the hostname verification algorithm interval To disable hostname verification, set hostnameVerificationAlgorithm to an empty String</p></td>
</tr>
<tr class="even">
<td><p><code>@idleTimeout</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the idle timeout, default time unit is seconds. Zero means don't timeout. This determines if a connection will timeout and be closed if no data is received within the timeout.</p>
<p>If you want change default time unit, use link</p></td>
</tr>
<tr class="odd">
<td><p><code>@idleTimeoutUnit</code></p></td>
<td><p><code>TimeUnit</code></p></td>
<td><p>Set the idle timeout unit. If not specified, default is seconds.</p></td>
</tr>
<tr class="even">
<td><p><code>@jdkSslEngineOptions</code></p></td>
<td><p><code>JdkSSLEngineOptions</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@keyStoreOptions</code></p></td>
<td><p><code>JksOptions</code></p></td>
<td><p>Set the key/cert options in jks format, aka Java keystore.</p></td>
</tr>
<tr class="even">
<td><p><code>@localAddress</code></p></td>
<td><p><code>String</code></p></td>
<td><p>Set the local interface to bind for network connections. When the local address is null, it will pick any local address, the default local address is null.</p></td>
</tr>
<tr class="odd">
<td><p><code>@logActivity</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set to true to enabled network activity logging: Netty's pipeline is configured for logging on Netty's logger.</p></td>
</tr>
<tr class="even">
<td><p><code>@metricsName</code></p></td>
<td><p><code>String</code></p></td>
<td><p>Set the metrics name identifying the reported metrics, useful for grouping metrics with the same name.</p></td>
</tr>
<tr class="odd">
<td><p><code>@openSslEngineOptions</code></p></td>
<td><p><code>OpenSSLEngineOptions</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@pemKeyCertOptions</code></p></td>
<td><p><code>PemKeyCertOptions</code></p></td>
<td><p>Set the key/cert store options in pem format.</p></td>
</tr>
<tr class="odd">
<td><p><code>@pemTrustOptions</code></p></td>
<td><p><code>PemTrustOptions</code></p></td>
<td><p>Set the trust options in pem format</p></td>
</tr>
<tr class="even">
<td><p><code>@pfxKeyCertOptions</code></p></td>
<td><p><code>PfxOptions</code></p></td>
<td><p>Set the key/cert options in pfx format.</p></td>
</tr>
<tr class="odd">
<td><p><code>@pfxTrustOptions</code></p></td>
<td><p><code>PfxOptions</code></p></td>
<td><p>Set the trust options in pfx format</p></td>
</tr>
<tr class="even">
<td><p><code>@proxyOptions</code></p></td>
<td><p><code>ProxyOptions</code></p></td>
<td><p>Set proxy options for connections via CONNECT proxy (e.g. Squid) or a SOCKS proxy.</p></td>
</tr>
<tr class="odd">
<td><p><code>@receiveBufferSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the TCP receive buffer size</p></td>
</tr>
<tr class="even">
<td><p><code>@reconnectAttempts</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the value of reconnect attempts</p></td>
</tr>
<tr class="odd">
<td><p><code>@reconnectInterval</code></p></td>
<td><p><code>Number (long)</code></p></td>
<td><p>Set the reconnect interval</p></td>
</tr>
<tr class="even">
<td><p><code>@reuseAddress</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set the value of reuse address</p></td>
</tr>
<tr class="odd">
<td><p><code>@reusePort</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set the value of reuse port. This is only supported by native transports.</p></td>
</tr>
<tr class="even">
<td><p><code>@sendBufferSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the TCP send buffer size</p></td>
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
<td><p>Set the SSL handshake timeout, default time unit is seconds.</p></td>
</tr>
<tr class="even">
<td><p><code>@sslHandshakeTimeoutUnit</code></p></td>
<td><p><code>TimeUnit</code></p></td>
<td><p>Set the SSL handshake timeout unit. If not specified, default is seconds.</p></td>
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
<td><p><code>@trafficClass</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the value of traffic class</p></td>
</tr>
<tr class="odd">
<td><p><code>@trustAll</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether all server certificates should be trusted</p></td>
</tr>
<tr class="even">
<td><p><code>@trustStoreOptions</code></p></td>
<td><p><code>JksOptions</code></p></td>
<td><p>Set the trust options in jks format, aka Java truststore</p></td>
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
</tbody>
</table>

# NetServerOptions

Options for configuring a link.

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
<td><p><code>@acceptBacklog</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the accept back log</p></td>
</tr>
<tr class="odd">
<td><p><code>@clientAuth</code></p></td>
<td><p><code>ClientAuth</code></p></td>
<td><p>Set whether client auth is required</p></td>
</tr>
<tr class="even">
<td><p><code>@clientAuthRequired</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether client auth is required</p></td>
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
<td><p><code>@enabledCipherSuites</code></p></td>
<td><p><code>Array of String</code></p></td>
<td><p>Add an enabled cipher suite, appended to the ordered suites.</p></td>
</tr>
<tr class="even">
<td><p><code>@enabledSecureTransportProtocols</code></p></td>
<td><p><code>Array of String</code></p></td>
<td><p>Sets the list of enabled SSL/TLS protocols.</p></td>
</tr>
<tr class="odd">
<td><p><code>@host</code></p></td>
<td><p><code>String</code></p></td>
<td><p>Set the host</p></td>
</tr>
<tr class="even">
<td><p><code>@idleTimeout</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the idle timeout, default time unit is seconds. Zero means don't timeout. This determines if a connection will timeout and be closed if no data is received within the timeout.</p>
<p>If you want change default time unit, use link</p></td>
</tr>
<tr class="odd">
<td><p><code>@idleTimeoutUnit</code></p></td>
<td><p><code>TimeUnit</code></p></td>
<td><p>Set the idle timeout unit. If not specified, default is seconds.</p></td>
</tr>
<tr class="even">
<td><p><code>@jdkSslEngineOptions</code></p></td>
<td><p><code>JdkSSLEngineOptions</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@keyStoreOptions</code></p></td>
<td><p><code>JksOptions</code></p></td>
<td><p>Set the key/cert options in jks format, aka Java keystore.</p></td>
</tr>
<tr class="even">
<td><p><code>@logActivity</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set to true to enabled network activity logging: Netty's pipeline is configured for logging on Netty's logger.</p></td>
</tr>
<tr class="odd">
<td><p><code>@openSslEngineOptions</code></p></td>
<td><p><code>OpenSSLEngineOptions</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@pemKeyCertOptions</code></p></td>
<td><p><code>PemKeyCertOptions</code></p></td>
<td><p>Set the key/cert store options in pem format.</p></td>
</tr>
<tr class="odd">
<td><p><code>@pemTrustOptions</code></p></td>
<td><p><code>PemTrustOptions</code></p></td>
<td><p>Set the trust options in pem format</p></td>
</tr>
<tr class="even">
<td><p><code>@pfxKeyCertOptions</code></p></td>
<td><p><code>PfxOptions</code></p></td>
<td><p>Set the key/cert options in pfx format.</p></td>
</tr>
<tr class="odd">
<td><p><code>@pfxTrustOptions</code></p></td>
<td><p><code>PfxOptions</code></p></td>
<td><p>Set the trust options in pfx format</p></td>
</tr>
<tr class="even">
<td><p><code>@port</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the port</p></td>
</tr>
<tr class="odd">
<td><p><code>@receiveBufferSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the TCP receive buffer size</p></td>
</tr>
<tr class="even">
<td><p><code>@reuseAddress</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set the value of reuse address</p></td>
</tr>
<tr class="odd">
<td><p><code>@reusePort</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set the value of reuse port. This is only supported by native transports.</p></td>
</tr>
<tr class="even">
<td><p><code>@sendBufferSize</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the TCP send buffer size</p></td>
</tr>
<tr class="odd">
<td><p><code>@sni</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether the server supports Server Name Indiciation</p></td>
</tr>
<tr class="even">
<td><p><code>@soLinger</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set whether SO_linger keep alive is enabled</p></td>
</tr>
<tr class="odd">
<td><p><code>@ssl</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether SSL/TLS is enabled</p></td>
</tr>
<tr class="even">
<td><p><code>@sslHandshakeTimeout</code></p></td>
<td><p><code>Number (long)</code></p></td>
<td><p>Set the SSL handshake timeout, default time unit is seconds.</p></td>
</tr>
<tr class="odd">
<td><p><code>@sslHandshakeTimeoutUnit</code></p></td>
<td><p><code>TimeUnit</code></p></td>
<td><p>Set the SSL handshake timeout unit. If not specified, default is seconds.</p></td>
</tr>
<tr class="even">
<td><p><code>@tcpCork</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Enable the <code>TCP_CORK</code> option - only with linux native transport.</p></td>
</tr>
<tr class="odd">
<td><p><code>@tcpFastOpen</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Enable the <code>TCP_FASTOPEN</code> option - only with linux native transport.</p></td>
</tr>
<tr class="even">
<td><p><code>@tcpKeepAlive</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether TCP keep alive is enabled</p></td>
</tr>
<tr class="odd">
<td><p><code>@tcpNoDelay</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether TCP no delay is enabled</p></td>
</tr>
<tr class="even">
<td><p><code>@tcpQuickAck</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Enable the <code>TCP_QUICKACK</code> option - only with linux native transport.</p></td>
</tr>
<tr class="odd">
<td><p><code>@trafficClass</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the value of traffic class</p></td>
</tr>
<tr class="even">
<td><p><code>@trustStoreOptions</code></p></td>
<td><p><code>JksOptions</code></p></td>
<td><p>Set the trust options in jks format, aka Java truststore</p></td>
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
</tbody>
</table>

# NetworkOptions

|                      |                |                                                                                                                |
| -------------------- | -------------- | -------------------------------------------------------------------------------------------------------------- |
| Name                 | Type           | Description                                                                                                    |
| `@logActivity`       | `Boolean`      | Set to true to enabled network activity logging: Netty's pipeline is configured for logging on Netty's logger. |
| `@receiveBufferSize` | `Number (int)` | Set the TCP receive buffer size                                                                                |
| `@reuseAddress`      | `Boolean`      | Set the value of reuse address                                                                                 |
| `@reusePort`         | `Boolean`      | Set the value of reuse port. This is only supported by native transports.                                      |
| `@sendBufferSize`    | `Number (int)` | Set the TCP send buffer size                                                                                   |
| `@trafficClass`      | `Number (int)` | Set the value of traffic class                                                                                 |

# OpenOptions

Describes how an link should be opened.

|                     |           |                                                                                                                       |
| ------------------- | --------- | --------------------------------------------------------------------------------------------------------------------- |
| Name                | Type      | Description                                                                                                           |
| `@append`           | `Boolean` | Whether the file should be opened in append mode. Defaults to `false`.                                                |
| `@create`           | `Boolean` | Set whether the file should be created if it does not already exist.                                                  |
| `@createNew`        | `Boolean` | Set whether the file should be created and fail if it does exist already.                                             |
| `@deleteOnClose`    | `Boolean` | Set whether the file should be deleted when it's closed, or the JVM is shutdown.                                      |
| `@dsync`            | `Boolean` | Set whether every write to the file's content ill be written synchronously to the underlying hardware.                |
| `@perms`            | `String`  | Set the permissions string                                                                                            |
| `@read`             | `Boolean` | Set whether the file is to be opened for reading                                                                      |
| `@sparse`           | `Boolean` | Set whether a hint should be provided that the file to created is sparse                                              |
| `@sync`             | `Boolean` | Set whether every write to the file's content and meta-data will be written synchronously to the underlying hardware. |
| `@truncateExisting` | `Boolean` | Set whether the file should be truncated to zero length on opening if it exists and is opened for write               |
| `@write`            | `Boolean` | Set whether the file is to be opened for writing                                                                      |

# OpenSSLEngineOptions

Configures a link to use OpenSsl.

|                        |           |                                                                         |
| ---------------------- | --------- | ----------------------------------------------------------------------- |
| Name                   | Type      | Description                                                             |
| `@sessionCacheEnabled` | `Boolean` | Set whether session cache is enabled in open SSL session server context |

# Option

Models command line options. Options are values passed to a command line
interface using -x or --x. Supported syntaxes depend on the parser.

Short name is generally used with a single dash, while long name
requires a double-dash.

|                 |                   |                                                                                                                                                                                                                               |
| --------------- | ----------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Name            | Type              | Description                                                                                                                                                                                                                   |
| `@argName`      | `String`          | Sets te arg name for this option.                                                                                                                                                                                             |
| `@choices`      | `Array of String` | Sets the list of values accepted by this option. If the value set by the user does not match once of these values, a link exception is thrown.                                                                                |
| `@defaultValue` | `String`          | Sets the default value of this option                                                                                                                                                                                         |
| `@description`  | `String`          | Sets te description of this option.                                                                                                                                                                                           |
| `@flag`         | `Boolean`         | Configures the current link to be a flag. It will be evaluated to `true` if it's found in the command line. If you need a flag that may receive a value, use, in this order: `
  option.setFlag(true).setSingleValued(true)
` |
| `@help`         | `Boolean`         | Sets whether or not this option is a "help" option                                                                                                                                                                            |
| `@hidden`       | `Boolean`         | Sets whether or not this option should be hidden                                                                                                                                                                              |
| `@longName`     | `String`          | Sets the long name of this option.                                                                                                                                                                                            |
| `@multiValued`  | `Boolean`         | Sets whether or not this option can receive several values.                                                                                                                                                                   |
| `@name`         | `String`          |                                                                                                                                                                                                                               |
| `@required`     | `Boolean`         | Sets whether or not this option is mandatory.                                                                                                                                                                                 |
| `@shortName`    | `String`          | Sets the short name of this option.                                                                                                                                                                                           |
| `@singleValued` | `Boolean`         | Sets whether or not this option can receive a value.                                                                                                                                                                          |

# PemKeyCertOptions

Key store options configuring a list of private key and its certificate
based on

Privacy-enhanced Electronic Email

(PEM) files.

A key file must contain a

non encrypted

private key in

PKCS8

format wrapped in a PEM block, for example:

\-----BEGIN PRIVATE KEY-----
MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDV6zPk5WqLwS0a ...
K5xBhtm1AhdnZjx5KfW3BecE -----END PRIVATE KEY-----

Or contain a

non encrypted

private key in

PKCS1

format wrapped in a PEM block, for example:

\-----BEGIN RSA PRIVATE KEY-----
MIIEowIBAAKCAQEAlO4gbHeFb/fmbUF/tOJfNPJumJUEqgzAzx8MBXv9Acyw9IRa ...
zJ14Yd+t2fsLYVs2H0gxaA4DW6neCzgY3eKpSU0EBHUCFSXp/1+/ -----END RSA
PRIVATE KEY-----

A certificate file must contain an X.509 certificate wrapped in a PEM
block, for example:

\-----BEGIN CERTIFICATE-----
MIIDezCCAmOgAwIBAgIEZOI/3TANBgkqhkiG9w0BAQsFADBuMRAwDgYDVQQGEwdV ...
+tmLSvYS39O2nqIzzAUfztkYnUlZmB0l/mKkVqbGJA== -----END CERTIFICATE-----

Keys and certificates can either be loaded by Vert.x from the
filesystem:

HttpServerOptions options = new HttpServerOptions();
options.setPemKeyCertOptions(new
PemKeyCertOptions().setKeyPath("/mykey.pem").setCertPath("/mycert.pem"));

Or directly provided as a buffer:

Buffer key = vertx.fileSystem().readFileBlocking("/mykey.pem"); Buffer
cert = vertx.fileSystem().readFileBlocking("/mycert.pem");
options.setPemKeyCertOptions(new
PemKeyCertOptions().setKeyValue(key).setCertValue(cert));

Several key/certificate pairs can be used:

HttpServerOptions options = new HttpServerOptions();
options.setPemKeyCertOptions(new PemKeyCertOptions()
.addKeyPath("/mykey1.pem").addCertPath("/mycert1.pem")
.addKeyPath("/mykey2.pem").addCertPath("/mycert2.pem"));

|               |                   |                                                                                    |
| ------------- | ----------------- | ---------------------------------------------------------------------------------- |
| Name          | Type              | Description                                                                        |
| `@certPath`   | `String`          | Set the path of the first certificate, replacing the previous certificates paths   |
| `@certPaths`  | `Array of String` | Set all the paths to the certificates files                                        |
| `@certValue`  | `Buffer`          | Set the first certificate as a buffer, replacing the previous certificates buffers |
| `@certValues` | `Array of Buffer` | Set all the certificates as a list of buffer                                       |
| `@keyPath`    | `String`          | Set the path of the first key file, replacing the keys paths                       |
| `@keyPaths`   | `Array of String` | Set all the paths to the keys files                                                |
| `@keyValue`   | `Buffer`          | Set the first key a a buffer, replacing the previous keys buffers                  |
| `@keyValues`  | `Array of Buffer` | Set all the keys as a list of buffer                                               |

# PemTrustOptions

Certificate Authority options configuring certificates based on

Privacy-enhanced Electronic Email

(PEM) files. The options is configured with a list of validating
certificates.

Validating certificates must contain X.509 certificates wrapped in a PEM
block:

\-----BEGIN CERTIFICATE-----
MIIDezCCAmOgAwIBAgIEVmLkwTANBgkqhkiG9w0BAQsFADBuMRAwDgYDVQQGEwdV ...
z5+DuODBJUQst141Jmgq8bS543IU/5apcKQeGNxEyQ== -----END CERTIFICATE-----

The certificates can either be loaded by Vert.x from the filesystem:

HttpServerOptions options = new HttpServerOptions();
options.setPemTrustOptions(new
PemTrustOptions().addCertPath("/cert.pem"));

Or directly provided as a buffer:

Buffer cert = vertx.fileSystem().readFileBlocking("/cert.pem");
HttpServerOptions options = new HttpServerOptions();
options.setPemTrustOptions(new PemTrustOptions().addCertValue(cert));

|               |                   |                         |
| ------------- | ----------------- | ----------------------- |
| Name          | Type              | Description             |
| `@certPaths`  | `Array of String` | Add a certificate path  |
| `@certValues` | `Array of Buffer` | Add a certificate value |

# PfxOptions

Key or trust store options configuring private key and/or certificates
based on PKCS\#12 files.

When used as a key store, it should point to a store containing a
private key and its certificate. When used as a trust store, it should
point to a store containing a list of accepted certificates.

The store can either be loaded by Vert.x from the filesystem:

HttpServerOptions options = new HttpServerOptions();
options.setPfxKeyCertOptions(new
PfxOptions().setPath("/mykeystore.p12").setPassword("foo"));

Or directly provided as a buffer:

Buffer store = vertx.fileSystem().readFileBlocking("/mykeystore.p12");
options.setPfxKeyCertOptions(new
PfxOptions().setValue(store).setPassword("foo"));

|             |          |                           |
| ----------- | -------- | ------------------------- |
| Name        | Type     | Description               |
| `@password` | `String` | Set the password          |
| `@path`     | `String` | Set the path              |
| `@value`    | `Buffer` | Set the store as a buffer |

# ProxyOptions

Proxy options for a net client or a net client.

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
<td><p><code>@host</code></p></td>
<td><p><code>String</code></p></td>
<td><p>Set proxy host.</p></td>
</tr>
<tr class="odd">
<td><p><code>@password</code></p></td>
<td><p><code>String</code></p></td>
<td><p>Set proxy password.</p></td>
</tr>
<tr class="even">
<td><p><code>@port</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set proxy port.</p></td>
</tr>
<tr class="odd">
<td><p><code>@type</code></p></td>
<td><p><code>ProxyType</code></p></td>
<td><p>Set proxy type.</p>
<p>ProxyType can be HTTP, SOCKS4 and SOCKS5</p></td>
</tr>
<tr class="even">
<td><p><code>@username</code></p></td>
<td><p><code>String</code></p></td>
<td><p>Set proxy username.</p></td>
</tr>
</tbody>
</table>

# RequestOptions

Options describing how an link will make connect to make a request.

|            |                |                                                     |
| ---------- | -------------- | --------------------------------------------------- |
| Name       | Type           | Description                                         |
| `@headers` | `String`       | Add a request header.                               |
| `@host`    | `String`       | Set the host name to be used by the client request. |
| `@port`    | `Number (int)` | Set the port to be used by the client request.      |
| `@ssl`     | `Boolean`      | Set whether SSL/TLS is enabled                      |
| `@uri`     | `String`       | Set the request relative URI                        |

# StreamPriority

This class represents HTTP/2 stream priority defined in RFC 7540 clause
5.3

|               |                  |                                    |
| ------------- | ---------------- | ---------------------------------- |
| Name          | Type             | Description                        |
| `@dependency` | `Number (int)`   | Set the priority dependency value. |
| `@exclusive`  | `Boolean`        | Set the priority exclusive value.  |
| `@weight`     | `Number (short)` | Set the priority weight.           |

# TCPSSLOptions

Base class. TCP and SSL related options

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
<td><p><code>@crlPaths</code></p></td>
<td><p><code>Array of String</code></p></td>
<td><p>Add a CRL path</p></td>
</tr>
<tr class="odd">
<td><p><code>@crlValues</code></p></td>
<td><p><code>Array of Buffer</code></p></td>
<td><p>Add a CRL value</p></td>
</tr>
<tr class="even">
<td><p><code>@enabledCipherSuites</code></p></td>
<td><p><code>Array of String</code></p></td>
<td><p>Add an enabled cipher suite, appended to the ordered suites.</p></td>
</tr>
<tr class="odd">
<td><p><code>@enabledSecureTransportProtocols</code></p></td>
<td><p><code>Array of String</code></p></td>
<td><p>Sets the list of enabled SSL/TLS protocols.</p></td>
</tr>
<tr class="even">
<td><p><code>@idleTimeout</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the idle timeout, default time unit is seconds. Zero means don't timeout. This determines if a connection will timeout and be closed if no data is received within the timeout.</p>
<p>If you want change default time unit, use link</p></td>
</tr>
<tr class="odd">
<td><p><code>@idleTimeoutUnit</code></p></td>
<td><p><code>TimeUnit</code></p></td>
<td><p>Set the idle timeout unit. If not specified, default is seconds.</p></td>
</tr>
<tr class="even">
<td><p><code>@jdkSslEngineOptions</code></p></td>
<td><p><code>JdkSSLEngineOptions</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="odd">
<td><p><code>@keyStoreOptions</code></p></td>
<td><p><code>JksOptions</code></p></td>
<td><p>Set the key/cert options in jks format, aka Java keystore.</p></td>
</tr>
<tr class="even">
<td><p><code>@logActivity</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set to true to enabled network activity logging: Netty's pipeline is configured for logging on Netty's logger.</p></td>
</tr>
<tr class="odd">
<td><p><code>@openSslEngineOptions</code></p></td>
<td><p><code>OpenSSLEngineOptions</code></p></td>
<td><p>-</p></td>
</tr>
<tr class="even">
<td><p><code>@pemKeyCertOptions</code></p></td>
<td><p><code>PemKeyCertOptions</code></p></td>
<td><p>Set the key/cert store options in pem format.</p></td>
</tr>
<tr class="odd">
<td><p><code>@pemTrustOptions</code></p></td>
<td><p><code>PemTrustOptions</code></p></td>
<td><p>Set the trust options in pem format</p></td>
</tr>
<tr class="even">
<td><p><code>@pfxKeyCertOptions</code></p></td>
<td><p><code>PfxOptions</code></p></td>
<td><p>Set the key/cert options in pfx format.</p></td>
</tr>
<tr class="odd">
<td><p><code>@pfxTrustOptions</code></p></td>
<td><p><code>PfxOptions</code></p></td>
<td><p>Set the trust options in pfx format</p></td>
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
<td><p><code>@soLinger</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set whether SO_linger keep alive is enabled</p></td>
</tr>
<tr class="odd">
<td><p><code>@ssl</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether SSL/TLS is enabled</p></td>
</tr>
<tr class="even">
<td><p><code>@sslHandshakeTimeout</code></p></td>
<td><p><code>Number (long)</code></p></td>
<td><p>Set the SSL handshake timeout, default time unit is seconds.</p></td>
</tr>
<tr class="odd">
<td><p><code>@sslHandshakeTimeoutUnit</code></p></td>
<td><p><code>TimeUnit</code></p></td>
<td><p>Set the SSL handshake timeout unit. If not specified, default is seconds.</p></td>
</tr>
<tr class="even">
<td><p><code>@tcpCork</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Enable the <code>TCP_CORK</code> option - only with linux native transport.</p></td>
</tr>
<tr class="odd">
<td><p><code>@tcpFastOpen</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Enable the <code>TCP_FASTOPEN</code> option - only with linux native transport.</p></td>
</tr>
<tr class="even">
<td><p><code>@tcpKeepAlive</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether TCP keep alive is enabled</p></td>
</tr>
<tr class="odd">
<td><p><code>@tcpNoDelay</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Set whether TCP no delay is enabled</p></td>
</tr>
<tr class="even">
<td><p><code>@tcpQuickAck</code></p></td>
<td><p><code>Boolean</code></p></td>
<td><p>Enable the <code>TCP_QUICKACK</code> option - only with linux native transport.</p></td>
</tr>
<tr class="odd">
<td><p><code>@trafficClass</code></p></td>
<td><p><code>Number (int)</code></p></td>
<td><p>Set the value of traffic class</p></td>
</tr>
<tr class="even">
<td><p><code>@trustStoreOptions</code></p></td>
<td><p><code>JksOptions</code></p></td>
<td><p>Set the trust options in jks format, aka Java truststore</p></td>
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
</tbody>
</table>

# VertxOptions

Instances of this class are used to configure link instances.

|                                   |                          |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| --------------------------------- | ------------------------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Name                              | Type                     | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| `@addressResolverOptions`         | `AddressResolverOptions` | Sets the address resolver configuration to configure resolving DNS servers, cache TTL, etc...                                                                                                                                                                                                                                                                                                                                                                                          |
| `@blockedThreadCheckInterval`     | `Number (long)`          | Sets the value of blocked thread check period, in link. The default value of link is                                                                                                                                                                                                                                                                                                                                                                                                   |
| `@blockedThreadCheckIntervalUnit` | `TimeUnit`               | Set the time unit of `blockedThreadCheckInterval`.                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| `@clusterHost`                    | `String`                 | Set the hostname to be used for clustering.                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| `@clusterPingInterval`            | `Number (long)`          | Set the value of cluster ping interval, in ms.                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| `@clusterPingReplyInterval`       | `Number (long)`          | Set the value of cluster ping reply interval, in ms.                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| `@clusterPort`                    | `Number (int)`           | Set the port to be used for clustering.                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| `@clusterPublicHost`              | `String`                 | Set the public facing hostname to be used for clustering. Sometimes, e.g. when running on certain clouds, the local address the server listens on for clustering is not the same address that other nodes connect to it at, as the OS / cloud infrastructure does some kind of proxying. If this is the case you can specify a public hostname which is different from the hostname the server listens at. The default value is null which means use the same as the cluster hostname. |
| `@clusterPublicPort`              | `Number (int)`           | See link for an explanation.                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| `@clustered`                      | `Boolean`                | Set whether or not the Vert.x instance will be clustered.                                                                                                                                                                                                                                                                                                                                                                                                                              |
| `@eventBusOptions`                | `EventBusOptions`        | Sets the event bus configuration to configure the host, port, ssl...                                                                                                                                                                                                                                                                                                                                                                                                                   |
| `@eventLoopPoolSize`              | `Number (int)`           | Set the number of event loop threads to be used by the Vert.x instance.                                                                                                                                                                                                                                                                                                                                                                                                                |
| `@fileResolverCachingEnabled`     | `Boolean`                | Set whether the Vert.x file resolver uses caching for classpath resources.                                                                                                                                                                                                                                                                                                                                                                                                             |
| `@fileSystemOptions`              | `FileSystemOptions`      | Set the file system options                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| `@haEnabled`                      | `Boolean`                | Set whether HA will be enabled on the Vert.x instance.                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| `@haGroup`                        | `String`                 | Set the HA group to be used when HA is enabled.                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| `@internalBlockingPoolSize`       | `Number (int)`           | Set the value of internal blocking pool size                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| `@maxEventLoopExecuteTime`        | `Number (long)`          | Sets the value of max event loop execute time, in link. The default value of linkis                                                                                                                                                                                                                                                                                                                                                                                                    |
| `@maxEventLoopExecuteTimeUnit`    | `TimeUnit`               | Set the time unit of `maxEventLoopExecuteTime`.                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| `@maxWorkerExecuteTime`           | `Number (long)`          | Sets the value of max worker execute time, in link. The default value of link is                                                                                                                                                                                                                                                                                                                                                                                                       |
| `@maxWorkerExecuteTimeUnit`       | `TimeUnit`               | Set the time unit of `maxWorkerExecuteTime`.                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| `@metricsOptions`                 | `MetricsOptions`         | Set the metrics options                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| `@preferNativeTransport`          | `Boolean`                | Set wether to prefer the native transport to the JDK transport.                                                                                                                                                                                                                                                                                                                                                                                                                        |
| `@quorumSize`                     | `Number (int)`           | Set the quorum size to be used when HA is enabled.                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| `@warningExceptionTime`           | `Number (long)`          | Set the threshold value above this, the blocked warning contains a stack trace. in link. The default value of link is                                                                                                                                                                                                                                                                                                                                                                  |
| `@warningExceptionTimeUnit`       | `TimeUnit`               | Set the time unit of `warningExceptionTime`.                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| `@workerPoolSize`                 | `Number (int)`           | Set the maximum number of worker threads to be used by the Vert.x instance.                                                                                                                                                                                                                                                                                                                                                                                                            |

# WebSocketConnectOptions

Options describing how an link connect a link.

|                 |                    |                                                     |
| --------------- | ------------------ | --------------------------------------------------- |
| Name            | Type               | Description                                         |
| `@headers`      | `String`           | Add a request header.                               |
| `@host`         | `String`           | Set the host name to be used by the client request. |
| `@port`         | `Number (int)`     | Set the port to be used by the client request.      |
| `@ssl`          | `Boolean`          | Set whether SSL/TLS is enabled                      |
| `@subProtocols` | `Array of String`  | Set the WebSocket sub protocols to use.             |
| `@uri`          | `String`           | Set the request relative URI                        |
| `@version`      | `WebsocketVersion` | Set the WebSocket version.                          |
