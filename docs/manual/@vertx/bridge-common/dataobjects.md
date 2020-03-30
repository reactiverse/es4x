# BridgeOptions

Specify the event bus bridge options.

|                       |                             |                                              |
| --------------------- | --------------------------- | -------------------------------------------- |
| Name                  | Type                        | Description                                  |
| `@inboundPermitteds`  | `Array of PermittedOptions` | Sets the list of inbound permitted options.  |
| `@outboundPermitteds` | `Array of PermittedOptions` | Sets the list of outbound permitted options. |

# PermittedOptions

Represents a match to allow for inbound and outbound traffic.

|                      |               |                                                                                                                                                                                                          |
| -------------------- | ------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Name                 | Type          | Description                                                                                                                                                                                              |
| `@address`           | `String`      | The exact address the message is being sent to. If you want to allow messages based on an exact address you use this field.                                                                              |
| `@addressRegex`      | `String`      | A regular expression that will be matched against the address. If you want to allow messages based on a regular expression you use this field. If the link value is specified this will be ignored.      |
| `@match`             | `Json object` | This allows you to allow messages based on their structure. Any fields in the match must exist in the message with the same values for them to be allowed. This currently only works with JSON messages. |
| `@requiredAuthority` | `String`      | Declare a specific authority that user must have in order to allow messages                                                                                                                              |
