A Vert.x client allowing applications to interact with a RabbitMQ broker
(AMQP 0.9.1)

**This service is experimental and the APIs are likely to change before
settling down.**

# Getting Started

## Maven

Add the following dependency to your maven project

``` xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-rabbitmq-client</artifactId>
 <version>${maven.version}</version>
</dependency>
```

## Gradle

Add the following dependency to your gradle project

``` groovy
dependencies {
 compile 'io.vertx:vertx-rabbitmq-client:${maven.version}'
}
```

## Create a client

You can create a client instance as follows using a full amqp uri:

``` js
import { RabbitMQClient } from "@vertx/rabbitmq"
let config = new RabbitMQOptions();
// full amqp uri
config.uri = "amqp://xvjvsrrc:VbuL1atClKt7zVNQha0bnnScbNvGiqgb@moose.rmq.cloudamqp.com/xvjvsrrc";
let client = RabbitMQClient.create(vertx, config);
```

Or you can also specify individual parameters manually:

``` js
import { RabbitMQClient } from "@vertx/rabbitmq"
let config = new RabbitMQOptions();
// Each parameter is optional
// The default parameter with be used if the parameter is not set
config.user = "user1";
config.password = "password1";
config.host = "localhost";
config.port = 5672;
config.virtualHost = "vhost1";
config.connectionTimeout = 6000;
config.requestedHeartbeat = 60;
config.handshakeTimeout = 6000;
config.requestedChannelMax = 5;
config.networkRecoveryInterval = 500;
config.automaticRecoveryEnabled = true;

let client = RabbitMQClient.create(vertx, config);
```

You can set multiples addresses to connect to a cluster;

``` js
import { RabbitMQClient } from "@vertx/rabbitmq"
let config = new RabbitMQOptions();
config.user = "user1";
config.password = "password1";
config.virtualHost = "vhost1";

config.addresses = [Java.type("com.rabbitmq.client.Address").parseAddresses("firstHost,secondHost:5672")];

let client = RabbitMQClient.create(vertx, config);
```

## Declare exchange with additional config

You can pass additional config parameters to RabbitMQ’s exchangeDeclare
method

``` js
let config = {
};

config.x-dead-letter-exchange = "my.deadletter.exchange";
config.alternate-exchange = "my.alternate.exchange";
// ...
client.exchangeDeclare("my.exchange", "fanout", true, false, config, (onResult, onResult_err) => {
  if (onResult.succeeded()) {
    console.log("Exchange successfully declared with config");
  } else {
    onResult.cause().printStackTrace();
  }
});
```

## Declare queue with additional config

You can pass additional config parameters to RabbitMQs queueDeclare
method

``` js
let config = {
};
config.x-message-ttl = 10000;

client.queueDeclare("my-queue", true, false, true, config, (queueResult, queueResult_err) => {
  if (queueResult.succeeded()) {
    console.log("Queue declared!");
  } else {
    console.error("Queue failed to be declared!");
    queueResult.cause().printStackTrace();
  }
});
```

# Operations

The following are some examples of the operations supported by the
RabbitMQService API. Consult the javadoc/documentation for detailed
information on all API methods.

## Publish

Publish a message to a queue

``` js
let message = {
  "body" : "Hello RabbitMQ, from Vert.x !"
};
client.basicPublish("", "my.queue", message, (pubResult, pubResult_err) => {
  if (pubResult.succeeded()) {
    console.log("Message published !");
  } else {
    pubResult.cause().printStackTrace();
  }
});
```

## Publish with confirm

Publish a message to a queue and confirm the broker acknowledged it.

