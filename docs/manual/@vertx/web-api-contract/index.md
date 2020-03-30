Vert.x Web API Contract extends Vert.x Web to support
[OpenAPI 3](https://www.openapis.org/), bringing to you a simple
interface to build your router and mount security and validation
handler.

If you are interested in building an application that routes API
Requests to event bus, check out [Vert.x Web API
Service](https://vertx.io/docs/vertx-web-api-service/java/)

# Using Vert.x API Contract

To use Vert.x API Contract, add the following dependency to the
*dependencies* section of your build descriptor:

  - Maven (in your `pom.xml`):

<!-- end list -->

``` xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-web-api-contract</artifactId>
 <version>${maven.version}</version>
</dependency>
```

  - Gradle (in your `build.gradle` file):

<!-- end list -->

``` groovy
dependencies {
 compile 'io.vertx:vertx-web-api-contract:${maven.version}'
}
```

# HTTP Requests validation

Vert.x provides a validation framework that will validate requests for
you and will put results of validation inside a container. To define a
`HTTPRequestValidationHandler`:

``` js
import { HTTPRequestValidationHandler } from "@vertx/web-api-contract"
// Create Validation Handler with some stuff
let validationHandler = HTTPRequestValidationHandler.create().addQueryParam("parameterName", 'INT', true).addFormParamWithPattern("formParameterName", "a{4}", true).addPathParam("pathParam", 'FLOAT');
```

Then you can mount your validation handler:

``` js
import { BodyHandler } from "@vertx/web"
// BodyHandler is required to manage body parameters like forms or json body
router.route().handler(BodyHandler.create());

router.get("/awesome/:pathParam").handler(validationHandler).handler((routingContext) => {
  // Get Request parameters container
  let params = routingContext.get("parsedParameters");

  // Get parameters
  let parameterName = params.queryParameter("parameterName").getInteger();
  let formParameterName = params.formParameter("formParameterName").getString();
  let pathParam = params.pathParameter("pathParam").getFloat();
}).failureHandler((routingContext) => {
  let failure = routingContext.failure();
  if (failure instanceof ValidationException) {
    // Something went wrong during validation!
    let validationErrorMessage = failure.getMessage();
  }
});
```

If validation succeeds, It returns request parameters inside
`RequestParameters`, otherwise It will throw a fail inside
`RoutingContext` with 400 status code and `ValidationException` failure.

## Types of request parameters

Every parameter has a type validator, a class that describes the
expected type of parameter. A type validator validates the value, casts
it in required language type and then loads it inside a
`RequestParameter` object. There are three ways to describe the type of
your parameter:

  - There is a set of prebuilt types that you can use: `ParameterType`

  - You can instantiate a custom instance of prebuilt type validators
    using static methods of `ParameterTypeValidator` and then load it
    into `HTTPRequestValidationHandler` using functions ending with
    `WithCustomTypeValidator`

  - You can create your own `ParameterTypeValidator` implementing
    `ParameterTypeValidator` interface

## Handling parameters

Now you can handle parameter values:

``` js
let params = routingContext.get("parsedParameters");
let awesomeParameter = params.queryParameter("awesomeParameter");
if ((awesomeParameter !== null && awesomeParameter !== undefined)) {
  if (!awesomeParameter.isEmpty()) {
    // Parameter exists and isn't empty
    // ParameterTypeValidator mapped the parameter in equivalent language object
    let awesome = awesomeParameter.getInteger();
  } else {
    // Parameter exists, but it's empty
  }
} else {
  // Parameter doesn't exist (it's not required)
}
```

As you can see, every parameter is mapped in respective language
objects. You can also get a json body:

``` js
let body = params.body();
if ((body !== null && body !== undefined)) {
  let jsonBody = body.getJsonObject();
}
```

## Manage validation failures

A validation error fails the `RoutingContext` with 400 status code and
`ValidationException` failure. You can manage these failures both at
route level using `failureHandler` or at router level using
`errorHandler`:

``` js
router.get("/awesome/:pathParam").handler(validationHandler).handler((routingContext) => {
  // Your logic
}).failureHandler((routingContext) => {
  let failure = routingContext.failure();
  if (failure instanceof ValidationException) {
    // Something went wrong during validation!
    let validationErrorMessage = failure.getMessage();
  }
});

// Manage the validation failure for all routes in the router
router.errorHandler(400, (routingContext) => {
  if (routingContext.failure() instanceof ValidationException) {
    // Something went wrong during validation!
    let validationErrorMessage = routingContext.failure().getMessage();
  } else {
    // Unknown 400 failure happened
    routingContext.response().setStatusCode(400).end();
  }
});
```

# OpenAPI 3

Vert.x allows you to use your OpenAPI 3 specification directly inside
your code using the design first approach. Vert.x-Web API Contract
provides:

  - OpenAPI 3 compliant API specification validation with automatic
    **loading of external Json schemas**

  - Automatic request validation

  - Automatic mount of security validation handlers

You can also use the community project
[`vertx-starter`](https://github.com/pmlopes/vertx-starter) to generate
server code from your OpenAPI 3 specification.

## The Router Factory

You can create your web service based on OpenAPI 3 specification with
`OpenAPI3RouterFactory`. This class, as name says, is a router factory
based on your OpenAPI 3 specification. `OpenAPI3RouterFactory` is
intended to give you a really simple user interface to use OpenAPI 3
related features. It includes:

  - Async loading of specification and its schema dependencies

  - Mount path with operationId or with combination of path and HTTP
    method

  - Automatic generation of validation handlers

  - Automatic conversion between OpenAPI style paths and Vert.x style
    paths

  - Lazy methods: operations are mounted in declaration order inside
    specification

  - Automatic mount of security handlers

## Create a new router factory

To create a new router factory, use method
`OpenAPI3RouterFactory.create`. As location It accepts absolute paths,
local paths and local or remote URLs (HTTP or file protocol).

For example to load a spec from the local filesystem:

``` js
import { OpenAPI3RouterFactory } from "@vertx/web-api-contract"
OpenAPI3RouterFactory.create(vertx, "src/main/resources/petstore.yaml", (ar) => {
  if (ar.succeeded()) {
    // Spec loaded with success
    let routerFactory = ar.result();
  } else {
    // Something went wrong during router factory initialization
    let exception = ar.cause();
  }
});
```

You can also construct a router factory from a remote spec:

``` js
import { OpenAPI3RouterFactory } from "@vertx/web-api-contract"
OpenAPI3RouterFactory.create(vertx, "https://raw.githubusercontent.com/OAI/OpenAPI-Specification/master/examples/v3.0/petstore.yaml", (ar) => {
  if (ar.succeeded()) {
    // Spec loaded with success
    let routerFactory = ar.result();
  } else {
    // Something went wrong during router factory initialization
    let exception = ar.cause();
  }
});
```

Or, you can also access a private remote spec by passing one or more
[AuthorizationValue](https://github.com/swagger-api/swagger-parser#usage):

``` js
Code not translatable
```

You can also modify the behaviours of the router factory with
`RouterFactoryOptions`.

## Mount the handlers

Now load your first operation handlers. To load an handler use
`addHandlerByOperationId`. To load a failure handler use
`addFailureHandlerByOperationId`

You can, of course, **add multiple handlers to same operation**, without
overwrite the existing ones.

For example:

``` js
routerFactory.addHandlerByOperationId("awesomeOperation", (routingContext) => {
  let params = routingContext.get("parsedParameters");
  let body = params.body();
  let jsonBody = body.getJsonObject();
  // Do something with body
});
routerFactory.addFailureHandlerByOperationId("awesomeOperation", (routingContext) => {
  // Handle failure
});
```

Now you can use parameter values as described above

## Define security handlers

A security handler is defined by a combination of schema name and scope.
You can mount only one security handler for a combination. For example:

``` js
routerFactory.addSecurityHandler("security_scheme_name", securityHandler);
```

You can of course use included Vert.x security handlers, for example:

``` js
import { JWTAuthHandler } from "@vertx/web"
routerFactory.addSecurityHandler("jwt_auth", JWTAuthHandler.create(jwtAuthProvider));
```

When you generate the `Router` the Router Factory fails if For
debugging/testing purpose

## Not Implemented Error

Router Factory automatically mounts a default handler for operations
without a specified handler. This default handler fails the routing
context with 501 `Not Implemented` error. You can enable/disable it with
`setMountNotImplementedHandler` and you can customize this error
handling with `errorHandler`

## Response Content Type Handler

Router Factory automatically mounts a `ResponseContentTypeHandler`
handler when contract requires it. You can disable this feature with
`setMountResponseContentTypeHandler`

## Operation model

If you need to access to your operation contract while handling the
request, you can configure the router factory to push it inside the
`RoutingContext` with `setOperationModelKey`. For example:

``` js
// Configure the operation model key and set options in router factory
options.operationModelKey = "operationPOJO";
routerFactory.setOptions(options);

// Add an handler that uses the operation model
routerFactory.addHandlerByOperationId("listPets", (routingContext) => {
  let operation = routingContext.get("operationPOJO");

  routingContext.response().setStatusCode(200).setStatusMessage("OK").end(operation.getOperationId());
});
```

## Body Handler

Router Factory automatically mounts a `BodyHandler` to manage request
bodies. You can configure the instance of `BodyHandler` (e.g. to change
upload directory) with `setBodyHandler`.

## `multipart/form-data` validation

The validation handler separates file uploads and form attributes as
explained:

  - If the parameter doesn’t have an encoding associated field:
    
      - If the parameter has `type: string` and `format: base64` or
        `format: binary` is a file upload with content-type
        `application/octet-stream`
    
      - Otherwise is a form attribute

  - If the parameter has the encoding associated field is a file upload

The form attributes are parsed and validated as other request
parameters, while for file uploads the validation handler just checks
the existence and the content type.

## Custom global handlers

If you need to mount handlers that must be executed for each operationin
your router before the operation specific handlers, you can use
`addGlobalHandler`

## Router factory handlers mount order

Handlers are loaded by the router factory in this order:

1.  Body handler

2.  Custom global handlers

3.  Global security handlers defined in upper spec level

4.  Operation specific security handlers

5.  Generated validation handler

6.  User handlers or "Not implemented" handler (if enabled)

## Generate the router

When you are ready, generate the router and use it:

``` js
let router = routerFactory.getRouter();

let server = vertx.createHttpServer(new HttpServerOptions()
  .setPort(8080)
  .setHost("localhost"));
server.requestHandler(router).listen();
```

This method can fail with a `RouterFactoryException` if you didn’t
provide the required security handlers.
