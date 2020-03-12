# Label

List of labels used in various Vert.x metrics. Labels that may not have
bounded values are disabled by default.

|               |                                                                                                                                                                                                      |
| ------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Name          | Description                                                                                                                                                                                          |
| `LOCAL`       | Local address in client-host or host-client connections (used in net, http and datagram domains)                                                                                                     |
| `REMOTE`      | Remote address in client-host or host-client connections (used in net and http domains)                                                                                                              |
| `HTTP_PATH`   | Path of the URI for client or server requests (used in http domain)                                                                                                                                  |
| `HTTP_METHOD` | Method (GET, POST, PUT, etc.) of an HTTP requests (used in http domain)                                                                                                                              |
| `HTTP_CODE`   | HTTP response code (used in http domain)                                                                                                                                                             |
| `CLASS_NAME`  | Class name. When used in error counters (in net, http, datagram and eventbus domains) it relates to an exception that occurred. When used in verticle domain, it relates to the verticle class name. |
| `EB_ADDRESS`  | Event bus address                                                                                                                                                                                    |
| `EB_SIDE`     | Event bus side of the metric, it can be either "local" or "remote"                                                                                                                                   |
| `EB_FAILURE`  | Event bus failure name from a ReplyFailure object                                                                                                                                                    |
| `POOL_TYPE`   | Pool type, such as "worker" or "datasource" (used in pools domain)                                                                                                                                   |
| `POOL_NAME`   | Pool name (used in pools domain)                                                                                                                                                                     |

# MatchType

The type of match.

|          |             |
| -------- | ----------- |
| Name     | Description |
| `EQUALS` | \-          |
| `REGEX`  | \-          |

# MetricsDomain

Metric domains with their associated prefixes.

|                   |                          |
| ----------------- | ------------------------ |
| Name              | Description              |
| `NET_SERVER`      | Net server metrics.      |
| `NET_CLIENT`      | Net client metrics.      |
| `HTTP_SERVER`     | Http server metrics.     |
| `HTTP_CLIENT`     | Http client metrics.     |
| `DATAGRAM_SOCKET` | Datagram socket metrics. |
| `EVENT_BUS`       | Event bus metrics.       |
| `NAMED_POOLS`     | Named pools metrics.     |
| `VERTICLES`       | Verticle metrics.        |
