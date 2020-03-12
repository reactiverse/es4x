This component provides a Vert.x wrapper around the Kafka Admin Client
API. The Kafka Admin Client is used to create, modify, and delete
topics. It also provides methods for handling ACLs (Access Control
Lists), consumer groups and many more.

# Creating the Kafka Admin Client

Creating the admin client is quite similar on how it works using the
native Kafka client library.

It needs to be configured with a bunch of properties as described in the
official Apache Kafka documentation, for the
[admin](https://kafka.apache.org/documentation/#adminclientconfigs).

To achieve that, a map can be configured with such properties passing it
to one of the static creation methods exposed by `KafkaAdminClient`.

``` js
import { KafkaAdminClient } from "@vertx/kafka-client"
// creating the admin client using properties config
let config = new (Java.type("java.util.Properties"))();
config.put(Java.type("org.apache.kafka.clients.admin.AdminClientConfig").BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

let adminClient = KafkaAdminClient.create(vertx, config);
```

# Using the Kafka Admin Client

## Listing topics

You can call the `listTopics` for listing the topics in the cluster. The
only parameter is the usual callback to handle the result, which
provides the topics list.

``` js
adminClient.listTopics((ar, ar_err) => {
  console.log("Topics= " + ar.result());
});
```

## Describe topics

You can call `describeTopics` to describe topics in the cluster.
Describing a topic means getting all related metadata like number of
partitions, replicas, leader, in-sync replicas and so on. The needed
parameters are the list of topics names to describe, and the usual
callback to handle the result providing a map with topic names and
related `TopicDescription`.

``` js
adminClient.describeTopics(Java.type("java.util.Collections").singletonList("my-topic"), (ar, ar_err) => {
  let topicDescription = ar.result()["first-topic"];

  console.log("Topic name=" + topicDescription.name + " isInternal= " + topicDescription.nternal + " partitions= " + topicDescription.partitions.length);

  topicDescription.partitions.forEach(topicPartitionInfo => {
    console.log("Partition id= " + topicPartitionInfo.partition + " leaderId= " + topicPartitionInfo.leader.id + " replicas= " + topicPartitionInfo.replicas + " isr= " + topicPartitionInfo.isr);
  });
});
```

## Create topic

You can call `createTopics` to create topics in the cluster. The needed
parameters are the list of the topics to create, and the usual callback
to handle the result. The topics to create are defined via the
`NewTopic` class specifying the name, the number of partitions and the
replication factor. It is also possible to describe the replicas
assignment, mapping each replica to the broker id, instead of specifying
the number of partitions and the replication factor (which in this case
has to be set to -1).

``` js
Code not translatable
```

## Delete topic

You can call `deleteTopics` to delete topics in the cluster. The needed
parameters are the list of the topics to delete, and the usual callback
to handle the result.

``` js
adminClient.deleteTopics(Java.type("java.util.Collections").singletonList("topicToDelete"), (ar, ar_err) => {
  // check if they were deleted successfully
});
```

## Describe configuration

You can call `describeConfigs` to describe resources configuration.
Describing resources configuration means getting all configuration
information for cluster resources like topics or brokers. The needed
parameters are the list of the resources for which you want the
configuration, and the usual callback to handle the result. The
resources are described by a collection of `ConfigResource` while the
result maps each resource with a corresponding `Config` which as more
`ConfigEntry` for each configuration parameter.

``` js
Code not translatable
```

## Alter configuration

You can call `alterConfigs` to alter resources configuration. Altering
resources configuration means updating configuration information for
cluster resources like topics or brokers. The needed parameters are the
list of the resources with the related configurations to updated, and
the usual callback to handle the result. It is possible to alter
configurations for different resources with just one call. The input
parameter maps each `ConfigResource` with the corresponding `Config` you
want to apply.

``` js
Code not translatable
```

## List consumer groups

You can call the `listConsumerGroups` for listing the consumer groups in
the cluster. The only parameter is the usual callback to handle the
result, which provides the consumer groups list.

``` js
adminClient.listConsumerGroups((ar, ar_err) => {
  console.log("ConsumerGroups= " + ar.result());
});
```

## Describe consumer groups

You can call `describeConsumerGroups` to describe consumer groups in the
cluster. Describing a consumer group means getting all related
information like members, related ids, topics subscribed, partitions
assignment and so on. The needed parameters are the list of consumer
groups names to describe, and the usual callback to handle the result
providing a map with consumer group names and related
`MemberDescription`.

``` js
adminClient.describeTopics(Java.type("java.util.Collections").singletonList("my-topic"), (ar, ar_err) => {
  let topicDescription = ar.result()["first-topic"];

  console.log("Topic name=" + topicDescription.name + " isInternal= " + topicDescription.nternal + " partitions= " + topicDescription.partitions.length);

  topicDescription.partitions.forEach(topicPartitionInfo => {
    console.log("Partition id= " + topicPartitionInfo.partition + " leaderId= " + topicPartitionInfo.leader.id + " replicas= " + topicPartitionInfo.replicas + " isr= " + topicPartitionInfo.isr);
  });
});
```
