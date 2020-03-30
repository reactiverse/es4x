This component provides a Kafka client for reading and sending messages
from/to an [Apache Kafka](https://kafka.apache.org/) cluster.

As consumer, the API provides methods for subscribing to a topic
partition receiving messages asynchronously or reading them as a stream
(even with the possibility to pause/resume the stream).

As producer, the API provides methods for sending message to a topic
partition like writing on a stream.

# Using the Vert.x Kafka client

To use this component, add the following dependency to the dependencies
section of your build descriptor:

  - Maven (in your `pom.xml`):

<!-- end list -->

``` xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-kafka-client</artifactId>
 <version>${maven.version}</version>
</dependency>
```

  - Gradle (in your `build.gradle` file):

<!-- end list -->

``` groovy
compile io.vertx:vertx-kafka-client:${maven.version}
```

# Creating Kafka clients

Creating consumers and producers is quite similar and on how it works
using the native Kafka client library.

They need to be configured with a bunch of properties as described in
the official Apache Kafka documentation, for the
[consumer](https://kafka.apache.org/documentation/#newconsumerconfigs)
and for the
[producer](https://kafka.apache.org/documentation/#producerconfigs).

To achieve that, a map can be configured with such properties passing it
to one of the static creation methods exposed by `KafkaConsumer` and
`KafkaProducer`

``` js
import { KafkaConsumer } from "@vertx/kafka-client"

// creating the consumer using map config
let config = {};
config["bootstrap.servers"] = "localhost:9092";
config["key.deserializer"] = "org.apache.kafka.common.serialization.StringDeserializer";
config["value.deserializer"] = "org.apache.kafka.common.serialization.StringDeserializer";
config["group.id"] = "my_group";
config["auto.offset.reset"] = "earliest";
config["enable.auto.commit"] = "false";

// use consumer for interacting with Apache Kafka
let consumer = KafkaConsumer.create(vertx, config);
```

In the above example, a `KafkaConsumer` instance is created using a map
instance in order to specify the Kafka nodes list to connect (just one)
and the deserializers to use for getting key and value from each
received message.

Likewise a producer can be created

``` js
import { KafkaProducer } from "@vertx/kafka-client"

// creating the producer using map and class types for key and value serializers/deserializers
let config = {};
config["bootstrap.servers"] = "localhost:9092";
config["key.serializer"] = "org.apache.kafka.common.serialization.StringSerializer";
config["value.serializer"] = "org.apache.kafka.common.serialization.StringSerializer";
config["acks"] = "1";

// use producer for interacting with Apache Kafka
let producer = KafkaProducer.create(vertx, config);
```

# Receiving messages from a topic joining a consumer group

In order to start receiving messages from Kafka topics, the consumer can
use the `subscribe` method for subscribing to a set of topics being part
of a consumer group (specified by the properties on creation).

It’s also possible to use the `subscribe` method for subscribing to more
topics specifying a Java regex.

You also need to register a handler for handling incoming messages using
the `handler`.

``` js
// register the handler for incoming messages
consumer.handler((record) => {
  console.log("Processing key=" + record.key() + ",value=" + record.value() + ",partition=" + record.partition() + ",offset=" + record.offset());
});

// subscribe to several topics with list
let topics = new (Java.type("java.util.HashSet"))();
topics.add("topic1");
topics.add("topic2");
topics.add("topic3");
consumer.subscribe(topics);

// or using a Java regex
let pattern = Java.type("java.util.regex.Pattern").compile("topic\\d");
consumer.subscribe(pattern);

// or just subscribe to a single topic
consumer.subscribe("a-single-topic");
```

The handler can be registered before or after the call to `subscribe()`;
messages won’t be consumed until both methods have been called. This
allows you to call `subscribe()`, then `seek()` and finally `handler()`
in order to only consume messages starting from a particular offset, for
example.

A handler can also be passed during subscription to be aware of the
subscription result and being notified when the operation is completed.

``` js
// register the handler for incoming messages
consumer.handler((record) => {
  console.log("Processing key=" + record.key() + ",value=" + record.value() + ",partition=" + record.partition() + ",offset=" + record.offset());
});

// subscribe to several topics
let topics = new (Java.type("java.util.HashSet"))();
topics.add("topic1");
topics.add("topic2");
topics.add("topic3");
consumer.subscribe(topics, (ar) => {
  if (ar.succeeded()) {
    console.log("subscribed");
  } else {
    console.log("Could not subscribe " + ar.cause().getMessage());
  }
});

// or just subscribe to a single topic
consumer.subscribe("a-single-topic", (ar) => {
  if (ar.succeeded()) {
    console.log("subscribed");
  } else {
    console.log("Could not subscribe " + ar.cause().getMessage());
  }
});
```

Using the consumer group way, the Kafka cluster assigns partitions to
the consumer taking into account other connected consumers in the same
consumer group, so that partitions can be spread across them.

The Kafka cluster handles partitions re-balancing when a consumer leaves
the group (so assigned partitions are free to be assigned to other
consumers) or a new consumer joins the group (so it wants partitions to
read from).

You can register handlers on a `KafkaConsumer` to be notified of the
partitions revocations and assignments by the Kafka cluster using
`partitionsRevokedHandler` and `partitionsAssignedHandler`.

``` js
// register the handler for incoming messages
consumer.handler((record) => {
  console.log("Processing key=" + record.key() + ",value=" + record.value() + ",partition=" + record.partition() + ",offset=" + record.offset());
});

// registering handlers for assigned and revoked partitions
consumer.partitionsAssignedHandler((topicPartitions) => {

  console.log("Partitions assigned");
  topicPartitions.forEach(topicPartition => {
    console.log(topicPartition.topic + " " + topicPartition.partition);
  });
});

consumer.partitionsRevokedHandler((topicPartitions) => {

  console.log("Partitions revoked");
  topicPartitions.forEach(topicPartition => {
    console.log(topicPartition.topic + " " + topicPartition.partition);
  });
});

// subscribes to the topic
consumer.subscribe("test", (ar) => {

  if (ar.succeeded()) {
    console.log("Consumer subscribed");
  }
});
```

After joining a consumer group for receiving messages, a consumer can
decide to leave the consumer group in order to not get messages anymore
using `unsubscribe`

``` js
// consumer is already member of a consumer group

// unsubscribing request
consumer.unsubscribe();
```

You can add an handler to be notified of the result

``` js
// consumer is already member of a consumer group

// unsubscribing request
consumer.unsubscribe((ar) => {

  if (ar.succeeded()) {
    console.log("Consumer unsubscribed");
  }
});
```

# Receiving messages from a topic requesting specific partitions

Besides being part of a consumer group for receiving messages from a
topic, a consumer can ask for a specific topic partition. When the
consumer is not part part of a consumer group the overall application
cannot rely on the re-balancing feature.

You can use `assign` in order to ask for specific partitions.

``` js
// register the handler for incoming messages
consumer.handler((record) => {
  console.log("key=" + record.key() + ",value=" + record.value() + ",partition=" + record.partition() + ",offset=" + record.offset());
});

//
let topicPartitions = new (Java.type("java.util.HashSet"))();
topicPartitions.add(new TopicPartition()
  .setTopic("test")
  .setPartition(0));

// requesting to be assigned the specific partition
consumer.assign(topicPartitions, (done) => {

  if (done.succeeded()) {
    console.log("Partition assigned");

    // requesting the assigned partitions
    consumer.assignment((done1) => {

      if (done1.succeeded()) {

        done1.result().forEach(topicPartition => {
          console.log(topicPartition.topic + " " + topicPartition.partition);
        });
      }
    });
  }
});
```

As with `subscribe()`, the handler can be registered before or after the
call to `assign()`; messages won’t be consumed until both methods have
been called. This allows you to call `assign()`, then `seek()` and
finally `handler()` in order to only consume messages starting from a
particular offset, for example.

Calling `assignment` provides the list of the current assigned
partitions.

# Receiving messages with explicit polling

Other than using the internal polling mechanism in order to receive
messages from Kafka, the client can subscribe to a topic, avoiding to
register the handler for getting the messages and then using the `poll`
method.

In this way, the user application is in charge to execute the poll for
getting messages when it needs, for example after processing the
previous ones.

``` js
// subscribes to the topic
consumer.subscribe("test", (ar) => {

  if (ar.succeeded()) {
    console.log("Consumer subscribed");

    vertx.setPeriodic(1000, (timerId) => {

      consumer.poll(100, (ar1) => {

        if (ar1.succeeded()) {

          let records = ar1.result();
          for (let i = 0;i < records.size();i++) {
            let record = records.recordAt(i);
            console.log("key=" + record.key() + ",value=" + record.value() + ",partition=" + record.partition() + ",offset=" + record.offset());
          }
        }
      });

    });
  }
});
```

After subscribing successfully, the application start a periodic timer
in order to execute the poll and getting messages from Kafka
periodically.

# Changing the subscription or assignment

You can change the subscribed topics, or assigned partitions after you
have started to consume messages, simply by calling `subscribe()` or
`assign()` again.

Note that due to internal buffering of messages it is possible that the
record handler will continue to observe messages from the old
subscription or assignment *after* the `subscribe()` or `assign()`
method’s completion handler has been called. This is not the case for
messages observed by the batch handler: Once the completion handler has
been called it will only observe messages read from the subscription or
assignment.

# Getting topic partition information

You can call the `partitionsFor` to get information about partitions for
a specified topic

``` js
// asking partitions information about specific topic
consumer.partitionsFor("test", (ar) => {

  if (ar.succeeded()) {

    ar.result().forEach(partitionInfo => {
      console.log(partitionInfo);
    });
  }
});
```

In addition `listTopics` provides all available topics with related
partitions

``` js
// asking information about available topics and related partitions
consumer.listTopics((ar) => {

  if (ar.succeeded()) {

    let map = ar.result();
    map.forEach((partitions, topic) => {
      console.log("topic = " + topic);
      console.log("partitions = " + map[topic]);
    });
  }
});
```

# Manual offset commit

In Apache Kafka the consumer is in charge to handle the offset of the
last read message.

This is executed by the commit operation executed automatically every
time a bunch of messages are read from a topic partition. The
configuration parameter `enable.auto.commit` must be set to `true` when
the consumer is created.

Manual offset commit, can be achieved with `commit`. It can be used to
achieve *at least once* delivery to be sure that the read messages are
processed before committing the offset.

``` js
// consumer is processing read messages

// committing offset of the last read message
consumer.commit((ar) => {

  if (ar.succeeded()) {
    console.log("Last read message offset committed");
  }
});
```

# Seeking in a topic partition

Apache Kafka can retain messages for a long period of time and the
consumer can seek inside a topic partition and obtain arbitrary access
to the messages.

You can use `seek` to change the offset for reading at a specific
position

``` js
let topicPartition = new TopicPartition()
  .setTopic("test")
  .setPartition(0);

// seek to a specific offset
consumer.seek(topicPartition, 10, (done) => {

  if (done.succeeded()) {
    console.log("Seeking done");
  }
});
```

When the consumer needs to re-read the stream from the beginning, it can
use `seekToBeginning`

``` js
let topicPartition = new TopicPartition()
  .setTopic("test")
  .setPartition(0);

// seek to the beginning of the partition
consumer.seekToBeginning(Java.type("java.util.Collections").singleton(topicPartition), (done) => {

  if (done.succeeded()) {
    console.log("Seeking done");
  }
});
```

Finally `seekToEnd` can be used to come back at the end of the partition

``` js
let topicPartition = new TopicPartition()
  .setTopic("test")
  .setPartition(0);

// seek to the end of the partition
consumer.seekToEnd(Java.type("java.util.Collections").singleton(topicPartition), (done) => {

  if (done.succeeded()) {
    console.log("Seeking done");
  }
});
```

Note that due to internal buffering of messages it is possible that the
record handler will continue to observe messages read from the original
offset for a time *after* the `seek*()` method’s completion handler has
been called. This is not the case for messages observed by the batch
handler: Once the `seek*()` completion handler has been called it will
only observe messages read from the new offset.

# Offset lookup

You can use the beginningOffsets API introduced in Kafka 0.10.1.1 to get
the first offset for a given partition. In contrast to
`seekToBeginning`, it does not change the consumer’s offset.

``` js
let topicPartitions = new (Java.type("java.util.HashSet"))();
let topicPartition = new TopicPartition()
  .setTopic("test")
  .setPartition(0);
topicPartitions.add(topicPartition);

consumer.beginningOffsets(topicPartitions, (done) => {
  if (done.succeeded()) {
    let results = done.result();
    results.forEach((beginningOffset, topic) => {
      console.log("Beginning offset for topic=" + topic.topic + ", partition=" + topic.partition + ", beginningOffset=" + beginningOffset);
    });
  }
});

// Convenience method for single-partition lookup
consumer.beginningOffsets(topicPartition, (done) => {
  if (done.succeeded()) {
    let beginningOffset = done.result();
    console.log("Beginning offset for topic=" + topicPartition.topic + ", partition=" + topicPartition.partition + ", beginningOffset=" + beginningOffset);
  }
});
```

You can use the endOffsets API introduced in Kafka 0.10.1.1 to get the
last offset for a given partition. In contrast to `seekToEnd`, it does
not change the consumer’s offset.

``` js
let topicPartitions = new (Java.type("java.util.HashSet"))();
let topicPartition = new TopicPartition()
  .setTopic("test")
  .setPartition(0);
topicPartitions.add(topicPartition);

consumer.endOffsets(topicPartitions, (done) => {
  if (done.succeeded()) {
    let results = done.result();
    results.forEach((endOffset, topic) => {
      console.log("End offset for topic=" + topic.topic + ", partition=" + topic.partition + ", endOffset=" + endOffset);
    });
  }
});

// Convenience method for single-partition lookup
consumer.endOffsets(topicPartition, (done) => {
  if (done.succeeded()) {
    let endOffset = done.result();
    console.log("End offset for topic=" + topicPartition.topic + ", partition=" + topicPartition.partition + ", endOffset=" + endOffset);
  }
});
```

You can use the offsetsForTimes API introduced in Kafka 0.10.1.1 to look
up an offset by timestamp, i.e. search parameter is an epoch timestamp
and the call returns the lowest offset with ingestion timestamp \>=
given timestamp.

``` js
Code not translatable
```

# Message flow control

A consumer can control the incoming message flow and pause/resume the
read operation from a topic, e.g it can pause the message flow when it
needs more time to process the actual messages and then resume to
continue message processing.

To achieve that you can use `pause` and `resume`.

In the case of the partition-specific pause and resume it is possible
that the record handler will continue to observe messages from a paused
partition for a time *after* the `pause()` method’s completion handler
has been called. This is not the case for messages observed by the batch
handler: Once the `pause()` completion handler has been called it will
only observe messages from those partitions which are not paused.

``` js
let topicPartition = new TopicPartition()
  .setTopic("test")
  .setPartition(0);

// registering the handler for incoming messages
consumer.handler((record) => {
  console.log("key=" + record.key() + ",value=" + record.value() + ",partition=" + record.partition() + ",offset=" + record.offset());

  // i.e. pause/resume on partition 0, after reading message up to offset 5
  if ((record.partition() === 0) && (record.offset() === 5)) {

    // pause the read operations
    consumer.pause(topicPartition, (ar) => {

      if (ar.succeeded()) {

        console.log("Paused");

        // resume read operation after a specific time
        vertx.setTimer(5000, (timeId) => {

          // resume read operations
          consumer.resume(topicPartition);
        });
      }
    });
  }
});
```

# Closing a consumer

Call close to close the consumer. Closing the consumer closes any open
connections and releases all consumer resources.

The close is actually asynchronous and might not complete until some
time after the call has returned. If you want to be notified when the
actual close has completed then you can pass in a handler.

This handler will then be called when the close has fully completed.

``` js
consumer.close((res) => {
  if (res.succeeded()) {
    console.log("Consumer is now closed");
  } else {
    console.log("close failed");
  }
});
```

# Sending messages to a topic

You can use `write` to send messages (records) to a topic.

The simplest way to send a message is to specify only the destination
topic and the related value, omitting its key or partition, in this case
the messages are sent in a round robin fashion across all the partitions
of the topic.

``` js
import { KafkaProducerRecord } from "@vertx/kafka-client"

for (let i = 0;i < 5;i++) {

  // only topic and message value are specified, round robin on destination partitions
  let record = KafkaProducerRecord.create("test", "message_" + i);

  producer.write(record);
}
```

You can receive message sent metadata like its topic, its destination
partition and its assigned offset.

``` js
import { KafkaProducerRecord } from "@vertx/kafka-client"

for (let i = 0;i < 5;i++) {

  // only topic and message value are specified, round robin on destination partitions
  let record = KafkaProducerRecord.create("test", "message_" + i);

  producer.send(record, (done) => {

    if (done.succeeded()) {

      let recordMetadata = done.result();
      console.log("Message " + record.value() + " written on topic=" + recordMetadata.topic + ", partition=" + recordMetadata.partition + ", offset=" + recordMetadata.offset);
    }

  });
}
```

When you need to assign a partition to a message, you can specify its
partition identifier or its key

``` js
import { KafkaProducerRecord } from "@vertx/kafka-client"

for (let i = 0;i < 10;i++) {

  // a destination partition is specified
  let record = KafkaProducerRecord.create("test", null, "message_" + i, 0);

  producer.write(record);
}
```

Since the producers identifies the destination using key hashing, you
can use that to guarantee that all messages with the same key are sent
to the same partition and retain the order.

``` js
import { KafkaProducerRecord } from "@vertx/kafka-client"

for (let i = 0;i < 10;i++) {

  // i.e. defining different keys for odd and even messages
  let key = i % 2;

  // a key is specified, all messages with same key will be sent to the same partition
  let record = KafkaProducerRecord.create("test", Java.type("java.lang.String").valueOf(key), "message_" + i);

  producer.write(record);
}
```

> **Note**
> 
> the shared producer is created on the first `createShared` call and
> its configuration is defined at this moment, shared producer usage
> must use the same configuration.

# Sharing a producer

Sometimes you want to share the same producer from within several
verticles or contexts.

Calling `KafkaProducer.createShared` returns a producer that can be
shared safely.

``` js
import { KafkaProducer } from "@vertx/kafka-client"

// Create a shared producer identified by 'the-producer'
let producer1 = KafkaProducer.createShared(vertx, "the-producer", config);

// Sometimes later you can close it
producer1.close();
```

The same resources (thread, connection) will be shared between the
producer returned by this method.

When you are done with the producer, just close it, when all shared
producers are closed, the resources will be released for you.

# Closing a producer

Call close to close the producer. Closing the producer closes any open
connections and releases all producer resources.

The close is actually asynchronous and might not complete until some
time after the call has returned. If you want to be notified when the
actual close has completed then you can pass in a handler.

This handler will then be called when the close has fully completed.

``` js
producer.close((res) => {
  if (res.succeeded()) {
    console.log("Producer is now closed");
  } else {
    console.log("close failed");
  }
});
```

# Getting topic partition information

You can call the `partitionsFor` to get information about partitions for
a specified topic:

``` js
// asking partitions information about specific topic
producer.partitionsFor("test", (ar) => {

  if (ar.succeeded()) {

    ar.result().forEach(partitionInfo => {
      console.log(partitionInfo);
    });
  }
});
```

# Handling errors

Errors handling (e.g timeout) between a Kafka client (consumer or
producer) and the Kafka cluster is done using `exceptionHandler` or
`exceptionHandler`

``` js
// setting handler for errors
consumer.exceptionHandler((e) => {
  console.log("Error = " + e.getMessage());
});
```

# Automatic clean-up in verticles

If you’re creating consumers and producer from inside verticles, those
consumers and producers will be automatically closed when the verticle
is undeployed.

# Using Vert.x serializers/deserializers

Vert.x Kafka client comes out of the box with serializers and
deserializers for buffers, json object and json array.

In a consumer you can use buffers

``` js
// Creating a consumer able to deserialize to buffers
let config = {};
config["bootstrap.servers"] = "localhost:9092";
config["key.deserializer"] = "io.vertx.kafka.client.serialization.BufferDeserializer";
config["value.deserializer"] = "io.vertx.kafka.client.serialization.BufferDeserializer";
config["group.id"] = "my_group";
config["auto.offset.reset"] = "earliest";
config["enable.auto.commit"] = "false";

// Creating a consumer able to deserialize to json object
config = {};
config["bootstrap.servers"] = "localhost:9092";
config["key.deserializer"] = "io.vertx.kafka.client.serialization.JsonObjectDeserializer";
config["value.deserializer"] = "io.vertx.kafka.client.serialization.JsonObjectDeserializer";
config["group.id"] = "my_group";
config["auto.offset.reset"] = "earliest";
config["enable.auto.commit"] = "false";

// Creating a consumer able to deserialize to json array
config = {};
config["bootstrap.servers"] = "localhost:9092";
config["key.deserializer"] = "io.vertx.kafka.client.serialization.JsonArrayDeserializer";
config["value.deserializer"] = "io.vertx.kafka.client.serialization.JsonArrayDeserializer";
config["group.id"] = "my_group";
config["auto.offset.reset"] = "earliest";
config["enable.auto.commit"] = "false";
```

Or in a producer

``` js
// Creating a producer able to serialize to buffers
let config = {};
config["bootstrap.servers"] = "localhost:9092";
config["key.serializer"] = "io.vertx.kafka.client.serialization.BufferSerializer";
config["value.serializer"] = "io.vertx.kafka.client.serialization.BufferSerializer";
config["acks"] = "1";

// Creating a producer able to serialize to json object
config = {};
config["bootstrap.servers"] = "localhost:9092";
config["key.serializer"] = "io.vertx.kafka.client.serialization.JsonObjectSerializer";
config["value.serializer"] = "io.vertx.kafka.client.serialization.JsonObjectSerializer";
config["acks"] = "1";

// Creating a producer able to serialize to json array
config = {};
config["bootstrap.servers"] = "localhost:9092";
config["key.serializer"] = "io.vertx.kafka.client.serialization.JsonArraySerializer";
config["value.serializer"] = "io.vertx.kafka.client.serialization.JsonArraySerializer";
config["acks"] = "1";
```

Unresolved directive in index.adoc - include::admin.adoc\[\]
