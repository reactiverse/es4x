# AuthOptions

A common base object for authentication options.

|      |      |             |
| ---- | ---- | ----------- |
| Name | Type | Description |

# KeyStoreOptions

Options describing how an JWT KeyStore should behave.

|             |          |             |
| ----------- | -------- | ----------- |
| Name        | Type     | Description |
| `@password` | `String` | \-          |
| `@path`     | `String` | \-          |
| `@type`     | `String` | \-          |

# PubSecKeyOptions

Options describing how a Cryptographic Key.

|                |           |             |
| -------------- | --------- | ----------- |
| Name           | Type      | Description |
| `@algorithm`   | `String`  | \-          |
| `@certificate` | `Boolean` | \-          |
| `@publicKey`   | `String`  | \-          |
| `@secretKey`   | `String`  | \-          |
| `@symmetric`   | `Boolean` | \-          |

# SecretOptions

Options describing a secret.

|           |          |             |
| --------- | -------- | ----------- |
| Name      | Type     | Description |
| `@secret` | `String` | \-          |
| `@type`   | `String` | \-          |
