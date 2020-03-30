# Using Vert.x MQTT

This component had officially released in the Vert.x stack, just
following dependency to the *dependencies* section of your build
descriptor:

  - Maven (in your `pom.xml`):

<!-- end list -->

``` xml
<dependency>
   <groupId>io.vertx</groupId>
   <artifactId>vertx-mqtt</artifactId>
   <version>${maven.version}</version>
</dependency>
```

  - Gradle (in your `build.gradle` file):

<!-- end list -->

``` groovy
compile io.vertx:vertx-mqtt:${maven.version}
```

# Vert.x MQTT server

This component provides a server which is able to handle connections,
communication and messages exchange with remote [MQTT](http://mqtt.org/)
clients. Its API provides a bunch of events related to raw protocol
messages received by clients and exposes some features in order to send
messages to them.

It’s not a fully featured MQTT broker but can be used for building
something like that or for protocol translation.

> **Warning**
> 
> this module has the tech preview status, this means the API can change
> between versions.

## Getting Started

### Handling client connection/disconnection

This example shows how it’s possible to handle the connection request
from a remote MQTT client. First, an `MqttServer` instance is created
and the `endpointHandler` method is used to specify the handler called
when a remote client sends a CONNECT message for connecting to the
server itself. The `MqttEndpoint` instance, provided as parameter to the
handler, brings all main information related to the CONNECT message like
client identifier, username/password, "will" information, clean session
flag, protocol version and the "keep alive" timeout. Inside that
handler, the *endpoint* instance provides the `accept` method for
replying to the remote client with the corresponding CONNACK message :
in this way, the connection is established. Finally, the server is
started using the `listen` method with the default behavior (on
localhost and default MQTT port 1883). The same method allows to specify
an handler in order to check if the server is started properly or not.

``` js
import { MqttServer } from "@vertx/mqtt"

let mqttServer = MqttServer.create(vertx);
mqttServer.endpointHandler((endpoint) => {

  // shows main connect info
  console.log("MQTT client [" + endpoint.clientIdentifier() + "] request to connect, clean session = " + endpoint.isCleanSession());

  if ((endpoint.auth() !== null && endpoint.auth() !== undefined)) {
    console.log("[username = " + endpoint.auth().username + ", password = " + endpoint.auth().password + "]");
  }
  if ((endpoint.will() !== null && endpoint.will() !== undefined)) {
    console.log("[will topic = " + endpoint.will().willTopic + " msg = " + new (Java.type("java.lang.String"))(endpoint.will().willMessageBytes) + " QoS = " + endpoint.will().willQos + " isRetain = " + endpoint.will().illRetain + "]");
  }

  console.log("[keep alive timeout = " + endpoint.keepAliveTimeSeconds() + "]");

  // accept connection from the remote client
  endpoint.accept(false);

}).listen((ar) => {

  if (ar.succeeded()) {

    console.log("MQTT server is listening on port " + ar.result().actualPort());
  } else {

    console.log("Error on starting the server");
    ar.cause().printStackTrace();
  }
});
```

The same *endpoint* instance provides the `disconnectHandler` for
specifying the handler called when the remote client sends a DISCONNECT
message in order to disconnect from the server; this handler takes no
parameters.

``` js
// handling disconnect message
endpoint.disconnectHandler((v) => {

  console.log("Received disconnect from client");
});
```

### Handling client connection/disconnection with SSL/TLS support

The server has the support for accepting connection requests through the
SSL/TLS protocol for authentication and encryption. In order to do that,
the `MqttServerOptions` class provides the `setSsl` method for setting
the usage of SSL/TLS (passing 'true' as value) and some other useful
methods for providing server certificate and related private key (as
Java key store reference, PEM or PFX format). In the following example,
the `setKeyCertOptions` method is used in order to pass the certificates
in PEM format. This method requires an instance of the possible
implementations of the `KeyCertOptions` interface and in this case the
`PemKeyCertOptions` class is used in order to provide the path for the
server certificate and the private key with the correspondent
`setCertPath` and `setKeyPath` methods. The MQTT server is started
passing the Vert.x instance as usual and the above MQTT options instance
to the creation method.

``` js
import { MqttServer } from "@vertx/mqtt"

let options = new MqttServerOptions()
  .setPort(8883)
  .setKeyCertOptions(new PemKeyCertOptions()
    .setKeyPath("./src/test/resources/tls/server-key.pem")
    .setCertPath("./src/test/resources/tls/server-cert.pem"))
  .setSsl(true);

let mqttServer = MqttServer.create(vertx, options);
mqttServer.endpointHandler((endpoint) => {

  // shows main connect info
  console.log("MQTT client [" + endpoint.clientIdentifier() + "] request to connect, clean session = " + endpoint.isCleanSession());

  if ((endpoint.auth() !== null && endpoint.auth() !== undefined)) {
    console.log("[username = " + endpoint.auth().username + ", password = " + endpoint.auth().password + "]");
  }
  if ((endpoint.will() !== null && endpoint.will() !== undefined)) {
    console.log("[will topic = " + endpoint.will().willTopic + " msg = " + new (Java.type("java.lang.String"))(endpoint.will().willMessageBytes) + " QoS = " + endpoint.will().willQos + " isRetain = " + endpoint.will().illRetain + "]");
  }

  console.log("[keep alive timeout = " + endpoint.keepAliveTimeSeconds() + "]");

  // accept connection from the remote client
  endpoint.accept(false);

}).listen((ar) => {

  if (ar.succeeded()) {

    console.log("MQTT server is listening on port " + ar.result().actualPort());
  } else {

    console.log("Error on starting the server");
    ar.cause().printStackTrace();
  }
});
```

All the other stuff related to handle endpoint connection and related
disconnection is managed in the same way without SSL/TLS support.

### Handling client subscription/unsubscription request

After a connection is established between client and server, the client
can send a subscription request for a topic using the SUBSCRIBE message.
The `MqttEndpoint` interface allows to specify an handler for the
incoming subscription request using the `subscribeHandler` method. Such
handler receives an instance of the `MqttSubscribeMessage` interface
which brings the list of topics with related QoS levels as desired by
the client. Finally, the endpoint instance provides the
`subscribeAcknowledge` method for replying to the client with the
related SUBACK message containing the granted QoS levels.

``` js
// handling requests for subscriptions
endpoint.subscribeHandler((subscribe) => {

  let grantedQosLevels = [];
  subscribe.topicSubscriptions().forEach(s => {
    console.log("Subscription for " + s.topicName() + " with QoS " + s.qualityOfService());
    grantedQosLevels.push(s.qualityOfService());
  });
  // ack the subscriptions request
  endpoint.subscribeAcknowledge(subscribe.messageId(), grantedQosLevels);

});
```

In the same way, it’s possible to use the `unsubscribeHandler` method on
the endpoint in order to specify the handler called when the client
sends an UNSUBSCRIBE message. This handler receives an instance of the
`MqttUnsubscribeMessage` interface as parameter with the list of topics
to unsubscribe. Finally, the endpoint instance provides the
`unsubscribeAcknowledge` method for replying to the client with the
related UNSUBACK message.

``` js
// handling requests for unsubscriptions
endpoint.unsubscribeHandler((unsubscribe) => {

  unsubscribe.topics().forEach(t => {
    console.log("Unsubscription for " + t);
  });
  // ack the subscriptions request
  endpoint.unsubscribeAcknowledge(unsubscribe.messageId());
});
```

### Handling client published message

In order to handle incoming messages published by the remote client, the
`MqttEndpoint` interface provides the `publishHandler` method for
specifying the handler called when the client sends a PUBLISH message.
This handler receives an instance of the `MqttPublishMessage` interface
as parameter with the payload, the QoS level, the duplicate and retain
flags.

If the QoS level is 0 (AT\_MOST\_ONCE), there is no need from the
endpoint to reply the client.

If the QoS level is 1 (AT\_LEAST\_ONCE), the endpoind needs to reply
with a PUBACK message using the available `publishAcknowledge` method.

If the QoS level is 2 (EXACTLY\_ONCE), the endpoint needs to reply with
a PUBREC message using the available `publishReceived` method; in this
case the same endpoint should handle the PUBREL message received from
the client as well (the remote client sends it after receiving the
PUBREC from the endpoint) and it can do that specifying the handler
through the `publishReleaseHandler` method. In order to close the QoS
level 2 delivery, the endpoint can use the `publishComplete` method for
sending the PUBCOMP message to the client.

``` js
// handling incoming published messages
endpoint.publishHandler((message) => {

  console.log("Just received message [" + message.payload().toString(Java.type("java.nio.charset.Charset").defaultCharset()) + "] with QoS [" + message.qosLevel() + "]");

  if (message.qosLevel() === 'AT_LEAST_ONCE') {
    endpoint.publishAcknowledge(message.messageId());
  } else if (message.qosLevel() === 'EXACTLY_ONCE') {
    endpoint.publishReceived(message.messageId());
  }

}).publishReleaseHandler((messageId) => {

  endpoint.publishComplete(messageId);
});
```

### Publish message to the client

The endpoint can publish a message to the remote client (sending a
PUBLISH message) using the `publish` method which takes the following
input parameters : the topic to publish, the payload, the QoS level, the
duplicate and retain flags.

If the QoS level is 0 (AT\_MOST\_ONCE), the endpoint won’t receiving any
feedback from the client.

If the QoS level is 1 (AT\_LEAST\_ONCE), the endpoint needs to handle
the PUBACK message received from the client in order to receive final
acknowledge of delivery. It’s possible using the
`publishAcknowledgeHandler` method specifying such an handler.

If the QoS level is 2 (EXACTLY\_ONCE), the endpoint needs to handle the
PUBREC message received from the client. The `publishReceivedHandler`
method allows to specify the handler for that. Inside that handler, the
endpoint can use the `publishRelease` method for replying to the client
with the PUBREL message. The last step is to handle the PUBCOMP message
received from the client as final acknowledge for the published message;
it’s possible using the `publishCompletionHandler` for specifying the
handler called when the final PUBCOMP message is received.

``` js
import { Buffer } from "@vertx/core"

// just as example, publish a message with QoS level 2
endpoint.publish("my_topic", Buffer.buffer("Hello from the Vert.x MQTT server"), 'EXACTLY_ONCE', false, false);

// specifing handlers for handling QoS 1 and 2
endpoint.publishAcknowledgeHandler((messageId) => {

  console.log("Received ack for message = " + messageId);

}).publishReceivedHandler((messageId) => {

  endpoint.publishRelease(messageId);

}).publishCompletionHandler((messageId) => {

  console.log("Received ack for message = " + messageId);
});
```

### Be notified by client keep alive

The underlying MQTT keep alive mechanism is handled by the server
internally. When the CONNECT message is received, the server takes care
of the keep alive timeout specified inside that message in order to
check if the client doesn’t send messages in such timeout. At same time,
for every PINGREQ received, the server replies with the related
PINGRESP.

Even if there is no need for the high level application to handle that,
the `MqttEndpoint` interface provides the `pingHandler` method for
specifying an handler called when a PINGREQ message is received from the
client. It’s just a notification to the application that the client
isn’t sending meaningful messages but only pings for keeping alive; in
any case the PINGRESP is automatically sent by the server internally as
described above.

``` js
// handling ping from client
endpoint.pingHandler((v) => {

  console.log("Ping received from client");
});
```

### Closing the server

The `MqttServer` interface provides the `close` method that can be used
for closing the server; it stops to listen for incoming connections and
closes all the active connections with remote clients. This method is
asynchronous and one overload provides the possibility to specify a
complention handler that will be called when the server is really
closed.

``` js
mqttServer.close((v) => {

  console.log("MQTT server closed");
});
```

### Automatic clean-up in verticles

If you’re creating MQTT servers from inside verticles, those servers
will be automatically closed when the verticle is undeployed.

### Scaling : sharing MQTT servers

The handlers related to the MQTT server are always executed in the same
event loop thread. It means that on a system with more cores, only one
instance is deployed so only one core is used. In order to use more
cores, it’s possible to deploy more instances of the MQTT server.

It’s possible to do that programmatically:

``` js
import { MqttServer } from "@vertx/mqtt"

for (let i = 0;i < 10;i++) {

  let mqttServer = MqttServer.create(vertx);
  mqttServer.endpointHandler((endpoint) => {
    // handling endpoint
  }).listen((ar) => {

    // handling start listening
  });

}
```

or using a verticle specifying the number of instances:

``` js
let options = new DeploymentOptions()
  .setInstances(10);
vertx.deployVerticle("com.mycompany.MyVerticle", options);
```

What’s really happen is that even only MQTT server is deployed but as
incoming connections arrive, Vert.x distributes them in a round-robin
fashion to any of the connect handlers executed on different cores.

# Vert.x MQTT client

This component provides an [MQTT](http://mqtt.org/) client which is
compliant with the 3.1.1 spec. Its API provides a bunch of methods for
connecting/disconnecting to a broker, publishing messages (with all
three different levels of QoS) and subscribing to topics.

> **Warning**
> 
> this module has the tech preview status, this means the API can change
> between versions.

## Getting started

### Connect/Disconnect

The client gives you opportunity to connect to a server and disconnect
from it. Also, you could specify things like the host and port of a
server you would like to connect to passing instance of
`MqttClientOptions` as a param through constructor.

This example shows how you could connect to a server and disconnect from
it using Vert.x MQTT client and calling `connect` and `disconnect`
methods.

``` js
import { MqttClient } from "@vertx/mqtt"
let client = MqttClient.create(vertx);

client.connect(1883, "mqtt.eclipse.org", (s) => {
  client.disconnect();
});
```

> **Note**
> 
> default address of server provided by `MqttClientOptions` is
> localhost:1883 and localhost:8883 if you are using SSL/TSL.

### Subscribe to a topic

Now, lest go deeper and take look at this example:

``` js
client.publishHandler((s) => {
  console.log("There are new message in topic: " + s.topicName());
  console.log("Content(as string) of the message: " + s.payload().toString());
  console.log("QoS: " + s.qosLevel());
}).subscribe("rpi2/temp", 2);
```

Here we have the example of usage of `subscribe` method. In order to
receive messages from rpi2/temp topic we call `subscribe` method.
Although, to handle received messages from server you need to provide a
handler, which will be called each time you have a new messages in the
topics you subscribe on. As this example shows, handler could be
provided via `publishHandler` method.

### Publishing message to a topic

If you would like to publish some message into topic then `publish`
should be called. Let’s take a look at the example:

``` js
import { Buffer } from "@vertx/core"
client.publish("temperature", Buffer.buffer("hello"), 'AT_LEAST_ONCE', false, false);
```

In the example we send message to topic with name "temperature".

### Keep connection with server alive

In order to keep connection with server you should time to time send
something to server otherwise server will close the connection. The
right way to keep connection alive is a `ping` method.

> **Important**
> 
> by default you client keep connections with server automatically. That
> means that you don’t need to call `ping` in order to keep connections
> with server. The `MqttClient` will do it for you.

If you want to disable this feature then you should call
`setAutoKeepAlive` with `false` as argument:

``` js
options.autoKeepAlive = false;
```

### Be notified when

  - publish is completed
    
    You could provide handler by calling `publishCompletionHandler`. The
    handler will be called each time publish is completed. This one is
    pretty useful because you could see the packetId of just received
    PUBACK or PUBCOMP packet.

<!-- end list -->

    import { Buffer } from "@vertx/core"
    client.publishCompletionHandler((id) => {
      console.log("Id of just received PUBACK or PUBCOMP packet is " + id);
    }).publish("hello", Buffer.buffer("hello"), 'EXACTLY_ONCE', false, false).publish("hello", Buffer.buffer("hello"), 'AT_LEAST_ONCE', false, false).publish("hello", Buffer.buffer("hello"), 'AT_LEAST_ONCE', false, false);

> **Warning**
> 
> The handler WILL NOT BE CALLED if sent publish packet with QoS=0.

  - subscribe completed
    
    ``` js
    client.subscribeCompletionHandler((mqttSubAckMessage) => {
      console.log("Id of just received SUBACK packet is " + mqttSubAckMessage.messageId());
      mqttSubAckMessage.grantedQoSLevels().forEach(s => {
        if (s === 128) {
          console.log("Failure");
        } else {
          console.log("Success. Maximum QoS is " + s);
        }
      });
    }).subscribe("temp", 1).subscribe("temp2", 2);
    ```

  - unsubscribe completed
    
    ``` js
    client.unsubscribeCompletionHandler((id) => {
      console.log("Id of just received UNSUBACK packet is " + id);
    }).subscribe("temp", 1).unsubscribe("temp");
    ```

  - unsubscribe sent
    
    ``` js
    Code not translatable
    ```

  - PINGRESP received
    
    ``` js
    client.pingResponseHandler((s) => {
      //The handler will be called time to time by default
      console.log("We have just received PINGRESP packet");
    });
    ```
