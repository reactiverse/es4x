# ClusterDescription

A detailed description of the cluster

|               |                 |                                         |
| ------------- | --------------- | --------------------------------------- |
| Name          | Type            | Description                             |
| `@clusterId`  | `String`        | Set the cluster ID                      |
| `@controller` | `Node`          | Set the controller node.                |
| `@nodes`      | `Array of Node` | Set the nodes belonging to this cluster |

# Config

A configuration object containing the configuration entries for a
resource

|            |                        |                                              |
| ---------- | ---------------------- | -------------------------------------------- |
| Name       | Type                   | Description                                  |
| `@entries` | `Array of ConfigEntry` | Set the configuration entries for a resource |

# ConfigEntry

A class representing a configuration entry containing name, value and
additional metadata

|              |                          |                                                                                                                           |
| ------------ | ------------------------ | ------------------------------------------------------------------------------------------------------------------------- |
| Name         | Type                     | Description                                                                                                               |
| `@default`   | `Boolean`                | Set whether the config value is the default or if it's been explicitly set                                                |
| `@name`      | `String`                 | Set the config name                                                                                                       |
| `@readOnly`  | `Boolean`                | Set whether the config is read-only and cannot be updated                                                                 |
| `@sensitive` | `Boolean`                | Set whether the config value is sensitive. The value is always set to null by the broker if the config value is sensitive |
| `@source`    | `ConfigSource`           | Set the source of this configuration entry                                                                                |
| `@synonyms`  | `Array of ConfigSynonym` | Set all config values that may be used as the value of this config along with their source, in the order of precedence    |
| `@value`     | `String`                 | Set the value or null. Null is returned if the config is unset or if isSensitive is true                                  |

# ConfigResource

A class representing resources that have configuration

|            |           |                                                                                                          |
| ---------- | --------- | -------------------------------------------------------------------------------------------------------- |
| Name       | Type      | Description                                                                                              |
| `@default` | `Boolean` | Set if this is the default resource of a resource type. Resource name is empty for the default resource. |
| `@name`    | `String`  | Set the resource name                                                                                    |
| `@type`    | `Type`    | Set the resource type                                                                                    |

# ConfigSynonym

Class representing a configuration synonym of a link

|           |                |                                                                                          |
| --------- | -------------- | ---------------------------------------------------------------------------------------- |
| Name      | Type           | Description                                                                              |
| `@name`   | `String`       | Set the name of this configuration                                                       |
| `@source` | `ConfigSource` | Set the source of this configuration                                                     |
| `@value`  | `String`       | Set the value of this configuration, which may be null if the configuration is sensitive |

# ConsumerGroupDescription

A detailed description of a single consumer group in the cluster

|                        |                              |                                                                                  |
| ---------------------- | ---------------------------- | -------------------------------------------------------------------------------- |
| Name                   | Type                         | Description                                                                      |
| `@coordinator`         | `Node`                       | Set the consumer group coordinator, or null if the coordinator is not known      |
| `@groupId`             | `String`                     | Set the id of the consumer group                                                 |
| `@members`             | `Array of MemberDescription` | Set a list of the members of the consumer group                                  |
| `@partitionAssignor`   | `String`                     | Set the consumer group partition assignor                                        |
| `@simpleConsumerGroup` | `Boolean`                    | Set if consumer group is simple or not                                           |
| `@state`               | `ConsumerGroupState`         | Set the consumer group state, or UNKNOWN if the state is too new for us to parse |

# ConsumerGroupListing

A listing of a consumer group in the cluster.

|                        |           |                                        |
| ---------------------- | --------- | -------------------------------------- |
| Name                   | Type      | Description                            |
| `@groupId`             | `String`  | Set the consumer group id              |
| `@simpleConsumerGroup` | `Boolean` | Set if consumer group is simple or not |

# ListConsumerGroupOffsetsOptions

|      |      |             |
| ---- | ---- | ----------- |
| Name | Type | Description |

# MemberAssignment

A description of the assignments of a specific group member

|                    |                           |                                  |
| ------------------ | ------------------------- | -------------------------------- |
| Name               | Type                      | Description                      |
| `@topicPartitions` | `Array of TopicPartition` | Set the list of topic partitions |

# MemberDescription

A detailed description of a single group instance in the cluster

|               |                    |                                                |
| ------------- | ------------------ | ---------------------------------------------- |
| Name          | Type               | Description                                    |
| `@assignment` | `MemberAssignment` | Set the assignment of the group member         |
| `@clientId`   | `String`           | Set the client id of the group member          |
| `@consumerId` | `String`           | Set the consumer id of the group member        |
| `@host`       | `String`           | Set the host where the group member is running |

