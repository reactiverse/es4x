Vert.x Web Client is an asynchronous HTTP and HTTP/2 client.

The Web Client makes easy to do HTTP request/response interactions with
a web server, and provides advanced features like:

  - Json body encoding / decoding

  - request/response pumping

  - request parameters

  - unified error handling

  - form submissions

The Web Client does not deprecate the Vert.x Core `HttpClient`, indeed
it is based on this client and inherits its configuration and great
features like pooling, HTTP/2 support, pipelining support, etc…​ The
`HttpClient` should be used when fine grained control over the HTTP
requests/responses is necessary.

The Web Client does not provide a WebSocket API, the Vert.x Core
`HttpClient` should be used. It also does not handle cookies at the
moment.

# Using the Web Client

To use Vert.x Web Client, add the following dependency to the
*dependencies* section of your build descriptor:

  - Maven (in your `pom.xml`):

<!-- end list -->

``` xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-web-client</artifactId>
 <version>${maven.version}</version>
</dependency>
```

  - Gradle (in your `build.gradle` file):

<!-- end list -->

``` groovy
dependencies {
 compile 'io.vertx:vertx-web-client:${maven.version}'
}
```

# Re-cap on Vert.x core HTTP client

Vert.x Web Client uses the API from Vert.x core, so it’s well worth
getting familiar with the basic concepts of using `HttpClient` using
Vert.x core, if you’re not already.

# Creating a Web Client

You create an `WebClient` instance with default options as follows

``` js
import { WebClient } from "@vertx/web-client"
let client = WebClient.create(vertx);
```

If you want to configure options for the client, you create it as
follows

``` js
import { WebClient } from "@vertx/web-client"
let options = new WebClientOptions()
  .setUserAgent("My-App/1.2.3");
options.keepAlive = false;
let client = WebClient.create(vertx, options);
```

Web Client options inherit Http Client options so you can set any one of
them.

If your already have an HTTP Client in your application you can also
reuse it

``` js
import { WebClient } from "@vertx/web-client"
let client = WebClient.wrap(httpClient);
```

> **Important**
> 
> In most cases, a Web Client should be created once on application
> startup and then reused. Otherwise you lose a lot of benefits such as
> connection pooling and may leak resources if instances are not closed
> properly.

# Making requests

## Simple requests with no body

Often, you’ll want to make HTTP requests with no request body. This is
usually the case with HTTP GET, OPTIONS and HEAD requests

``` js
import { WebClient } from "@vertx/web-client"

let client = WebClient.create(vertx);

// Send a GET request
client.get(8080, "myserver.mycompany.com", "/some-uri").send((ar) => {
  if (ar.succeeded()) {
    // Obtain response
    let response = ar.result();

    console.log("Received response with status code" + response.statusCode());
  } else {
    console.log("Something went wrong " + ar.cause().getMessage());
  }
});

// Send a HEAD request
client.head(8080, "myserver.mycompany.com", "/some-uri").send((ar) => {
  if (ar.succeeded()) {
    // Obtain response
    let response = ar.result();

    console.log("Received response with status code" + response.statusCode());
  } else {
    console.log("Something went wrong " + ar.cause().getMessage());
  }
});
```

You can add query parameters to the request URI in a fluent fashion

``` js
client.get(8080, "myserver.mycompany.com", "/some-uri").addQueryParam("param", "param_value").send((ar) => {
  if (ar.succeeded()) {
    // Obtain response
    let response = ar.result();

    console.log("Received response with status code" + response.statusCode());
  } else {
    console.log("Something went wrong " + ar.cause().getMessage());
  }
});
```

Any request URI parameter will pre-populate the request

``` js
let request = client.get(8080, "myserver.mycompany.com", "/some-uri?param1=param1_value&param2=param2_value");

// Add param3
request.addQueryParam("param3", "param3_value");

// Overwrite param2
request.setQueryParam("param2", "another_param2_value");
```

Setting a request URI discards existing query parameters

``` js
let request = client.get(8080, "myserver.mycompany.com", "/some-uri");

// Add param1
request.addQueryParam("param1", "param1_value");

// Overwrite param1 and add param2
request.uri("/some-uri?param1=param1_value&param2=param2_value");
```

## Writing request bodies

When you need to make a request with a body, you use the same API and
call then `sendXXX` methods that expects a body to send.

Use `sendBuffer` to send a buffer body

