# MongoAuthOptions

Options configuring Mongo authentication.

|                            |                 |                                                                                                                     |
| -------------------------- | --------------- | ------------------------------------------------------------------------------------------------------------------- |
| Name                       | Type            | Description                                                                                                         |
| `@collectionName`          | `String`        | The property name to be used to set the name of the collection inside the config.                                   |
| `@config`                  | `Json object`   | The mongo client configuration: see Mongo Client documentation.                                                     |
| `@datasourceName`          | `String`        | The mongo data source name: see Mongo Client documentation.                                                         |
| `@passwordField`           | `String`        | The property name to be used to set the name of the field, where the password is stored inside                      |
| `@permissionField`         | `String`        | The property name to be used to set the name of the field, where the permissions are stored inside.                 |
| `@roleField`               | `String`        | The property name to be used to set the name of the field, where the roles are stored inside.                       |
| `@saltField`               | `String`        | The property name to be used to set the name of the field, where the SALT is stored inside.                         |
| `@saltStyle`               | `HashSaltStyle` | The property name to be used to set the name of the field, where the salt style is stored inside                    |
| `@shared`                  | `Boolean`       | Use a shared Mongo client or not.                                                                                   |
| `@usernameCredentialField` | `String`        | The property name to be used to set the name of the field, where the username for the credentials is stored inside. |
| `@usernameField`           | `String`        | The property name to be used to set the name of the field, where the username is stored inside.                     |