``` js
let message = {
  "body" : "Hello RabbitMQ, from Vert.x !"
};

// Put the channel in confirm mode. This can be done once at init.
client.confirmSelect((confirmResult, confirmResult_err) => {
  if (confirmResult.succeeded()) {
    client.basicPublish("", "my.queue", message, (pubResult, pubResult_err) => {
      if (pubResult.succeeded()) {
        // Check the message got confirmed by the broker.
        client.waitForConfirms((waitResult, waitResult_err) => {
          if (waitResult.succeeded()) {
            console.log("Message published !")} else {
            waitResult.cause().printStackTrace()}
        });
      } else {
        pubResult.cause().printStackTrace();
      }
    });
  } else {
    confirmResult.cause().printStackTrace();
  }
});
```

## Consume

Consume messages from a queue.

``` js
// Create a stream of messages from a queue
client.basicConsumer("my.queue", (rabbitMQConsumerAsyncResult, rabbitMQConsumerAsyncResult_err) => {
  if (rabbitMQConsumerAsyncResult.succeeded()) {
    console.log("RabbitMQ consumer created !");
    let mqConsumer = rabbitMQConsumerAsyncResult.result();
    mqConsumer.handler((message) => {
      console.log("Got message: " + message.body().toString());
    });
  } else {
    rabbitMQConsumerAsyncResult.cause().printStackTrace();
  }
});
```

At any moment of time you can pause or resume the stream. When stream is
paused you won’t receive any message.

``` js
consumer.pause();
consumer.resume();
```

There are actually a set of options to specify when creating a
consumption stream.

The `QueueOptions` lets you specify:

  - The size of internal queue with `setMaxInternalQueueSize`

  - Should the stream keep more recent messages when queue size is
    exceed with `setKeepMostRecent`

<!-- end list -->

``` js
let options = new QueueOptions()
  .setMaxInternalQueueSize(1000)
  .setKeepMostRecent(true);

client.basicConsumer("my.queue", options, (rabbitMQConsumerAsyncResult, rabbitMQConsumerAsyncResult_err) => {
  if (rabbitMQConsumerAsyncResult.succeeded()) {
    console.log("RabbitMQ consumer created !");
  } else {
    rabbitMQConsumerAsyncResult.cause().printStackTrace();
  }
});
```

When you want to stop consuming message from a queue, you can do:

``` js
rabbitMQConsumer.cancel((cancelResult, cancelResult_err) => {
  if (cancelResult.succeeded()) {
    console.log("Consumption successfully stopped");
  } else {
    console.log("Tired in attempt to stop consumption");
    cancelResult.cause().printStackTrace();
  }
});
```

You can get notified by the end handler when the queue won’t process any
more messages:

``` js
rabbitMQConsumer.endHandler((v) => {
  console.log("It is the end of the stream");
});
```

You can set the exception handler to be notified of any error that may
occur when a message is processed:

``` js
consumer.exceptionHandler((e) => {
  console.log("An exception occurred in the process of message handling");
  e.printStackTrace();
});
```

And finally, you may want to retrive a related to the consumer tag:

``` js
let consumerTag = consumer.consumerTag();
console.log("Consumer tag is: " + consumerTag);
```

## Get

Will get a message from a queue

``` js
client.basicGet("my.queue", true, (getResult, getResult_err) => {
  if (getResult.succeeded()) {
    let msg = getResult.result();
    console.log("Got message: " + msg.body);
  } else {
    getResult.cause().printStackTrace();
  }
});
```

## Consume messages without auto-ack

``` js
// Setup the rabbitmq consumer
client.basicConsumer("my.queue", new QueueOptions()
  .setAutoAck(false), (consumeResult, consumeResult_err) => {
  if (consumeResult.succeeded()) {
    console.log("RabbitMQ consumer created !");
    let consumer = consumeResult.result();

    // Set the handler which messages will be sent to
    consumer.handler((msg) => {
      let json = msg.body();
      console.log("Got message: " + json.body);
      // ack
      client.basicAck(json.deliveryTag, false, (asyncResult, asyncResult_err) => {
      });
    });
  } else {
    consumeResult.cause().printStackTrace();
  }
});
```

# Running the tests

You will need to have RabbitMQ installed and running with default ports
on localhost for this to work.
