# OperationRequest

|           |               |                                                                                                   |
| --------- | ------------- | ------------------------------------------------------------------------------------------------- |
| Name      | Type          | Description                                                                                       |
| `@extra`  | `Json object` | Get extra payload                                                                                 |
| `@params` | `Json object` | Get request parsedParameters as JSON                                                              |
| `@user`   | `Json object` | Get request principal user as routingContext.user().principal(), null if no user is authenticated |

# OperationResponse

|                  |                    |             |
| ---------------- | ------------------ | ----------- |
| Name             | Type               | Description |
| `@payload`       | `Buffer`           | \-          |
| `@statusCode`    | `Number (Integer)` | \-          |
| `@statusMessage` | `String`           | \-          |

# RouterFactoryOptions

|                                    |           |                                                                                                                                                                                                                                                                                                                         |
| ---------------------------------- | --------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Name                               | Type      | Description                                                                                                                                                                                                                                                                                                             |
| `@mountNotImplementedHandler`      | `Boolean` | If true, Router Factory will automatically mount an handler that return HTTP 501 status code for each operation where you didn't specify an handler. You can customize the response with link                                                                                                                           |
| `@mountResponseContentTypeHandler` | `Boolean` | If true, when required, the factory will mount a link                                                                                                                                                                                                                                                                   |
| `@mountValidationFailureHandler`   | `Boolean` | Enable or disable validation failure handler. If you enable it during router creation a failure handler that manages ValidationException will be mounted. You can change the validation failure handler with with function link. If failure is different from ValidationException, next failure handler will be called. |
| `@operationModelKey`               | `String`  | When set, an additional handler will be created to expose the operation model in the routing context under the given key. When the key is null, the handler is not added.                                                                                                                                               |
| `@requireSecurityHandlers`         | `Boolean` | If true, when you call link the factory will mount for every path the required security handlers and, if a security handler is not defined, it throws an link                                                                                                                                                           |