# NewTopic

A new topic to be created

|                      |                  |                                                                                                 |
| -------------------- | ---------------- | ----------------------------------------------------------------------------------------------- |
| Name                 | Type             | Description                                                                                     |
| `@config`            | `String`         | Set the configuration for the new topic or null if no configs ever specified                    |
| `@name`              | `String`         | Set the name of the topic to be created                                                         |
| `@numPartitions`     | `Number (int)`   | Set the number of partitions for the new topic or -1 if a replica assignment has been specified |
| `@replicationFactor` | `Number (short)` | Set the replication factor for the new topic or -1 if a replica assignment has been specified   |

# Node

Information about a Kafka cluster node

|             |                |                                              |
| ----------- | -------------- | -------------------------------------------- |
| Name        | Type           | Description                                  |
| `@empty`    | `Boolean`      |                                              |
| `@hasRack`  | `Boolean`      | Set if this node has a defined rack          |
| `@host`     | `String`       | Set the host name for this node              |
| `@id`       | `Number (int)` | Set the node id of this node                 |
| `@idString` | `String`       | Set the string representation of the node id |
| `@isEmpty`  | `Boolean`      | Set if this node is empty                    |
| `@port`     | `Number (int)` | Set the port for this node                   |
| `@rack`     | `String`       | Set the rack for this node                   |

# OffsetAndMetadata

Provide additional metadata when an offset is committed

|             |                 |                                                  |
| ----------- | --------------- | ------------------------------------------------ |
| Name        | Type            | Description                                      |
| `@metadata` | `String`        | Set additional metadata for the offset committed |
| `@offset`   | `Number (long)` | Set the offset to commit                         |

# OffsetAndTimestamp

Represent information related to a Offset with timestamp information

|              |                 |                   |
| ------------ | --------------- | ----------------- |
| Name         | Type            | Description       |
| `@offset`    | `Number (long)` | Set the offset    |
| `@timestamp` | `Number (long)` | Set the timestamp |

# PartitionInfo

Information about a specific Kafka topic partition

|                   |                 |                                                          |
| ----------------- | --------------- | -------------------------------------------------------- |
| Name              | Type            | Description                                              |
| `@inSyncReplicas` | `Array of Node` | Set the subset of the replicas that are in sync          |
| `@leader`         | `Node`          | Set the node id of the node currently acting as a leader |
| `@partition`      | `Number (int)`  | Set the partition id                                     |
| `@replicas`       | `Array of Node` | Set the complete set of replicas for this partition      |
| `@topic`          | `String`        | Set the topic name                                       |

# RecordMetadata

Metadata related to a Kafka record

|              |                 |                                                        |
| ------------ | --------------- | ------------------------------------------------------ |
| Name         | Type            | Description                                            |
| `@checksum`  | `Number (long)` | Set the checksum (CRC32) of the record.                |
| `@offset`    | `Number (long)` | Set the offset of the record in the topic/partition.   |
| `@partition` | `Number (int)`  | Set the partition the record was sent to               |
| `@timestamp` | `Number (long)` | Set the timestamp of the record in the topic/partition |
| `@topic`     | `String`        | Set the topic the record was appended to               |

# TopicDescription

A detailed description of a single topic in the cluster

|               |                               |                                                                                                                                                      |
| ------------- | ----------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------- |
| Name          | Type                          | Description                                                                                                                                          |
| `@internal`   | `Boolean`                     | Set whether the topic is internal to Kafka.                                                                                                          |
| `@name`       | `String`                      | Set the name of the topic.                                                                                                                           |
| `@partitions` | `Array of TopicPartitionInfo` | Set A list of partitions where the index represents the partition id and the element contains leadership and replica information for that partition. |

# TopicPartition

Represent information related to a partition for a topic

|              |                |                          |
| ------------ | -------------- | ------------------------ |
| Name         | Type           | Description              |
| `@partition` | `Number (int)` | Set the partition number |
| `@topic`     | `String`       | Set the topic name       |

# TopicPartitionInfo

A class containing leadership, replicas and ISR information for a topic
partition.

|              |                 |                                                          |
| ------------ | --------------- | -------------------------------------------------------- |
| Name         | Type            | Description                                              |
| `@isr`       | `Array of Node` | Set the subset of the replicas that are in sync          |
| `@leader`    | `Node`          | Set the node id of the node currently acting as a leader |
| `@partition` | `Number (int)`  | Set the partition id                                     |
| `@replicas`  | `Array of Node` | Set the complete set of replicas for this partition      |
