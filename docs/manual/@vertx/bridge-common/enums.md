# BridgeEventType

Bridge Event Types.

|                  |                                                                                                  |
| ---------------- | ------------------------------------------------------------------------------------------------ |
| Name             | Description                                                                                      |
| `SOCKET_CREATED` | This event will occur when a new SockJS socket is created.                                       |
| `SOCKET_CLOSED`  | This event will occur when a SockJS socket is closed.                                            |
| `SOCKET_IDLE`    | This event will occur when SockJS socket is on idle for longer period of time than configured.   |
| `SOCKET_PING`    | This event will occur when the last ping timestamp is updated for the SockJS socket.             |
| `SEND`           | This event will occur when a message is attempted to be sent from the client to the server.      |
| `PUBLISH`        | This event will occur when a message is attempted to be published from the client to the server. |
| `RECEIVE`        | This event will occur when a message is attempted to be delivered from the server to the client. |
| `REGISTER`       | This event will occur when a client attempts to register a handler.                              |
| `UNREGISTER`     | This event will occur when a client attempts to unregister a handler.                            |
