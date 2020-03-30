# LoggerFormat

The possible out of the box formats.

|           |                                                                                                    |
| --------- | -------------------------------------------------------------------------------------------------- |
| Name      | Description                                                                                        |
| `DEFAULT` | remote-client - - \[timestamp\] "method uri version" status content-length "referrer" "user-agent" |
| `SHORT`   | remote-client - method uri version status content-length duration ms                               |
| `TINY`    | method uri status - content-length duration                                                        |

# Transport

The available SockJS transports

|                |                                                                                                                            |
| -------------- | -------------------------------------------------------------------------------------------------------------------------- |
| Name           | Description                                                                                                                |
| `WEBSOCKET`    | rfc 6455                                                                                                                   |
| `EVENT_SOURCE` | Event source                                                                                                               |
| `HTML_FILE`    | HtmlFile.                                                                                                                  |
| `JSON_P`       | Slow and old fashioned JSONP polling. This transport will show "busy indicator" (aka: "spinning wheel") when sending data. |
| `XHR`          | Long-polling using cross domain XHR                                                                                        |
