# JDBCAuthOptions

Options configuring JDBC authentication.

|                        |               |                                                                                                         |
| ---------------------- | ------------- | ------------------------------------------------------------------------------------------------------- |
| Name                   | Type          | Description                                                                                             |
| `@authenticationQuery` | `String`      | Set the authentication query to use. Use this if you want to override the default authentication query. |
| `@config`              | `Json object` | The configuration of the JDBC client: refer to the Vert.x JDBC Client configuration.                    |
| `@datasourceName`      | `String`      | Set the data source name to use, only use in shared mode.                                               |
| `@permissionsQuery`    | `String`      | Set the permissions query to use. Use this if you want to override the default permissions query.       |
| `@rolesPrefix`         | `String`      | Set the role prefix to distinguish from permissions when checking for isPermitted requests.             |
| `@rolesQuery`          | `String`      | Set the roles query to use. Use this if you want to override the default roles query.                   |
| `@shared`              | `Boolean`     | Set whether the JDBC client is shared or non shared.                                                    |
