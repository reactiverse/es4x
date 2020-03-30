This component provides a simple way to expose health checks. Health
checks are used to express the current state of the application in very
simple terms: *UP* or *DOWN*. The health checks can be used
individually, or in combination to Vert.x Web or the event bus.

This component provides a Vert.x Web handler on which you can register
procedure testing the health of the application. The handler computes
the final state and returns the result as JSON.

# Using Vert.x Health Checks

Notice that you generally need Vert.x Web to use this component. In
addition add the following dependency:

  - Maven (in your `pom.xml`):

<!-- end list -->

``` xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-health-check</artifactId>
 <version>${maven.version}</version>
</dependency>
```

  - Gradle (in your `build.gradle` file):

<!-- end list -->

``` groovy
compile 'io.vertx:vertx-health-check:${maven.version}'
```

## Creating the health check object.

The central object is `HealthChecks`. You can create a new instance
using:

``` js
Code not translatable
```

Once you have created this object you can register and unregister
procedures. See more about this below.

## Registering the Vert.x Web handler

To create the Vert.x Web handler managing your health check you can
either:

  - using an existing instance of `HealthChecks`

  - let the handler create one instance for you.

<!-- end list -->

``` js
import { HealthCheckHandler } from "@vertx/health-checks"
import { HealthChecks } from "@vertx/health-checks"
import { Router } from "@vertx/web"
let healthCheckHandler1 = HealthCheckHandler.create(vertx);
let healthCheckHandler2 = HealthCheckHandler.createWithHealthChecks(HealthChecks.create(vertx));

let router = Router.router(vertx);
// Populate the router with routes...
// Register the health check handler
router.get("/health*").handler(healthCheckHandler1);
// Or
router.get("/ping*").handler(healthCheckHandler2);
```

Procedure registration can be directly made on the `HealthCheckHandler`
instance. Alternatively, if you have created the `HealthChecks` instance
beforehand, you can register the procedure on this object directly.
Registrations and unregistrations can be done at anytime, even after the
route registration:

``` js
Code not translatable
```

# Procedures

A procedure is a function checking some aspect of the system to deduce
the current health. It reports a `Status` indicating whether or not the
test has passed or failed. This function must not block and report to
the given `Future` whether or not it succeed.

When you register a procedure, you give a name, and the function
(handler) executing the check.

Rules deducing the status are the following

  - if the future is mark as failed, the check is considered as *KO*

  - if the future is completed successfully but without a `Status`, the
    check is considered as *OK*.

  - if the future is completed successfully with a `Status` marked as
    *OK*, the check is considered as *OK*.

  - if the future is completed successfully with a `Status` marked as
    *KO*, the check is considered as *KO*.

`Status` can also provide additional data:

``` js
Code not translatable
```

Procedures can be organised by groups. The procedure name indicates the
group. The procedures are organized as a tree and the structure is
mapped to HTTP urls (see below).

``` js
import { HealthCheckHandler } from "@vertx/health-checks"
let healthCheckHandler = HealthCheckHandler.create(vertx);

// Register procedures
// Procedure can be grouped. The group is deduced using a name with "/".
// Groups can contains other group
healthCheckHandler.register("a-group/my-procedure-name", (future) => {
  //....
});
healthCheckHandler.register("a-group/a-second-group/my-second-procedure-name", (future) => {
  //....
});

router.get("/health").handler(healthCheckHandler);
```

# HTTP responses and JSON Output

When using the Vert.x web handler, the overall health check is retrieved
using a HTTP GET or POST (depending on the route you registered) on the
route given when exposing the `HealthCheckHandler`.

If no procedure are registered, the response is `204 - NO CONTENT`,
indicating that the system is *UP* but no procedures has been executed.
The response does not contain a payload.

If there is at least one procedure registered, this procedure is
executed and the outcome status is computed. The response would use the
following status code:

  - `200` : Everything is fine

  - `503` : At least one procedure has reported a non-healthy state

  - `500` : One procedure has thrown an error or has not reported a
    status in time

The content is a JSON document indicating the overall result
(`outcome`). It’s either `UP` or `DOWN`. A `checks` array is also given
indicating the result of the different executed procedures. If the
procedure has reported additional data, the data is also given:

    {
    "checks" : [
    {
      "id" : "A",
      "status" : "UP"
    },
    {
      "id" : "B",
      "status" : "DOWN",
      "data" : {
        "some-data" : "some-value"
      }
    }
    ],
    "outcome" : "DOWN"
    }

In case of groups/ hierarchy, the `checks` array depicts this structure:

    {
    "checks" : [
    {
      "id" : "my-group",
      "status" : "UP",
      "checks" : [
      {
        "id" : "check-2",
        "status" : "UP",
      },
      {
        "id" : "check-1",
        "status" : "UP"
      }]
    }],
    "outcome" : "UP"
    }

If a procedure throws an error, reports a failure (exception), the JSON
document provides the `cause` in the `data` section. If a procedure does
not report back before a timeout, the indicated cause is `Timeout`.

# Examples of procedures

This section provides example of common health checks.

## JDBC

This check reports whether or not a connection to the database can be
established:

``` js
Code not translatable
```

## Service availability

This check reports whether or not a service (here a HTTP endpoint) is
available in the service discovery:

``` js
Code not translatable
```

## Event bus

This check reports whether a consumer is ready on the event bus. The
protocol, in this example, is a simple ping/pong, but it can be more
sophisticated. This check can be used to check whether or not a verticle
is ready if it’s listening on a specific event address.

``` js
Code not translatable
```

# Authentication

When using the Vert.x web handler, you can pass a `AuthProvider` use to
authenticate the request. Check \<a
href="http://vertx.io/docs/\#authentication\_and\_authorisation"\>Vert.x
Auth\</a\> for more details about available authentication providers.

The Vert.x Web handler creates a JSON object containing:

  - the request headers

  - the request params

  - the form param if any

  - the content as JSON if any and if the request set the content type
    to `application/json`.

The resulting object is passed to the auth provider to authenticate the
request. If the authentication failed, it returns a `403 - FORBIDDEN`
response.

# Exposing health checks on the event bus

While exposing the health checks using HTTP with the Vert.x web handler
is convenient, it can be useful to expose the data differently. This
section gives an example to expose the data on the event bus:

``` js
vertx.eventBus().consumer("health", (message) => {
  healthChecks.invoke(message.reply);
});
```
