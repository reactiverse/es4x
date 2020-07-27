# AuthOptions

A common base object for authentication options.

|      |      |             |
| ---- | ---- | ----------- |
| Name | Type | Description |

# JWTOptions

Options related to creation of new tokens. If any expiresInMinutes,
audience, subject, issuer are not provided, there is no default. The jwt
generated won't include those properties in the payload. Generated JWTs
will include an iat claim by default unless noTimestamp is specified.

|                     |                   |                                |
| ------------------- | ----------------- | ------------------------------ |
| Name                | Type              | Description                    |
| `@algorithm`        | `String`          | \-                             |
| `@audience`         | `Array of String` | \-                             |
| `@audiences`        | `Array of String` | \-                             |
| `@expiresInMinutes` | `Number (int)`    | \-                             |
| `@expiresInSeconds` | `Number (int)`    | \-                             |
| `@header`           | `Json object`     | \-                             |
| `@ignoreExpiration` | `Boolean`         | \-                             |
| `@issuer`           | `String`          | \-                             |
| `@leeway`           | `Number (int)`    | \-                             |
| `@noTimestamp`      | `Boolean`         | \-                             |
| `@permissions`      | `Array of String` | The permissions of this token. |
| `@subject`          | `String`          | \-                             |

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
