# SslMode

The different values for the sslmode parameter provide different levels
of protection. See more information in

Protection Provided in Different Modes

.

|               |                                                                                                                                                                   |
| ------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Name          | Description                                                                                                                                                       |
| `DISABLE`     | only try a non-SSL connection.                                                                                                                                    |
| `ALLOW`       | first try a non-SSL connection; if that fails, try an SSL connection.                                                                                             |
| `PREFER`      | first try an SSL connection; if that fails, try a non-SSL connection.                                                                                             |
| `REQUIRE`     | only try an SSL connection. If a root CA file is present, verify the certificate in the same way as if verify-ca was specified.                                   |
| `VERIFY_CA`   | only try an SSL connection, and verify that the server certificate is issued by a trusted certificate authority (CA).                                             |
| `VERIFY_FULL` | only try an SSL connection, verify that the server certificate is issued by a trusted CA and that the requested server host name matches that in the certificate. |
