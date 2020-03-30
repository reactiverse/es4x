# BridgeOptions

Options for configuring the event bus bridge.

|                         |                             |             |
| ----------------------- | --------------------------- | ----------- |
| Name                    | Type                        | Description |
| `@inboundPermitted`     | `Array of PermittedOptions` | \-          |
| `@inboundPermitteds`    | `Array of PermittedOptions` | \-          |
| `@maxAddressLength`     | `Number (int)`              | \-          |
| `@maxHandlersPerSocket` | `Number (int)`              | \-          |
| `@outboundPermitted`    | `Array of PermittedOptions` | \-          |
| `@outboundPermitteds`   | `Array of PermittedOptions` | \-          |
| `@pingTimeout`          | `Number (long)`             | \-          |
| `@replyTimeout`         | `Number (long)`             | \-          |

# Http2PushMapping

|                    |           |             |
| ------------------ | --------- | ----------- |
| Name               | Type      | Description |
| `@extensionTarget` | `String`  | \-          |
| `@filePath`        | `String`  | \-          |
| `@noPush`          | `Boolean` | \-          |

# PermittedOptions

Specify a match to allow for inbound and outbound traffic using the
link.

|                      |               |             |
| -------------------- | ------------- | ----------- |
| Name                 | Type          | Description |
| `@address`           | `String`      | \-          |
| `@addressRegex`      | `String`      | \-          |
| `@match`             | `Json object` | \-          |
| `@requiredAuthority` | `String`      | \-          |

# SockJSHandlerOptions

Options for configuring a SockJS handler

|                       |                   |             |
| --------------------- | ----------------- | ----------- |
| Name                  | Type              | Description |
| `@disabledTransports` | `Array of String` | \-          |
| `@heartbeatInterval`  | `Number (long)`   | \-          |
| `@insertJSESSIONID`   | `Boolean`         | \-          |
| `@libraryURL`         | `String`          | \-          |
| `@maxBytesStreaming`  | `Number (int)`    | \-          |
| `@sessionTimeout`     | `Number (long)`   | \-          |
