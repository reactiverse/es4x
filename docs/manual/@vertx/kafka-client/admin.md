This component provides a vert.x wrapper around the most important
functions of Kafka’s AdminUtils. AdminUtils are used to create, modify,
and delete topics. Other functionality covered by AdminUtils, but not
this wrapper, includes Partition Management, Broker Configuration
management, etc.

> **Warning**
> 
> this class is now deprecated see `KafkaAdminClient` instead.

# Using the AdminUtils

## Create a topic

You can call `createTopic` to create a topic. Parameters are: topic
name, number of partitions, number of replicas, and the usual callback
to handle the result. It might return an error, e.g. if the number of
requested replicas is greater than the number of brokers.

``` js
import { Vertx } from "@vertx/core"
import { AdminUtils } from "@vertx/kafka-client"
let adminUtils = AdminUtils.create(Vertx.vertx(), "localhost:2181", true);
// Create topic 'myNewTopic' with 2 partition and 1 replicas
adminUtils.createTopic("myNewTopic", 2, 1, (result, result_err) => {
  if (result.succeeded()) {
    console.log("Creation of topic myNewTopic successful!")} else {
    console.log("Creation of topic myNewTopic failed: " + result.cause().getLocalizedMessage())}
});
```

## Delete a topic

You can call `deleteTopic` to delete a topic. Parameters are: topic
name, and the usual callback to handle the result. It might return an
error, e.g. if the topic does not exist.

``` js
import { Vertx } from "@vertx/core"
import { AdminUtils } from "@vertx/kafka-client"
let adminUtils = AdminUtils.create(Vertx.vertx(), "localhost:2181", true);
// Delete topic 'myNewTopic'
adminUtils.deleteTopic("myNewTopic", (result, result_err) => {
  if (result.succeeded()) {
    console.log("Deletion of topic myNewTopic successful!")} else {
    console.log("Deletion of topic myNewTopic failed: " + result.cause().getLocalizedMessage())}
});
```

## Change a topic’s configuration

If you need to update the configuration of a topic, e.g., you want to
update the retention policy, you can call `changeTopicConfig` to update
a topic. Parameters are: topic name, a Map (String → String) with
parameters to be changed, and the usual callback to handle the result.
It might return an error, e.g. if the topic does not exist.

``` js
import { Vertx } from "@vertx/core"
import { AdminUtils } from "@vertx/kafka-client"
let adminUtils = AdminUtils.create(Vertx.vertx(), "localhost:2181", true);
// Set retention to 1000 ms and max size of the topic partition to 1 kiByte
let properties = {};
properties["delete.retention.ms"] = "1000";
properties["retention.bytes"] = "1024";
adminUtils.changeTopicConfig("myNewTopic", properties, (result, result_err) => {
  if (result.succeeded()) {
    console.log("Configuration change of topic myNewTopic successful!")} else {
    console.log("Configuration change of topic myNewTopic failed: " + result.cause().getLocalizedMessage())}
});
}
```

## Check if a topic exists

If you want to check if a topic exists, you can call `topicExists`.
Parameters are: topic name, and the usual callback to handle the result.
It might return an error, e.g. if the topic does not exist.

``` js
import { Vertx } from "@vertx/core"
import { AdminUtils } from "@vertx/kafka-client"
let adminUtils = AdminUtils.create(Vertx.vertx(), "localhost:2181", true);
adminUtils.topicExists("myNewTopic", (result, result_err) => {
  if (result.succeeded()) {
    console.log("Topic myNewTopic exists: " + result.result());
  } else {
    console.log("Failed to check if topic myNewTopic exists: " + result.cause().getLocalizedMessage())}
});
```
