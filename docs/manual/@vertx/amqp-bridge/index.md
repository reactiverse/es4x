This component provides AMQP 1.0 producer and consumer support via a
bridging layer implementing the Vert.x event bus MessageProducer and
MessageConsumer APIs over the top of
[vertx-proton](https://github.com/vert-x3/vertx-proton/).

> **Warning**
> 
> This component will be removed in Vert.x 4, instead you should use
> [Vert.x AMQP Client](https://vertx.io/docs/vertx-amqp-client/java/)

# Using Vert.x AMQP Bridge

To use Vert.x AMQP Bridge, add the following dependency to the
*dependencies* section of your build descriptor:

  - Maven (in your `pom.xml`):

<!-- end list -->

``` xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-amqp-bridge</artifactId>
 <version>${maven.version}</version>
</dependency>
```

  - Gradle (in your `build.gradle` file):

<!-- end list -->

``` groovy
compile io.vertx:vertx-amqp-bridge:${maven.version}
```

# Getting Started

## Sending a Message

Here is a simple example of creating a `MessageProducer` and sending a
message with it. First, an `AmqpBridge` is created and started to
establish the underlying AMQP connection, then when this is complete the
producer is created and a message sent using it. You can optionally
supply a username and password when starting the bridge, as well as
supplying `AmqpBridgeOptions` in order to configure various options such
as for using SSL connections.

``` js
import { AmqpBridge } from "@vertx/amqp-bridge"
let bridge = AmqpBridge.create(vertx);
// Start the bridge, then use the event loop thread to process things thereafter.
bridge.start("localhost", 5672, (res, res_err) => {
  // Set up a producer using the bridge, send a message with it.
  let producer = bridge.createProducer("myAmqpAddress");

  let amqpMsgPayload = {
  };
  amqpMsgPayload.body = "myStringContent";

  producer.send(amqpMsgPayload);
});
```

## Receiving a Message

Here is a simple example of creating a `MessageConsumer` and registering
a handler with it. First, an `AmqpBridge` is created and started to
establish the underlying AMQP connection, then when this is complete the
consumer is created and a handler registered that prints the body of
incoming AMQP messages.

``` js
import { AmqpBridge } from "@vertx/amqp-bridge"
let bridge = AmqpBridge.create(vertx);
// Start the bridge, then use the event loop thread to process things thereafter.
bridge.start("localhost", 5672, (res, res_err) => {
  // Set up a consumer using the bridge, register a handler for it.
  let consumer = bridge.createConsumer("myAmqpAddress");
  consumer.handler((vertxMsg) => {
    let amqpMsgPayload = vertxMsg.body();
    let amqpBody = amqpMsgPayload.body;

    console.log("Received a message with body: " + amqpBody);
  });
});
```

Receipt of the AMQP message is accepted automatically as soon as the
consumer’s handler returns upon delivering the message to the
application.

# Message Payload

## Overview

The message payload is passed as a JsonObject with elements representing
various sections of the [AMQP
message](http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-messaging-v1.0-os.html#section-message-format).

The top-level elements supported are:

**body**: The content for the body section of the AMQP message.
**body\_type**: An optional String used to indicate whether the "body"
element represents an AmqpValue (default), Data, or AmqpSequence
section. The values used are "value", "data", and "sequence"
respectively. **header**: An optional JsonObject representing the
elements of the message Header section. Expanded below. **properties**:
An optional JsonObject representing the elements of the message
Properties section. Expanded below. **application\_properties**: An
optional JsonObject containing any application defined
properties(/headers). **message\_annotations**: An optional JsonObject
representing any message annotations.

The elements of the optional "header" sub-element are:

**durable**: optional boolean indicating whether the message is durable
(default false). **priority**: optional short indicating the message
priority (default 4). **ttl**: optional long indicating ttl in
milliseconds (no default). See also 'properties' absolute expiry time.
**first\_acquirer**: boolean indicating if this is the first acquirer of
the message (default false) **delivery\_count**: long indicating the
number of previous *failed* delivery attempts for message.

The elements of the optional "properties" sub-element are:

**to**: optional string with address message is being sent to (no
default). **reply\_to**: optional string with address for replies (no
default). Set automatically when sent with reply handler.
**message\_id**: optional string with message id (no default). Set
automatically when sending with reply handler. **correlation\_id**:
optional string with correlation id (no default). Set automatically when
implicit reply is sent. **subject**: optional string with message
subject (no default). **group\_id**: optional string with message group
id (no default). **group\_sequence**: optional long with message group
sequence (no default). **reply\_to\_group\_id**: optional string with
message reply to group id (no default). **content\_type**: optional
string with message content type (no default). Only for use with Data
body sections. **content\_encoding**: optional string with message
content encoding (no default). **creation\_time**: optional long with
message creation time in milliseconds since the unix epoch (no default).
**absolute\_expiry\_time**: optional long with absolute expiry time as
milliseconds since the unix epoch (no default). **user\_id**: optional
string with the id of the user sending the message (no default).

## Application Properties

To send a message with application properties, the
"application\_properties" element is added to the payload, containing a
JsonObject whose contents represent the application property entries,
which have string keys and a object representing a simple value such as
String, Boolean, Integer, etc. For example, adding a property to a sent
message could look something like:

``` js
let applicationProperties = {
};
applicationProperties.name = "value";

let amqpMsgPayload = {
};
amqpMsgPayload.application_properties = applicationProperties;

producer.send(amqpMsgPayload);
```

When receiving a message with application properties, the
"application\_properties" element is added to the JsonObject payload
returned, containing a JsonObject whose contents represent the
application property entries. For example, retrieving an
application-property from a received message might look like:

``` js
// Check the application properties section was present before use, it may not be
let appProps = amqpMsgPayload.application_properties;
if ((appProps !== null && appProps !== undefined)) {
  let propValue = appProps.propertyName;
}
```

# Flow Control

Message transfer between peers, such as clients and servers, is governed
by credit in AMQP 1.0, with receiving peers granting sending peers a
number of credits to allow them to send messages. As each message is
sent a unit of credit is used up, with the receiving peer needing to
replenish the senders credit over time in order for message delivery to
progress. This allows for recipients to flow control senders by
governing the amount of outstanding credit available.

## Producers

While a MessageProducer will buffer outgoing messages if there are
insufficient credits to send them all immediately, and then send them
once credit is granted, it is typically more desirable for the
application to work in tandem with the producer and attempt to send only
what it knows can actually currently be sent.

This is possible by inspecting whether the producer write queue is full,
i.e it currently has no credit to send:

``` js
producer.writeQueueFull();
```

This check can be used in concert with a handler that can be registered
to receive callbacks whenever the producer receives more credit and is
able to send messages immediately rather than buffer them:

``` js
producer.drainHandler((v) => {
  // ...do stuff and send...
});
```

## Consumers

In the case of a MessageConsumer, the bridge automatically gives 1000
credits to the sending peer when the consumer handler is registered, and
replenishes this credit automatically as messages are delivered to the
handler. It is possible to adjust the amount of credit given initially
(the value must be at least 1) by adjusting the maximum buffered message
value before registering a handler, for example:

``` js
consumer.setMaxBufferedMessages(5);
consumer.handler((msg) => {
  // ...handle received messages...
});
```

# Connecting using SSL

You can also optionally supply `AmqpBridgeOptions` when creating the
bridge in order to configure various options, the most typically used of
which are around behaviour for SSL connections.

The following is an example of using configuration to create a bridge
connecting to a server using SSL, authenticating with a username and
password, and supplying a PKCS12 based trust store to verify trust of
the server certificate:

``` js
import { AmqpBridge } from "@vertx/amqp-bridge"
let bridgeOptions = new AmqpBridgeOptions();
bridgeOptions.ssl = true;

let trustOptions = new PfxOptions()
  .setPath("path/to/pkcs12.truststore")
  .setPassword("password");
bridgeOptions.pfxTrustOptions = trustOptions;

let bridge = AmqpBridge.create(vertx, bridgeOptions);
bridge.start("localhost", 5672, "username", "password", (res, res_err) => {
  // ..do things with the bridge..
});
```

The following is an example of using configuration to create a bridge
connecting to a server requiring SSL Client Certificate Authentication,
supplying both a PKCS12 based trust store to verify trust of the server
certificate and also a PKCS12 based key store containing an SSL key and
certificate the server can use to verify the client:

``` js
import { AmqpBridge } from "@vertx/amqp-bridge"
let bridgeOptions = new AmqpBridgeOptions();
bridgeOptions.ssl = true;

let trustOptions = new PfxOptions()
  .setPath("path/to/pkcs12.truststore")
  .setPassword("password");
bridgeOptions.pfxTrustOptions = trustOptions;

let keyCertOptions = new PfxOptions()
  .setPath("path/to/pkcs12.keystore")
  .setPassword("password");
bridgeOptions.pfxKeyCertOptions = keyCertOptions;

let bridge = AmqpBridge.create(vertx, bridgeOptions);
bridge.start("localhost", 5672, (res, res_err) => {
  // ..do things with the bridge..
});
```

# Sending and Receiving replies.

Like many messaging protocols, AMQP includes support for a reply-to
address to be set on each message sent so that recipients can be told
where to send any responses required. The vert.x `Message` objects also
support the concept of a reply address, though when using the Event Bus
the sender doesn’t set it explicitly, and it is instead populated
implicitly if a message is sent with a reply `Handler`. This section
describes how the bridge handles sending and receiving AMQP messages
with reply-to while using the Vert.x producer, consumer, and message
APIs implemented by the bridge.

## Sent messages seeking a reply.

There are two options when sending messages to which responses are
required:

  - Populate the AMQP reply-to address of the outgoing message
    explicitly.

  - Provide a reply handler when sending to populate it implicitly.

With the first option, you may explicitly populate the "reply\_to"
element of the message "properties" section, as outlined in the [message
payload overview](#message_payload). Here you would provide a string
containing the name of the AMQP address on the server to which
recipients should direct their responses, typically a named queue to
which you have already established a consumer to receive the replies.
This route may be necessary if you need to receive multiple replies to a
given sent AMQP message.

With the second option a reply `Handler` may also be given in addition
to the message payload when sending a message, to be registered such
that it is invoked when a \[single\] response message is received for
the message being sent.

To facilitate this, upon startup the bridge internally creates a
consumer from a server-named dynamic address, the name of which it then
uses as the reply-to address on any AMQP messages sent when a
replyHandler was given. The bridge also populates the *message-id* of
the outgoing AMQP message, and uses this value to keep track of the
reply handler. Incoming messages on the internal 'reply consumer' have
their *correlation-id* values inspected in order to match them to the
reply handler originally given, requiring that reply senders populate
the *correlation-id* field with the *message-id* of the original
message.

The following shows the process for the second option:

![producer reply handler](../../images/producer-reply-handler.png)

1.  The producer is used to send a message to an AMQP address, providing
    a reply handler.

2.  The bridge send implementation populates the *reply-to* and
    *message-id* fields of the outgoing AMQP message, records the
    handler, and sends the message to the server.

3.  The receiving application (perhaps also a Vert.x AMQP bridge)
    consumes the message and sends a reply to its *reply-to* address,
    setting its *correlation-id* field as the original messages
    *message-id*.

4.  The server dispatches the reply message to the internal 'reply
    consumer' of the bridge.

5.  The bridge processes the AMQP message, creating the Vert.x Message
    with JsonObject body, uses the *correlation-id* value to match it
    with the reply handler, and then invokes the handler with the reply
    message.

The following is a basic example of sending a message and providing a
reply-handler to process the response:

``` js
let amqpMsgPayload = {
};
amqpMsgPayload.body = "myRequest";

producer.send(amqpMsgPayload, (res, res_err) => {
  let amqpReplyMessagePayload = res.result().body();
  // ...do something with reply message...
});
```

## Received messages seeking a reply.

When a message arrives, its replyAddress may be inspected. If the AMQP
message had its *reply-to* field populated, then the address given will
be returned from the Vert.x message replyAddress method. If no
*reply-to* value was present on the message, the value returned will be
null.

There are two options when receiving messages to which responses are
required:

  - Populate the AMQP reply-to address of an outgoing message sent
    explicitly using a producer.

  - Send a reply using the Message reply method.

With the first option, you may explicitly populate the "reply\_to"
element of the message "properties" section, as outlined in the [message
payload overview](#message_payload), and send it explicitly using a
producer established to the address using the bridge.

With the second option, a reply message may be sent by calling the reply
method on the Vert.x message object. The reply method implementation
ensures that the outgoing message *correlation-id* is populated
appropriately using the *message-id* of the original message, such that
the response can be matched in the case the original message was sent
from a Vert.x AMQP bridge producer with a reply handler provided.

The following outlines the process for both routes, of receiving a
message sent by an application (not shown), and sending a reply:

![consumer reply](../../images/consumer-reply.png)

1.  The server sends an AMQP message to the consumer, with a reply-to
    value set to another address.

2.  The bridge processes the AMQP message, creating the Vert.x Message
    with JsonObject body. The Message replyAddress is set to the
    reply-to value from the AMQP message.

3.  The Message is passed to the consumer Handler, which processes it,
    inspecting the replyAddress and preparing to send a response.

4.  The handler chooses to either send a reply using an explicit
    producer, or call the reply method on the message object.

5.  The reply message arrives at the response address on the server,
    ready to be sent to a reply consumer for the original sending
    application .

The following is a basic example of sending a reply using the message
reply method:

``` js
consumer.handler((msg) => {
  // ...do something with received message...then reply...
  let replyAddress = msg.replyAddress();
  if ((replyAddress !== null && replyAddress !== undefined)) {
    let amqpReplyMessagePayload = {
    };
    amqpReplyMessagePayload.body = "myResponse";

    msg.reply(amqpReplyMessagePayload);
  }
});
```
