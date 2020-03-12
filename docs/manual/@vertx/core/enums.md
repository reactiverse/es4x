# ClientAuth

Configures the engine to require/request client authentication.

Created by manishk on 10/2/2015.

|            |                                                                                                                                                                                    |
| ---------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Name       | Description                                                                                                                                                                        |
| `NONE`     | No client authentication is requested or required.                                                                                                                                 |
| `REQUEST`  | Accept authentication if presented by client. If this option is set and the client chooses not to provide authentication information about itself, the negotiations will continue. |
| `REQUIRED` | Require client to present authentication, if not presented then negotiations will be declined.                                                                                     |

# DnsResponseCode

Represents the possible response codes a server may send after receiving
a query. A response code of 0 indicates no error.

|             |                                            |
| ----------- | ------------------------------------------ |
| Name        | Description                                |
| `NOERROR`   | ID 0, no error                             |
| `FORMERROR` | ID 1, format error                         |
| `SERVFAIL`  | ID 2, server failure                       |
| `NXDOMAIN`  | ID 3, name error                           |
| `NOTIMPL`   | ID 4, not implemented                      |
| `REFUSED`   | ID 5, operation refused                    |
| `YXDOMAIN`  | ID 6, domain name should not exist         |
| `YXRRSET`   | ID 7, resource record set should not exist |
| `NXRRSET`   | ID 8, rrset does not exist                 |
| `NOTAUTH`   | ID 9, not authoritative for zone           |
| `NOTZONE`   | ID 10, name not in zone                    |
| `BADVERS`   | ID 11, bad extension mechanism for version |
| `BADSIG`    | ID 12, bad signature                       |
| `BADKEY`    | ID 13, bad key                             |
| `BADTIME`   | ID 14, bad timestamp                       |

# HttpMethod

Represents an HTTP method

|           |             |
| --------- | ----------- |
| Name      | Description |
| `OPTIONS` | \-          |
| `GET`     | \-          |
| `HEAD`    | \-          |
| `POST`    | \-          |
| `PUT`     | \-          |
| `DELETE`  | \-          |
| `TRACE`   | \-          |
| `CONNECT` | \-          |
| `PATCH`   | \-          |
| `OTHER`   | \-          |

# HttpVersion

Represents the version of the HTTP protocol.

|            |             |
| ---------- | ----------- |
| Name       | Description |
| `HTTP_1_0` | \-          |
| `HTTP_1_1` | \-          |
| `HTTP_2`   | \-          |

# JsonEventType

The possibles types of link emitted by the link.

|                |                                     |
| -------------- | ----------------------------------- |
| Name           | Description                         |
| `START_OBJECT` | Signals the start of a JSON object. |
| `END_OBJECT`   | Signals the end of a JSON object.   |
| `START_ARRAY`  | Signals the start of a JSON array.  |
| `END_ARRAY`    | Signals the end of a JSON array.    |
| `VALUE`        | Signals a JSON value.               |

# ProxyType

The type of a TCP proxy server.

|          |                        |
| -------- | ---------------------- |
| Name     | Description            |
| `HTTP`   | HTTP CONNECT ssl proxy |
| `SOCKS4` | SOCKS4/4a tcp proxy    |
| `SOCKS5` | SOCSK5 tcp proxy       |

# ReplyFailure

Represents the type of reply failure

|                     |                                                                                                   |
| ------------------- | ------------------------------------------------------------------------------------------------- |
| Name                | Description                                                                                       |
| `TIMEOUT`           | The message send failed because no reply was received before the timeout time.                    |
| `NO_HANDLERS`       | The message send failed because no handlers were available to handle the message.                 |
| `RECIPIENT_FAILURE` | The message send failed because the recipient actively sent back a failure (rejected the message) |

# WebsocketVersion

Represents the WebSocket version

|       |             |
| ----- | ----------- |
| Name  | Description |
| `V00` | \-          |
| `V07` | \-          |
| `V08` | \-          |
| `V13` | \-          |
