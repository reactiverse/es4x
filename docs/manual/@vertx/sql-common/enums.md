# FetchDirection

Represents the fetch direction hint

|           |             |
| --------- | ----------- |
| Name      | Description |
| `FORWARD` | \-          |
| `REVERSE` | \-          |
| `UNKNOWN` | \-          |

# ResultSetConcurrency

Represents the resultset concurrency hint

|             |             |
| ----------- | ----------- |
| Name        | Description |
| `READ_ONLY` | \-          |
| `UPDATABLE` | \-          |

# ResultSetType

Represents the resultset type hint

|                      |             |
| -------------------- | ----------- |
| Name                 | Description |
| `FORWARD_ONLY`       | \-          |
| `SCROLL_INSENSITIVE` | \-          |
| `SCROLL_SENSITIVE`   | \-          |

# TransactionIsolation

Represents a Transaction Isolation Level

|                    |                                                                                                                                                                                                                                                                                                                                                                                                   |
| ------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Name               | Description                                                                                                                                                                                                                                                                                                                                                                                       |
| `READ_UNCOMMITTED` | Implements dirty read, or isolation level 0 locking, which means that no shared locks are issued and no exclusive locks are honored. When this option is set, it is possible to read uncommitted or dirty data; values in the data can be changed and rows can appear or disappear in the data set before the end of the transaction. This is the least restrictive of the four isolation levels. |
| `READ_COMMITTED`   | Specifies that shared locks are held while the data is being read to avoid dirty reads, but the data can be changed before the end of the transaction, resulting in nonrepeatable reads or phantom data.                                                                                                                                                                                          |
| `REPEATABLE_READ`  | Locks are placed on all data that is used in a query, preventing other users from updating the data, but new phantom rows can be inserted into the data set by another user and are included in later reads in the current transaction. Because concurrency is lower than the default isolation level, use this option only when necessary.                                                       |
| `SERIALIZABLE`     | Places a range lock on the data set, preventing other users from updating or inserting rows into the data set until the transaction is complete. This is the most restrictive of the four isolation levels. Because concurrency is lower, use this option only when necessary.                                                                                                                    |
| `NONE`             | For engines that support it, none isolation means that each statement would essentially be its own transaction.                                                                                                                                                                                                                                                                                   |
