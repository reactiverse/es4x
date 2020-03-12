# CassandraClientOptions

Eclipse Vert.x Cassandra client options.

|                  |                   |                                                                                    |
| ---------------- | ----------------- | ---------------------------------------------------------------------------------- |
| Name             | Type              | Description                                                                        |
| `@contactPoints` | `Array of String` | Set a list of hosts, where some of cluster nodes is located.                       |
| `@keyspace`      | `String`          | Set the keyspace to use when creating the Cassandra session. Defaults to `null`.   |
| `@port`          | `Number (int)`    | Set which port should be used for all the hosts to connect to a cassandra service. |
