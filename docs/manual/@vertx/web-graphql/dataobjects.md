# ApolloWSOptions

Options for configuring the link.

|              |                 |                                                                                                    |
| ------------ | --------------- | -------------------------------------------------------------------------------------------------- |
| Name         | Type            | Description                                                                                        |
| `@keepAlive` | `Number (long)` | Set the interval in milliseconds to send `KEEPALIVE` messages to all clients. Defaults to `30000`. |

# GraphQLHandlerOptions

Options for configuring the link.

|                           |           |                                                                  |
| ------------------------- | --------- | ---------------------------------------------------------------- |
| Name                      | Type      | Description                                                      |
| `@requestBatchingEnabled` | `Boolean` | Whether request batching should be enabled. Defaults to `false`. |

# GraphiQLHandlerOptions

Embedded GraphiQL user interface options.

|               |               |                                                                                             |
| ------------- | ------------- | ------------------------------------------------------------------------------------------- |
| Name          | Type          | Description                                                                                 |
| `@enabled`    | `Boolean`     | Whether the GraphiQL development tool should be enabled. Defaults to `false`.               |
| `@graphQLUri` | `String`      | Set the GraphQL endpoint URI. Defaults to the path used to get the GraphiQL user interface. |
| `@headers`    | `String`      | A fixed set of HTTP headers to add to GraphiQL requests. Defaults to `null`.                |
| `@query`      | `String`      | Initial value of the query area in the GraphiQL user interface. Defaults to `null`.         |
| `@variables`  | `Json object` | Initial value of the variables area in the GraphiQL user interface. Defaults to `null`.     |
