# QueueOptions

Aimed to specify queue consumer settings when calling link

|                         |                |             |
| ----------------------- | -------------- | ----------- |
| Name                    | Type           | Description |
| `@autoAck`              | `Boolean`      |             |
| `@keepMostRecent`       | `Boolean`      |             |
| `@maxInternalQueueSize` | `Number (int)` |             |

# RabbitMQOptions

RabbitMQ client options, most

|                             |                    |                                                                                                             |
| --------------------------- | ------------------ | ----------------------------------------------------------------------------------------------------------- |
| Name                        | Type               | Description                                                                                                 |
| `@automaticRecoveryEnabled` | `Boolean`          | Enables or disables automatic connection recovery.                                                          |
| `@connectionRetries`        | `Number (Integer)` | Set the number of connection retries to attempt when connecting, the `null` value disables it.              |
| `@connectionRetryDelay`     | `Number (long)`    | Set the delay in milliseconds between connection retries.                                                   |
| `@connectionTimeout`        | `Number (int)`     | Set the TCP connection timeout, in milliseconds, `zero` for infinite).                                      |
| `@handshakeTimeout`         | `Number (int)`     | Set the AMQP 0-9-1 protocol handshake timeout, in milliseconds                                              |
| `@host`                     | `String`           | Set the default host to use for connections.                                                                |
| `@includeProperties`        | `Boolean`          | Set wether to include properties when a broker message is passed on the event bus                           |
| `@networkRecoveryInterval`  | `Number (long)`    | Set how long in milliseconds will automatic recovery wait before attempting to reconnect, default is `5000` |
| `@password`                 | `String`           | Set the password to use when connecting to the broker.                                                      |
| `@port`                     | `Number (int)`     | Set the default port to use for connections.                                                                |
| `@requestedChannelMax`      | `Number (int)`     | Set the initially requested maximum channel number, `zero` for unlimited.                                   |
| `@requestedHeartbeat`       | `Number (int)`     | Set the initially requested heartbeat interval, in seconds, `zero` for none.                                |
| `@uri`                      | `String`           | \-                                                                                                          |
| `@user`                     | `String`           | Set the AMQP user name to use when connecting to the broker.                                                |
| `@virtualHost`              | `String`           | Set the virtual host to use when connecting to the broker.                                                  |
