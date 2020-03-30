# AggregateOptions

|        |             |
| ------ | ----------- |
| Name   | Description |
| `NONE` | \-          |
| `SUM`  | \-          |
| `MIN`  | \-          |
| `MAX`  | \-          |

# BitFieldOverflowOptions

|        |             |
| ------ | ----------- |
| Name   | Description |
| `WRAP` | \-          |
| `SAT`  | \-          |
| `FAIL` | \-          |

# BitOperation

|       |             |
| ----- | ----------- |
| Name  | Description |
| `AND` | \-          |
| `OR`  | \-          |
| `XOR` | \-          |
| `NOT` | \-          |

# ClientReplyOptions

|        |             |
| ------ | ----------- |
| Name   | Description |
| `ON`   | \-          |
| `OFF`  | \-          |
| `SKIP` | \-          |

# FailoverOptions

|            |             |
| ---------- | ----------- |
| Name       | Description |
| `FORCE`    | \-          |
| `TAKEOVER` | \-          |

# GeoUnit

|      |             |
| ---- | ----------- |
| Name | Description |
| `m`  | Meter       |
| `km` | Kilometer   |
| `mi` | Mile        |
| `ft` | Feet        |

# InsertOptions

|          |             |
| -------- | ----------- |
| Name     | Description |
| `BEFORE` | \-          |
| `AFTER`  | \-          |

# ObjectCmd

|            |             |
| ---------- | ----------- |
| Name       | Description |
| `REFCOUNT` | \-          |
| `ENCODING` | \-          |
| `IDLETIME` | \-          |

# RangeOptions

|              |             |
| ------------ | ----------- |
| Name         | Description |
| `NONE`       | \-          |
| `WITHSCORES` | \-          |

# RedisClientType

Define what kind of behavior is expected from the client.

|              |                                                                                                                                             |
| ------------ | ------------------------------------------------------------------------------------------------------------------------------------------- |
| Name         | Description                                                                                                                                 |
| `STANDALONE` | The client should work in single server mode (the default).                                                                                 |
| `SENTINEL`   | The client should work in sentinel mode. When this mode is active use the link to define which role to get the client connection to.        |
| `CLUSTER`    | The client should work in cluster mode. When this mode is active use the link to define when slave nodes can be used for read only queries. |

# RedisRole

Define which kind of role to be used in HA mode.

|            |                                 |
| ---------- | ------------------------------- |
| Name       | Description                     |
| `MASTER`   | Use a MASTER node connection.   |
| `SLAVE`    | Use a SLAVE node connection.    |
| `SENTINEL` | Use a SENTINEL node connection. |

# RedisSlaves

When should Redis Slave nodes be used for queries.

|          |                                                               |
| -------- | ------------------------------------------------------------- |
| Name     | Description                                                   |
| `NEVER`  | Never use SLAVES, queries are always run on a MASTER node.    |
| `SHARE`  | Queries can be randomly run on both MASTER and SLAVE nodes.   |
| `ALWAYS` | Queries are always run on SLAVE nodes (never on MASTER node). |

# ResetOptions

|        |             |
| ------ | ----------- |
| Name   | Description |
| `HARD` | \-          |
| `SOFT` | \-          |

# ResponseType

Define the response types that the client can receive from REDIS.

|           |                                               |
| --------- | --------------------------------------------- |
| Name      | Description                                   |
| `SIMPLE`  | C String simple String.                       |
| `ERROR`   | C String simple String representing an error. |
| `INTEGER` | 64 bit integer value.                         |
| `BULK`    | byte array value.                             |
| `MULTI`   | List of multiple bulk responses.              |

# ScriptDebugOptions

|        |             |
| ------ | ----------- |
| Name   | Description |
| `YES`  | \-          |
| `SYNC` | \-          |
| `NO`   | \-          |

# ShutdownOptions

|          |             |
| -------- | ----------- |
| Name     | Description |
| `NONE`   | \-          |
| `SAVE`   | \-          |
| `NOSAVE` | \-          |

# SlotCmd

|             |             |
| ----------- | ----------- |
| Name        | Description |
| `IMPORTING` | \-          |
| `MIGRATING` | \-          |
| `STABLE`    | \-          |
| `NODE`      | \-          |
