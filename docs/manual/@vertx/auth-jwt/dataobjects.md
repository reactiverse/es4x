# JWTAuthOptions

Options describing how an JWT Auth should behave.

|                        |                             |             |
| ---------------------- | --------------------------- | ----------- |
| Name                   | Type                        | Description |
| `@jwks`                | `Array of Json object`      | \-          |
| `@jwtOptions`          | `JWTOptions`                | \-          |
| `@keyStore`            | `KeyStoreOptions`           | \-          |
| `@permissionsClaimKey` | `String`                    | \-          |
| `@pubSecKeys`          | `Array of PubSecKeyOptions` | \-          |
| `@secrets`             | `Array of SecretOptions`    | \-          |

# JWTKeyStoreOptions

Options describing how an JWT KeyStore should behave.

|             |          |             |
| ----------- | -------- | ----------- |
| Name        | Type     | Description |
| `@password` | `String` | \-          |
| `@path`     | `String` | \-          |
| `@type`     | `String` | \-          |

# JWTOptions

Options related to creation of new tokens. If any expiresInMinutes,
audience, subject, issuer are not provided, there is no default. The jwt
generated won't include those properties in the payload. Generated JWTs
will include an iat claim by default unless noTimestamp is specified.

|                     |                   |             |
| ------------------- | ----------------- | ----------- |
| Name                | Type              | Description |
| `@algorithm`        | `String`          | \-          |
| `@audience`         | `Array of String` | \-          |
| `@audiences`        | `Array of String` | \-          |
| `@expiresInMinutes` | `Number (int)`    | \-          |
| `@expiresInSeconds` | `Number (int)`    | \-          |
| `@header`           | `Json object`     | \-          |
| `@ignoreExpiration` | `Boolean`         | \-          |
| `@issuer`           | `String`          | \-          |
| `@leeway`           | `Number (int)`    | \-          |
| `@noTimestamp`      | `Boolean`         | \-          |
| `@permissions`      | `Array of String` | \-          |
| `@subject`          | `String`          | \-          |
