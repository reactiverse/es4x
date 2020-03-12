# MySQLSetOption

MySQL set options which can be used by link.

|                                     |             |
| ----------------------------------- | ----------- |
| Name                                | Description |
| `MYSQL_OPTION_MULTI_STATEMENTS_ON`  | \-          |
| `MYSQL_OPTION_MULTI_STATEMENTS_OFF` | \-          |

# SslMode

This parameter specifies the desired security state of the connection to
the server. More information can be found in

MySQL Reference Manual

|                   |                                                                                                                                                                                                                               |
| ----------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Name              | Description                                                                                                                                                                                                                   |
| `DISABLED`        | establish an unencrypted connection.                                                                                                                                                                                          |
| `PREFERRED`       | establish an encrypted connection if the server supports encrypted connections, falling back to an unencrypted connection if an encrypted connection cannot be established.                                                   |
| `REQUIRED`        | establish an encrypted connection if the server supports encrypted connections. The connection attempt fails if an encrypted connection cannot be established.                                                                |
| `VERIFY_CA`       | Like REQUIRED, but additionally verify the server Certificate Authority (CA) certificate against the configured CA certificates. The connection attempt fails if no valid matching CA certificates are found.                 |
| `VERIFY_IDENTITY` | Like VERIFY\_CA, but additionally perform host name identity verification by checking the host name the client uses for connecting to the server against the identity in the certificate that the server sends to the client. |