``` js
// Send a buffer to the server using POST, the content-length header will be set for you
client.post(8080, "myserver.mycompany.com", "/some-uri").sendBuffer(buffer, (ar) => {
  if (ar.succeeded()) {
    // Ok
  }
});
```

Sending a single buffer is useful but often you don’t want to load fully
the content in memory because it may be too large or you want to handle
many concurrent requests and want to use just the minimum for each
request. For this purpose the Web Client can send `ReadStream<Buffer>`
(e.g a `AsyncFile` is a ReadStream\<Buffer\>\`) with the `sendStream`
method

``` js
// When the stream len is unknown sendStream sends the file to the server using chunked transfer encoding
client.post(8080, "myserver.mycompany.com", "/some-uri").sendStream(stream, (ar) => {
  if (ar.succeeded()) {
    // Ok
  }
});
```

The Web Client takes care of setting up the transfer pump for you. Since
the length of the stream is not know the request will use chunked
transfer encoding .

When you know the size of the stream, you shall specify before using the
`content-length` header

``` js
fs.open("content.txt", new OpenOptions(), (fileRes) => {
  if (fileRes.succeeded()) {
    let fileStream = fileRes.result();

    let fileLen = "1024";

    // Send the file to the server using POST
    client.post(8080, "myserver.mycompany.com", "/some-uri").putHeader("content-length", fileLen).sendStream(fileStream, (ar) => {
      if (ar.succeeded()) {
        // Ok
      }
    });
  }
});
```

The POST will not be chunked.

### Json bodies

Often you’ll want to send Json body requests, to send a `JsonObject` use
the `sendJsonObject`

``` js
client.post(8080, "myserver.mycompany.com", "/some-uri").sendJsonObject({
  "firstName" : "Dale",
  "lastName" : "Cooper"
}, (ar) => {
  if (ar.succeeded()) {
    // Ok
  }
});
```

In Java, Groovy or Kotlin, you can use the `sendJson` method that maps a
POJO (Plain Old Java Object) to a Json object using `Json.encode` method

``` js
client.post(8080, "myserver.mycompany.com", "/some-uri").sendJson(new (Java.type("examples.WebClientExamples.User"))("Dale", "Cooper"), (ar) => {
  if (ar.succeeded()) {
    // Ok
  }
});
```

> **Note**
> 
> the `Json.encode` uses the Jackson mapper to encode the object to
> Json.

### Form submissions

You can send http form submissions bodies with the `sendForm` variant.

``` js
import { MultiMap } from "@vertx/core"
let form = MultiMap.caseInsensitiveMultiMap();
form.set("firstName", "Dale");
form.set("lastName", "Cooper");

// Submit the form as a form URL encoded body
client.post(8080, "myserver.mycompany.com", "/some-uri").sendForm(form, (ar) => {
  if (ar.succeeded()) {
    // Ok
  }
});
```

By default the form is submitted with the
`application/x-www-form-urlencoded` content type header. You can set the
`content-type` header to `multipart/form-data` instead

``` js
import { MultiMap } from "@vertx/core"
let form = MultiMap.caseInsensitiveMultiMap();
form.set("firstName", "Dale");
form.set("lastName", "Cooper");

// Submit the form as a multipart form body
client.post(8080, "myserver.mycompany.com", "/some-uri").putHeader("content-type", "multipart/form-data").sendForm(form, (ar) => {
  if (ar.succeeded()) {
    // Ok
  }
});
```

If you want to upload files and send attributes, you can create a
`MultipartForm` and use `sendMultipartForm`.

``` js
import { MultipartForm } from "@vertx/web-common"
let form = MultipartForm.create().attribute("imageDescription", "a very nice image").binaryFileUpload("imageFile", "image.jpg", "/path/to/image", "image/jpeg");

// Submit the form as a multipart form body
client.post(8080, "myserver.mycompany.com", "/some-uri").sendMultipartForm(form, (ar) => {
  if (ar.succeeded()) {
    // Ok
  }
});
```

## Writing request headers

You can write headers to a request using the headers multi-map as
follows:

``` js
let request = client.get(8080, "myserver.mycompany.com", "/some-uri");
let headers = request.headers();
headers.set("content-type", "application/json");
headers.set("other-header", "foo");
```

The headers are an instance of `MultiMap` which provides operations for
adding, setting and removing entries. Http headers allow more than one
value for a specific key.

You can also write headers using putHeader

``` js
let request = client.get(8080, "myserver.mycompany.com", "/some-uri");
request.putHeader("content-type", "application/json");
request.putHeader("other-header", "foo");
```

## Configure the request to add authentication.

Authentication can be performed manually by setting the correct headers,
or, using our predefined methods (We strongly suggest having HTTPS
enabled, especially for authenticated requests):

In basic HTTP authentication, a request contains a header field of the
form `Authorization: Basic <credentials>`, where credentials is the
base64 encoding of id and password joined by a colon.

You can configure the request to add basic access authentication as
follows:

``` js
let request = client.get(8080, "myserver.mycompany.com", "/some-uri").basicAuthentication("myid", "mypassword");
```

In OAuth 2.0, a request contains a header field of the form
`Authorization: Bearer <bearerToken>`, where bearerToken is the bearer
token issued by an authorization server to access protected resources.

You can configure the request to add bearer token authentication as
follows:

``` js
let request = client.get(8080, "myserver.mycompany.com", "/some-uri").bearerTokenAuthentication("myBearerToken");
```

## Reusing requests

The `send` method can be called multiple times safely, making it very
easy to configure and reuse `HttpRequest` objects

``` js
let get = client.get(8080, "myserver.mycompany.com", "/some-uri");
get.send((ar) => {
  if (ar.succeeded()) {
    // Ok
  }
});

// Same request again
get.send((ar) => {
  if (ar.succeeded()) {
    // Ok
  }
});
```

Beware though that `HttpRequest` instances are mutable. Therefore you
should call the `copy` method before modifying a cached instance.

``` js
let get = client.get(8080, "myserver.mycompany.com", "/some-uri");
get.send((ar) => {
  if (ar.succeeded()) {
    // Ok
  }
});

// The "get" request instance remains unmodified
get.copy().putHeader("a-header", "with-some-value").send((ar) => {
  if (ar.succeeded()) {
    // Ok
  }
});
```

## Timeouts

You can set a timeout for a specific http request using `timeout`.

``` js
client.get(8080, "myserver.mycompany.com", "/some-uri").timeout(5000).send((ar) => {
  if (ar.succeeded()) {
    // Ok
  } else {
    // Might be a timeout when cause is java.util.concurrent.TimeoutException
  }
});
```

If the request does not return any data within the timeout period an
exception will be passed to the response handler.

# Handling http responses

When the Web Client sends a request you always deal with a single async
result `HttpResponse`.

On a success result the callback happens after the response has been
received

``` js
client.get(8080, "myserver.mycompany.com", "/some-uri").send((ar) => {
  if (ar.succeeded()) {

    let response = ar.result();

    console.log("Received response with status code" + response.statusCode());
  } else {
    console.log("Something went wrong " + ar.cause().getMessage());
  }
});
```

> **Caution**
> 
> By default, a Vert.x Web Client request ends with an error only if
> something wrong happens at the network level. In other words, a `404
> Not Found` response, or a response with the wrong content type, are
> **not** considered as failures. Use [response
> predicates](#response-predicates) if you want the Web Client to
> perform sanity checks automatically.

> **Warning**
> 
> Responses are fully buffered, use `BodyCodec.pipe` to pipe the
> response to a write stream

## Decoding responses

By default the Web Client provides an http response body as a `Buffer`
and does not apply any decoding.

Custom response body decoding can be achieved using `BodyCodec`:

  - Plain String

  - Json object

  - Json mapped POJO

  - `WriteStream`

A body codec can decode an arbitrary binary data stream into a specific
object instance, saving you the decoding step in your response handlers.

Use `BodyCodec.jsonObject` To decode a Json object:

``` js
import { BodyCodec } from "@vertx/web-common"
client.get(8080, "myserver.mycompany.com", "/some-uri").as(BodyCodec.jsonObject()).send((ar) => {
  if (ar.succeeded()) {
    let response = ar.result();

    let body = response.body();

    console.log("Received response with status code" + response.statusCode() + " with body " + body);
  } else {
    console.log("Something went wrong " + ar.cause().getMessage());
  }
});
```

In Java, Groovy or Kotlin, custom Json mapped POJO can be decoded

``` js
import { BodyCodec } from "@vertx/web-common"
client.get(8080, "myserver.mycompany.com", "/some-uri").as(BodyCodec.json(Java.type("examples.WebClientExamples.User").class)).send((ar) => {
  if (ar.succeeded()) {
    let response = ar.result();

    let user = response.body();

    console.log("Received response with status code" + response.statusCode() + " with body " + user.getFirstName() + " " + user.getLastName());
  } else {
    console.log("Something went wrong " + ar.cause().getMessage());
  }
});
```

When large response are expected, use the `BodyCodec.pipe`. This body
codec pumps the response body buffers to a `WriteStream` and signals the
success or the failure of the operation in the async result response

``` js
import { BodyCodec } from "@vertx/web-common"
client.get(8080, "myserver.mycompany.com", "/some-uri").as(BodyCodec.pipe(writeStream)).send((ar) => {
  if (ar.succeeded()) {

    let response = ar.result();

    console.log("Received response with status code" + response.statusCode());
  } else {
    console.log("Something went wrong " + ar.cause().getMessage());
  }
});
```

Finally if you are not interested at all by the response content, the
`BodyCodec.none` simply discards the entire response body

``` js
import { BodyCodec } from "@vertx/web-common"
client.get(8080, "myserver.mycompany.com", "/some-uri").as(BodyCodec.none()).send((ar) => {
  if (ar.succeeded()) {

    let response = ar.result();

    console.log("Received response with status code" + response.statusCode());
  } else {
    console.log("Something went wrong " + ar.cause().getMessage());
  }
});
```

When you don’t know in advance the content type of the http response,
you can still use the `bodyAsXXX()` methods that decode the response to
a specific type

``` js
client.get(8080, "myserver.mycompany.com", "/some-uri").send((ar) => {
  if (ar.succeeded()) {

    let response = ar.result();

    // Decode the body as a json object
    let body = response.bodyAsJsonObject();

    console.log("Received response with status code" + response.statusCode() + " with body " + body);
  } else {
    console.log("Something went wrong " + ar.cause().getMessage());
  }
});
```

> **Warning**
> 
> this is only valid for the response decoded as a buffer.

## Response predicates

By default, a Vert.x Web Client request ends with an error only if
something wrong happens at the network level.

In other words, you must perform sanity checks manually after the
response is received:

``` js
client.get(8080, "myserver.mycompany.com", "/some-uri").send((ar) => {
  if (ar.succeeded()) {

    let response = ar.result();

    if (response.statusCode() === 200 && response.getHeader("content-type") == "application/json") {

      // Decode the body as a json object
      let body = response.bodyAsJsonObject();
      console.log("Received response with status code" + response.statusCode() + " with body " + body);

    } else {
      console.log("Something went wrong " + response.statusCode());
    }

  } else {
    console.log("Something went wrong " + ar.cause().getMessage());
  }
});
```

You can trade flexibility for clarity and conciseness using *response
predicates*.

`Response predicates` can fail a request when the response does not
match a criteria.

The Web Client comes with a set of out of the box predicates ready to
use:

``` js
import { ResponsePredicate } from "@vertx/web-client"
client.get(8080, "myserver.mycompany.com", "/some-uri").expect(ResponsePredicate.SC_SUCCESS).expect(ResponsePredicate.JSON).send((ar) => {
  if (ar.succeeded()) {

    let response = ar.result();

    // Safely decode the body as a json object
    let body = response.bodyAsJsonObject();
    console.log("Received response with status code" + response.statusCode() + " with body " + body);

  } else {
    console.log("Something went wrong " + ar.cause().getMessage());
  }
});
```

You can also create custom predicates when existing predicates don’t fit
your needs:

``` js
import { ResponsePredicateResult } from "@vertx/web-client"

// Check CORS header allowing to do POST
let methodsPredicate = (resp) => {
  let methods = resp.getHeader("Access-Control-Allow-Methods");
  if ((methods !== null && methods !== undefined)) {
    if (methods.contains("POST")) {
      return ResponsePredicateResult.success()
    }
  }
  return ResponsePredicateResult.failure("Does not work")
};

// Send pre-flight CORS request
client.request('OPTIONS', 8080, "myserver.mycompany.com", "/some-uri").putHeader("Origin", "Server-b.com").putHeader("Access-Control-Request-Method", "POST").expect(methodsPredicate).send((ar) => {
  if (ar.succeeded()) {
    // Process the POST request now
  } else {
    console.log("Something went wrong " + ar.cause().getMessage());
  }
});
```

> **Tip**
> 
> Response predicates are evaluated *before* the response body is
> received. Therefore you can’t inspect the response body in a predicate
> test function.

### Predefined predicates

As a convenience, the Web Client ships a few predicates for common uses
cases .

For status codes, e.g `ResponsePredicate.SC_SUCCESS` to verify that the
response has a `2xx` code, you can also create a custom one:

``` js
import { ResponsePredicate } from "@vertx/web-client"
client.get(8080, "myserver.mycompany.com", "/some-uri").expect(ResponsePredicate.status(200, 202)).send((ar) => {
  // ....
});
```

For content types, e.g `ResponsePredicate.JSON` to verify that the
response body contains JSON data, you can also create a custom one:

``` js
import { ResponsePredicate } from "@vertx/web-client"
client.get(8080, "myserver.mycompany.com", "/some-uri").expect(ResponsePredicate.contentType("some/content-type")).send((ar) => {
  // ....
});
```

Please refer to the `ResponsePredicate` documentation for a full list of
predefined predicates.

## Handling 30x redirections

By default the client follows redirections, you can configure the
default behavior in the `WebClientOptions`:

``` js
import { WebClient } from "@vertx/web-client"

// Change the default behavior to not follow redirects
let client = WebClient.create(vertx, new WebClientOptions()
  .setFollowRedirects(false));
```

The client will follow at most `16` requests redirections, it can be
changed in the same options:

``` js
import { WebClient } from "@vertx/web-client"

// Follow at most 5 redirections
let client = WebClient.create(vertx, new WebClientOptions()
  .setMaxRedirects(5));
```

> **Note**
> 
> For security reason, client won’t follow redirects for request with
> methods different from GET or HEAD

# Using HTTPS

Vert.x Web Client can be configured to use HTTPS in exactly the same way
as the Vert.x `HttpClient`.

You can specify the behavior per request

``` js
client.get(443, "myserver.mycompany.com", "/some-uri").ssl(true).send((ar) => {
  if (ar.succeeded()) {
    // Obtain response
    let response = ar.result();

    console.log("Received response with status code" + response.statusCode());
  } else {
    console.log("Something went wrong " + ar.cause().getMessage());
  }
});
```

Or using create methods with absolute URI argument

``` js
client.getAbs("https://myserver.mycompany.com:4043/some-uri").send((ar) => {
  if (ar.succeeded()) {
    // Obtain response
    let response = ar.result();

    console.log("Received response with status code" + response.statusCode());
  } else {
    console.log("Something went wrong " + ar.cause().getMessage());
  }
});
```

# Sessions management

Vert.x web offers a web session management facility; to use it, you
create a `WebClientSession` for every user (session) and use it instead
of the `WebClient`.

## Creating a WebSession

You create a `WebClientSession` instance as follows

``` js
import { WebClient } from "@vertx/web-client"
let client = WebClient.create(vertx);
let session = Java.type("io.vertx.ext.web.client.WebClientSession").create(client);
```

## Making requests

Once created, a `WebClientSession` can be used instead of a `WebClient`
to do HTTP(s) requests and automatically manage any cookies received
from the server(s) you are calling.

## Setting session level headers

You can set any session level headers to be added to every request as
follows:

``` js
let session = Java.type("io.vertx.ext.web.client.WebClientSession").create(client);
session.addHeader("my-jwt-token", jwtToken);
```

The headers will then be added to every request; notice that these
headers will be sent to all hosts; if you need to send different headers
to different hosts, you have to add them manually to every single
request and not to the `WebClientSession`.

# Domain sockets

Since 3.7.1 the Web Client supports domain sockets, e.g you can interact
with the [local Docker
daemon](https://docs.docker.com/engine/reference/commandline/dockerd/).

To achieve this, the `Vertx` instance must be created using a native
transport, you can read the Vert.x core documentation that explains it
clearly.

``` js
import { SocketAddress } from "@vertx/core"
import { BodyCodec } from "@vertx/web-common"
import { ResponsePredicate } from "@vertx/web-client"

// Creates the unix domain socket address to access the Docker API
let serverAddress = SocketAddress.domainSocketAddress("/var/run/docker.sock");

// We still need to specify host and port so the request HTTP header will be localhost:8080
// otherwise it will be a malformed HTTP request
// the actual value does not matter much for this example
client.request('GET', serverAddress, 8080, "localhost", "/images/json").expect(ResponsePredicate.SC_ACCEPTED).as(BodyCodec.jsonObject()).send((ar) => {
  if (ar.succeeded()) {
    // Obtain response
    let response = ar.result();

    console.log("Current Docker images" + response.body());
  } else {
    console.log("Something went wrong " + ar.cause().getMessage());
  }
});
```
